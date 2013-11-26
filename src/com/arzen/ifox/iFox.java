package com.arzen.ifox;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.arzen.utils.JarUtil;

public abstract class iFox {
	/**
	 * fragment 容器id
	 */
	public static int CONTAINER_ID = android.R.id.primary;
	/**
	 * home fragment pkg
	 */
	public static final String PKG_HOME_FRAGMENT = "com.arzen.iFoxLib.fragment.HomeFragment";
	
	public static final String PKG_PAY_FRAGMENT = "com.arzen.iFoxLib.fragment.PayFragment";

	public static Activity mActivity;
	
	public final static String dexFile = "iFoxLib.apk";
	
	private static JarUtil mJarUtil;

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
		iFox.mActivity = activity;

		JarUtil jarUtil = new JarUtil(iFox.mActivity);
		// 初始化lib资源,导入资源,以便做到调用,lib apk 动态加载view
		jarUtil.initIFoxLibResource(mActivity, iFox.dexFile);
		
//		String classPath = "com.arzen.iFoxLib.DynamicTest";
//		String returnString = (String) jarUtil.executeJarClass(iFox.dexFile, classPath, "helloWorld", 
//				new Class[] { }, new Object[]{});
//		return jarUtil;
	}
	
	/**
	 * 初始化动态更新包资源 必须在setContentView前执行
	 * @return
	 */
	public static JarUtil initLibApkResource()
	{
		if(iFox.mActivity == null){
			return null;
		}
		if(mJarUtil == null)
			mJarUtil = new JarUtil(iFox.mActivity);
		
		// 初始化lib资源,导入资源,以便做到调用,lib apk 动态加载view
		mJarUtil.initIFoxLibResource(mActivity, iFox.dexFile);
		
		return mJarUtil;
	}

	/**
	 * 加载lib 主页
	 */
	public static void loadHomePage(Activity activity) {
		try {
			Fragment f = (Fragment) activity.getClassLoader().loadClass(PKG_HOME_FRAGMENT).newInstance();
			FragmentManager fm = activity.getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.add(CONTAINER_ID, f);
			ft.commit();
			fm.executePendingTransactions();
		} catch (Exception e) {
			Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		
//		try {
//			Class<Fragment> clazz =  (Class<Fragment>) activity.getClassLoader().loadClass(PKG_HOME_FRAGMENT).newInstance();
//			if (clazz != null) {
//				Fragment homeFragment;
//				try {
//					homeFragment = clazz.newInstance();
//					FragmentUtil.changeFragmentToContainer(iFox.mActivity, homeFragment, "home", false, false);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
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

	public static void loginPage(final Activity activity, final Bundle bundle, final LoginListener listener) {

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

	public static void chargePage(final Activity activity, final Bundle bundle, final ChargeListener listener) {
		Intent intent = new Intent(activity,PayActivity.class);
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
