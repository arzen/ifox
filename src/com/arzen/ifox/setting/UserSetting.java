package com.arzen.ifox.setting;

import android.content.Context;

import com.arzen.ifox.utils.SettingUtils;

public class UserSetting {

	/**
	 * 保存数据
	 * 
	 * @param gid
	 */
	public static void saveData(Context context, String gid, long time,String alipay_notify_url) {
		SettingUtils settingUtils = getSettingUtils(context);
		settingUtils.putString(KeyConstants.SHARED_KEY_GID, gid);
		settingUtils.putLong(KeyConstants.SHARED_KEY_TIME, time);
		settingUtils.putString(KeyConstants.SHARED_ALIPAY_NOTIFY_URL, alipay_notify_url);
		settingUtils.commitOperate();
	};

	/**
	 * 上次初始化保存的时间,
	 * 
	 * @param context
	 */
	public static long getInitTime(Context context) {
		SettingUtils settingUtils = getSettingUtils(context);
		return settingUtils.getLong(KeyConstants.SHARED_KEY_TIME, 0);
	}

	/**
	 * 得到gid
	 * 
	 * @param context
	 */
	public static String getGID(Context context) {
		SettingUtils settingUtils = getSettingUtils(context);
		return settingUtils.getString(KeyConstants.SHARED_KEY_GID, "");
	}
	
	/**
	 * 得到回调地址
	 * 
	 * @param context
	 */
	public static String getNotifyUrl(Context context) {
		SettingUtils settingUtils = getSettingUtils(context);
		return settingUtils.getString(KeyConstants.SHARED_ALIPAY_NOTIFY_URL, "");
	}

	/**
	 * 保存token数据
	 * 
	 * @param gid
	 */
	public static void saveToken(Context context, String token) {
		SettingUtils settingUtils = getSettingUtils(context);
		settingUtils.putString(KeyConstants.SHARED_KEY_TOKEN, token);
		settingUtils.commitOperate();
	};

	/**
	 * 得到token
	 * 
	 * @param context
	 */
	public static String getToken(Context context) {
		SettingUtils settingUtils = getSettingUtils(context);
		return settingUtils.getString(KeyConstants.SHARED_KEY_TOKEN, "");
	}

	/**
	 * 获取
	 * 
	 * @return
	 */
	private static SettingUtils getSettingUtils(Context context) {
		SettingUtils settingUtils = new SettingUtils(context, KeyConstants.SHARED_NAME_USER, Context.MODE_PRIVATE);
		return settingUtils;
	}

	public static long getScore(Context context) {
		SettingUtils settingUtils = getSettingUtils(context);
		return settingUtils.getLong(KeyConstants.SHARED_KEY_SOCRE, 0);
	}

	public static void saveScore(Context context, long score) {
		SettingUtils settingUtils = getSettingUtils(context);
		settingUtils.putLong(KeyConstants.SHARED_KEY_SOCRE, score);
		settingUtils.commitOperate();
	};
}
