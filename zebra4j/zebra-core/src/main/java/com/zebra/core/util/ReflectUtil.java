package com.zebra.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 反射工具类
 * Created by lzy@js-dev.cn on 2016/10/27.
 */
public class ReflectUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReflectUtil.class);
	
	/**
	 * 根据类名创建实例
	 * @param className
	 * @return
	 */
	public static Object newInstance(String className){
		Class<?> cls = ClassUtil.loadClass(className);
		return newInstance(cls);
	}
	
	/**
	 * 根据类创建实例
	 * @param cls
	 * @return
	 */
	public static Object newInstance(Class<?> cls){
		Object instance = null;
		try {
			instance = cls.newInstance();
		} catch (Exception e) {
			LOGGER.error("new instance failure", e);
			e.printStackTrace();
		}
		return instance;
	}
	
	/**
	 * 调用方法
	 * @param obj
	 * @param method
	 * @param args
	 * @return
	 */
	public static Object invokeMethod(Object obj, Method method, Object... args){
		Object result = null;
		method.setAccessible(true);
		try {
			result = method.invoke(obj, args);
		} catch (Exception e) {
			 LOGGER.error("method invoke failure", e);
		}
		return result;
	}
	
	/**
	 * 设置成员变量的值
	 * @param obj
	 * @param field
	 * @param value
	 * @return
	 */
	public static boolean setField(Object obj, Field field, Object value){
		boolean result = false;
		field.setAccessible(true);
		try{
			field.set(obj, value);
			result = true;
		}catch (Exception e){
			LOGGER.error("field set failure",e);
			result = false;
		}
		return result;
	}
	
}
