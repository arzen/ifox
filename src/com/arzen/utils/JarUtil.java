package com.arzen.utils;

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
import android.util.Log;
import dalvik.system.DexClassLoader;

public class JarUtil {
	private Context mContext;
	public String mVertionCode = "";

	public AssetManager mAssetManager;
	public Resources mResources;
	public Theme mTheme;
	public ClassLoader mClassLoader;

	public JarUtil(Context context) {
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
	public Object executeJarClass(String jarName, String classPath, String methodName, Class[] argsClass, Object[] args) {
		Log.e("-----------", "executeJarClass()_start");
		Object ret = null;
		Class<?> c = getClassObject(jarName, classPath);
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
	public Class getClassObject(String jarName, String classPath) {
		try {
			ClassLoader classLoader = null;
			if (mClassLoader == null) {
				File file = writeIfoxLib(jarName);
				File fo = getOptimizedDirectory();
				// get APK version code
				this.mVertionCode = getApkVersionCode(file.getAbsolutePath());
				// 用DexClassLoader加载用dx.bat命令再编译过的jar包
				classLoader = new DexClassLoader(file.getAbsolutePath(), fo.getAbsolutePath(), null, ClassLoader.getSystemClassLoader().getParent());
				mClassLoader = classLoader;
			} else {
				classLoader = mClassLoader;
			}
			// 可以在加载完的时候将生成在本地的.jar和.dex删除
			// file.delete();
			// file = new File(jarPath + "/" + jarName.substring(0,
			// jarName.length() - 4) + ".dex");
			// file.delete();
			Class c = classLoader.loadClass(classPath);
			return c;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 输出dex lib apk
	 * 
	 * @param jarPath
	 * @param jarName
	 * @return
	 */
	public File writeIfoxLib(String jarName) {
		if (jarName == null || jarName.equals(""))
			return null;

		File f = new File(mContext.getFilesDir(), "dex");
		if (!f.exists()) {
			f.mkdir();
		}

		f = new File(f, Integer.toHexString(jarName.hashCode()) + ".apk");
		if (!f.exists()) {// 输出apk到 命名空间目录下
			try {
				InputStream ins = mContext.getAssets().open(jarName);
				byte[] bytes = new byte[ins.available()];
				ins.read(bytes);
				ins.close();

				FileOutputStream fos = new FileOutputStream(f);
				fos.write(bytes);
				fos.close();

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return f;

		// try {
		// // 从assects中读取文件并放到jarPath目录
		// File file = new File(jarPath, jarName);
		// if (!file.exists()) {
		// file.createNewFile();
		// try {
		// InputStream in = context.getAssets().open(jarName);
		// BufferedInputStream bis = new BufferedInputStream(in);
		// FileOutputStream fos = new FileOutputStream(file);
		//
		// byte[] b = new byte[1024];
		// int len = 0;
		// while ((len = bis.read(b)) != -1) {
		// fos.write(b, 0, len);
		// }
		// fos.flush();
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// return file;
		// } catch (Exception e) {
		// // TODO: handle exception
		// }
		// return null;
	}

	// /**
	// * 初始化ifox lib资源
	// */
	// public void initIfoxLib(String jarName, String classPath) {
	// try {
	// // 从assects中读取文件并放到jarPath目录
	// File file = writeIfoxLib(jarName);
	// File fo = getOptimizedDirectory();
	// // 用DexClassLoader加载用dx.bat命令再编译过的jar包
	// DexClassLoader classLoader = new DexClassLoader(file.getAbsolutePath(),
	// fo.getAbsolutePath(), null, context.getClassLoader());
	//
	// cl = classLoader;
	// // get APK version code
	// this.verCode = getApkVersionCode(file.getPath());
	//
	// // 添加apk资源到包下
	// try {
	// AssetManager am = (AssetManager) AssetManager.class.newInstance();
	// am.getClass().getMethod("addAssetPath", String.class).invoke(am,
	// file.getAbsolutePath());
	//
	// asm = am;
	// } catch (Exception e) {
	// throw new RuntimeException(e);
	// }
	//
	// Resources superRes = context.getResources();
	//
	// Resources res = new Resources(asm, superRes.getDisplayMetrics(),
	// superRes.getConfiguration());
	//
	// thm = res.newTheme();
	// thm.setTo(context.getTheme());
	//
	// // 可以在加载完的时候将生成在本地的.jar和.dex删除
	// // file.delete();
	// // file = new File(jarPath + "/" + jarName.substring(0,
	// // jarName.length() - 4) + ".dex");
	// // file.delete();
	//
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// }
	// }

	/**
	 * 初始化lib资源,以便读取apk里面的资源
	 */
	public void initIFoxLibResource(Activity activity, String jarName) {
		try {
			// 获取本地dex apk file
			File f = writeIfoxLib(jarName);
			File fo = getOptimizedDirectory();
			// 得到
			DexClassLoader dcl = new DexClassLoader(f.getAbsolutePath(), fo.getAbsolutePath(), null, ClassLoader.getSystemClassLoader().getParent());
			mClassLoader = dcl;
			try {
				AssetManager am = (AssetManager) AssetManager.class.newInstance();
				am.getClass().getMethod("addAssetPath", String.class).invoke(am, f.getAbsolutePath());
				mAssetManager = am;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			Resources superRes = activity.getResources();

			mResources = new Resources(mAssetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());

			mTheme = mResources.newTheme();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
