package com.arzen.ifox;

import com.arzen.ifox.iFox.ChargeListener;

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
	 * 设置支付回调接口
	 * 
	 * @param chargeListener
	 */
	public static void setPayCallBackListener(ChargeListener chargeListener) {
		mChargeListener = chargeListener;
	}

	public static ChargeListener getChargeListener() {
		return mChargeListener;
	}

}
