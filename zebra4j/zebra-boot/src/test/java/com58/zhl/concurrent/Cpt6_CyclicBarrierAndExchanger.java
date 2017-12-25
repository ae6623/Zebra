package com58.zhl.concurrent;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Exchanger;

import com58.zhl.util.Convert;
import com58.zhl.util.Operator;

/**
 * CyclicBarrier 栅栏
 * Exchanger 线程交换
 * @author 58_zhl
 *
 */
public class Cpt6_CyclicBarrierAndExchanger {

	private static int TEST_COUNT=10;
	
	/**
	 * 展示栅栏的应用方式
	 */
	public static void testCyclicBarrier(){
		final CyclicBarrier cb=new CyclicBarrier(TEST_COUNT,new Runnable(){
			private int count=0;
			@Override
			public void run() {
				count++;
				System.out.println("=========================第"+count+"次释放线程==========================");
			}
		});
		
		for(int i=0;i<TEST_COUNT;i++){
			new Thread(){
				public void run(){
					try {
						Operator.process2();	//处理过程
						System.out.println(this.getName()+"第一步处理完成");
						cb.await();	//等待所有线程，当消耗完当前所有线程的信号量后，继续往下执行
						Operator.process2();	//基于第一步的所有处理结果，进行第二步的处理
						System.out.println(this.getName()+"第二步处理完成");
						cb.await();	//第二次栅栏，等待第二步处理完成
						Operator.process2();
						System.out.println(this.getName()+"执行结束");
					} catch (InterruptedException e) {
						//不需要处理中断只需返回就可以了
					} catch (BrokenBarrierException e) {
						//如果另一个 线程在当前线程等待时被中断或超时，
						//或者重置了 barrier，或者在调用 await 时 barrier 
						//被损坏，抑或由于异常而导致屏障操作（如果存在）失败。
					}
				}
			}.start();
		}
	}
	
	/**
	 * 展示线程交换
	 */
	public static void testExchanger(){
		final Exchanger<Byte[]> exchanger=new Exchanger<Byte[]>();
		final Byte[] b1=new Byte[100];
		final Byte[] b2=new Byte[100];
		final int n=10;
		Thread input=new Thread(){
			public void run(){
				Byte[] currentB=b1;
				for(int i=0;i<n;i++){
					Cpt6_CyclicBarrierAndExchanger.addByte(currentB);
					try {
						currentB=exchanger.exchange(currentB);	//执行数组交换，已经填充完毕了
//						System.out.println("int:"+currentB);
					} catch (InterruptedException e) { }
				}
			}
		};
		Thread output=new Thread(){
			public void run(){
				Byte[] currentB=b2;
				for(int i=0;i<n;i++){
					try {
						//先读取再处理
						currentB=exchanger.exchange(currentB);	//执行数组交换，已经输出完毕了
//						System.out.println("out:"+currentB);
						Cpt6_CyclicBarrierAndExchanger.writeByte(currentB);
					} catch (InterruptedException e) { }
				}
			}
		};
		input.start();
		output.start();
	}
	
	private static void addByte(Byte[] b){
		StringBuilder sb=new StringBuilder("in:");
		for(int i=0;i<b.length;i++){
			try {
				b[i]=Operator.getByte();	//模拟缓慢的读取数据
				sb.append(Convert.toHexString(b[i])).append(" ");	//打印对包
			} catch (InterruptedException e) { }
		}
		System.out.println(sb);
	}
	
	private static void writeByte(Byte[] bs){
		try {
			StringBuilder sb=new StringBuilder("ot:");
			Operator.randomSleep2();
			for(int i=0;i<bs.length;i++){
				sb.append(Convert.toHexString(bs[i])).append(" ");	//对包,在通信协议开发中，常用的一种调试技术
			}
			System.out.println(sb);
		} catch (InterruptedException e) { }
	}
	
	
	public static void main(String args[]){
		/**
		 * CyclicBarrier比较适用于类似多路归并的算法计算，因为下轮的计算开始需要完整的
		 * 上轮计算结果，因此CyclicBarrier可以方便的协调多路计算结果不能同时计算
		 * 完成，导致无法进行下一轮计算的问题。
		 * 例如：归并算法
		 */
//		testCyclicBarrier();	//栅栏的应用方式
		
		/**
		 * 据说遗传算法及基于遗传算法思想延伸出来的组合优化、机器学习、信号处理、
		 * 自适应控制和人工生命算法，也适合Exchanger的应用场景。
		 */
		testExchanger();
	}
	
}
