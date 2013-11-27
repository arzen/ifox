package com.arzen.ifox.api;

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
			Request request = new Request();
			request.setUrl(url);
			request.setParser(new JsonParser(Init.class));
			request.setOnRequestListener(onRequestListener);
			HttpConnectManager.getInstance(activity.getApplicationContext()).doGet(request);
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
}
