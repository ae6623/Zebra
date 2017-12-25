package com58.zhl.concurrent;

import java.util.HashMap;

/**
 * String锁管理
 * @author 58_zhl
 *
 */
public class Cpt1_StringLockManager {
	
	/**
	 * 用于管理锁的map
	 */
	private static HashMap<String,LockBean> locks=new HashMap<String,LockBean>();
	
	/**
	 * 获取锁操作
	 * @param keyStr 锁字符串
	 * @return
	 */
	public static String getLock(String keyStr){
		synchronized(Cpt1_StringLockManager.class){	//用全局锁来锁定获取锁操作的步骤
			LockBean bean=locks.get(keyStr);
			if(bean==null){
				bean=new LockBean(keyStr);
				locks.put(keyStr, bean);	//用锁字符串作为key,而bean是具体的锁信息。
			}
			bean.n++;	//统计某把锁的个数，当锁个数为0时，可以清除对该锁的管理
			//bean.key保存了第一个放入该map中的锁对象,因为只有内存地址相等的情况下，才能认为是同一把锁。
			//其它任何情况，都不会认为是同一把锁
			return bean.key;
		}
	}
	
	/**
	 * 当锁使用完毕时，必须在finally{}块中使用该函数来释放锁
	 * @param keyStr 锁字符串
	 */
	public static void removeLock(String keyStr){
		synchronized(Cpt1_StringLockManager.class){	//删除操作同样需要使用全局锁来确保getLock和removeLock是线程安全的
			LockBean bean=locks.get(keyStr);
			if(bean!=null){
				if((--bean.n)==0){	//当锁计数为0的时候，在locks中删除该锁
					locks.remove(keyStr);
//					System.out.println("已删除锁");
				}else{	//如果锁计数不为0，则仅仅是将计数器减一，不删除锁因为可能还有其它一个或多个线程持有锁
					System.out.println("未删除锁");
				}
			}
		}
	}

	/**
	 * 锁管理的工具bean
	 * @author 58
	 *
	 */
	private static class LockBean{
		String key;	//锁字符串
		int n;	//锁计数器
		LockBean(String key){
			this.key=key;
			this.n=0;
		}
	}
	
	public static void testLock(String lockStr, String threadName) {
		synchronized (lockStr) {
			//注意：synchronized(){ try{....}finally{....} }这种写法在同步块中是最常见的写法。
			//就像db访问时的try{}catch()finally{}的写法
			try {
				for (int i = 0; i < 10; i++) {
					try {
						System.out.println(lockStr + "===" + threadName);
						Thread.sleep(1000);	//睡一秒，为了保证有足够的时间来观察执行过程
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} finally {
				removeLock(lockStr);	//必须要在这里removeLock，否则就会出现内存溢出的情况
			}
		}
	}
	

	public static void test22(String lockStr, String threadName) {
		synchronized (lockStr) {
			//注意：synchronized(){ try{....}finally{....} }这种写法在同步块中是最常见的写法。
			//就像db访问时的try{}catch()finally{}的写法
			try {
				for (int i = 0; i < 10; i++) {
					try {
						System.out.println(lockStr + "===" + threadName);
						Thread.sleep(1000);	//睡一秒，为了保证有足够的时间来观察执行过程
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} finally {
				removeLock(lockStr);	//必须要在这里removeLock，否则就会出现内存溢出的情况
			}
		}
	}
	
	/**
	 * 普通没有锁管理
	 */
	public static void test1(final String a1,final String a2){
		Thread run=new Thread(){
			@Override
			public void run() {
				Cpt1_StringLockManager.testLock(a1, this.getName());	//未对a1进行管理，直接使用
			}
		};
		run.start();
		run=new Thread(){
			@Override
			public void run() {
				Cpt1_StringLockManager.test22(a2, this.getName());	//未对a2进行管理，直接使用
			}
		};
		run.start();
	}
	
	/**
	 * 锁管理的情况下
	 */
	public static void test2(final String a1,final String a2){
		Thread run=new Thread(){
			@Override
			public void run() {
				String lockStr=getLock(a1);	//通过锁管理器获取锁对象
				Cpt1_StringLockManager.testLock(lockStr, this.getName());
			}
		};
		run.start();
		run=new Thread(){
			@Override
			public void run() {
				String lockStr=getLock(a2);	//通过锁管理器获取锁对象
				Cpt1_StringLockManager.test22(lockStr, this.getName());
			}
		};
		run.start();
	}
	
	public static void test3(final String lockStr){
		final String n="1234";
		
		Thread run=new Thread(){
			@Override
			public void run() {
				Cpt1_StringLockManager.testLock(lockStr, this.getName());
			}
		};
		run.start();
		run=new Thread(){
			@Override
			public void run() {
				Cpt1_StringLockManager.test22(n, this.getName());
			}
		};
		run.start();
	}
	
	/**
	 * 测试锁管理的性能
	 */
	public static void testLockManagerPerformance(){
		long count=0;
		int n=500000;
		for(int i=0;i<n;i++){
			String lockStr=i+"-2993282883282928";
			long start=System.nanoTime();
			getLock(lockStr);
			long end=System.nanoTime();
			count+=(end-start);
		}	//首先获取锁

		for(int i=0;i<n;i++){
			String lockStr=i+"-2993282883282928";
			long start=System.nanoTime();
			removeLock(lockStr);
			long end=System.nanoTime();
			count+=(end-start);
		}	//释放锁
		
		long nano=count/n;
		long s=1000*1000*1000;	//1秒=1000毫秒 1毫秒=1000微妙 1微妙=1000纳秒
		
		System.out.println("共："+count+"ns\t平均："+nano+"ns\t"+"每秒 "+(s/nano)+"并发");
	}
	
	public static void main(String args[]){
		String n="1234";
		String a1=new String(n+"="+n);
		String a2=n+"="+n;
		/**
		 * a1和a2使用同样的方式生成，而且字符串的值相同，但是两个String在内存中
		 * 却不是一个字符串，而是两个。可见：n+"="+n操作隐含执行的new String的动作
		 */
//		test1(a1,a2);	//没有成功锁住
		
//		test2(a1,a2);	//成功锁住了
		
//		test3(n);	//猜猜能否锁住？锁住了！为什么？
		
//		System.out.println("12".hashCode()+"=="+"21".hashCode());
		
//		testLockManagerPerformance();	//锁管理器性能测试
		
	}
	
}
