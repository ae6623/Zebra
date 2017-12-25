package com58.zhl.concurrent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Collections.unmodifiableMap方法封装出来的不可修改map
 * @author 58_zhl
 *
 */
public class Cpt2_CollectionsUtil {

	private static String tkey="123";
	/**
	 * 
	 * @param map
	 */
	public static void testUnmodifiableMap(Map<String,UtilBean> map){
		UtilBean ub=map.get(tkey);
		ub.itg=12;
		Map<String,UtilBean> unmodifiableMap=Collections.unmodifiableMap(map);
		ub=unmodifiableMap.get(tkey);
		try{
			ub.itg=13;	//这里不会引发异常，如需要限制，还得对UtilBean做限制,例如私有化变量，但不提供set方法
			unmodifiableMap.put(tkey, null);	//这里可以引发异常
			unmodifiableMap.put("343333", new UtilBean("343333",22));	//也不能这么修改
			//注意：千万不要错误的使用这种工具，否则将会很危险
			//因为并发导致的异常大多数情况下都很难重现
			
		}catch(UnsupportedOperationException uoe){
			System.out.println("异常发生，不能进行修改");
		}
	}
	
	public static class UtilBean{
		String str=null;
		int itg;
		UtilBean(String str,int itg){
			this.str=str;
			this.itg=itg;
		}
	}
	
	public static void main(String args[]){
		Map<String,UtilBean> maps=new HashMap<String,UtilBean>();
		maps.put(tkey, new UtilBean("123",1));	//随便加了测试数据以后
		testUnmodifiableMap(maps);
		
	}
	
}
