package com.arzen.ifox.setting;

import android.content.Context;

import com.arzen.ifox.utils.SettingUtils;

public class UserSetting {
	
	/**
	 * 保存数据
	 * @param gid
	 */
	public static void saveData(Context context,String gid){
		SettingUtils settingUtils = getSettingUtils(context);
		settingUtils.putString(KeyConstants.SHARED_KEY_GID, gid);
		settingUtils.commitOperate();
	};
	/**
	 * 得到gid
	 * @param context
	 */
	public static String getGID(Context context){
		SettingUtils settingUtils = getSettingUtils(context);
		return settingUtils.getString(KeyConstants.SHARED_KEY_GID, "");
	}
	
	/**
	 * 保存token数据
	 * @param gid
	 */
	public static void saveToken(Context context,String token){
		SettingUtils settingUtils = getSettingUtils(context);
		settingUtils.putString(KeyConstants.SHARED_KEY_TOKEN, token);
		settingUtils.commitOperate();
	};
	
	/**
	 * 得到token
	 * @param context
	 */
	public static String getToken(Context context){
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
}
