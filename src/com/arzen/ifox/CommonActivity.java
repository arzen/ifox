package com.arzen.ifox;

import com.arzen.ifox.setting.KeyConstants;
import com.arzen.ifox.utils.DynamicLibManager;
import com.arzen.ifox.utils.MsgUtil;

import dalvik.system.DexClassLoader;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * 公用activity,动态库下所有资源的载体,所有fragment统一处理,方便动态升级
 * 支付,登录,注册,修改密码等,需要填写相应参数,进行统一加载动态库,显示
 * @author Encore.liang
 * 
 */
public class CommonActivity extends BaseActivity {
	
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

		// 设置一个空的view,加载动态fragment
		FrameLayout rootView = new FrameLayout(this);
		rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		rootView.setId(KeyConstants.KEY_CONTAINER_ID);
		setContentView(rootView);

		mBundle = getIntent().getExtras();
		//bundle 如果为空抛出异常
		if (mBundle == null || mBundle.getString(KeyConstants.KEY_PACKAGE_NAME) == null) {
			throw new IllegalArgumentException("bundle or param packageName is null!");
		}
		//加载动态库fragment
		loadDynamicFragment();
	}

	/**
	 * 初始化动态库资源,得到包底下的图片资源等,必须在super.onCreate()前执行
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
	 * 加载动态库Fragment并显示
	 */
	public void loadDynamicFragment() {
		try {
			//获取需要加载的fragment包名
			String packageName = mBundle.getString(KeyConstants.KEY_PACKAGE_NAME);
			//加载fragment
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
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 公用activity广播,方便接收回调以便处理
	 */
	public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
		}
	};
	
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
