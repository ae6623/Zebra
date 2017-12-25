package com58.zhl.concurrent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * hook、Daemon、BlockingQueue实现日志输出功能
 * @author 58_zhl
 *
 */
public class Cptt12_LogWriter {
	
	/**
	 * log集合
	 */
	private static BlockingQueue<String> logs=new ArrayBlockingQueue<String>(30);
	
	
	/**
	 * 日志输出线程..
	 *
	 */
	static class LogWriteThread extends Thread{
		
		public void run(){
			System.out.println("日志输出进程已启动....");
			while(true){	//该线程会作为守护线程，因此不需要有退出的口，因为在主进程执行完毕后，会自动退出
				try {
					String str=logs.take();
					System.out.println("logWrite："+str);
					Thread.sleep(500);
				} catch (InterruptedException e) {
					System.out.println("弹出日志异常");
				}
			}
		}
		
	}
	
	/**
	 * 钩子进程输出所有未输出的日志信息
	 * @author 58_zhl
	 *
	 */
	static class HookLogWriteThread extends Thread{
		public void run(){	//钩子线程的run主要针对java系统执行System.exit(n);内部程序请求退出JVM
			System.out.println("钩子进程启动....");
			int n=0;
			while(!logs.isEmpty()){
				try {
					System.out.println("睡眠 "+(++n)+" 次");
					Thread.sleep(500);	//因为这里睡眠，所以实时性不够好。。如果不睡眠则不停的循环浪费资源
				} catch (InterruptedException e) {
					System.out.println("钩子进程请求中断...");
				}
			}
			System.out.println("钩子进程执行完毕...");
		}

	}
	
	/**
	 * 中断信号捕获
	 * @author 58_zhl
	 *
	 */
	static class TerminateThread implements SignalHandler{
		@Override
		public void handle(Signal arg0) {
			System.out.println("系统中断....");
			int n=0;
			while(!logs.isEmpty()){
				try {
					System.out.println("睡眠 "+(++n)+" 次");
					Thread.sleep(500);
				} catch (InterruptedException e) {
					System.out.println("钩子进程请求中断...");
				}
			}
			System.out.println("系统中断完毕....");
			System.exit(-1);
		}
		
	}
	
	/**
	 * 生成日志线程
	 *
	 */
	static class GeneratorLogThread extends Thread{
		
		public void run(){
			String baseStr=this.getName()+"==第";
			for(int i=1;i<11;i++){	//模拟生成日志，到Logs队列中
				try {
					Cptt12_LogWriter.logs.put(baseStr+i+" 条日志");
				} catch (InterruptedException e) {
					System.out.println(baseStr+"请求中断");
				}
			}
			System.out.println(this.getName()+"生产日志完毕...");
		}
	}
	
	/**
	 * 测试普通钩子效果
	 */
	public static void testLogWriter_1(){
		Thread logWriterThread=new LogWriteThread();
		logWriterThread.setDaemon(true);	//设置为守护进程 
		logWriterThread.start();
		new GeneratorLogThread().start();	//
		new GeneratorLogThread().start();
		Runtime.getRuntime().addShutdownHook(new HookLogWriteThread());	//设置钩子线程
	}
	
	/**
	 * 测试信号捕获
	 * @throws InterruptedException 
	 */
	public static void testLogWriter_2() throws InterruptedException{
		Thread logWriterThread=new LogWriteThread();
		logWriterThread.start();
		new GeneratorLogThread().start();	//
		new GeneratorLogThread().start();
		Thread.sleep(1000);
		Signal signal=new Signal("INT");	//程序结束信号，该信号可以被阻塞和处理
		Signal.handle(signal, new TerminateThread());
	}
	
	public static void main(String args[]) throws InterruptedException{
		/**
		 * 该类型常用于本地程序的开发，主线程主要负责启动服务线程，在服务线程未结束时，JVM一直处于运行状态。
		 * 而清理服务线程资源的线程，往往设置为守护进程，这样在主线程启动的所有服务线程结束时，这些守护线程
		 * 会自动退出，不会影响进程的正常退出。但如想要在程序正常退出后，需清理守护进程的资源，则需要用钩子
		 */
		testLogWriter_1();	//钩子方式
		
		/**
		 * 有时候，我们可能需要捕获系统的事件行为，来判断用户期望的请求操作。例如：执行ctrl+C不需要正常启动
		 * 应用程序，而是异常退出，这样就不需要做资源保存工作等。这时，可以通过Signal注册一系列的系统信号来
		 * 执行对应的操作。下面虽然用Signal来清理系统资源，但是在任何情况下，应该优先使用钩子来清理资源，而
		 * 捕获系统信号，应该作为一种事件处理。
		 */
		//java com58.zhl.concurrent.Cptt12_LogWriter
//		testLogWriter_2();	//中断信号处理
	}
	
}
