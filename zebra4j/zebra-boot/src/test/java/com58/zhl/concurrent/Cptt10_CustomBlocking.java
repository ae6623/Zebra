package com58.zhl.concurrent;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 阻塞行为的应用
 * @author 58_zhl
 *
 */
public class Cptt10_CustomBlocking {
	
	private static int LEN=3;
	
	static interface MyBlockingInterface<T>{
		/**
		 * 添加操作
		 * @param e
		 * @throws InterruptedException
		 */
		public void put(T e) throws InterruptedException;
		
		/**
		 * 弹出操作
		 * @return
		 * @throws InterruptedException
		 */
		public T take() throws InterruptedException;
	}
	
	/**
	 * 自定义的阻塞队列，仅仅是介绍一种用法，因为现实应用中，很可能某个类的
	 * 行为需要拥有阻塞的功能，这时可能用现有API不太容易实现
	 * @author 58_zhl
	 * @param <T>
	 */
	static class MyBlokingLinked<T> implements MyBlockingInterface<T>{
		
		private LinkedList<T> list=new LinkedList<T>();
		
		public void put(T e) throws InterruptedException{
			synchronized(this){
				//可能上面还有一些与业务相关的代码.....
				while(list.size()==LEN)	//这段操作时，需要暂时堵塞...
					wait();
				list.addLast(e);
				notifyAll();	//注意这里一定要使用notifyAll
			}
		}
		
		public T take() throws InterruptedException{
			synchronized(this){
				T element=null;
				while(list.size()==0)
					wait();
				element=list.removeFirst();
				notifyAll();	//注意这里一定要使用notifyAll
				return element;
			}
		}
	}
	

	/**
	 * 一个限定池
	 * @author Administrator
	 *
	 */
	static class MyBlockingQueue<T> implements MyBlockingInterface<T>{
		
		private final Lock lock=new ReentrantLock();
		
		/**
		 * 用于堵塞元素数组已经处于饱和状态时，后续请求添加的线程
		 */
		private final Condition notFull=lock.newCondition();
		
		/**
		 * 用于堵塞元素数组为空时，后续请求获取元素的线程
		 */
		private final Condition notEmpty=lock.newCondition();
		
		/**
		 * 队列元素个数
		 */
		private int count=0;
		
		/**
		 * 当前的队列头数组的索引
		 */
		private int head=0;
		
		/**
		 * 当前的队列尾数组的索引
		 */
		private int last=0;
		
		private final T[] items=(T[])new Object[LEN];
		
		public void put(T x) throws InterruptedException{
			lock.lock();
			try{
				while(count==items.length)	//如果已经达到了最大值，需要进入阻塞状态
					notFull.await();
				items[last]=x;
				if(++last==items.length)	//如果可用的索引已达到了数组末尾，则从0继续开始保存
					last=0;
				++count;	//添加元素计数器
				notEmpty.signal();	//唤醒那些因队列已空，但仍在等待弹出的线程
			}finally{
				lock.unlock();
			}
		}
		
		public T take() throws InterruptedException{
			lock.lock();
			try{
				while(count==0)	//如果当前数组中没有元素了，则处于等待状态
					notEmpty.await();
				T x=items[head];	//取出队列头
				items[head]=null;
				if(++head==items.length)	//如果队列头已经到了数组末尾，则从下标0继续取数据
					head=0;
				--count;
				notFull.signal();	//唤醒那些因队列已满，仍在等待添加的线程
				return x;
			}finally{
				lock.unlock();
			}
		}
		
	}
	
	/**
	 * 测试自定义的阻塞队列
	 */
	public static void testMyBlockingQueue(final MyBlockingInterface<String> list){
//		final MyBlokingLinked<String> list=new MyBlokingLinked<String>();
		final int count=10;	//元素个数
		Thread t1=new Thread(){	//添加线程
			public void run(){
				try {
					for(int i=0;i<count;i++){
						list.put(i+"");
						System.out.println("添加元素："+i);
						if(i==4){
							Thread.sleep(4000);	//添加线程睡眠3秒，观察获取线程
						}
					}
				} catch (InterruptedException e) { }
			}
		};
		Thread t2=new Thread(){	//获取线程
			public void run(){
				for(int i=0;i<count;i++){
					try {
						String t=list.take();	//获取元素0后，等待了一段时间，证明了添加线程添加0元素后睡眠，使获取线程进入阻塞
						System.out.println("======获取元素："+t);
					} catch (InterruptedException e) { }
				}
			}
		};
		t1.start();
		t2.start();
	}
	
	public static void main(String args[]) throws Exception{
//		testMyBlockingQueue(new MyBlokingLinked<String>());	//wait+linked实现方式

		testMyBlockingQueue(new MyBlockingQueue<String>());	//condition实现方式
	}
	
}
