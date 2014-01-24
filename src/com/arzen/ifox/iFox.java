package com.arzen.ifox;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.arzen.ifox.api.HttpIfoxApi;
import com.arzen.ifox.api.HttpSetting;
import com.arzen.ifox.bean.CommitScore;
import com.arzen.ifox.bean.DynamicUpdate;
import com.arzen.ifox.bean.Init.InitData;
import com.arzen.ifox.bean.Share;
import com.arzen.ifox.bean.DynamicUpdate.DynamicData;
import com.arzen.ifox.bean.Init;
import com.arzen.ifox.setting.KeyConstants;
import com.arzen.ifox.setting.UserSetting;
import com.arzen.ifox.utils.CommonUtil;
import com.arzen.ifox.utils.DynamicLibManager;
import com.arzen.ifox.utils.DynamicLibUtils;
import com.arzen.ifox.utils.MsgUtil;
import com.baidu.mobstat.StatService;
import com.encore.libs.http.HttpConnectManager;
import com.encore.libs.http.OnRequestListener;
import com.encore.libs.utils.NetWorkUtils;

public abstract class iFox {

	private static final String TAG = "IFox";

	/**
	 * 当前游戏id
	 */
	// public static String GID = "";

	private static String mAppKey = "";

	private static String mAppSecrect = "";

	/**
	 * 检查时间,检测是否需要请求
	 */
	private static long mCheckTime = 24 * 60 * 60 * 1000;

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
	public static void init(final Activity activity, String appKey, String appSecrect, InitCallBack listener) {
		if (activity == null || appKey == null || appSecrect == null || appKey.equals("") || appSecrect.equals("") || listener == null) {
			throw new RuntimeException("appkey and appSecrect and InitCallBack 不能未空!");
		}
		boolean isHasNetWork = NetWorkUtils.isNetworkAvailable(activity.getApplicationContext());
		if (!isHasNetWork) { // 没有网络
			listener.onFail("没有网络");
			return;
		}

		/**
		 * 获取sdcard下的配置文件,是否打开debug模式,默认关
		 */
		boolean debug = getDebugModel(activity.getApplicationContext());
		/**
		 * 获取配置文件,切换测试服务器和正式服务器
		 */
		String serverUrl = getServerUrl(activity.getApplicationContext());
		if (serverUrl != null){
			HttpSetting.setServerUrl(activity, serverUrl);
		}

		com.encore.libs.utils.Log.DEBUG = debug;
		
		//设置统计
		StatService.setAppChannel(activity, getBaiduCid(activity), true);
		StatService.setDebugOn(debug);

		mAppKey = appKey;
		mAppSecrect = appSecrect;
		// 初始化应用信息,此步不同下面工作就无法进行
		initAppInfo(activity, appKey, appSecrect, listener);
	}
	
