package com.zebra.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystemNotFoundException;
import java.util.Properties;

/**
 * 读取配置文件工具类
 * Created by 58 on 2016/10/26.
 */
public class PropsUtil {

	/**
	 * logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(PropsUtil.class);

	public static Properties loadProps(String fileName){
		Properties props = null;
		InputStream is = null;
		try{
			is = ClassUtil.getClassLoader().getResourceAsStream(fileName);
			if( is == null){
				throw new FileSystemNotFoundException("file :[" + fileName + "] not found !");
			}
			props = new Properties();
			props.load(is);
		}catch (IOException e){

		}finally {
			if( is != null){
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return props;

	}

	public static String getString(Properties props, String key){
		return getString(props, key, "");
	}

	public static String getString(Properties props, String key, String defaultValue){
		String value = defaultValue;
		if( props.contains(key)){
			value = props.getProperty(key);
		}
		return value;
	}

	public static int getInt(Properties props, String key){
		return getInt(props, key, 0);
	}

	public static int getInt(Properties props, String key, int defaultValue){
		int value = defaultValue;
		if(props.contains(key)){
			value = Integer.valueOf(props.getProperty(key));
		}
		return value;
	}

	public static boolean getBoolean(Properties props, String key){
		return getBoolean(props,key,false);
	}

	public static boolean getBoolean(Properties props, String key, boolean defaultValue){
		boolean value = defaultValue;
		if(props.contains(key)){
			value = Boolean.valueOf(props.getProperty(key));
		}
		return value;
	}





}
