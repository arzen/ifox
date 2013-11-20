package com.arzen.ifox;

import android.app.Activity;
import android.os.Bundle;

public abstract class iFox {

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
	public static void init(final Activity act, String appKey, String appSecrect) {
		
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
	
	public static void loginPage(final Activity activity,final Bundle bundle, final LoginListener listener) {
		
	}
	
	public static interface LoginListener{
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
	
	public static void chargePage(final Activity activity,final Bundle bundle, final ChargeListener listener) {
		
	}
	
	public static interface ChargeListener{
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
	
}
