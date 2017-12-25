package com58.zhl.util;

import java.util.Random;

public class Operator {
	private static Random random=new Random(47);	//因子为47是为了保证每次运行的结果都是一致的，方便展示用
	private static int failFactor=50;	//失败因子，不要太大，否则全是成功了
	private static int valueScope=200;	//随机数的取值范围，因为取值大小决定着睡眠时间，因此不宜太大
	/**
	 * 模拟请求访问
	 * @return 
	 */
	@Deprecated
	public static boolean request(){
		int n=random.nextInt(valueScope);	//注意：该方法没有使用任何同步机制，但为什么能确保是线程安全？
		try {
			Thread.sleep(n);	//随机的睡眠时间，模拟请求处理过程所需要的时间
		} catch (InterruptedException e) { System.out.println(" request 请求中断..."); }
		return (n%failFactor!=0);	//
	}
	
	public static boolean request2() throws InterruptedException{
		int n=random.nextInt(valueScope);	//注意：该方法没有使用任何同步机制，但为什么能确保是线程安全？
		Thread.sleep(n);	//随机的睡眠时间，模拟请求处理过程所需要的时间
		return (n%failFactor!=0);	//
	}
	
	/**
	 * 随机等待
	 */
	@Deprecated
	public static void randomSleep(){
		int n=random.nextInt(100);
		try {
			Thread.sleep(n);
		} catch (InterruptedException e) {
			System.out.println("randomSleep 请求中断...");
		}
	}
	
	/**
	 * 随机等待
	 * @throws InterruptedException 
	 */
	public static void randomSleep2() throws InterruptedException{
		int n=random.nextInt(100);
		Thread.sleep(n);
	}
	
	/**
	 * 模拟处理过程
	 */
	@Deprecated
	public static void process(){
		int n=random.nextInt(valueScope);
		try{
			Thread.sleep(n);
		}catch(InterruptedException e){ System.out.println(" process 请求中断..."); }
	}
	
	/**
	 * 模拟处理过程
	 * @throws InterruptedException 
	 */
	public static void process2() throws InterruptedException{
		int n=random.nextInt(valueScope);
		Thread.sleep(n);
	}
	
	public static byte getByte() throws InterruptedException{
		int n=random.nextInt(255);
		Thread.sleep(n);
		return (byte)n;
	}
	
}
