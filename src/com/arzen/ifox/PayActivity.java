package com.arzen.ifox;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.arzen.ifox.iFox.ChargeListener;
import com.arzen.ifox.setting.KeyConstants;
import com.arzen.ifox.utils.DynamicLibManager;
import com.arzen.ifox.utils.MsgUtil;
import com.encore.libs.utils.Log;

import dalvik.system.DexClassLoader;

public class PayActivity extends Activity {

	public final String TAG = "PayActivity";

	private AssetManager mAssetManager;
	private Resources mResources;
	private Theme mTheme;
	private DexClassLoader mClassLoader;

	// 支付参数
	private Bundle mBundle;
	/**
	 * 支付回调接口
	 */
	public static ChargeListener mChargeListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 初始化动态库资源 必须在super.onCreate()前执行
		initResources();

		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			Log.d(TAG, "横屏");
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			Log.d(TAG, "竖屏");
		}

		FrameLayout rootView = new FrameLayout(this);
		rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		rootView.setId(KeyConstants.KEY_CONTAINER_ID);
		setContentView(rootView);
		mBundle = getIntent().getExtras();
		// 显示支付页
		showPayFragment();
		// 注册支付广播
		registerReceiver(mPayResultReceiver, new IntentFilter(KeyConstants.RECEIVER_RESULT_ACTION));
	}

	/**
	 * 初始化动态库资源 必须在super.onCreate()前执行
	 */
	public void initResources() {
		// init
		DynamicLibManager jarUtil = iFox.initLibApkResource(this);
		if (jarUtil == null) { // 未初始化退出
			finish();
			return;
		}
		mAssetManager = jarUtil.getmAssetManager();
		mClassLoader = (DexClassLoader) jarUtil.getmClassLoader();
		mTheme = jarUtil.getmTheme();
		mResources = jarUtil.getmResources();
		
		mTheme = mResources.newTheme();
		mTheme.setTo(super.getTheme());
	}

	/**
	 * 显示支付页面
	 */
	public void showPayFragment() {
		try {
			Fragment f = (Fragment) getClassLoader().loadClass(KeyConstants.PKG_PAY_FRAGMENT).newInstance();
			if (f == null) {
				MsgUtil.msg("load fragment class is null!", this);
				return;
			}
			if (mBundle != null) {
				f.setArguments(mBundle);
			}
			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(KeyConstants.KEY_CONTAINER_ID, f, TAG);
			ft.commit();
			fm.executePendingTransactions();
		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
   
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(KeyConstants.RECEIVER_RESULT_ACTION);
			Bundle bundle = new Bundle();
			bundle.putString(KeyConstants.INTENT_KEY_PAY_RESULT, KeyConstants.INTENT_KEY_PAY_CANCEL);
			intent.putExtras(bundle);
			sendBroadcast(intent);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public AssetManager getAssets() {
		return mAssetManager == null ? super.getAssets() : mAssetManager;
	}

	@Override
	public Resources getResources() {
		return mResources == null ? super.getResources() : mResources;
	}

	@Override
	public Theme getTheme() {
		return mTheme == null ? super.getTheme() : mTheme;
	}

	@Override
	public ClassLoader getClassLoader() {
		return mClassLoader == null ? super.getClassLoader() : mClassLoader;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		try {
			unregisterReceiver(mPayResultReceiver);
			mPayResultReceiver = null;
		} catch (Exception e) {
		}

	}

	/**
	 * 支付结果回调
	 */
	public BroadcastReceiver mPayResultReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(KeyConstants.RECEIVER_RESULT_ACTION) && mChargeListener != null) { // 支付结果
				Bundle bundle = intent.getExtras();
				String result = bundle.getString(KeyConstants.INTENT_KEY_PAY_RESULT);
				String msg = bundle.getString(KeyConstants.INTENT_KEY_PAY_MSG);

				Log.d(TAG, "mPayResultReceiver (result:" + result + " msg:" + msg + ")");

				if (result == null) {
					return;
				}

				if (result.equals(KeyConstants.INTENT_KEY_PAY_SUCCESS)) {
					mChargeListener.onSuccess(bundle);
				} else if (result.equals(KeyConstants.INTENT_KEY_PAY_FAIL)) {
					mChargeListener.onFail(msg);
				} else if (result.equals(KeyConstants.INTENT_KEY_PAY_CANCEL)) {
					mChargeListener.onCancel();
				}
				// 退出
				finish();
				mChargeListener = null;
			}
		}
	};

	/**
	 * 设置支付回调接口
	 * 
	 * @param chargeListener
	 */
	public static void setPayCallBackListener(ChargeListener chargeListener) {
		mChargeListener = chargeListener;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
}
