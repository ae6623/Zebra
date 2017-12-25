package com58.zhl.concurrent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 非阻塞算法实现
 * @author 58_zhl
 *
 */
public class Cptt11_Nonblocking {
	
	private static int concurrentCount=10;	//准备并发的数量
	
	/**
	 * 队列基础接口
	 * @author 58_zhl
	 * @param <T>
	 */
	static interface MyLinkedInterface<T>{
		/**
		 * 添加元素到列表中
		 * @param element
		 */
		public void add(T element);
		/**
		 * 显示所有元素
		 */
		public void displayElement();
	}
	
	/**
	 * 链表节点
	 * @author 58_zhl
	 * @param <T>
	 */
	private static class Node<T>{
		T element;
		Node<T> next=null;
		
		Node(T element,Node<T> next){
			this.element=element;
			this.next=next;
		}
	}
	
	/**
	 * 非线程安全的链表
	 * @author 58_zhl
	 */
	static class MyLinked_1<T> implements MyLinkedInterface<T>{
		private Node<T> head=new Node<T>(null,null);	//链表头
		private Node<T> last=head;	//尾节点
		
		public void add(T element) {
			Node<T> addNode=new Node<T>(element,null);	//新增节点
			last.next=addNode;	//将addNode添加到尾节点
//			try {
//					Thread.sleep(20);	//这句是为了增大并发冲突的概率
//			} catch (InterruptedException e) { }
			last=addNode;	//更新尾节点
		}

		public void displayElement() {
			Node<T> current=head.next;
			StringBuilder sb=new StringBuilder();
			int count=0;
			while(current!=null){
				T temp=current.element;
				sb.append(temp.toString()+" ");
				current=current.next;
				count++;
			}
			System.out.println(sb.toString());
			System.out.println("共"+count+"元素");
		}
	}

	/**
	 * 同步的线程安全的链表
	 * @author 58_zhl
	 */
	static class MyLinked_2<T> extends MyLinked_1<T> implements
			MyLinkedInterface<T> {
		@Override
		public synchronized void add(T element) {
			super.add(element);
		}
	}
	
	/**
	 * 非阻塞线程安全的链表
	 * @author 58_zhl
	 */
	static class MyLinked_3<T> implements MyLinkedInterface<T>{
		/**
		 * 默认头节点
		 */
		private final AtomicReference<Node<T>> head
				=new AtomicReference<Node<T>>(new Node<T>(null,null));
		
		/**
		 * 尾节点，默认和头结点指向同一个Node
		 * 注:AtomicReference使用是用于维护对某类型变量操作时原子化，而不是AtomicReference
		 * 封装后的对象，可以进行原子化。因此，这里把head、last变量都设置为final类型的，仅能
		 * 赋值一次，之后所有的操作对赋值的改变都是对AtomicReference维护的内部变量进行的。
		 */
		private final AtomicReference<Node<T>> last
				=new AtomicReference<Node<T>>(head.get());
		
		public void add(T element) {
			Node<T> newNode=new Node<T>(element,null);
			while(true){
				Node<T> currentLast=last.get();	//获取最新的尾节点
				Node<T> lastNext=currentLast.next.get();	//获取尾节点的下一个节点，用于做参照值
				if(currentLast==last.get()){	//判断在执行上面两句的时候，尾节点有没有发生变化，如果有，则可能需要重新获取到lastNext的值
					if(lastNext!=null){	//如果lastNext不等于null，则说明该链表处于中间状态，因为正常情况下，lastNext一定是null的
						//如果不处于中间状态，则推进节点，即帮助其它线程完成剩余操作
						/**
						 * 正常情况下，更新分两步：
						 * last.next=newNode;	//进入此if语句，则说明，完成了这一步，在下一步时未完成
						 * last=newNode;
						 */
						last.compareAndSet(currentLast, lastNext);	//并发中，这句也不见得会成功，因为可能并发时又有新的节点被添加了
						//上面只是帮助其它线程推进了节点，但是还要通过下次循环，把自己持有的新节点添加进链表
					}else{
						//如果链表处于稳定状态，稳定状态时，需要通过执行下面两步来完成节点添加
						/**
						 * if中执行后的效果类似
						 * last.next=newNode;
						 */
						if(currentLast.next.compareAndSet(null, newNode)){	//尝试将最新的节点添加到尾列表，这里认为之前的next是null,并且现在希望next是newNode
//							try {
//								Thread.sleep(20);	//这里可以同样为了增大并发冲突的概率
//							} catch (InterruptedException e) { }
							/**
							 * 这句执行后的操作类似于
							 * last=newNode;
							 */
							last.compareAndSet(currentLast, newNode);
							return;	//添加节点成功后跳出循环
						}
					}
				}
			}
		}

		public void displayElement() {
			if(head.get().next!=null){
				Node<T> current=head.get().next.get();
				StringBuilder sb=new StringBuilder();
				int count=0;
				Set<T> hasSet=new HashSet<T>();
				while(current!=null){
					T element=current.item;
					sb.append(element).append(" ");
					hasSet.add(element);
					count++;
					current=current.next.get();
				}
				System.out.println(sb.toString());
				System.out.println("共"+count+"元素,hasSet排重后"+hasSet.size());
			}
		}
		
		private static class Node<T>{
			T item;
			AtomicReference<Node<T>> next;
			private Node(T item,Node<T> next){
				this.item=item;
				this.next=new AtomicReference<Node<T>>(next);
			}
		}
	}
	
	
	public static void testLinked(final MyLinkedInterface<Integer> linked){
		for(int i=0;i<10;i++){	//首先，单线程添加是个元素，测试链表的可用性
			linked.add(i);
		}
		linked.displayElement();
		//下面开始并发
		final CountDownLatch latch=new CountDownLatch(concurrentCount);
		ExecutorService es=Executors.newCachedThreadPool();
		List<Callable<Long>> list=new ArrayList<Callable<Long>>();
		for(int i=10;i<concurrentCount+10;i++){
			final int idx=i;
			list.add(new Callable<Long>(){
				
				public Long call() throws Exception {
					latch.countDown();
					latch.await();
					long start=System.nanoTime();
					linked.add(idx);
					long end=System.nanoTime();
					return (end-start);
				}
			});
		}
		try {
			List<Future<Long>> result=es.invokeAll(list);
			System.out.println("请求"+list.size()+"次并发...");
			long countTime=0;
			int executeCount=0;
			for(Future<Long> r:result){
				countTime+=r.get();
				executeCount++;
			}
			System.out.println("并发"+executeCount+"次访问，全部返回...");
			System.out.println("并发添加完所有元素的时间："+countTime+"ns");
			linked.displayElement();
			es.shutdown();
		} catch (InterruptedException e) { 
			
		}catch (ExecutionException e) {
			
		}
		
	}
	
	public static void main(String args[]){
//		testLinked(new MyLinked_1<Integer>());	//非线程安全的链表
		System.out.println("-----------------------------------------------------------");
//		testLinked(new MyLinked_2<Integer>());	//线程安全的链表
		System.out.println("-----------------------------------------------------------");
		testLinked(new MyLinked_3<Integer>());	//非阻塞的链表 
	}
	
}
