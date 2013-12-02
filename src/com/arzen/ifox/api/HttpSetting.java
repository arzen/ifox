package com.arzen.ifox.api;

import android.app.Activity;

import com.arzen.ifox.iFox;
import com.arzen.ifox.setting.KeyConstants;
import com.arzen.ifox.utils.DynamicLibManager;

/**
 * http 设置类
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
	private static  String IFOX_INIT = null;
	/**
	 * ifox update url
	 */
	private static String IFOX_UPDATE_URL = null;
	
	/**
	 * 获取ifox init request url
	 * @param activity
	 * @return
	 */
	public static String getInitUrl(Activity activity)
	{
		if(IFOX_INIT == null || IFOX_INIT.equals("")){
			DynamicLibManager jarUtil = iFox.getJarUtil();
			Object result = null;
			if(jarUtil == null){
				jarUtil = new DynamicLibManager(activity);
				iFox.setJarUtil(jarUtil);
			}
			 //获取服务器地址
			result = jarUtil.executeJarClass(activity,iFox.DEX_FILE, KeyConstants.CLASSPATH_HTTP_SETTING, "getIFoxInitUrl", new Class[]{}, new Object[]{});
			if(result != null && result instanceof String){
				IFOX_INIT  = (String) result;
			}
		}
		return IFOX_INIT;
	}
	
	/**
	 * 获取ifox 动态库更新url
	 * @param activity
	 * @return
	 */
	public static String getDynamicUpdateUrl(Activity activity)
	{
		if(IFOX_UPDATE_URL == null || IFOX_UPDATE_URL.equals("")){
			DynamicLibManager jarUtil = iFox.getJarUtil();
			Object result = null;
			if(jarUtil == null){
				jarUtil = new DynamicLibManager(activity);
				iFox.setJarUtil(jarUtil);
			}
			 //获取服务器地址
			result = jarUtil.executeJarClass(activity,iFox.DEX_FILE, KeyConstants.CLASSPATH_HTTP_SETTING, "getDynamicUpdateUrl", new Class[]{}, new Object[]{});
			if(result != null && result instanceof String){
				IFOX_UPDATE_URL  = (String) result;
			}
		}
		return IFOX_UPDATE_URL;
	}

//	private static String SERVER_URL = "";
//	
//	/**
//	 * 获取服务器地址,从 lib apk下读取,以便更新修改
//	 * @param activity
//	 * @return
//	 */
//	public static String getServerUrl(Activity activity)
//	{
//		if(SERVER_URL == null || SERVER_URL.equals("")){
//			JarUtil jarUtil = iFox.getJarUtil();
//			Object result = null;
//			if(jarUtil == null){
//				jarUtil = new JarUtil(activity);
//			}
//			 //获取服务器地址
//			result = jarUtil.executeJarClass(iFox.DEX_FILE, KeyConstants.CLASSPATH_HTTP_SETTING, "getServerUrl", new Class[]{}, new Object[]{});
//			if(result != null && result instanceof String){
//				setServerUrl((String) result);
//			}
//		}
//		return SERVER_URL;
//	}
//	
//	/**
//	 * 设置服务器url
//	 * @param serverUrl
//	 */
//	private static void setServerUrl(String serverUrl)
//	{
//		SERVER_URL = serverUrl;
//	}
}
