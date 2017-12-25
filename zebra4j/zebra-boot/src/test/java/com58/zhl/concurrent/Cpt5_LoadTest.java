package com58.zhl.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import com58.zhl.util.Operator;

/**
 * 通过模拟压力测试，来介绍线程池、协调多线程工作的闩、原子变量等类的实现，并
 * 初步介绍各个类的作用，特性。
 * @author 58_zhl
 *
 */
public class Cpt5_LoadTest {
	
	/**
	 * 聚集并发的聚集数量
	 */
	private static final int GATHER_COUNT=100;
	
	/**
	 * 疲劳测试的最大并发数
	 */
	private static final int MAX_CONCURRENT=50;	//
	
	/**
	 * 疲劳测试的时间，毫秒
	 */
	private static final int TIRED_TIME=20*1000;
	
	
	public interface TestInterface{
		/**
		 * 开始执行测试
		 */
		public void startTest();
		
		/**
		 * 获取监控线程，用于监视，因为每个测试场景的实现内部维护的状态都不相同
		 * 为了方便起见，直接返回Thread来打印结果，而没有单独抽象出内部状态集合
		 * 接口
		 * @return
		 */
		public Thread getMonitorThread();
		
	}
	
	public static class GatherTest implements TestInterface{
		private CountDownLatch latch=new CountDownLatch(GATHER_COUNT);	//闩，用于等待其他线程
		private final List<Procedure> procedureList=new ArrayList<Procedure>();	//引用维护集合
		private AtomicInteger readyCount=new AtomicInteger(0);	//已到达闩位置的线程数量i++
		private int flag=1;	//状态，1为准备中，2为开始执行
		private int successRequest=0;	//请求成功数量统计
		private int sucessProcess=0;	//请求成功后，正确处理的统计数量，真是环境中可能会拿具体结果集来对返回结果进行比对
		private int failCount=0;	//请求失败数
		
		public void startTest(){
			for(int i=0;i<GATHER_COUNT;i++){
				procedureList.add(new Procedure());
			}
			List<Future<CallBean>> list;
			try {
				ExecutorService es=Executors.newCachedThreadPool();
				list = es.invokeAll(procedureList);
				flag=2;	//将状态处于执行中，已经可以进行执行结果输出了
				System.out.println("已提交任务组.....");
				for(Future<CallBean> rq:list){
					try {
						CallBean result=rq.get();	//因为这里是主线程，所以如果循环到这里没有执行完结果，那么就处于等待状态，一直到该线程执行完，才会继续遍历。
//						System.out.println(result.threadName+"取值完毕...");	//调试用
						if(result.result==1){
							successRequest++;	//统计成功数
							Operator.process();	//模拟处理结果
							sucessProcess++;	//统计正确请求数
						}else{
							failCount++;	//统计失败数
						}
					} catch (ExecutionException e) {
						System.out.println("startTest 执行异常....");
					}
				}
				flag=3;	//退出监控循环
				es.shutdown();	//一定要执行这一句才会立刻退出，newCachedThreadPool会缓存一部分线程在
								//池中，以备在指定时间内还有新线程使用该连接池，只有执行了该方法才会清除
								//线程池内的缓存线程实体。
			} catch (InterruptedException e) {
				System.out.println(" startTest 请求中断....");
			}
		}
		
		private class Procedure implements Callable<CallBean>{
			@Override
			public CallBean call() throws Exception {
				//因为是聚集之后，统一并发，因此readyCount++操作可能会存在竞争
				GatherTest.this.readyCount.getAndIncrement();	//统计准备好的数量
				Operator.randomSleep();	//可能是登陆，或者到达指定页面准备好做某一个测试点的过程
				CallBean callBean=new CallBean();
				callBean.threadName=Thread.currentThread().getName();
				System.out.println(Thread.currentThread().getName()+"等待中...");
				GatherTest.this.latch.countDown();	//准备好以后将线程数量减一
				GatherTest.this.latch.await();	//等待其它线程
				callBean.result=Operator.request()?1:2;	//模拟请求
				return callBean;
			}
		}

		private class CallBean{
			int result;
			String threadName;
		}
		
		@Override
		public Thread getMonitorThread() {
			return new Thread(){
				public void run(){
					while(true){
						boolean b=false;
						switch(GatherTest.this.flag){
						case 1:	//准备状态
							System.out.println("聚集总数：" + Cpt5_LoadTest.GATHER_COUNT
									+ "    已就绪：" + GatherTest.this.readyCount);
							break;
						case 2: // 执行状态
							System.out.println("成功请求数："
									+ GatherTest.this.successRequest
									+ "    成功处理数:"
									+ GatherTest.this.sucessProcess
									+ "   失败总数：" + GatherTest.this.failCount);
							break;
						case 3: //执行结束
							b=true;
							System.out.println("成功请求数："
									+ GatherTest.this.successRequest
									+ "    成功处理数:"
									+ GatherTest.this.sucessProcess
									+ "   失败总数：" + GatherTest.this.failCount);
							System.out.println("执行结束.....");
							break;
						default:break;
						}
						if(b) break;	//如果执行结束，退出循环
						try {
							Thread.sleep(300);	//有准备就绪的打印结果
						} catch (InterruptedException e) {
							System.out.println("monitor 请求中断....");
							break;	//必须处理中断
						}
					}
				}
			};
		}
	}
	
