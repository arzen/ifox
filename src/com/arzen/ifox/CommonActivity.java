package com.arzen.ifox;

import java.util.Random;

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

import com.arzen.ifox.iFox.ChangePasswordListener;
import com.arzen.ifox.iFox.ChargeListener;
import com.arzen.ifox.iFox.LoginListener;
import com.arzen.ifox.download.DownloadManager;
import com.arzen.ifox.setting.KeyConstants;
import com.arzen.ifox.setting.UserSetting;
import com.arzen.ifox.utils.DynamicLibManager;
import com.arzen.ifox.utils.MsgUtil;
import com.baidu.mobstat.StatService;
import com.encore.libs.utils.Log;
import com.unionpay.UPPayAssistEx;
import com.unionpay.uppay.PayActivity;

import dalvik.system.DexClassLoader;

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
		registerReceiver(mPayBroadcastReceiver, new IntentFilter(KeyConstants.RECEIVER_PAY_START_ACTION));
		registerReceiver(mDownloadBroadcastReceiver, new IntentFilter(KeyConstants.RECEIVER_DOWNLOAD_ACTION));
	}

	/**
	 * 初始化动态库资源,得到包底下的图片资源等,必须在super.onCreate()前执行
	 */
	public void initResources() {
		// init
		DynamicLibManager dl = DynamicLibManager.initLibApkResource(this);
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
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		try {
			// 获取需要加载的fragment包名
			String packageName = mBundle.getString(KeyConstants.KEY_PACKAGE_NAME);
			DynamicLibManager dl = DynamicLibManager.getDynamicLibManager(this);
			Class[] argsClass = { Activity.class, Integer.class, KeyEvent.class };
			Object[] values = { CommonActivity.this, keyCode, event };
			boolean flag = (Boolean) dl.executeJarClass(this, DynamicLibManager.DEX_FILE, packageName, "onKeyDown", argsClass, values);
			if (!flag) {
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
			if (intent.getAction().equals(KeyConstants.RECEIVER_RESULT_ACTION) && intent != null) {
				Bundle bundle = intent.getExtras();
				// 得到当前结果需要处理的动作， 支付，登录等
				String disposeAction = bundle.getString(KeyConstants.RECEIVER_KEY_DISPOSE_ACTION);
				if (disposeAction != null && disposeAction.equals(KeyConstants.RECEIVER_ACTION_PAY) && getChargeListener() != null) {
					disposePayReceiver(bundle);
				} else if (disposeAction != null && disposeAction.equals(KeyConstants.RECEIVER_ACTION_LOGIN) && getLoginListener() != null) {
					disposeLoginReceiver(bundle);
				} else if (disposeAction != null && disposeAction.equals(KeyConstants.RECEIVER_ACTION_CHANGE_PASSWORD) && getChangePasswordListener() != null) {
					disposeChangePwd(bundle);
				} else {
					finish();
				}
			}
		}
	};
	/**
	 * 支付广播
	 */
	public BroadcastReceiver mPayBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent != null && intent.getAction().equals(KeyConstants.RECEIVER_PAY_START_ACTION)) {
				int payType = intent.getIntExtra(KeyConstants.INTENT_DATA_KEY_PAY_TYPE, -1);
				if (payType != -1) {
					switch (payType) {
					case KeyConstants.PAY_TYPE_WIIPAY:
						toWayPay();
						break;
					case KeyConstants.PAY_TYPE_UNIONPAY:
						String tn = intent.getStringExtra(KeyConstants.INTENT_DATA_KEY_PAY_TN);
						toUnionpay(tn);
						break;
					}
				}
			}
		}
	};
	
	public BroadcastReceiver mDownloadBroadcastReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent != null && intent.getAction().equals(KeyConstants.RECEIVER_DOWNLOAD_ACTION)) {
				String downloadUrl = intent.getStringExtra("downloadUrl");
				String gameName = intent.getStringExtra("gameName");
				int id = intent.getIntExtra("id", new Random().nextInt(999999));
				
				DownloadManager downloadManager = new DownloadManager();
				downloadManager.downloadFile(getApplicationContext(), downloadUrl, gameName, id);
			}
		}
	};

	/**
	 * 银联支付
	 * 
	 * @param tn
	 *            流水号
	 */
	public void toUnionpay(String tn) {
		UPPayAssistEx.startPayByJAR(this, PayActivity.class, null, null, tn, "00"); // 00 正式 01测试
	}

	/**
	 * 跳转微派支付
	 */
	public void toWayPay() {

//		// 检测微派支付有没更新
//		new ApkUpdate(this, new ApkUpdateCallback() {
//			@Override
//			public void launch(Map<String, String> arg0) {
//				// TODO Auto-generated method stub
//					WayPay mWayPay = new WayPay(mBundle);
//				Log.d("PayFragment", "way pay");
//				mWayPay.toPay(CommonActivity.this, "0001");
//			}
//		});
	}

	/**
	 * 处理修改密码回调
	 * 
	 * @param bundle
	 */
	public void disposeChangePwd(Bundle bundle) {
		String result = bundle.getString(KeyConstants.INTENT_KEY_RESULT);
		if (result == null) {
			return;
		}
		ChangePasswordListener listener = getChangePasswordListener();
		if (result.equals(KeyConstants.INTENT_KEY_SUCCESS)) {
			listener.onSuccess();
		} else if (result.equals(KeyConstants.INTENT_KEY_CANCEL)) {
			listener.onCancel();
		}
		// 退出
		finish();
		setChangePasswordListener(null);
	}

	/**
	 * 处理登录回调
	 * 
	 * @param bundle
	 */
	public void disposeLoginReceiver(Bundle bundle) {
		String result = bundle.getString(KeyConstants.INTENT_KEY_RESULT);
		if (result == null) {
			return;
		}
		LoginListener listener = getLoginListener();

		if (result.equals(KeyConstants.INTENT_KEY_SUCCESS)) {
			String token = bundle.getString(KeyConstants.INTENT_DATA_KEY_TOKEN);
			String uid = bundle.getString(KeyConstants.INTENT_DATA_KEY_UID);
			UserSetting.saveToken(this, token); // 保存token
			Log.d(TAG, "loginReceiver: token:" + token + " uid:" + uid);
			listener.onSuccess(bundle);
		} else if (result.equals(KeyConstants.INTENT_KEY_CANCEL)) {
			listener.onCancel();
		}

		// 退出
		finish();
		setLoginListener(null);
	}

	/**
	 * 处理支付receiver回调
	 * 
	 * @param bundle
	 */
	public void disposePayReceiver(Bundle bundle) {
		String result = bundle.getString(KeyConstants.INTENT_KEY_RESULT);
		String msg = bundle.getString(KeyConstants.INTENT_KEY_MSG);

//		Log.d(TAG, "mPayResultReceiver (result:" + result + " msg:" + msg + ")");

		if (result == null) {
			return;
		}
		ChargeListener chargeListener = getChargeListener();
		if (result.equals(KeyConstants.INTENT_KEY_SUCCESS)) {
			chargeListener.onSuccess(bundle);
		} else if (result.equals(KeyConstants.INTENT_KEY_FAIL)) {
			chargeListener.onFail(msg);
		} else if (result.equals(KeyConstants.INTENT_KEY_CANCEL)) {
			chargeListener.onCancel();
		}
		// 退出
		finish();
		setPayCallBackListener(null);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			unregisterReceiver(mBroadcastReceiver);
			mBroadcastReceiver = null;

			unregisterReceiver(mPayBroadcastReceiver);
			mPayBroadcastReceiver = null;
			
			unregisterReceiver(mDownloadBroadcastReceiver);
			mDownloadBroadcastReceiver = null;
		} catch (Exception e) {
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		/*************************************************
		 * 
		 * 步骤3：处理银联手机支付控件返回的支付结果
		 * 
		 ************************************************/
		if(requestCode == 10){
			if (data == null) {
				return;
			}

			/*
			 * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
			 */
			String result = data.getExtras().getString("pay_result");
			Intent intent = new Intent(KeyConstants.ACTION_PAY_RESULT_RECEIVER);
			intent.putExtra(KeyConstants.INTENT_KEY_RESULT, result);
			sendBroadcast(intent);
		}
		
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
