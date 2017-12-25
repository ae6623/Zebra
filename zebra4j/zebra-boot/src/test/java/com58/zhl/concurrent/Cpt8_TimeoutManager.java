package com58.zhl.concurrent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 基于DelayQueue实现一个超时管理
 * @author 58_zhl
 *
 */
public class Cpt8_TimeoutManager {
	
	private static List<String> result=new ArrayList<String>();
	
	/**
	 * 遍历队列以便观察结果
	 * @param queue
	 */
	private static String iteratorDelayQueue(DelayQueue<Session> queue){
		Iterator<Session> it=queue.iterator();
		StringBuilder sb=new StringBuilder();
		while(it.hasNext()){
			Session dt=it.next();
			sb.append(dt.toString()+"\n");
		}
		return sb.toString();
	}

	
	public static void testDelayQueue(){
		DelayQueue<Session> queue=new DelayQueue<Session>();
		Random random=new Random(47);
		StringBuilder sb=new StringBuilder();
		List<Session> list=new ArrayList<Session>();
		//生产对象添加到队列中
		for(int i=0;i<5;i++){
			long timeout=(random.nextInt(10)+1)*1000;	//11以内的整数乘以1000毫秒
			Session temp=new Session(timeout);
			sb.append("id:"+temp.id+"-").append(timeout).append(" ");
			list.add(temp);
			queue.offer(temp);
		}
		System.out.println("=========================添加到队列中的顺序=========================");
		System.out.println(sb.toString());
		//可以先观察queue的排序结果
		System.out.println("=========================队列中实际的顺序========================");
		System.out.println(iteratorDelayQueue(queue));
		System.out.println("=========================启动清理线程==============================");
		monitorThread(queue);	//启动监控清理线程
		//可先不执行延迟清理，进行观察
		updateObject(list,queue);	//模拟因session最新被调用，而延迟清理
	}
	
	public static void monitorThread(final DelayQueue<Session> queue){
		Thread thread=new Thread(){
			public void run(){
				while(!queue.isEmpty()){
					try {
						Session dt=queue.take();
						String str=dt.toString2()+"=====已被清理"+"\t currentTime:"+System.currentTimeMillis();
						System.out.println(str);
						result.add(str);
						result.add(iteratorDelayQueue(queue));	//每次清理之后，都要保存一下队列的快照
					} catch (InterruptedException e) { System.out.println("清理中断...."); return; }
				}
				System.out.println("清理完所有缓存！！！！！");
				
				System.out.println("======================延迟对象生命周期运行时queue的快照========================");
				for(String tstr:result){
					System.out.println(tstr);
				}
				System.out.println("==============================================");
			}
		};
		thread.start();
	}
	
	/**
	 * 模拟在清理session池的时候，session因重新调用而导致清理时间延迟
	 * @param list
	 */
	public static void updateObject(final List<Session> list,final DelayQueue<Session> queue){
		Thread thread=new Thread(){
			public void run(){
				try {
					//对于iteratorDelayQueue可能存在同步的问题，但是这里因sleep时间点的问题，不会发生异常
					//暂时不需要处理
					Thread.sleep(1000);	//睡眠1000ms
					//list(4)默认生命周期是2000ms，睡眠1000后，现在应该还有1000ms左右
					list.get(4).updateTriger();
					result.add("id:"+list.get(4).id+" 寿命延长\t currentTime:"+System.currentTimeMillis());
					Thread.sleep(1000);	//再次睡眠1000ms
					//再次延长list(4)，这次延时后list(4)的总生命周期应该是4000ms
					list.get(4).updateTriger();
					result.add("id:"+list.get(4).id+" 寿命延长\t currentTime:"+System.currentTimeMillis());
					//执行到此处时，一共睡眠了2000ms，list(1)的初始生命是6000ms,此时延迟应该总共生命周期为8000ms
					list.get(1).updateTriger();
					result.add("id:"+list.get(2).id+" 寿命延长\t currentTime:"+System.currentTimeMillis());
				} catch (InterruptedException e) { }
			}
		};
		thread.start();
	}
	
	/**
	 * 超时对象封装
	 * @author 58_zhl
	 *
	 */
	static class Session implements Delayed{
		/**
		 * 用于生成ID
		 */
		private static AtomicInteger objectId=new AtomicInteger(0);
		/**
		 * 本对象的ID
		 */
		private int id=objectId.incrementAndGet();	//注意与getAndIncrement方法的区别
	    /**
	     * 到期时间
	     */
	    private AtomicLong triger=null;
	    /**
	     * 创建时间
	     */
	    private long createTime=System.nanoTime();
	    /**
	     * 该对象被更新的次数
	     */
	    private AtomicInteger count=new AtomicInteger(0);
	    /**
	     * 该对象的超时时间，对象存在的生命周期
	     */
	    private final long timeout;
	    
