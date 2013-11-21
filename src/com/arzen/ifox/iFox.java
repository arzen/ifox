package com.arzen.ifox;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.arzen.utils.JarUtil;

import dalvik.system.DexClassLoader;

public abstract class iFox {
	
	static Activity act;
	static DexClassLoader cl;
	final static String dexFile = "iFoxLib.apk";

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
		iFox.act = act;
		
		String jarPath = iFox.act.getCacheDir().getPath(); 
		JarUtil jarUtil = new JarUtil(iFox.act); 
		String msg = jarUtil.executeJarClass(jarPath, iFox.dexFile,"com.arzen.iFoxLib.DynamicTest", "helloWorld").toString();
		Toast.makeText(act, msg, Toast.LENGTH_SHORT).show();
		
//		iFox.cl = loadDexFile(iFox.dexFile);
//		Class libProviderClazz = null;
//		try {
//			libProviderClazz = cl.loadClass("com.dynamic.DynamicTest");
//			IDynamic lib = (IDynamic)libProviderClazz.newInstance();
//			Toast.makeText(act, lib.helloWorld(), Toast.LENGTH_SHORT).show();
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
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
	
	private static DexClassLoader loadDexFile(String dexFile) {
		String filePath = iFox.act.getAssets().toString()
                + File.separator + dexFile;
		Log.e("iFox",filePath);
		final File optimizedDexOutputPath = new File(filePath);
		
        DexClassLoader cl = new DexClassLoader(optimizedDexOutputPath.getAbsolutePath(),
        		iFox.act.getAssets().toString(), null, iFox.act.getClassLoader());
		return cl; 
	}
	
}
