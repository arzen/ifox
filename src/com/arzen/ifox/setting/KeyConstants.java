package com.arzen.ifox.setting;
/**
 * keyContants 公用全局变量类
 * @author Encore.liang
 *
 */
public class KeyConstants {
	/**
	 * home fragment pkg
	 */
	public static final String PKG_HOME_FRAGMENT = "com.arzen.iFoxLib.fragment.HomeFragment";
	/**
	 * 支付页面
	 */
	public static final String PKG_PAY_FRAGMENT = "com.arzen.iFoxLib.fragment.PayFragment";
	/**
	 * fragment 容器id
	 */
	public static int KEY_CONTAINER_ID = android.R.id.primary;
	
	/**
	 * lib apk httpSetting 类
	 */
	public static String CLASSPATH_HTTP_SETTING = "com.arzen.iFoxLib.api.HttpSetting";
	

	/**
	 * intent 传递数据key ->gid游戏id
	 */
	public static String INTENT_DATA_KEY_GID = "gid";
	
	/**
	 * intent 传递数据key ->cid 渠道id
	 */
	public static String INTENT_DATA_KEY_CID = "cid";
	
	/**
	 * intent 传递数据key ->登录后的token
	 */
	public static String INTENT_DATA_KEY_TOKEN = "token";
}
