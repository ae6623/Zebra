package com58.zhl.concurrent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Cpt7_Executor {

	public static void testThreadPoolExecutor(){
		BlockingQueue<Runnable> bq=new ArrayBlockingQueue<Runnable>(10);
		//在调用shutdown方法后，抛出RejectedExecutionException的异常
		//handler仅影响在执行shutdown后提交任务的响应情况
		RejectedExecutionHandler handler=new ThreadPoolExecutor.AbortPolicy();	//默认就是使用该机制
		//尽量不要自己创建线程工厂，除非有特殊的需求并且非常的了解线程池的工作机制，或者需要自己的管理机制
		//如果不传递默认线程工厂参数，则使用Executors.DefaultThreadFactory
		//了解Executors.DefaultThreadFactory的实现
		ThreadFactory tf=new MyThreadFactory();
		
 		//线程池中存在10个线程，最大允许20个线程，比10个多的线程在2秒钟内，没接到任务，就会自动消除
		//使用DelayQueue作为任务队列，超出线程范围时，采用拒绝处理的政策
		ThreadPoolExecutor tpe=new ThreadPoolExecutor(10, 20, 2, TimeUnit.SECONDS, bq, tf,handler);
//		int count=0;
		for(int i=0;i<23;i++){
			tpe.execute(new RunEntity());
			System.out.println("add"+i+"========");
		}
		tpe.shutdown();
	}
	
	static class RunEntity extends Thread{
		@Override
		public void run() {
			System.out.println(Thread.currentThread().getName()+"-------------------------");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) { System.out.println("中断");}
		}
		
	}
	
	static class MyThreadFactory implements ThreadFactory{
		@Override
		public Thread newThread(final Runnable r) {
			//该方法的实现一定要处理参数r,否则将无法在线程池中正确的工作
			//可参照Executors.DefaultThreadFactory()的实现
			return new Thread(){
				public void run(){
					r.run();
				}
			};
		}
		
	}
	
	public static void main(String args[]){
		/*
		 * 线程池的存在，本身就是为了给应用程序提供便捷的多线程管理机制，包括线程监控、绑定和管理
		 * 线程资源。因此ThreadPoolExecutor类可以通过调整参数和可扩展的钩子方法，实现用户自定义的
		 * 线程池管理。但大多数情况下，都可以通过Executors的new_XXX_ThreadPool()方法，来创建大多数
		 * 使用场景的线程池。
		 */
		testThreadPoolExecutor();
//		System.out.println(TimeUnit.NANOSECONDS.name());
	}
	
}
