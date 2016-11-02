package com.zebra.core;

/**
 * Created by lzy on 2016/11/2 0002.
 */
public class ConfigConstant {
	private static final String SMART_PREFIX = "zebra.core";

	/**
	 * 默认配置文件名称
	 */
	public static final String CONFIG_FILE = "app.properties";

	/**
	 * JDBC
	 */
	public static final String JDBC_DRIVER = SMART_PREFIX + ".jdbc.driver";
	public static final String JDBC_USERNAME = SMART_PREFIX + ".jdbc.username";
	public static final String JDBC_PASSWORD = SMART_PREFIX + ".jdbc.password";

	public static final String APP_ABASE_PACKAGE = SMART_PREFIX + ".app.base_package";
	public static final String APP_PATH_JSP = SMART_PREFIX + ".app.path_jsp";
	public static final String APP_PATH_ASSET= SMART_PREFIX + ".app.path_asset";
	public static final String APP_UPLOAD_LIMIT = SMART_PREFIX + ".app.upload_limit";
}
