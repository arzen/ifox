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
	public static final String PKG_PAY_FRAGMENT = "com.arzen.iFoxLib.dynamic.PayDynamic";
	/**
	 * 登录页面
	 */
	public static final String PKG_LOGIN_FRAGMENT = "com.arzen.iFoxLib.dynamic.LoginDynamic";
	/**
	 * 修改密码页面
	 */
	public static final String PKG_CHANGE_PASSWORD_FRAGMENT = "com.arzen.iFoxLib.dynamic.ChangePasswordDynamic";

	/**
	 * 排行榜页面
	 */
	public static final String PKG_TOP_FRAGMENT = "com.arzen.iFoxLib.dynamic.TopDynamic";

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
	 * intent 传递数据key ->登录后的clientId
	 */
	public static String INTENT_DATA_KEY_CLIENTID = "clientId";
	/**
	 * intent 传递数据key ->登录后的clientSecret
	 */
	public static String INTENT_DATA_KEY_CLIENTSECRET = "clientSecret";
	
	/**
	 * intent 传递数据key ->支付宝回调地址key
	 */
	public static String INTENT_DATA_KEY_NOTIFY_URL = "notifyUrl";
	
	/**
	 * intent 传递数据key ->gid游戏id
	 */
	public static String INTENT_DATA_KEY_GID = "gid";

	/**
	 * intent 传递数据key ->cid 渠道id
	 */
	public static String INTENT_DATA_KEY_CID = "cid";
	
	/**
	 * intent 传递数据key ->百度统计cid
	 */
	public static String INTENT_DATA_KEY_BAIDU_CID = "baidu_cid";

	/**
	 * intent 传递数据key ->登录后的token
	 */
	public static String INTENT_DATA_KEY_TOKEN = "token";
	/**
	 * intent 传递数据key ->登录后的得到的uid
	 */
	public static String INTENT_DATA_KEY_UID = "uid";
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
	 * 固定道具金额
	 */
	public static String INTENT_DATA_KEY_PROP_PRICE = "prop_price";

	/**
	 * 结果回调action
	 */
	public static final String RECEIVER_RESULT_ACTION = "android.action.result.receiver";

	/**
	 * 支付action
	 */
	public static final String RECEIVER_PAY_START_ACTION = "android.action.pay.start.receiver";

	/**
	 * 下载action
	 */
	public static final String RECEIVER_DOWNLOAD_ACTION = "android.action.download.receiver";

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
	 * change password action
	 */
	public static final String RECEIVER_ACTION_CHANGE_PASSWORD = "android.receiver.action.changepassword";

	/**
	 * 支付结果action
	 */
	public static final String ACTION_PAY_RESULT_RECEIVER = "com.action.pay.result.receiver";

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

	/**
	 * 用户速数据shaed
	 */
	public static final String SHARED_NAME_USER = "setting";
	/**
	 * key gid
	 */
	public static final String SHARED_KEY_GID = "game_gid";

	/**
	 * key gid
	 */
	public static final String SHARED_KEY_TIME = "time";
	/**
	 * 分数
	 */
	public static final String SHARED_KEY_SOCRE = "score";
	/**
	 * 支付宝回调地址
	 */
	public static final String SHARED_ALIPAY_NOTIFY_URL = "alipay_notify_url";
	/**
	 * token
	 */
	public static final String SHARED_KEY_TOKEN = "token";

	/**
	 * 支付方式 微派
	 */
	public static final int PAY_TYPE_WIIPAY = 1;
	/**
	 * 支付宝
	 */
	public static final int PAY_TYPE_ALIPAY = 2;
	/**
	 * 银联
	 */
	public static final int PAY_TYPE_UNIONPAY = 3;

	/**
	 * intent 传递数据key ->支付流水号
	 */
	public static String INTENT_DATA_KEY_PAY_TN = "tn";

	/**
	 * intent 传递数据key ->支付方式
	 */
	public static String INTENT_DATA_KEY_PAY_TYPE = "payType";
	/**
	 * 创建订单action
	 */
	public static final String ACTION_CREATEORDER_ACTIVITY = "com.action.create.order.receiver";

}