	public static class TiredTest implements TestInterface{
		
		/**
		 * 注意：为什么下面不适用volatile int 而是用AtomicInteger，是因为即使是volatile
		 * 变量，也不能保证 n++这个操作是原子化的。java中，除了锁机制以外，只能通过AtomicInteger
		 * 来实现原子化递增。
		 */
		
		private AtomicInteger requestCount=new AtomicInteger(0);	//请求总数量
		
		private AtomicInteger successRequestCount=new AtomicInteger(0);	//成功数量
		
		private AtomicInteger successProcessCount=new AtomicInteger(0);	//成功处理数量
		
		private AtomicInteger currentExecuteCount=new AtomicInteger(0);	//当前正在执行的线程数
		
		private AtomicInteger interruptedCount=new AtomicInteger(0);	//中断线程数
		
		private int executeFlag=0;	//执行状态
		
		private Semaphore semaphore=new Semaphore(Cpt5_LoadTest.MAX_CONCURRENT);	//信号量，用于协调生产者与消费者的工作
		
		@Override
		public void startTest() {
			final ExecutorService esPool=Executors.newCachedThreadPool();
			Thread procedure=new Thread(){
				public void run(){
					while(true){
						try{
							TiredTest.this.semaphore.acquire();	//获取许可后才可以生产并提交到线程池中执行
							ProcedureUnit pUnit=new ProcedureUnit();
							synchronized(esPool){
								if(!esPool.isShutdown()){	//这里必须判断后提交任务，否则会出现RejectedExecutionException
															//因为有可能在执行shutdownNow后，正好执行到new ProcedureUnit()
															//这一句，而这一句是不能响应中断的
									esPool.submit(pUnit);	//提交执行
								}
							}
						}catch(InterruptedException e){
							System.out.println("生产者线程已停止....");
							break;
						}
					}
				}
			};
			procedure.start();
			try {
				Thread.sleep(Cpt5_LoadTest.TIRED_TIME);
				procedure.interrupt();	//生产者停止
				synchronized(esPool){
					esPool.shutdownNow();	//立刻停止，只有该方法才能发出interruptedException中断
				}
				executeFlag=2;	//状态置为已结束
			} catch (InterruptedException e) {
				System.out.println("startTest 请求中断....");
			}
		}
		
		private class ProcedureUnit implements Callable<Integer>{

			/**
			 * 注意该方法的递增TiredTest类统计变量的位置，如果位置不对，很可能导致
			 * 统计不准确，
			 */
			@Override
			public Integer call() throws Exception {	//生产单元
				try{
					TiredTest.this.currentExecuteCount.getAndIncrement();	//当前正在执行的线程数
					boolean result=Operator.request2();	//请求某个页面
					TiredTest.this.requestCount.getAndIncrement();	//如果请求完成，则进行请求总数的统计
					if(result){	//如果请求成功了
						TiredTest.this.successRequestCount.getAndIncrement();	//统计正确响应数
						Operator.process2();					//如果这里产生中断，则不统计到successProcessCount
						TiredTest.this.successProcessCount.getAndIncrement();	//成功处理数
						return 1;
					}
				}catch(InterruptedException e){	//这里捕获的是请求异常
					//这里捕获异常，则说明是本地中断请求产生的异常，不应该进行总数统计
					TiredTest.this.interruptedCount.getAndIncrement();	//中断数量
				}finally{
					TiredTest.this.currentExecuteCount.getAndDecrement();	//不论何种情况发生异常，当前线程都要结束执行
					TiredTest.this.semaphore.release();	//一定要释放许可，否则将导致生产者的生产
				}
				return null;
			}
		}
		

		@Override
		public Thread getMonitorThread() {
			return new Thread(){
				public void run() {
					while (true) {
						StringBuilder sb=new StringBuilder();
						sb.append("请求总数:").append(TiredTest.this.requestCount).append("\t");
						sb.append("成功响应:").append(TiredTest.this.successRequestCount).append("\t");
						sb.append("处理成功:").append(TiredTest.this.successProcessCount).append("\t");
						sb.append("正在执行线程数:").append(TiredTest.this.currentExecuteCount).append("\t");
						sb.append("中断线程数：").append(TiredTest.this.interruptedCount).append("\t");
						System.out.println(sb.toString());
						if(executeFlag==2){
							break;
						}
						try {
							Thread.sleep(300); // 有准备就绪的打印结果
						} catch (InterruptedException e) {
							System.out.println("monitor 请求中断....");
							break; // 必须处理中断
						}
					}
				}
			};
		}
		
	}
	
	public static void test(final TestInterface test){
		//----------------------可以基于testInterface抽象出一系列的行为---------------------------------
		Thread t=new Thread(){
			public void run(){
				test.startTest();
			}
		};
		t.start();
		test.getMonitorThread().start();
		System.out.println("监控启动..");
		//暂时只抽象出两个方法，用来演示,应该有更多的操作被分离实现更详细的统计及后续行为
		//----------------------可以基于testInterface抽象出一系列的行为---------------------------------
	}
	
	public static void main(String args[]){
//		test(new GatherTest());	//聚集
		
		test(new TiredTest());	//疲劳测试
	}
	
}
