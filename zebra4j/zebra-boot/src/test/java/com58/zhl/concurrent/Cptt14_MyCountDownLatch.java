package com58.zhl.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 基于AbstractQueuedSynchronizer框架的CountDownLatch
 * @author 58_zhl
 *
 */
public class Cptt14_MyCountDownLatch {

	/**
	 * 自定义CountDownLatch
	 * @author 58_zhl
	 *
	 */
	static class MyCountDownLatch {

		private final Sync sync;

		public MyCountDownLatch(int count) {
			this.sync = new Sync(count);
		}
		
		public void await() throws InterruptedException {
	        sync.acquireSharedInterruptibly(1);	//内部会调用重载的tryAcquireShared(int acquires)
	    }
		
		public boolean await(long timeout, TimeUnit unit)
				throws InterruptedException {
			return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));	//内部会调用重载的tryAcquireShared(int acquires)
		}
		
		public void countDown() {
	        sync.releaseShared(1);	//内部会调用重载的tryReleaseShared(arg)方法
	    }
		
		public long getCount() {
	        return sync.getCount();
	    }

		private static final class Sync extends AbstractQueuedSynchronizer {

			private static final long serialVersionUID = -7364194632496799924L;

			Sync(int count) {
				setState(count);
			}

			int getCount() {
				return getState();
			}
			
			public int tryAcquireShared(int acquires) {
				return getState() == 0 ? 1 : -1;	//很显然，这句代码不是线程安全的。思考一下CountDownLatch的特性，做出解释
			}

			/**
			 * 如果状态state为0，则返回true
			 */
			public boolean tryReleaseShared(int releases) {
				for (;;) {	//非阻塞算法
					int c = getState();
					if (c == 0)	//先判断是否为0，如果为0，那么直接返回，这样就可以保证nextc一定不会小于0
						return false;
					int nextc = c - 1;
					System.out.println(nextc+"====="+c);
					if (compareAndSetState(c, nextc))
						return nextc == 0;
				}
			}
		}
	}
	
	/**
	 * 执行结果，可以看到
	 * @throws InterruptedException 
	 */
	public static void test1() throws InterruptedException{
		int n1=2;	//自定义线程的阻塞
		final MyCountDownLatch countDown=new MyCountDownLatch(n1);
		for(int i=0;i<n1;i++){
			Thread t=new Thread(){
				public void run(){
					try {
						countDown.countDown();
						countDown.await();
						System.out.println("thread_1...进入执行......");
						Thread.sleep(3000);	//睡眠三秒重...
						System.out.println("thread_1...执行完毕......");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
			t.start();
		}
		//这个循环结束的时候，countDown的聚集点已经达到了，进入执行阶段
		Thread.sleep(100);
		for(int i=0;i<n1;i++){
			Thread t=new Thread(){
				public void run(){
					try {
						countDown.countDown();
						countDown.await();	//企图用同一个countDownLatch第二次聚集,但是已经没有聚集效果了
						System.out.println("t2....进入执行......");
						System.out.println("t2....执行完毕......");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
			t.start();
			Thread.sleep(500);
			System.out.println("================");
		}
	}
	
	
	public static void main(String args[]) throws InterruptedException {
		test1();
	}
	
}
