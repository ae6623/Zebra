package com58.zhl.concurrent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

import com58.zhl.util.DatabaseConnection;

/**
 * 打造基于数据库操作的非阻塞算法
 * @author 58_zhl
 *
 */
public class Cptt13_DBNonblock {

	/**
	 * 并发数量
	 */
	private static int count=10;
	
	private static CountDownLatch latch=new CountDownLatch(count);
	
	public static void testConcurrent() throws InterruptedException{
		for(int i=0;i<count;i++){
			Thread th=new AccessDB(i+1);
			th.start();
			if(i+1==count){	//最后一个线程启动完毕
				System.out.println("======================");
			}else{
				Thread.sleep(500);
			}
		}
	}
	
	
	private static class AccessDB extends Thread{
		
		private int id;
		
		public AccessDB(int id){
			this.id=id;
		}
		
		public void run(){
			Connection conn=DatabaseConnection.getConnection();
			String sql="UPDATE TT1 SET STATE=2 WHERE ID=1 AND STATE=1 ";
			try {
				PreparedStatement ps=conn.prepareStatement(sql);
				latch.countDown();
				System.out.println(id+"进入等待....");
				latch.await();
				int rows=ps.executeUpdate();
				if(rows==1){
					System.out.println(id+"执行修改状态后的其它操作.....");
				}else{
					System.out.println(id+"未执行....");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				DatabaseConnection.closeConnection(conn);
			}
			
		}
		
	}
	
	public static void main(String args[]) throws InterruptedException{
		/*
		 *对于我们来说，非阻塞算法应该是一种概念，而不是一种具体的算法或技术。
		 *我们应该找出程序中的"原子"操作，在不影响业务逻辑的情况下，编写非阻塞
		 *的函数
		 */
		testConcurrent();
	}
	
}
