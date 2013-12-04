package com.arzen.ifox;

import com.arzen.ifox.iFox.ChargeListener;
import com.arzen.ifox.setting.KeyConstants;
import com.arzen.ifox.utils.DynamicLibManager;
import com.arzen.ifox.utils.MsgUtil;
import com.encore.libs.utils.Log;

import dalvik.system.DexClassLoader;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * 公用activity,动态库下所有资源的载体,所有fragment统一处理,方便动态升级
 * 支付,登录,注册,修改密码等,需要填写相应参数,进行统一加载动态库,显示
 * 
 * @author Encore.liang
 * 
 */
public class CommonActivity extends BaseActivity {

	public static final String TAG = "CommonActivity";

	// 资源管理
	private AssetManager mAssetManager;
	// 资源
	private Resources mResources;
	// 主题
	private Theme mTheme;
	// 动态库加载
	private DexClassLoader mClassLoader;

	// 参数,必须要要有需要加载的包名,否则抛出异常
	private Bundle mBundle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// 初始化动态库资源,必须在onCreate()前执行
		initResources();

		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// 设置一个空的view,加载动态fragment
		FrameLayout rootView = new FrameLayout(this);
		rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		rootView.setId(KeyConstants.KEY_CONTAINER_ID);
		setContentView(rootView);

		mBundle = getIntent().getExtras();
		// bundle 如果为空抛出异常
		if (mBundle == null || mBundle.getString(KeyConstants.KEY_PACKAGE_NAME) == null) {
			throw new IllegalArgumentException("bundle or param packageName is null!");
		}
		// 加载动态库fragment
		loadDynamicFragment();

		registerReceiver(mBroadcastReceiver, new IntentFilter(KeyConstants.RECEIVER_RESULT_ACTION));
	}

	/**
	 * 初始化动态库资源,得到包底下的图片资源等,必须在super.onCreate()前执行
	 */
	public void initResources() {
		// init
		DynamicLibManager dl = iFox.initLibApkResource(this);
		if (dl == null) { // 未初始化退出
			finish();
			return;
		}
		mAssetManager = dl.getmAssetManager();
		mClassLoader = (DexClassLoader) dl.getmClassLoader();
		mTheme = dl.getmTheme();
		mResources = dl.getmResources();

		mTheme = mResources.newTheme();
		mTheme.setTo(super.getTheme());
	}

	/**
	 * 加载动态库Fragment并显示
	 */
	public void loadDynamicFragment() {
		try {
			// 获取需要加载的fragment包名
			String packageName = mBundle.getString(KeyConstants.KEY_PACKAGE_NAME);
			// 加载fragment
			Fragment f = (Fragment) getClassLoader().loadClass(packageName).newInstance();
			if (f == null) {
				MsgUtil.msg("load fragment class is null!", this);
				return;
			}
			if (mBundle != null) {
				f.setArguments(mBundle);
			}
			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(KeyConstants.KEY_CONTAINER_ID, f);
			ft.commit();
			fm.executePendingTransactions();
		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		try {
			// 获取需要加载的fragment包名
			String packageName = mBundle.getString(KeyConstants.KEY_PACKAGE_NAME);
			DynamicLibManager dl = iFox.getDynamicLibManager(this);
			Class[] argsClass = { Activity.class, Integer.class, KeyEvent.class };
			Object[] values = { CommonActivity.this, keyCode, event };
			boolean flag = (Boolean) dl.executeJarClass(this, iFox.DEX_FILE, packageName, "onKeyDown", argsClass, values);
			if(!flag){
				return flag;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 公用activity广播,方便接收回调以便处理
	 */
	public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(KeyConstants.RECEIVER_RESULT_ACTION) && getChargeListener() != null) { // 支付结果
				Bundle bundle = intent.getExtras();
				String result = bundle.getString(KeyConstants.INTENT_KEY_PAY_RESULT);
				String msg = bundle.getString(KeyConstants.INTENT_KEY_PAY_MSG);

				Log.d(TAG, "mPayResultReceiver (result:" + result + " msg:" + msg + ")");

				if (result == null) {
					return;
				}

				ChargeListener chargeListener = getChargeListener();

				if (result.equals(KeyConstants.INTENT_KEY_PAY_SUCCESS)) {
					chargeListener.onSuccess(bundle);
				} else if (result.equals(KeyConstants.INTENT_KEY_PAY_FAIL)) {
					chargeListener.onFail(msg);
				} else if (result.equals(KeyConstants.INTENT_KEY_PAY_CANCEL)) {
					chargeListener.onCancel();
				}
				// 退出
				finish();
				setPayCallBackListener(null);
			}
		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			unregisterReceiver(mBroadcastReceiver);
			mBroadcastReceiver = null;
		} catch (Exception e) {
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
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
}
