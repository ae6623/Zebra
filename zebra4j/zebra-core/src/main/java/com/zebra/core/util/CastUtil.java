package com.zebra.core.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 转换工具类 boolean string number
 * Created by 58 on 2016/10/26.
 */
public final class CastUtil {

	/**
	 * 转换为String
	 * @param obj
	 * @return
	 */
	public static String castString(Object obj){
		return CastUtil.castString(obj,"");
	}

	/**
	 * 转换为String （提供默认值）
	 * @param obj
	 * @param defaultValue
	 * @return
	 */
	public static String castString(Object obj,String defaultValue){
		return obj !=null ? String.valueOf(obj.toString()) : defaultValue;
	}

	/**
	 * 转换成int
	 * @param obj
	 * @return
	 */
	public static int castInt(Object obj){
		return CastUtil.castInt(obj,0);
	}

	/**
	 * 转换成int (提供默认值)
	 * @param obj
	 * @param defaultValue
	 * @return
	 */
	private static int castInt(Object obj, int defaultValue) {
		int value = defaultValue;
		if(obj != null) {
			String strValue = castString(obj);
			if(StringUtils.isNotBlank(strValue)){
				try{
					value = Integer.parseInt(strValue);
				}catch(NumberFormatException e){
					value = defaultValue;
				}
			}
		}
		return value;
	}

	/**
	 * 转换成Long
	 * @param obj
	 * @return
	 */
	public static long castLong(Object obj){
		return CastUtil.castLong(obj,0);
	}

	/**
	 * 转换成Long (提供默认值)
	 * @param obj
	 * @param defaultValue
	 * @return
	 */
	private static long castLong(Object obj, long defaultValue) {
		long value = defaultValue;
		if( obj != null) {
			String strValue = castString(obj);
			if(StringUtils.isNotBlank(strValue)){
				try{
					value = Long.parseLong(strValue);
				}catch (NumberFormatException e){
					value = defaultValue;
				}
			}
		}
		return value;
	}

	/**
	 * 转换为Double
	 * @param obj
	 * @return
	 */
	public static double castDouble(Object obj){
		return CastUtil.castDouble(obj,0);
	}

	/**
	 * 转换为Double (提供默认值)
	 * @param obj
	 * @param defaultValue
	 * @return
	 */
	private static double castDouble(Object obj, double defaultValue) {
		double value = defaultValue;
		if( obj != null){
			String strValue = castString(obj);
			if(StringUtils.isNotBlank(strValue)) {
				try {
					value = Double.parseDouble(strValue);
				}catch (NumberFormatException e){
					value = defaultValue;
				}
			}
		}
		return value;
	}


}
