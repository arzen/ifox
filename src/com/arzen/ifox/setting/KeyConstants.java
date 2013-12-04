package com.arzen.ifox.setting;

/**
 * keyContants 公用全局变量类
 * 
 * @author Encore.liang
 * 
 */
public class KeyConstants {
	/**
	 * 支付页面
	 */
	public static final String PKG_PAY_FRAGMENT = "com.arzen.iFoxLib.fragment.PayFragment";
	/**
	 * 登录页面
	 */
	public static final String PKG_LOGIN_FRAGMENT = "com.arzen.iFoxLib.fragment.LoginFragment";
	
	/**
	 * 公用activty action
	 */
	public static final String ACTION_COMMON_ACTIVITY = "com.action.common.activty";
	
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
	 * 结果回调action
	 */
	public static final String RECEIVER_RESULT_ACTION = "android.action.result.receiver";
	
	/**
	 * 结果回调action key
	 */
	public static final String RECEIVER_KEY_DISPOSE_ACTION = "disposeAction";
	
	/**
	 * pay action
	 */
	public static final String RECEIVER_ACTION_PAY = "android.receiver.action.pay";
	/**
	 * login action
	 */
	public static final String RECEIVER_ACTION_LOGIN = "android.receiver.action.login";
	
	/**
	 * 结果key
	 */
	public static final String INTENT_KEY_RESULT = "result";
	/**
	 * 提示key
	 */
	public static final String INTENT_KEY_MSG = "showMsg";

	/**
	 * 结果成功key
	 */
	public static final String INTENT_KEY_SUCCESS = "success";

	/**
	 * 结果失败。
	 */
	public static final String INTENT_KEY_FAIL = "fail";
	/**
	 * 结果取消。key
	 */
	public static final String INTENT_KEY_CANCEL = "cancel";
}
