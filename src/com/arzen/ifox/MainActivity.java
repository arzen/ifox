package com.arzen.ifox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.arzen.utils.JarUtil;

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
import dalvik.system.DexClassLoader;

public class MainActivity extends Activity {
	
	
	private AssetManager mAssetManager;
	private Resources mResources;
	private Theme mTheme;
	private ClassLoader mClassLoader;
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		// init
		JarUtil jarUtil = iFox.init(this, null, null);
		mAssetManager = jarUtil.getmAssetManager();
		mClassLoader = jarUtil.getmClassLoader();
		mTheme = jarUtil.getmTheme();
		mResources = jarUtil.getmResources();
		mTheme.setTo(super.getTheme());

		super.onCreate(savedInstanceState);

		FrameLayout rootView = new FrameLayout(this);
		rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		rootView.setId(iFox.CONTAINER_ID);
		setContentView(rootView);
		

		//加载主页
		iFox.loadHomePage(this);
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
