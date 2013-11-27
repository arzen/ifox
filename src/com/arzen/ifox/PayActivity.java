package com.arzen.ifox;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.arzen.ifox.setting.KeyConstants;
import com.arzen.ifox.utils.JarUtil;
import com.arzen.ifox.utils.MsgUtil;

public class PayActivity extends Activity {

	private AssetManager mAssetManager;
	private Resources mResources;
	private Theme mTheme;
	private ClassLoader mClassLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// init
		JarUtil jarUtil = iFox.initLibApkResource(this);
		if (jarUtil == null) { // 未初始化退出
			finish();
			return;
		}
		mAssetManager = jarUtil.getmAssetManager();
		mClassLoader = jarUtil.getmClassLoader();
		mTheme = jarUtil.getmTheme();
		mResources = jarUtil.getmResources();
//		mTheme.setTo(super.getTheme());

		super.onCreate(savedInstanceState);

		FrameLayout rootView = new FrameLayout(this);
		rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		rootView.setId(KeyConstants.KEY_CONTAINER_ID);
		setContentView(rootView);
		
		

		try { 
			Fragment f = (Fragment) getClassLoader().loadClass(KeyConstants.PKG_PAY_FRAGMENT).newInstance();
			if(f == null){
				MsgUtil.msg("load fragment class is null!", this);
				return;
			}
			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.add(KeyConstants.KEY_CONTAINER_ID, f);
			ft.commit();
			fm.executePendingTransactions();
		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
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

//	@Override
//	public Theme getTheme() {
//		return mTheme == null ? super.getTheme() : mTheme;
//	}

	@Override
	public ClassLoader getClassLoader() {
		return mClassLoader == null ? super.getClassLoader() : mClassLoader;
	}
}