	/**
	 * 生成百度统计 渠道cid   包名 + cid  
	 * @param activity
	 * @return
	 */
	public static String getBaiduCid(Activity activity)
	{
		PackageInfo info;
		String channelId = getChannelId(activity.getApplicationContext());
		String cid = channelId;
		try {
			info = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
			cid =info.packageName + "-" + channelId;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return cid;
	}

	public static interface InitCallBack {
		/**
		 * 初始化成功
		 */
		public void onSuccess();

		/**
		 * 初始化失败,必须重新调用初始化
		 */
		public void onFail(String msg);
	}

	/**
	 * 初始化应用信息
	 */
	private static void initAppInfo(final Activity activity, String appKey, String appSecrect, final InitCallBack cb) {
		long initTime = UserSetting.getInitTime(activity); // 得到上次检查的时间
		String gid = UserSetting.getGID(activity);
		if (System.currentTimeMillis() - initTime > mCheckTime || gid == null || gid.equals("")) { // 如果大于一天
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
										disposeInit(activity, init);
										cb.onSuccess();
									} else {
										cb.onFail(init.getMsg());
										MsgUtil.msg(init.getMsg(), activity);
									}
								} else if (state == HttpConnectManager.STATE_TIME_OUT) { // 请求超时
									cb.onFail(activity.getString(R.string.time_out));
								} else { // 请求失败
									cb.onFail(activity.getString(R.string.request_fail));
								}
							}
						});
					}
				}
			});
		} else {
			// 初始化dex resource资源
			DynamicLibManager.initDexResource(activity);
			cb.onSuccess();
		}
	}
	/**
	 * 处理初始化
	 * @param activity
	 * @param init
	 */
	private static void disposeInit(Activity activity, Init init)
	{
		// 初始化dex resource资源
		DynamicLibManager.initDexResource(activity);
		// 设置当前游戏id
		String gid = init.getData().getGid();
		// 保存当前gid
		UserSetting.saveData(activity, gid, System.currentTimeMillis(), init.getData().getAlipay_notify_url());

		if (DynamicLibManager.getDynamicLibManager(activity) != null) {
			// 检查动态库是否有更新
			checkUpdate(activity, gid, getChannelId(activity), DynamicLibManager.getDynamicLibManager(activity).getmVertionCode());
		}
		// 获得当前token
		String token = UserSetting.getToken(activity.getApplicationContext());
		// 获取分享模版
		madeShareMsg(activity, token, gid, false);
	}

	/**
	 * 检查动态库是否有更新 如果有更新,并在wifi网络下,自动下载动态库
	 * 
	 * @param gid
	 *            游戏id
	 * @param cid
	 *            渠道id
	 * @param currentVertion
	 *            当前包下的版本号
	 */
	private static void checkUpdate(final Activity activity, String gid, String cid, String currentVersion) {
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
						String latest = data.getLatest();
						if (latest.equals("false") && !data.getUrl().equals("")) { // false 有新版本 true 没新版本
							DynamicLibUtils.downloadNewDynamicLib(activity.getApplicationContext(), data.getUrl()); // 下载动态库
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
		if (gid.equals("")) {
			MsgUtil.msg("未初始化!", activity);
			return;
		}
		// 设置登录回调
		BaseActivity.setLoginListener(listener);
		// 获得当前token
		String token = UserSetting.getToken(activity.getApplicationContext());

		Intent intent = new Intent(KeyConstants.ACTION_COMMON_ACTIVITY);
		if (bundle == null) {
			bundle = new Bundle();
		}
		bundle.putString(KeyConstants.KEY_PACKAGE_NAME, KeyConstants.PKG_LOGIN_FRAGMENT);
		bundle.putString(KeyConstants.INTENT_DATA_KEY_GID, gid); // 游戏id
		bundle.putString(KeyConstants.INTENT_DATA_KEY_CID, getChannelId(activity.getApplicationContext())); // 渠道id
		bundle.putString(KeyConstants.INTENT_DATA_KEY_BAIDU_CID, getBaiduCid(activity)); // 百度统计id
		bundle.putString(KeyConstants.INTENT_DATA_KEY_TOKEN, token);
		bundle.putString(KeyConstants.INTENT_DATA_KEY_CLIENTID, mAppKey);
		bundle.putString(KeyConstants.INTENT_DATA_KEY_CLIENTSECRET, mAppSecrect);
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

	// /**
	// * 修改密码
	// *
	// * @param activity
	// * 上下文
	// * @param bundle
	// * 必须含有 key = 'token' 的值
	// * @param listener
	// * 修改密码回调
	// */
	// public static void changePassword(final Activity activity, String appKey,
	// String appSecrect, Bundle bundle, final ChangePasswordListener listener)
	// {
	// if (activity == null || listener == null) {
	// try {
	// throw new Exception("context or listener is null!");
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return;
	// }
	//
	// String token = UserSetting.getToken(activity.getApplicationContext());
	// String gid = UserSetting.getGID(activity.getApplicationContext());
	// if (gid.equals("")) {
	// MsgUtil.msg("未初始化!", activity);
	// return;
	// } else if (token.equals("")) {
	// MsgUtil.msg("未登录!", activity);
	// return;
	// }
	//
	// BaseActivity.setChangePasswordListener(listener);
	//
	// Intent intent = new Intent(KeyConstants.ACTION_COMMON_ACTIVITY);
	// if (bundle == null) {
	// bundle = new Bundle();
	// }
	// bundle.putString(KeyConstants.KEY_PACKAGE_NAME,
	// KeyConstants.PKG_CHANGE_PASSWORD_FRAGMENT);
	// bundle.putString(KeyConstants.INTENT_DATA_KEY_GID, gid); // 游戏id
	// bundle.putString(KeyConstants.INTENT_DATA_KEY_CID,
	// getChannelId(activity.getApplicationContext())); // 渠道id
	// bundle.putString(KeyConstants.INTENT_DATA_KEY_TOKEN, token);
	// bundle.putString(KeyConstants.INTENT_DATA_KEY_CLIENTID, appKey);
	// bundle.putString(KeyConstants.INTENT_DATA_KEY_CLIENTSECRET, appSecrect);
	// intent.putExtras(bundle);
	// activity.startActivity(intent);
	// }

	public static interface ChangePasswordListener {
		/**
		 * 修改成功的回调
		 * 
		 * @param bundle
		 *            回调参数
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

		String token = UserSetting.getToken(activity.getApplicationContext());
		String gid = UserSetting.getGID(activity.getApplicationContext());
		if (gid.equals("")) {
			MsgUtil.msg("未初始化!", activity);
			return;
		} else if (token.equals("")) {
			MsgUtil.msg("未登录!", activity);
			return;
		}
		// 设置支付回调接口
		BaseActivity.setPayCallBackListener(listener);

		Intent intent = new Intent(KeyConstants.ACTION_COMMON_ACTIVITY);
		if (bundle == null) {
			bundle = new Bundle();
		}
		bundle.putString(KeyConstants.KEY_PACKAGE_NAME, KeyConstants.PKG_PAY_FRAGMENT);
		bundle.putString(KeyConstants.INTENT_DATA_KEY_GID, gid); // 游戏id
		bundle.putString(KeyConstants.INTENT_DATA_KEY_CID, getChannelId(activity.getApplicationContext())); // 渠道id
		bundle.putString(KeyConstants.INTENT_DATA_KEY_BAIDU_CID, getBaiduCid(activity)); // 百度统计id
		bundle.putString(KeyConstants.INTENT_DATA_KEY_TOKEN, token);
		bundle.putString(KeyConstants.INTENT_DATA_KEY_CLIENTID, mAppKey);
		bundle.putString(KeyConstants.INTENT_DATA_KEY_CLIENTSECRET, mAppSecrect);
		bundle.putString(KeyConstants.INTENT_DATA_KEY_NOTIFY_URL, UserSetting.getNotifyUrl(activity.getApplicationContext()));
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
	 * 打开排行榜页面
	 * 
	 * @param activity
	 */
	public static void leaderboardPage(final Activity activity) {
		if (activity == null) {
			return;
		}

		String gid = UserSetting.getGID(activity);
		if (gid.equals("")) {
			MsgUtil.msg("未初始化!", activity);
			return;
		}
		// 获得当前token
		String token = UserSetting.getToken(activity.getApplicationContext());
		if (token.equals("")) {
			MsgUtil.msg("未登录", activity);
			return;
		}

		Intent intent = new Intent(KeyConstants.ACTION_COMMON_ACTIVITY);
		Bundle bundle = new Bundle();
		bundle.putString(KeyConstants.KEY_PACKAGE_NAME, KeyConstants.PKG_TOP_FRAGMENT);
		bundle.putString(KeyConstants.INTENT_DATA_KEY_GID, gid); // 游戏id
		bundle.putString(KeyConstants.INTENT_DATA_KEY_CID, getChannelId(activity.getApplicationContext())); // 渠道id
		bundle.putString(KeyConstants.INTENT_DATA_KEY_BAIDU_CID, getBaiduCid(activity)); // 百度统计id
		bundle.putString(KeyConstants.INTENT_DATA_KEY_TOKEN, token);
		bundle.putString(KeyConstants.INTENT_DATA_KEY_CLIENTID, mAppKey);
		bundle.putString(KeyConstants.INTENT_DATA_KEY_CLIENTSECRET, mAppSecrect);
		intent.putExtras(bundle);
		activity.startActivity(intent);
	}

	/**
	 * 提交排行榜分数
	 * 
	 * @param activity
	 * @param score
	 *            提交的分数
	 * @param lid
	 *            需要提交的排行榜id
	 * @param cb
	 *            回调
	 */
	public static void submitScore(final Activity activity, final long score, final int lid, final OnCommitScoreCallBack cb) {
		if (activity == null) {
			return;
		}
		String gid = UserSetting.getGID(activity);
		if (gid.equals("")) {
			MsgUtil.msg("未初始化!", activity);
			return;
		}
		// 获得当前token
		String token = UserSetting.getToken(activity.getApplicationContext());
		if (token.equals("")) {
			MsgUtil.msg("未登录", activity);
			return;
		}

		long s = UserSetting.getScore(activity.getApplicationContext());
		if (score < s) {
			return;
		}

		HttpIfoxApi.commitScore(activity, gid, lid, score, token, new OnRequestListener() {

			@Override
			public void onResponse(final String url, final int state, final Object result, final int type) {
				// TODO Auto-generated method stub
				if (activity == null)
					return;
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (state == HttpConnectManager.STATE_SUC && result != null && result instanceof CommitScore) {
							UserSetting.saveScore(activity.getApplicationContext(), score);
							CommitScore commitScore = (CommitScore) result;
							// 如果返回成功
							if (commitScore.getCode() == HttpSetting.RESULT_CODE_OK) {
								if (cb != null) {
									cb.onSuccess();
								}
							} else {
								if (cb != null) {
									cb.onFail(commitScore.getMsg());
								}
							}
						} else if (state == HttpConnectManager.STATE_TIME_OUT) { // 请求超时
							Log.d(TAG, "check update time out");
							if (cb != null) {
								cb.onFail("请求超时!");
							}
						} else { // 请求失败
							if (cb != null) {
								cb.onFail("提交失败,检查网络!");
							}
						}
					}
				});
			}
		});
	}

	/**
	 * 分享
	 * 
	 * @param activity
	 */
	public static void share(Activity activity) {

		String gid = UserSetting.getGID(activity);
		if (gid.equals("")) {
			MsgUtil.msg("未初始化!", activity);
			return;
		}
		// 获得当前token
		String token = UserSetting.getToken(activity.getApplicationContext());
		if (token.equals("")) {
			MsgUtil.msg("未登录", activity);
			return;
		}

		if (mShareMsg != null && !mShareMsg.equals("")) {
			CommonUtil.shareText(activity, "分享", mShareMsg);
		} else {
			madeShareMsg(activity, token, gid, true);
		}

	}

	private static String mShareMsg = null;

	/**
	 * 获取分享模版
	 * 
	 * @param activity
	 * @param token
	 * @param gid
	 * @param isStartShare
	 */
	private static void madeShareMsg(final Activity activity, final String token, final String gid, final boolean isStartShare) {
		if (token != null && gid != null && !token.equals("") && !gid.equals("")) {
			HttpIfoxApi.getShareMsg(activity, gid, token, new OnRequestListener() {

				@Override
				public void onResponse(final String url, final int state, final Object result, final int type) {
					// TODO Auto-generated method stub
					if (activity == null)
						return;
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (state == HttpConnectManager.STATE_SUC && result != null && result instanceof Share) {
								try {
									Share share = (Share) result;
									mShareMsg = share.getData().getMsg();
									if (isStartShare) {
										CommonUtil.shareText(activity, "分享", mShareMsg);
									}
								} catch (Exception e) {
									if (isStartShare) {
										MsgUtil.msg("分享失败,请重试!", activity);
									}
								}
							} else if (state == HttpConnectManager.STATE_TIME_OUT) { // 请求超时
								if (isStartShare) {
									MsgUtil.msg("分享失败,请重试!", activity);
								}
							} else { // 请求失败
								if (isStartShare) {
									MsgUtil.msg("分享失败,请重试!", activity);
								}
							}
						}
					});
				}
			});
		}
	}

	public interface OnCommitScoreCallBack {
		public void onSuccess();

		public void onFail(String msg);
	}

	// 配置文件
	private static HashMap<String, String> mConfigs = new HashMap<String, String>();
	// 测试配置文件
	private static HashMap<String, String> mTestConfigs = new HashMap<String, String>();

	/**
	 * 渠道号
	 * 
	 * @return
	 */
	private static String getChannelId(Context context) {
		return getConfig(context, "channel_id");
	}

	private static String getConfig(Context context, String key) {
		// return res;
		if (mConfigs.size() == 0) {
			initConfig(context);
		}
		return mConfigs.get(key);
	}

	private static void initConfig(Context context) {
		String configs = readFile(context, "config.txt", true);
		if (!configs.equals("")) {
			// File skynet_config.txt exists in assets directory
			try {
				JSONObject jo = new JSONObject(configs);
				Iterator<?> keys = jo.keys();
				while (keys.hasNext()) {
					String key = keys.next().toString();
					mConfigs.put(key, jo.getString(key));
				}
			} catch (JSONException e) {
			}
		}
	}

	private static String readFile(Context context, String fileName, boolean isAssetFile) {
		if (TextUtils.isEmpty(fileName)) {
			return "";
		}
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try {
			if (isAssetFile) {
				is = context.getAssets().open(fileName);
			} else {
				is = new FileInputStream(fileName);
			}
			byte[] buffer = new byte[1024];
			int readBytes = is.read(buffer);
			baos = new ByteArrayOutputStream(1024);
			while (0 < readBytes) {
				baos.write(buffer, 0, readBytes);
				readBytes = is.read(buffer);
			}
			String s = baos.toString();

			return s;
		} catch (IOException e) {
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			if (null != baos) {
				try {
					baos.close();
				} catch (IOException e) {
				}
			}
		}
		return "";
	}

	private static void initTestConfig(Context context) {
		if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			String configs = readFile(context, Environment.getExternalStorageDirectory().getAbsolutePath() + "/config.txt", false);
			if (!configs.equals("")) {
				// File skynet_config.txt exists in assets directory
				try {
					JSONObject jo = new JSONObject(configs);
					Iterator<?> keys = jo.keys();
					while (keys.hasNext()) {
						String key = keys.next().toString();
						mTestConfigs.put(key, jo.getString(key));
					}
				} catch (JSONException e) {
				}
			}
		}
	}

	/**
	 * 模式
	 * 
	 * @return
	 */
	private static boolean getDebugModel(Context context) {
		String debug = getTestConfig(context, "debug");
		if (debug == null) {
			return false;
		}
		return Boolean.parseBoolean(debug);
	}

	public static String getServerUrl(Context context) {
		String serverUrl = getTestConfig(context, "serverUrl");
		return serverUrl;
	}

	private static String getTestConfig(Context context, String key) {
		// return res;
		if (mTestConfigs.size() == 0) {
			initTestConfig(context);
		}
		if (mTestConfigs.size() == 0) {
			return null;
		}
		return mTestConfigs.get(key);
	}

}