	    public Session(long timeout){
	    	this.timeout=TimeUnit.NANOSECONDS.convert(timeout, TimeUnit.MILLISECONDS);	//保存对象
	    	this.triger=new AtomicLong(createTime+this.timeout);	//对象的到期时间
	    }
	    
	    /**
	     * 更新超时时间，每使用某个对象时，应该调用此方法，
	     * 来延长对象在内存的生命周期
	     */
	    public void updateTriger(){
	    	/*
	    	 * 统计被更新的次数，在对象创建后，因再次使用导致对象的生命周期延长的次数
	    	 */
	    	count.getAndIncrement();
	    	long oldValue=0;	//获取当前值
	    	long newValue=0;
	    	do{
	    		oldValue=triger.get();
	    		newValue=System.nanoTime()+timeout;
	    	}while(!triger.weakCompareAndSet(oldValue, newValue));
	    }
	    
	    /**
	     * 对比方法，按照从大到小的顺序
	     * 在offer的时候只调用compareTo方法
	     */
		@Override
		public int compareTo(Delayed o) {
			Session dt=(Session)o;
			System.out.println("compareTo()方法被调用....id:"+id+"\t currentTime:"+System.currentTimeMillis());
			return (this.triger.get()>dt.triger.get()?1:(this.triger.get()<dt.triger.get()?-1:0));
		}

		/**
		 * 返回与次对象相关的剩余延迟时间，注意，该方法和compareTo必须保持一致的排序
		 * 离当前时间越远的时间，返回的值越大，这样与compareTo方法的排序结果保持一致
		 * 
		 * 在take的时候仅调用getDelay方法，但是compareTo方法将影响到新添加元素在队列
		 * 中的位置，而这个位置可能决定getDelay方法的检索
		 */
		@Override
		public long getDelay(TimeUnit unit) {
			//unit是时间单位，如果triger中保存的不是unit单位的时间，则可以进行转换
			//注意：这里一定要进行单位转换，只有这样才能达到最优的阻塞状态，而不是进行不停的循环
			long remainTime=triger.get()-System.nanoTime();
			System.out.println("id:"+id+"\tDelay:"+remainTime+" ns\tTimeUnit:"+unit.name());
			return remainTime;
		}
		
		public String toString() {
			long start=createTime/1000000;
			long end=triger.get() / 1000000;
			return "id:" + id + "\t创建时间:" + start + " ms\t到期时间:" + end
					+ " ms\t生命周期：" + (timeout / 1000000) + " ms";
		}
		
		public String toString2(){
			long start=createTime/1000000;
			long end=triger.get() / 1000000;
			return "id:"+id+"总寿命："+ (end-start)+" ms被更新的次数:"+ count.get();
		}
		
	}
	
	
	public static void main(String args[]){
		
		/**
		 * DelayQueue内置封装了PriorityQueue。在进入take操作时，仅仅是在内置PriorityQueue对象上执行poll操作。
		 * 实际上在DelayQueue中，是不会调用compareTo方法的，但是在每次offer元素到DelayQueue中时，首先将元素
		 * 压入内置的PriorityQueue，然后判断当前压入到PriorityQueue的元素是否比压入当前元素之前的队列头元素
		 * 还要小，如果还要小，则立刻执行available.signalAll();来唤醒DelayQueue的take操作。详情请参见代码实现
		 * DelayQueue.take、DelayQueue.offer方法
		 * 
		 * PriorityQueue实现了弹出队列头部后的重排序操作，因PriorityQueue的重排序较复杂，因此我们仅需要知道每当
		 * poll操作后，PriorityQueue都会对优先级队列进行重排序就可以了。但是如果队列头元素一直未能poll，则不会
		 * 进行重排序。简单来说：PriorityQueue中的元素，一但成为头元素，那么它将不会再次成为中间节点。
		 * 另外，PriorityQueue在offer的时候，也会插入到合适的位置并重排序，及使队列处于按优先级排序状态。
		 */
		
		testDelayQueue();	//测试超时管理队列
	}

