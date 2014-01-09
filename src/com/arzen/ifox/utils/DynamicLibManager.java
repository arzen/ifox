package com.arzen.ifox.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Environment;
import android.util.Log;
import dalvik.system.DexClassLoader;

public class DynamicLibManager {
	
	/**
	 * 动态库操作类
	 */
	private static DynamicLibManager mDynamicLibManager;

	private static final String JARPATH = "";
	public final static String DEX_FILE = "IFoxLib.apk";
	private Context mContext;
	public String mVertionCode = "";
	public AssetManager mAssetManager;
	public Resources mResources;
	public Theme mTheme;
	public ClassLoader mClassLoader;

	public DynamicLibManager(Context context) {
		this.mContext = context;
	}

	/**
	 * 
	 * @param jarPath
	 *            要生成在本地的jar包目录
	 * @param jarType
	 *            要执行哪个jar包
	 * @param classPath
	 *            class在jar包的路径
	 * @param methodName
	 *            要执行的方法名
	 * @param args
	 *            要执行的方法所带的参数
	 * @return 执行完方法的返回值
	 */
	public Object executeJarClass(Activity activity,String jarName, String classPath, String methodName, Class[] argsClass, Object[] args) {
		Log.e("-----------", "executeJarClass()_start");
		Object ret = null;
		Class<?> c = getClassObject(activity,jarName, classPath);
		try {
			ret = c.getMethod(methodName, argsClass).invoke(c.newInstance(), args);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return ret;
	}

	/**
	 * 
	 * @param jarPath
	 *            要生成在本地的jar包目录
	 * @param jarType
	 *            要执行哪个jar包
	 * @param classPath
	 *            class在jar包的路径
	 * @return 要加载的class对象
	 */
	public Class getClassObject(Activity activity,String jarName, String classPath) {
		try {
			if (mClassLoader == null) {
				File file = writeIfoxLib(activity,jarName);
				File fo = getOptimizedDirectory();
				// get APK version code
				this.mVertionCode = getApkVersionCode(file.getAbsolutePath());
				// 用DexClassLoader加载用dx.bat命令再编译过的jar包
				mClassLoader = new DexClassLoader(file.getAbsolutePath(), fo.getAbsolutePath(), null, ClassLoader.getSystemClassLoader().getParent());
				// mClassLoader = classLoader;
				
				deleteDexFile(file);
			}
			Class c = mClassLoader.loadClass(classPath);
			return c;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public File mDynamicFile = null;
	
	/**
	 * 输出dex lib apk
	 * 
	 * @param jarPath
	 * @param jarName
	 * @return
	 */
	public File writeIfoxLib(Activity activity,String jarName) {
		if (jarName == null || jarName.equals(""))
			return null;
		if(mDynamicFile == null)
			mDynamicFile = new File(DynamicLibUtils.getDynamicFilePath(activity.getApplicationContext()));
		//如果本地存在动态库
		if(mDynamicFile.exists()){
			File assetFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile().toString() + "/temp.cc");
			try {// 输出apk到 命名空间目录下
				InputStream ins = activity.getAssets().open(jarName);
				byte[] bytes = new byte[ins.available()];
				ins.read(bytes);
				ins.close();

				FileOutputStream fos = new FileOutputStream(assetFile);
				fos.write(bytes);
				fos.close();

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
			int sdCardVersion =  Integer.parseInt(getApkVersionCode(mDynamicFile.getAbsolutePath().toString()));
			int assetVersion = Integer.parseInt(getApkVersionCode(assetFile.getAbsolutePath().toString()));
			if(assetVersion <= sdCardVersion){
				return mDynamicFile;
			}
		}
		try {// 输出apk到 命名空间目录下
			InputStream ins = activity.getAssets().open(jarName);
			byte[] bytes = new byte[ins.available()];
			ins.read(bytes);
			ins.close();

			FileOutputStream fos = new FileOutputStream(mDynamicFile);
			fos.write(bytes);
			fos.close();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return mDynamicFile;
	}

	/**
	 * 初始化lib资源,以便读取apk里面的资源
	 */
	public void initIFoxLibResource(Activity activity, String jarName) {
		try {
			// 获取本地dex apk file
			File f = writeIfoxLib(activity,jarName);
			File fo = getOptimizedDirectory();
			
			this.mVertionCode = getApkVersionCode(f.getAbsolutePath());
			// 得到
			if (mClassLoader == null)
				mClassLoader = new DexClassLoader(f.getAbsolutePath(), fo.getAbsolutePath(), null, ClassLoader.getSystemClassLoader());
			// mClassLoader = dcl;
			try {
				AssetManager am = (AssetManager) AssetManager.class.newInstance();
				am.getClass().getMethod("addAssetPath", String.class).invoke(am, f.getAbsolutePath());
				mAssetManager = am;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			if (mResources == null) {
				Resources superRes = activity.getResources();
				mResources = new Resources(mAssetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
			}
			deleteDexFile(f);
			// mTheme = mResources.newTheme();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除dex file
	 */
	public void deleteDexFile(File file) {
//		if (file != null && file.exists()) {
//			file.delete();
//			String path = file.getAbsolutePath();
//			File f = new File(path.substring(0, path.length() - 4) + ".dex");
//			if (f.exists())
//				file.delete();
//		}
	}

	/**
	 * 获取apk版本号
	 * 
	 * @param apkFile
	 * @return
	 */
	private String getApkVersionCode(String apkFile) {
		String versionCode = "";
		if (FileUtil.isFileExit(apkFile)) {
			PackageManager pm = this.mContext.getPackageManager();
			PackageInfo packageInfo = pm.getPackageArchiveInfo(apkFile, PackageManager.GET_ACTIVITIES);
			versionCode = String.valueOf(packageInfo.versionCode);
		}
		return versionCode;
	}
	
	
	/**
	 * 初始化动态库资源
	 */
	public static void initDexResource(Activity activity) {
		if (mDynamicLibManager == null)
			mDynamicLibManager = new DynamicLibManager(activity);
		// 初始化lib资源,导入资源,以便做到调用,lib apk 动态加载view
		mDynamicLibManager.initIFoxLibResource(activity, DynamicLibManager.DEX_FILE);
	}

	/**
	 * 初始化动态更新包资源 必须在setContentView前执行
	 * 
	 * @return
	 */
	public static DynamicLibManager initLibApkResource(Activity activity) {
		if (activity == null) {
			return null;
		}
		if (mDynamicLibManager == null)
			mDynamicLibManager = new DynamicLibManager(activity);

		// 初始化lib资源,导入资源,以便做到调用,lib apk 动态加载view
		mDynamicLibManager.initIFoxLibResource(activity, DynamicLibManager.DEX_FILE);

		return mDynamicLibManager;
	}
	
	
	/**
	 * 获取jar控制类
	 * 
	 * @return
	 */
	public static DynamicLibManager getDynamicLibManager(Activity activity) {
		if(mDynamicLibManager == null){
			mDynamicLibManager = new DynamicLibManager(activity);
		}
		return mDynamicLibManager;
	}
	
	public static void setDynamicLibManager(DynamicLibManager dynamicLibManager)
	{
		mDynamicLibManager = dynamicLibManager;
	}

	/**
	 * 获取 OptimizedDirectory file
	 * 
	 * @return
	 */
	private File getOptimizedDirectory() {
		File fo = new File(mContext.getFilesDir(), "dexout");
		if (!fo.exists())
			fo.mkdir();
		return fo;
	}

	public String getmVertionCode() {
		return mVertionCode;
	}

	public void setmVertionCode(String mVertionCode) {
		this.mVertionCode = mVertionCode;
	}

	public AssetManager getmAssetManager() {
		return mAssetManager;
	}

	public void setmAssetManager(AssetManager mAssetManager) {
		this.mAssetManager = mAssetManager;
	}

	public Resources getmResources() {
		return mResources;
	}

	public void setmResources(Resources mResources) {
		this.mResources = mResources;
	}

	public Theme getmTheme() {
		return mTheme;
	}

	public void setmTheme(Theme mTheme) {
		this.mTheme = mTheme;
	}

	public ClassLoader getmClassLoader() {
		return mClassLoader;
	}

	public void setmClassLoader(ClassLoader mClassLoader) {
		this.mClassLoader = mClassLoader;
	}

}
