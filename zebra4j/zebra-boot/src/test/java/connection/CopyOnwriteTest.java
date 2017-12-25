package connection;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * Created by lzy@js-dev.cn on 2017/1/15 0015.
 * 频繁遍历，又要更新
 */
public class CopyOnwriteTest {

	/**
	 * 等待线程
	 */
	private static CountDownLatch countDownLatch = new CountDownLatch(2);

	/**
	 * 循环次数
	 */
	private static int forNum = 1000;

	/**
	 * 初始化数组
	 */
	private static void initList(List<Integer> list) {
		for (int i=0; i<100; i++){
			list.add(i);
		}
	}

	/**
	 * 测试
	 * @param list
	 * @throws InterruptedException
	 */
	public static void test(final List<Integer> list) throws InterruptedException {
		Thread worker = new Thread() {
			public void run() {
				Random random = new Random();
				for(int i=0; i<forNum; i++) {
					int flag = random.nextInt(10);
					if(flag % 2 == 0) {
						list.add(flag);
					}else {
						int idx = list.size()/2;
						if(idx!=0) {
							list.remove(idx);
						}
					}
				}
				//阻塞
				CopyOnwriteTest.countDownLatch.countDown();
			}
		};
		worker.start();
		System.out.println("修改更新list的线程启动");

		//开始遍历
		worker = new Thread(){
			public void run() {
				for(int i=0;i<(forNum/2);i++){
					Iterator<Integer> it = list.iterator();
					while(it.hasNext()) {
						it.next();
					}
				}
				CopyOnwriteTest.countDownLatch.countDown();
			}
		};
		worker.start();
		System.out.println("遍历list的线程启动");
	}

	public static void main(String[] args) throws InterruptedException {
		long start = System.currentTimeMillis();
		List<Integer> list = new CopyOnWriteArrayList<>();
		initList(list);
		test(list);
		countDownLatch.await();
		System.out.println("执行完成");
		long end=System.currentTimeMillis();
		System.out.println((end-start));
	}

}
