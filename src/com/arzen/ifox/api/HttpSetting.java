package com.arzen.ifox.api;

import android.app.Activity;

import com.arzen.ifox.iFox;
import com.arzen.ifox.setting.KeyConstants;
import com.arzen.ifox.utils.DynamicLibManager;

/**
 * http 设置类
 * 
 * @author Encore.liang
 * 
 */
public class HttpSetting {
	/**
	 * 请求返回码
	 */
	public static final int RESULT_CODE_OK = 200;

	/**
	 * ifox init url
	 */
	private static String IFOX_INIT = null;
	/**
	 * ifox update url
	 */
	private static String IFOX_UPDATE_URL = null;
	/**
	 * commit score url
	 */
	private static String IFOX_COMMIT_SCORE_URL = null;
	/**
	 * share url
	 */
	private static String IFOX_SHARE_URL = null;
	/**
	 * 获取ifox init request url
	 * 
	 * @param activity
	 * @return
	 */
	public static String getInitUrl(Activity activity) {
		if (IFOX_INIT == null || IFOX_INIT.equals("")) {
			DynamicLibManager jarUtil = DynamicLibManager.getDynamicLibManager(activity);
			Object result = null;
			// 获取服务器地址
			result = jarUtil.executeJarClass(activity, DynamicLibManager.DEX_FILE, KeyConstants.CLASSPATH_HTTP_SETTING, "getIFoxInitUrl", new Class[] {}, new Object[] {});
			if (result != null && result instanceof String) {
				IFOX_INIT = (String) result;
			}
		}
		return IFOX_INIT;
	}
	/**
	 * 设置服务器地址
	 * @param activity
	 * @param url
	 */
	public static void setServerUrl(Activity activity,String url){
		DynamicLibManager jarUtil = DynamicLibManager.getDynamicLibManager(activity);
		jarUtil.executeJarClass(activity, DynamicLibManager.DEX_FILE, KeyConstants.CLASSPATH_HTTP_SETTING, "setServerUrl", new Class[] {String.class}, new Object[] {url});
	}

	/**
	 * 获取ifox 动态库更新url
	 * 
	 * @param activity
	 * @return
	 */
	public static String getDynamicUpdateUrl(Activity activity) {
		if (IFOX_UPDATE_URL == null || IFOX_UPDATE_URL.equals("")) {
			DynamicLibManager jarUtil = DynamicLibManager.getDynamicLibManager(activity);
			Object result = null;
			// 获取服务器地址
			result = jarUtil.executeJarClass(activity, DynamicLibManager.DEX_FILE, KeyConstants.CLASSPATH_HTTP_SETTING, "getDynamicUpdateUrl", new Class[] {}, new Object[] {});
			if (result != null && result instanceof String) {
				IFOX_UPDATE_URL = (String) result;
			}
		}
		return IFOX_UPDATE_URL;
	}
	/**
	 * 获取提交分数URL
	 * @param activity
	 * @return
	 */
	public static String getCommitScoreUrl(Activity activity) {
		if (IFOX_COMMIT_SCORE_URL == null || IFOX_COMMIT_SCORE_URL.equals("")) {
			DynamicLibManager jarUtil = DynamicLibManager.getDynamicLibManager(activity);
			Object result = null;
			// 获取服务器地址
			result = jarUtil.executeJarClass(activity, DynamicLibManager.DEX_FILE, KeyConstants.CLASSPATH_HTTP_SETTING, "getCommitScore", new Class[] {}, new Object[] {});
			if (result != null && result instanceof String) {
				IFOX_COMMIT_SCORE_URL = (String) result;
			}
		}
		return IFOX_COMMIT_SCORE_URL;
	}
	
	
	/**
	 * 获取提交分数URL
	 * @param activity
	 * @return
	 */
	public static String getShareUrl(Activity activity) {
		if (IFOX_COMMIT_SCORE_URL == null || IFOX_COMMIT_SCORE_URL.equals("")) {
			DynamicLibManager jarUtil = DynamicLibManager.getDynamicLibManager(activity);
			Object result = null;
			// 获取服务器地址
			result = jarUtil.executeJarClass(activity, 
					DynamicLibManager.DEX_FILE, 
					KeyConstants.CLASSPATH_HTTP_SETTING, 
					"getShareUrl", new Class[] {}, new Object[] {});
			if (result != null && result instanceof String) {
				IFOX_COMMIT_SCORE_URL = (String) result;
			}
		}
		return IFOX_COMMIT_SCORE_URL;
	}

}
