package com.arzen.ifox;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.arzen.ifox.api.HttpIfoxApi;
import com.arzen.ifox.api.HttpSetting;
import com.arzen.ifox.bean.CommitScore;
import com.arzen.ifox.bean.DynamicUpdate;
import com.arzen.ifox.bean.DynamicUpdate.DynamicData;
import com.arzen.ifox.bean.Init;
import com.arzen.ifox.setting.KeyConstants;
import com.arzen.ifox.setting.UserSetting;
import com.arzen.ifox.utils.CommonUtil;
import com.arzen.ifox.utils.DynamicLibManager;
import com.arzen.ifox.utils.DynamicLibUtils;
import com.arzen.ifox.utils.MsgUtil;
import com.encore.libs.http.HttpConnectManager;
import com.encore.libs.http.OnRequestListener;
import com.encore.libs.utils.NetWorkUtils;
import com.unionpay.uppay.widget.ac;

public abstract class iFox {

	private static final String TAG = "IFox";

	/**
	 * 当前游戏id
	 */
	// public static String GID = "";

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
		// 初始化应用信息,此步不同下面工作就无法进行
		initAppInfo(activity, appKey, appSecrect);
	}

	/**
	 * 初始化应用信息
	 */
	private static void initAppInfo(final Activity activity, String appKey, String appSecrect) {
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
									DynamicLibManager.initDexResource(activity);
									// 设置当前游戏id
									String gid = init.getData().getGid();
									// 保存当前gid
									UserSetting.saveData(activity, gid);

									if (DynamicLibManager.getDynamicLibManager(activity) != null) {
										// 检查动态库是否有更新
										checkUpdate(activity, gid, getChannelId(activity), DynamicLibManager.getDynamicLibManager(activity).getmVertionCode());
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
						if (latest.equals("false") && !data.getUrl().equals("")) { // false
																					// 有新版本
																					// true
																					// 没新版本
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

	public static void loginPage(final Activity activity, String appKey, String appSecrect, Bundle bundle, final LoginListener listener) {
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
		bundle.putString(KeyConstants.INTENT_DATA_KEY_TOKEN, token);
		bundle.putString(KeyConstants.INTENT_DATA_KEY_CLIENTID, appKey);
		bundle.putString(KeyConstants.INTENT_DATA_KEY_CLIENTSECRET, appSecrect);
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
	 * 
	 * @param activity
	 *            上下文
	 * @param bundle
	 *            必须含有 key = 'token' 的值
	 * @param listener
	 *            修改密码回调
	 */
	public static void changePassword(final Activity activity, String appKey, String appSecrect, Bundle bundle, final ChangePasswordListener listener) {
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
		if (gid.equals("")) {
			MsgUtil.msg("未初始化!", activity);
			return;
		} else if (token.equals("")) {
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
		bundle.putString(KeyConstants.INTENT_DATA_KEY_CID, getChannelId(activity.getApplicationContext())); // 渠道id
		bundle.putString(KeyConstants.INTENT_DATA_KEY_TOKEN, token);
		bundle.putString(KeyConstants.INTENT_DATA_KEY_CLIENTID, appKey);
		bundle.putString(KeyConstants.INTENT_DATA_KEY_CLIENTSECRET, appSecrect);
		intent.putExtras(bundle);
		activity.startActivity(intent);
	}

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

	public static void chargePage(final Activity activity, String appKey, String appSecrect, Bundle bundle, final ChargeListener listener) {
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
		bundle.putString(KeyConstants.INTENT_DATA_KEY_TOKEN, token);
		bundle.putString(KeyConstants.INTENT_DATA_KEY_CLIENTID, appKey);
		bundle.putString(KeyConstants.INTENT_DATA_KEY_CLIENTSECRET, appSecrect);
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
	public static void TopPage(final Activity activity, String appKey, String appSecrect) {
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
		bundle.putString(KeyConstants.INTENT_DATA_KEY_TOKEN, token);
		bundle.putString(KeyConstants.INTENT_DATA_KEY_CLIENTID, appKey);
		bundle.putString(KeyConstants.INTENT_DATA_KEY_CLIENTSECRET, appSecrect);
		intent.putExtras(bundle);
		activity.startActivity(intent);
	}

	/**
	 * 提交分数
	 * 
	 * @param activity
	 * @param score
	 *            提交的分数
	 * @param cb
	 *            回调可传null
	 */
	public static void commitScore(final Activity activity, final long score, final OnCommitScoreCallBack cb) {
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

		HttpIfoxApi.commitScore(activity, gid, 0, score, token, new OnRequestListener() {

			@Override
			public void onResponse(final String url, final int state, final Object result, final int type) {
				// TODO Auto-generated method stub
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

	public interface OnCommitScoreCallBack {
		public void onSuccess();

		public void onFail(String msg);
	}

	private static HashMap<String, String> mConfigs = new HashMap<String, String>();

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
		String configs = readFile(context, "config.txt");
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

	private static String readFile(Context context, String fileName) {
		if (TextUtils.isEmpty(fileName)) {
			return "";
		}
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try {
			is = context.getAssets().open(fileName);
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
}
