package com58.zhl.concurrent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * CopyOnWriteArrayList类的应用场景，在需要频繁遍历的同时，又需要对列表有频率的
 * 更新时
 * @author 58_zhl
 *
 */
public class Cpt3_ConcurrentUtilClass {
	
	/**
	 * 用于控制住线程暂停
	 */
	private static CountDownLatch countDownLatch=new CountDownLatch(2);	//栅栏
	
	/**
	 * 循环次数
	 */
	private static int forIndex=100000;
	
	/**
	 * 测试各种数组的性能
	 * @throws InterruptedException 
	 */
	public static void testList(final List<Integer> list) throws InterruptedException{
		Thread run=new Thread(){
			public void run(){
				Random random=new Random();
				for(int i=0;i<forIndex;i++){	//模拟频繁的执行修改元素操作
					int flag=random.nextInt(10);
					if(flag%2==0){
						list.add(i);
					}else{
						int idx=list.size()/2;
						if(idx!=0){
							list.remove(idx);
						}
					}
				}
				Cpt3_ConcurrentUtilClass.countDownLatch.countDown();	//递减，让主线程可以执行时间统计
			}
		};
		run.start();
		System.out.println("修改更新list的线程启动");
		run=new Thread(){	//可以模拟大量变量，或者少量遍历
			public void run(){
				for(int i=0;i<(forIndex/2);i++){	//模拟多次遍历
					Iterator<Integer> it=list.iterator();	
					//如果CopyOnWriteArrayList，是因为迭代期是迭代时刻的一个快照，因此不需要阻塞写操作
					//但是如果普通的数组，因为迭代器会失效Iterator，那么仅能用线程安全的方式，阻塞写操作
					//才能进行安全迭代，这样可以有效的增加吞吐量
					while(it.hasNext()){
						it.next();
					}
				}
				Cpt3_ConcurrentUtilClass.countDownLatch.countDown();	//递减，让主线程可执行时间统计
			}
		};
		run.start();
		System.out.println("遍历list线程启动");
	}
	
	public static void initList(List<Integer> list){
		for(int i=0;i<100;i++){
			list.add(i);
		}
	}
	
	public static void main(String args[]) throws InterruptedException{
		long start=System.currentTimeMillis();
//		List<Integer> list=Collections.synchronizedList(new ArrayList<Integer>());	//线程安全的数组，但是迭代器却不能线程安全
		List<Integer> list=new CopyOnWriteArrayList<Integer>();
		initList(list);	//先让数组有一定的数据
		
		testList(list);
		countDownLatch.await();
		System.out.println("执行完成");
		long end=System.currentTimeMillis();
		System.out.println((end-start));
		
		/**
		 * 思考：网游中，如何展示角色当前所在位置屏幕范围内的其他人物或怪物信息？
		 */
	}
	
	
}
