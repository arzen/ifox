package com.arzen.ifox.api;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import com.arzen.ifox.bean.Init;
import com.encore.libs.http.HttpConnectManager;
import com.encore.libs.http.OnRequestListener;
import com.encore.libs.http.Request;
import com.encore.libs.json.JsonParser;

import android.app.Activity;
import android.content.Context;

/**
 * ifox 请求类
 * 
 * @author Encore.liang
 * 
 */
public class HttpIfoxApi {
	
	/**
	 * 参数app_key
	 */
	public static final String PARAM_APP_KEY = "app_key";

	/**
	 * 参数app_secret
	 */
	public static final String PARAM_APP_SECRET = "app_secret";

	/**
	 * 参数package
	 */
	public static final String PARAM_PACKAGE = "package";

	/**
	 * 请求初始化信息,当前请求必须成功,如果返回失败,则没必要继续进行下一步工作
	 * 
	 * @param packageName
	 * @param app_key
	 * @param app_secret
	 */
	public static void requestInitData(Activity activity, String packageName, String app_key, String app_secret, OnRequestListener onRequestListener) {
		// 得到初始化 url
		String url = HttpSetting.getInitUrl(activity);
		if (checkUrlIsCorrect(url)) {

			Map<String, Object> maps = new HashMap<String, Object>();
			maps.put(PARAM_APP_KEY,app_key);
			maps.put(PARAM_APP_SECRET, app_secret);
			maps.put(PARAM_PACKAGE, packageName);
			
			String postParam = createParams(maps);
			
			Request request = new Request();
			request.setUrl(url);
			request.setParser(new JsonParser(Init.class));
			request.setOnRequestListener(onRequestListener);
			HttpConnectManager.getInstance(activity.getApplicationContext()).doPost(request,postParam);
		}
	}

	/**
	 * 检查url是否正确
	 * 
	 * @param url
	 * @return
	 */
	private static boolean checkUrlIsCorrect(String url) {
		if (url != null && !url.equals("")) {
			return true;
		}
		return false;
	}
	
	/**
	 * 创建参数
	 * 
	 * @param params
	 * @return
	 */
	public static String createParams(Map<String, Object> params) {
		StringBuffer sb = new StringBuffer();
		String paramString = "";
		try {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				sb.append(entry.getKey());
				sb.append("=");
				sb.append(entry.getValue());
				sb.append("&");
			}
			int lastIndex = sb.toString().lastIndexOf("&");
			paramString = sb.toString().substring(0, lastIndex);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return paramString.toString();
	}
}