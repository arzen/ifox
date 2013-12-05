package com.arzen.ifox;

import com.arzen.ifox.iFox.ChangePasswordListener;
import com.arzen.ifox.iFox.ChargeListener;
import com.arzen.ifox.iFox.LoginListener;

import android.app.Activity;

/**
 * 父类
 * 
 * @author Encore.liang
 * 
 */
public class BaseActivity extends Activity {
	/**
	 * 支付回调接口
	 */
	private static ChargeListener mChargeListener;
	/**
	 * 登录回调接口
	 */
	private static LoginListener mLoginListener;
	
	private static ChangePasswordListener mChangePasswordListener;

	/**
	 * 设置支付回调接口
	 * 
	 * @param chargeListener
	 */
	public static void setPayCallBackListener(ChargeListener chargeListener) {
		mChargeListener = chargeListener;
	}

	protected static ChargeListener getChargeListener() {
		return mChargeListener;
	}

	/**
	 * 设置登录
	 * @param listener
	 */
	public static void setLoginListener(LoginListener listener) {
		mLoginListener = listener;
	}

	protected static LoginListener getLoginListener() {
		return mLoginListener;
	}
	/**
	 * 设置修改密码回调
	 * @param listener
	 */
	public static void setChangePasswordListener(ChangePasswordListener listener){
		mChangePasswordListener = listener;
	}
	
	public static ChangePasswordListener getChangePasswordListener()
	{
		return mChangePasswordListener;
	}
}
