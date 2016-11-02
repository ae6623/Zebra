package com.zebra.core.helper;

import com.zebra.core.ConfigConstant;
import com.zebra.core.util.PropsUtil;

import java.util.Properties;

/**
 * 属性读取工具类，用于工程加载时读取约定的配置文件
 * Created by lzy@js-dev.cn on 2016/10/26.
 */
public class ConfigHelper {
	/**
	 * 配置文件 smart.properties
	 */
	private static final Properties CONFIG_PROS = PropsUtil.loadProps(ConfigConstant.CONFIG_FILE);
}