	/**
	 * ====以下为运行结果简要说明
	 * 
	 *   compareTo()方法被调用....id:2	 currentTime:1387271463047
	 *   compareTo()方法被调用....id:2	 currentTime:1387271463047
	 *   compareTo()方法被调用....id:3	 currentTime:1387271463047
	 *   compareTo()方法被调用....id:3	 currentTime:1387271463047
	 *   compareTo()方法被调用....id:4	 currentTime:1387271463047
	 *   compareTo()方法被调用....id:4	 currentTime:1387271463047
	 *   compareTo()方法被调用....id:4	 currentTime:1387271463048
	 *   compareTo()方法被调用....id:5	 currentTime:1387271463048
	 *   compareTo()方法被调用....id:5	 currentTime:1387271463048
	 *   compareTo()方法被调用....id:5	 currentTime:1387271463048
	 *   =========================添加到队列中的顺序=========================
	 *   id:1-9000 id:2-6000 id:3-4000 id:4-2000 id:5-2000 
	 *   =========================队列中实际的顺序========================
	 *   id:4	创建时间:95280919 ms	到期时间:95282919 ms	生命周期：2000 ms
	 *   id:5	创建时间:95280919 ms	到期时间:95282919 ms	生命周期：2000 ms
	 *   id:2	创建时间:95280918 ms	到期时间:95286918 ms	生命周期：6000 ms
	 *   id:1	创建时间:95280917 ms	到期时间:95289917 ms	生命周期：9000 ms
	 *   id:3	创建时间:95280918 ms	到期时间:95284918 ms	生命周期：4000 ms
	 *   
	 *   =========================启动清理线程==============================
	 *   id:4	Delay:1998754758 ns	TimeUnit:NANOSECONDS	// TODO 第一次排序后，启动线程发现需要堵塞1998754758纳秒
	 *   id:4	Delay:-383338 ns	TimeUnit:NANOSECONDS	// TODO 当堵塞指定时间后立刻调用getDelay方法，发现返回值小于0，立刻弹出
	 *   compareTo()方法被调用....id:5	 currentTime:1387271465049	//弹出过程中，还会进行重排序
	 *   compareTo()方法被调用....id:3	 currentTime:1387271465049
	 *   compareTo()方法被调用....id:3	 currentTime:1387271465049
	 *   id:4总寿命：2000 ms被更新的次数:0=====已被清理	 currentTime:1387271465049
	 *   id:5	Delay:1000503689 ns	TimeUnit:NANOSECONDS
	 *   id:5	Delay:998916441 ns	TimeUnit:NANOSECONDS
	 *   id:5	Delay:-1077481 ns	TimeUnit:NANOSECONDS
	 *   compareTo()方法被调用....id:3	 currentTime:1387271467051
	 *   compareTo()方法被调用....id:1	 currentTime:1387271467051
	 *   id:5总寿命：4001 ms被更新的次数:2=====已被清理	 currentTime:1387271467051
	 *   id:3	Delay:-2868151 ns	TimeUnit:NANOSECONDS
	 *   compareTo()方法被调用....id:2	 currentTime:1387271467051
	 *   id:3总寿命：4000 ms被更新的次数:0=====已被清理	 currentTime:1387271467051
	 *   id:2	Delay:3998453678 ns	TimeUnit:NANOSECONDS
	 *   id:2	Delay:-194911 ns	TimeUnit:NANOSECONDS
	 *   id:2总寿命：8002 ms被更新的次数:1=====已被清理	 currentTime:1387271471050
	 *   id:1	Delay:996749726 ns	TimeUnit:NANOSECONDS
	 *   id:1	Delay:-1034123 ns	TimeUnit:NANOSECONDS
	 *   id:1总寿命：9000 ms被更新的次数:0=====已被清理	 currentTime:1387271472048
	 *   清理完所有缓存！！！！！
	 *   ======================延迟对象生命周期运行时queue的快照========================
	 *   id:5 寿命延长	 currentTime:1387271464049   //TODO 寿命虽然延长了，但是不能立刻生效，因为只有等到第一个take元素弹出后，才有机会进行重排序
	 *   id:4总寿命：2000 ms被更新的次数:0=====已被清理	 currentTime:1387271465049	//TODO 第一个元素弹出了
	 *   id:5	创建时间:95280919 ms	到期时间:95283920 ms	生命周期：2000 ms	//TODO 这是弹出后，并且重排序之后的结果
	 *   id:3	创建时间:95280918 ms	到期时间:95284918 ms	生命周期：4000 ms
	 *   id:2	创建时间:95280918 ms	到期时间:95286918 ms	生命周期：6000 ms
	 *   id:1	创建时间:95280917 ms	到期时间:95289917 ms	生命周期：9000 ms
	 *   
	 *   id:5 寿命延长	 currentTime:1387271465050
	 *   id:3 寿命延长	 currentTime:1387271465050
	 *   id:5总寿命：4001 ms被更新的次数:2=====已被清理	 currentTime:1387271467051
	 *   id:3	创建时间:95280918 ms	到期时间:95284918 ms	生命周期：4000 ms
	 *   id:1	创建时间:95280917 ms	到期时间:95289917 ms	生命周期：9000 ms
	 *   id:2	创建时间:95280918 ms	到期时间:95288920 ms	生命周期：6000 ms
	 *   
	 *   id:3总寿命：4000 ms被更新的次数:0=====已被清理	 currentTime:1387271467051
	 *   id:2	创建时间:95280918 ms	到期时间:95288920 ms	生命周期：6000 ms
	 *   id:1	创建时间:95280917 ms	到期时间:95289917 ms	生命周期：9000 ms
	 *   
	 *   id:2总寿命：8002 ms被更新的次数:1=====已被清理	 currentTime:1387271471050
	 *   id:1	创建时间:95280917 ms	到期时间:95289917 ms	生命周期：9000 ms
	 *   
	 *   id:1总寿命：9000 ms被更新的次数:0=====已被清理	 currentTime:1387271472048
	 *   
	 *   ==============================================
	 */
	
}
