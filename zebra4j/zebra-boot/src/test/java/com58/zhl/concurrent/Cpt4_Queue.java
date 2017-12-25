package com58.zhl.concurrent;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 可阻塞队列的功能及行为
 * @author 58_zhl
 */
public class Cpt4_Queue {

	private static int n=100;
	
	private static int DIS_MODE=1;
	
	public static void testBlockingQueue(final Queue<Integer> queue){
		Thread run=new Thread(){
			public void run(){
				for(int i=0;i<n;i++){
					if(queue instanceof BlockingQueue<?>){
						try {
							//注意：这里一定要用put方法，因为只有put方法才是有界队列的可阻塞方法。
							//如果是用add,那么当有界队列满了以后，就会抛出异常。
							((BlockingQueue<Integer>) queue).put(i);
						} catch (InterruptedException e) { }
					}else{						
						queue.add(i);	//非阻塞方法
					}
					System.out.println("生产："+i);
					try{
						threadSleep(1);	//当前模式的生产者睡觉
					}catch(InterruptedException e){}
				}
			}
		};
		run.start();
		run=new Thread(){
			public void run(){
				for(int i=0;i<n;i++){
					Integer element=null;
					if(queue instanceof BlockingQueue<?>){
						try {
							//只有BlockingQueue下的take方法才是阻塞的方法
							element=((BlockingQueue<Integer>)queue).take();
						} catch (InterruptedException e) { }
					}else{
						element = queue.poll();	//该方法是不受阻塞的
					}
					System.out.println("消费："+element);
					try {
						threadSleep(2);	//当面模式的消费者睡眠时间
					} catch (InterruptedException e) { }
				}
			}
		};
		run.start();
		
	}
	
	/**
	 * 线程睡眠
	 * @param flag	1表示生产者，2表示消费者
	 * @throws InterruptedException 
	 * @throws Exception 
	 */
	private static void threadSleep(int flag) throws InterruptedException{
		if(DIS_MODE==1){
			switch(flag){
			case 1:
				Thread.sleep(20);break;	//生产慢
			case 2:
				Thread.sleep(1);break;	//消费快
			default:
				break;
			}
		}else if(DIS_MODE==2){
			switch(flag){
			case 1:
				Thread.sleep(1);break;	//生产快
			case 2:
				Thread.sleep(40);break;	//消费慢
			default:
				break;
			}
		}else{
			throw new RuntimeException("不支持...");
		}
	}
	
	public static void main(String args[]){
		
		//------------------------------------------------------------
		//线程安全的队列
//		testBlockingQueue(new ConcurrentLinkedQueue<Integer>());	//直接抛出异常，未能正确消费，除非自己编写管理机制，及线程唤醒机制
		
		
		//可阻塞队列，其它数据结构的实现请参照API，BlockingQueue<T>接口
		//因为使用有界队列，因此基于数组的有界队列列更合适一点
		
		//模拟生产者速度慢，消费者速度快
		DIS_MODE=1;testBlockingQueue(new ArrayBlockingQueue<Integer>(3));	//现象说明....
		
		//模拟生产者快，消费者速度慢
//		DIS_MODE=2;testBlockingQueue(new ArrayBlockingQueue<Integer>(10));	//现象说明....
		
		//------------------------------------------------------------
		
	}
	
}
