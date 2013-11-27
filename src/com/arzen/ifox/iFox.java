package com.arzen.ifox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.arzen.ifox.api.HttpIfoxApi;
import com.arzen.ifox.api.HttpSetting;
import com.arzen.ifox.bean.Init;
import com.arzen.ifox.setting.KeyConstants;
import com.arzen.ifox.utils.CommonUtil;
import com.arzen.ifox.utils.JarUtil;
import com.arzen.ifox.utils.MsgUtil;
import com.encore.libs.http.HttpConnectManager;
import com.encore.libs.http.OnRequestListener;
import com.encore.libs.utils.NetWorkUtils;

public abstract class iFox {

	public final static String DEX_FILE = "iFoxLib.apk";
	/**
	 * 当前游戏id
	 */
	public static String GID = "";

	private static JarUtil mJarUtil;

	/**
	 * 初始化
	 * 
	 * @param Activity
	 *            游戏的的主Activity
	 * @param appKey
	 *            游戏的在平台中的app key
	 * @param appSecrect
	 *            游戏的在平台中的app secrect
	 * 
	 */
	public static void init(final Activity activity, String appKey, String appSecrect) {
		if (activity == null) {
			return;
		}
		boolean isHasNetWork = NetWorkUtils.isWifiConnected(activity.getApplicationContext());
		if (!isHasNetWork) { // 没有网络
			MsgUtil.msg(R.string.not_network, activity);
			return;
		}

		String packageName = CommonUtil.getPackageName(activity.getApplicationContext());
		// 请求初始化信息
		HttpIfoxApi.requestInitData(activity, packageName, appKey, appSecrect, new OnRequestListener() {

			@Override
			public void onResponse(final String url, final int state, final Object result, final int type) {
				// TODO Auto-generated method stub
				// 请求成功
				if(activity != null){
					activity.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (state == HttpConnectManager.STATE_SUC && result != null && result instanceof Init) {
								Init init = (Init) result;
								// 如果返回成功
								if (init.getCode() == HttpSetting.RESULT_CODE_OK) {
									// 初始化dex resource资源
									initDexResource(activity);
									// 设置当前游戏id
									GID = init.getData().getGid();
								} else {
									MsgUtil.msg(init.getMsg(), activity);
								}
							} else if (state == HttpConnectManager.STATE_TIME_OUT) { // 请求超时
								MsgUtil.msg(R.string.time_out, activity);
							} else { // 请求失败
								MsgUtil.msg(R.string.request_fail, activity);
							}
						}
					});
				}
			}
		});
		
		// 初始化dex resource资源
//		initDexResource(activity);
	}

	/**
	 * 初始化动态库资源
	 */
	private static void initDexResource(Activity activity) {
		if (mJarUtil == null)
			mJarUtil = new JarUtil(activity);
		// 初始化lib资源,导入资源,以便做到调用,lib apk 动态加载view
		mJarUtil.initIFoxLibResource(activity, iFox.DEX_FILE);
	}

	/**
	 * 初始化动态更新包资源 必须在setContentView前执行
	 * 
	 * @return
	 */
	public static JarUtil initLibApkResource(Activity activity) {
		if (activity == null) {
			return null;
		}
		if (mJarUtil == null)
			mJarUtil = new JarUtil(activity);

		// 初始化lib资源,导入资源,以便做到调用,lib apk 动态加载view
		mJarUtil.initIFoxLibResource(activity, iFox.DEX_FILE);

		return mJarUtil;
	}

	/**
	 * 打开登录界面
	 * 
	 * @param Activity
	 *            游戏的的主Activity
	 * @param bundle
	 *            需要额外传入的参数，如果没有，留空
	 * @param listener
	 *            登录结果回调的处理
	 * 
	 */

	public static void loginPage(final Activity activity, final Bundle bundle, final LoginListener listener) {

	}

	public static interface LoginListener {
		/**
		 * 登录成功的回调
		 * 
		 * @param bundle
		 *            回调参数
		 */
		public void onSuccess(Bundle bundle);

		/**
		 * 登录过程取消
		 */
		public void onCancel();

	}

	/**
	 * 打开支付界面
	 * 
	 * @param Activity
	 *            游戏的的主Activity
	 * @param bundle
	 *            需要额外传入的参数，如果没有，留空
	 * @param listener
	 *            支付结果回调的处理
	 * 
	 */

	public static void chargePage(final Activity activity, Bundle bundle, final ChargeListener listener) {
		Intent intent = new Intent(activity, PayActivity.class);
		
		if(bundle == null){
			bundle = new Bundle();
		}
		bundle.putString(KeyConstants.INTENT_DATA_KEY_GID, GID); //游戏id
		bundle.putString(KeyConstants.INTENT_DATA_KEY_CID, "11111"); //渠道id
		bundle.putString(KeyConstants.INTENT_DATA_KEY_TOKEN, "token"); //token
		intent.putExtras(bundle);
		activity.startActivity(intent);
	}

	public static interface ChargeListener {
		/**
		 * 支付成功的回调
		 * 
		 * @param bundle
		 *            回调参数
		 */
		public void onSuccess(Bundle bundle);

		/**
		 * 支付失败
		 */
		public void onFail();

		/**
		 * 支付完成
		 */
		public void onFinish();

		/**
		 * 支付过程取消
		 */
		public void onCancel();

	}

	/**
	 * 获取jar控制类
	 * 
	 * @return
	 */
	public static JarUtil getJarUtil() {
		return mJarUtil;
	}
}
