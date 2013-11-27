package com.arzen.ifox.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class CommonUtil {
	/**
	 * Check whether the specified permission is granted to the current package.
	 * 
	 * @param context
	 * @param permissionName
	 *            The permission.
	 * @return True if granted, false otherwise.
	 */
	public static boolean checkPermission(Context context, String permissionName) {
		PackageManager packageManager = context.getPackageManager();
		String pkgName = context.getPackageName();
		return packageManager.checkPermission(permissionName, pkgName) == PackageManager.PERMISSION_GRANTED;
	}


	public static boolean isZero(String id) {
		for (int i = 0; i < id.length(); i++) {
			char index = id.charAt(i);
			if (index != '0')
				return false;
		}
		return true;
	}
	/**
	 * 获取当前应用包名
	 * @param context
	 * @return
	 */
	public static String getPackageName(Context context){
		String packageName = "";
		try {
			PackageInfo info =  context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			packageName = info.packageName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return packageName;
	}
}
