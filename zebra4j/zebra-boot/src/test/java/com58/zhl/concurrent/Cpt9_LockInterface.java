package com58.zhl.concurrent;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 基于Lock接口的类
 * @author 58_zhl
 *
 */
public class Cpt9_LockInterface {
	
	/**
	 * 基于Lock的锁分离管理器
	 * @author 58_zhl
	 */
	static class LockManager {
		private static final int N=10;
		private static Lock[] locks=new Lock[N];
		
		static {
			for(int i=0;i<N;i++){
				locks[i]=new ReentrantLock();	//创建锁对象
			}
		}
		/**
		 * 获取锁，该方法封装了Lock，并对Lock锁进行分解
		 * @param obj
		 * @return
		 */
		public static Lock getLock(Object obj){
			int hasCode=obj.hashCode();
			int idx=(hasCode<<1>>>1)%N;	//去除hasCode的符号位，并取模，得到锁数组的下标
			System.out.println("get lock_"+idx);
			return locks[idx];
		}
	}
	
	/**
	 * 测试Lock可以中断已进入排队中的请求
	 * @param key
	 */
	public static void synchrMethod(String key){
		Lock lock=LockManager.getLock(key);
		try {
			lock.lockInterruptibly();
			try{
				Thread.sleep(3000);	//睡三秒
				String lockAddress=lock.toString();
				int start=lockAddress.indexOf("@");
				int end=lockAddress.indexOf("[");
				System.out.println("key:"+key+" lockAddress:"+lockAddress.substring(start+1,end)+"\t执行完成.....");
			} catch (InterruptedException e) {
				System.out.println("synchrMethod  "+key+" 执行中，但已被中断....");
			}finally{
				lock.unlock();	//释放锁
			}
		} catch (InterruptedException e1) {
			System.out.println(key+" 正在排队中，并未获取到锁，已被中断...");
		}
	}
	
	public static void testLock(){
		Thread t1=new Thread(){
			public void run(){
				synchrMethod("1");
			}
		};
		t1.start();
		Thread t2=new Thread(){
			public void run(){
				synchrMethod("2");
			}
		};
		t2.start();
		//t1、t2并未有任何一个线程等待另一个执行完成，实现了锁分解
	}
	
	/**
	 * 测试Lock可以中断已经进入排队中的请求
	 * @throws InterruptedException
	 */
	public static void testInterruptLock() throws InterruptedException{
		final String lockStr=new String("lockStr_1");
		Thread t1=new Thread(){
			public void run(){
				synchrMethod(lockStr);
				System.out.println("线程t1结束....");	//注意：即使synchrMethod已经响应了interrupt信号，这里依然会执行
			}
		};
		t1.start();
		Thread.sleep(100);	//让t1等待一段时间，防止t2先获取到锁
		Thread t2=new Thread(){
			public void run(){
				synchrMethod(lockStr);
				System.out.println("线程t2结束....");
			}
		};
		t2.start();
		Thread.sleep(2000);
		t2.interrupt();	//t2未获取到锁应该打印: 排队中的Lock也已经中断
		Thread.sleep(100);	//睡100毫秒，防止t1先执行结束，如果t1先执行结束，那么t2有一定概率瞬间执行 Thread.sleep(3000);
		t1.interrupt();	//t1先获取到锁应该打印: synchrMethod  lockStr_1 已中断....
	}
	
	public static void main(String args[]) throws Exception{
//		testLock();	//Lock的典型用法
		
		testInterruptLock();	//测试Lock支持中断进入排队中的请求
	}
}
