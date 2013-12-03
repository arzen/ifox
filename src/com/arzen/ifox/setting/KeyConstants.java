package com.arzen.ifox.setting;

/**
 * keyContants 公用全局变量类
 * 
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
	 * 登录页面
	 */
	public static final String PKG_LOGIN_FRAGMENT = "com.arzen.iFoxLib.fragment.LoginFragment";
	
	/**
	 * 动态加载fragment key
	 */
	public static final String KEY_PACKAGE_NAME = "keyPackage";
	
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
	/**
	 * intent 传递数据key -> 支付游戏需要传递记录的数据;如：区服信息，角色信息等 "sn=133242434&role=张三"
	 */
	public static String INTENT_DATA_KEY_EXTRA = "extra";
	
	/**
	 * 支付key 传递过来的道具编号
	 */
	public static String INTENT_DATA_KEY_PID = "pid";
	/**
	 * 支付金额
	 */
	public static String INTENT_DATA_KEY_AMOUNT = "amount";

	/**
	 * 订单号
	 */
	public static String INTENT_DATA_KEY_ORDERID = "orderid";
	
	/**
	 * 支付回调action
	 */
	public static final String PAY_RESULT_RECEIVER_ACTION = "android.action.pay.result.receiver";
	/**
	 * 支付结果key
	 */
	public static final String INTENT_KEY_PAY_RESULT = "result";
	/**
	 * 支付提示key
	 */
	public static final String INTENT_KEY_PAY_MSG = "showMsg";

	/**
	 * 支付成功
	 */
	public static final String INTENT_KEY_PAY_SUCCESS = "success";

	/**
	 * 支付失败。
	 */
	public static final String INTENT_KEY_PAY_FAIL = "fail";
	/**
	 * 支付取消。
	 */
	public static final String INTENT_KEY_PAY_CANCEL = "cancel";
}
