package com.arzen.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.arzen.ifox.iFox;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import dalvik.system.DexClassLoader;

public class JarUtil {
	private Context context;
	public String verCode=""; 

	public JarUtil(Context context) {  
	    this.context = context;  
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
	public Object executeJarClass(String jarPath, String jarName,  
	        String classPath, String methodName, Object... args) {  
	    Log.e("-----------", "executeJarClass()_start");  
	    Object ret = null;  
	    Class c = getClassObject(jarPath, jarName, classPath);  
	    Class[] argsClass = new Class[args.length];  
	    for (int i = 0; i < args.length; i++) {  

	        argsClass[i] = args[i].getClass();  
	    }  
	    try {  
	        ret = c.getMethod(methodName, argsClass).invoke(c.newInstance(),  
	                args);  
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
	public Class getClassObject(String jarPath, String jarName, String classPath) {  
	    try {  
	        // 从assects中读取文件并放到jarPath目录  
	        File file = new File(jarPath, jarName);  
	        if (!file.exists()) {  
	            file.createNewFile();  
	        }  
	        try {  
	            InputStream in = context.getAssets().open(jarName);  
	            BufferedInputStream bis = new BufferedInputStream(in);  
	            FileOutputStream fos = new FileOutputStream(file);  

	            byte[] b = new byte[1024];  
	            int len = 0;  
	            while ((len = bis.read(b)) != -1) {  
	                fos.write(b, 0, len);  
	            }  
	            fos.flush();  

	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  

	        // 用DexClassLoader加载用dx.bat命令再编译过的jar包  
	        DexClassLoader classLoader = new DexClassLoader(file.getPath(),  
	                jarPath, null, ClassLoader.getSystemClassLoader()  
	                        .getParent());  
	        //get APK version code
	        this.verCode = getApkVersionCode(file.getPath());
	         // 可以在加载完的时候将生成在本地的.jar和.dex删除  
	         file.delete();  
	         file = new File(jarPath + "/"  
	                 + jarName.substring(0, jarName.length() - 4) + ".dex");  
	         file.delete();  

	         Class c = classLoader.loadClass(classPath);  
	         return c;  
	     } catch (Exception ex) {  
	         ex.printStackTrace();  
	     }  
	     return null;  
	 }
	
	private String getApkVersionCode(String apkFile) {
		String versionCode = "";
		if (FileUtil.isFileExit(apkFile)) {
			PackageManager pm = this.context.getPackageManager();
			PackageInfo packageInfo = pm.getPackageArchiveInfo(apkFile, PackageManager.GET_ACTIVITIES);
			versionCode = String.valueOf(packageInfo.versionCode);
		}
		return versionCode;
	}
	
}
