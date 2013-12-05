package com.arzen.ifox;

import org.w3c.dom.ls.LSException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.arzen.ifox.api.HttpIfoxApi;
import com.arzen.ifox.api.HttpSetting;
import com.arzen.ifox.bean.DynamicUpdate;
import com.arzen.ifox.bean.DynamicUpdate.DynamicData;
import com.arzen.ifox.bean.Init;
import com.arzen.ifox.setting.KeyConstants;
import com.arzen.ifox.setting.UserSetting;
import com.arzen.ifox.utils.CommonUtil;
import com.arzen.ifox.utils.DynamicLibUtils;
import com.arzen.ifox.utils.DynamicLibManager;
import com.arzen.ifox.utils.MsgUtil;
import com.encore.libs.http.HttpConnectManager;
import com.encore.libs.http.OnRequestListener;
import com.encore.libs.utils.NetWorkUtils;

public abstract class iFox {

	private static final String TAG = "IFox";
	
	public final static String DEX_FILE = "iFoxLib.apk";
	/**
	 * 当前游戏id
	 */
//	public static String GID = "";
	/**
	 * 动态库操作类
	 */
	private static DynamicLibManager mDynamicLibManager;
	//是否初始化成功
	private static boolean isInitSuccess = false;
	

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
		boolean isHasNetWork = NetWorkUtils.isNetworkAvailable(activity.getApplicationContext());
		if (!isHasNetWork) { // 没有网络
			MsgUtil.msg(R.string.not_network, activity);
			return;
		}
		//初始化应用信息,此步不同下面工作就无法进行
		initAppInfo(activity, appKey, appSecrect);
	}
	
	/**
	 * 初始化应用信息
	 */
	private static void initAppInfo(final Activity activity, String appKey, String appSecrect)
	{
		String packageName = CommonUtil.getPackageName(activity.getApplicationContext());
		// 请求初始化信息
		HttpIfoxApi.requestInitData(activity, packageName, appKey, appSecrect, new OnRequestListener() {

			@Override
			public void onResponse(final String url, final int state, final Object result, final int type) {
				// TODO Auto-generated method stub
				// 请求成功
				if (activity != null) {
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
									String gid = init.getData().getGid();
									//保存当前gid
									UserSetting.saveData(activity, gid);

									if (mDynamicLibManager != null) {
										// 检查动态库是否有更新
										checkUpdate(activity, gid, "cid", mDynamicLibManager.getmVertionCode());
									}
									
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
	}
	
	/**
	 * 检查动态库是否有更新
	 * 如果有更新,并在wifi网络下,自动下载动态库
	 * @param gid 游戏id
	 * @param cid 渠道id
	 * @param currentVertion 当前包下的版本号
	 */
	public static void checkUpdate(final Activity activity,String gid,String cid,String currentVersion)
	{
		Log.d(TAG, "start check update!");
		HttpIfoxApi.requestDynamicUpdateData(activity, gid, cid, currentVersion, new OnRequestListener() {
			
			@Override
			public void onResponse(final String url, final int state, final Object result, final int type) {
				// TODO Auto-generated method stub
				if (state == HttpConnectManager.STATE_SUC && result != null && result instanceof DynamicUpdate) {
					DynamicUpdate dynamicUpdate = (DynamicUpdate) result;
					// 如果返回成功
					if (dynamicUpdate.getCode() == HttpSetting.RESULT_CODE_OK) {
						DynamicData data = dynamicUpdate.getData();
						String latest =  data.getLatest();
						if(latest.equals("false") && !data.getUrl().equals("")){ //false 有新版本  true 没新版本
							DynamicLibUtils.downloadNewDynamicLib(activity.getApplicationContext(),data.getUrl()); //下载动态库
						}
					} else {
						MsgUtil.msg(dynamicUpdate.getMsg(), activity);
					}
				} else if (state == HttpConnectManager.STATE_TIME_OUT) { // 请求超时
					Log.d(TAG, "check update time out");
				} else { // 请求失败
					Log.d(TAG, "check update fail");
				}
			}
		});
	}

	/**
	 * 初始化动态库资源
	 */
	private static void initDexResource(Activity activity) {
		if (mDynamicLibManager == null)
			mDynamicLibManager = new DynamicLibManager(activity);
		// 初始化lib资源,导入资源,以便做到调用,lib apk 动态加载view
		mDynamicLibManager.initIFoxLibResource(activity, iFox.DEX_FILE);
	}

	/**
	 * 初始化动态更新包资源 必须在setContentView前执行
	 * 
	 * @return
	 */
	public static DynamicLibManager initLibApkResource(Activity activity) {
		if (activity == null) {
			return null;
		}
		if (mDynamicLibManager == null)
			mDynamicLibManager = new DynamicLibManager(activity);

		// 初始化lib资源,导入资源,以便做到调用,lib apk 动态加载view
		mDynamicLibManager.initIFoxLibResource(activity, iFox.DEX_FILE);

		return mDynamicLibManager;
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

	public static void loginPage(final Activity activity, Bundle bundle, final LoginListener listener) {
		if (activity == null || listener == null) {
			try {
				throw new Exception("context or listener is null!");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		
		String gid = UserSetting.getGID(activity);
		if(gid.equals("")){
			MsgUtil.msg("未初始化!", activity);
			return;
		}
		//设置登录回调
		BaseActivity.setLoginListener(listener);
		//获得当前token
		String token = UserSetting.getToken(activity.getApplicationContext());
		
		Intent intent = new Intent(KeyConstants.ACTION_COMMON_ACTIVITY);
		if (bundle == null) {
			bundle = new Bundle();
		}
		bundle.putString(KeyConstants.KEY_PACKAGE_NAME, KeyConstants.PKG_LOGIN_FRAGMENT);
		bundle.putString(KeyConstants.INTENT_DATA_KEY_GID, gid); // 游戏id
		bundle.putString(KeyConstants.INTENT_DATA_KEY_CID, "11111"); // 渠道id
		bundle.putString(KeyConstants.INTENT_DATA_KEY_TOKEN, token);
		intent.putExtras(bundle);
		activity.startActivity(intent);
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
	 * 修改密码
	 * @param activity 上下文
	 * @param bundle 必须含有 key = 'token' 的值
	 * @param listener 修改密码回调
	 */
	public static void changePassword(final Activity activity, Bundle bundle, final ChangePasswordListener listener) {
		if (activity == null || listener == null) {
			try {
				throw new Exception("context or listener is null!");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		String token = UserSetting.getToken(activity.getApplicationContext());
		String gid = UserSetting.getGID(activity.getApplicationContext());
		if(gid.equals("")){
			MsgUtil.msg("未初始化!", activity);
			return;
		}else if(token.equals("")){
			MsgUtil.msg("未登录!", activity);
			return;
		}
		
		
		BaseActivity.setChangePasswordListener(listener);
		
		Intent intent = new Intent(KeyConstants.ACTION_COMMON_ACTIVITY);
		if (bundle == null) {
			bundle = new Bundle();
		}
		bundle.putString(KeyConstants.KEY_PACKAGE_NAME, KeyConstants.PKG_CHANGE_PASSWORD_FRAGMENT);
		bundle.putString(KeyConstants.INTENT_DATA_KEY_GID, gid); // 游戏id
		bundle.putString(KeyConstants.INTENT_DATA_KEY_CID, "11111"); // 渠道id
		bundle.putString(KeyConstants.INTENT_DATA_KEY_TOKEN, token);
		intent.putExtras(bundle);
		activity.startActivity(intent);
	}

	public static interface ChangePasswordListener {
		/**
		 * 修改成功的回调
		 * 
		 * @param bundle 回调参数
		 */
		public void onSuccess();

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
		if (activity == null || listener == null) {
			try {
				throw new Exception("上下文,与接口不能为空");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		
		String gid = UserSetting.getGID(activity);
		if(gid.equals("")){
			MsgUtil.msg("未初始化!", activity);
			return;
		}
		// 获得当前token
		String token = UserSetting.getToken(activity.getApplicationContext());
		//设置支付回调接口
		BaseActivity.setPayCallBackListener(listener);
		
		Intent intent = new Intent(KeyConstants.ACTION_COMMON_ACTIVITY);
		if (bundle == null) {
			bundle = new Bundle();
		}
		bundle.putString(KeyConstants.KEY_PACKAGE_NAME, KeyConstants.PKG_PAY_FRAGMENT);
		bundle.putString(KeyConstants.INTENT_DATA_KEY_GID, gid); // 游戏id
		bundle.putString(KeyConstants.INTENT_DATA_KEY_CID, "11111"); // 渠道id
		bundle.putString(KeyConstants.INTENT_DATA_KEY_TOKEN, token);
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
		public void onFail(String msg);

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
	public static DynamicLibManager getDynamicLibManager(Activity activity) {
		if(mDynamicLibManager == null){
			mDynamicLibManager = new DynamicLibManager(activity);
		}
		return mDynamicLibManager;
	}
	
	public static void setDynamicLibManager(DynamicLibManager jarUtil)
	{
		mDynamicLibManager = jarUtil;
	}
}
