package com.arzen.ifox.utils;

import java.io.File;

import android.content.Context;

import com.arzen.ifox.iFox;
import com.encore.libs.http.download.DownloadTask;
import com.encore.libs.http.download.DownloadTask.OnDownloadListener;
import com.encore.libs.utils.Log;
import com.encore.libs.utils.NetWorkUtils;

/**
 * 动态库管理类
 * 
 * @author Encore.liang
 * 
 */
public class DynamicLibUtils {

	public static final String TAG = "DynamicLibUtils";

	/**
	 * sdcard 下目录
	 */
	public static final String DYNAMIC_DECRTOY = "dynamic";

	/**
	 * 判断存储空间是否大于这个值才能下载
	 */
	public static final int mMinSize = 10; // mb

	/**
	 * 下载动态库
	 * 
	 * @param downloadUrl
	 */
	public static void downloadNewDynamicLib(Context context, String downloadUrl) {
		boolean isHasSdCard = false;

		long sdcardFreeSize = 0; // sdCard剩余空间
		long systemFreeSize = 0; // 系统剩余空间
		// 存在sdCard
		if (CommonUtil.externalMemoryAvailable()) {
			sdcardFreeSize = CommonUtil.getSDFreeSize();
			Log.d(TAG, "sdcardFreeSize:" + sdcardFreeSize + "MB");
			isHasSdCard = true;
		}
		systemFreeSize = CommonUtil.readSystemFreeSize();
		Log.d(TAG, "systemFreeSize:" + systemFreeSize + "MB");
		String downloadPathString = null;
		if (isHasSdCard && sdcardFreeSize >= mMinSize) { // sdCard剩余空间大于10MB 并存在sdcard
			downloadPathString = getSdCardDynamicFilePath(context);
		} else if (systemFreeSize >= mMinSize) {
			downloadPathString = getSystemDynamicFilePathString(context);
		}
		
//		downloadUrl = "http://101.199.109.89/wsdl35.yunpan.cn/share.php?method=Share.download&fhash=dbd866ce7748ebe00331d420e2a02fd67ffe4475&xqid=282875528&fname=iFoxLib.apk&fsize=1962686&nid=13859665053993982&cqid=c3032dc23be3304e81d31956406904bb&st=d1ded37a0faf6067a92da11e45084caf&e=1386139357&dt=35.d31f20a51877bff73830105e8dc766f3";
//		downloadUrl = "";
		//当前是wifi环境,并且url等不能为空
		if (NetWorkUtils.isWifiConnected(context) 
				&& downloadUrl != null && !downloadUrl.equals("") 
				&& downloadPathString != null && !downloadUrl.equals("")) {
			
			File file = new File(downloadPathString);
			if(file.exists()){
				file.delete();
			}
			DownloadTask downloadTask = new DownloadTask(downloadUrl, downloadPathString); //下载
			downloadTask.setOnDownloadListener(new OnDownloadListener() {
				
				@Override
				public void onDownloadSuccess() {
					// TODO Auto-generated method stub
					Log.d(TAG, "onDownloadSuccess()");
				}
				
				@Override
				public void onDownloadStop() {
					// TODO Auto-generated method stub
					Log.d(TAG, "onDownloadStop()");
				}
				
				@Override
				public void onDownloadStart() {
					// TODO Auto-generated method stub
					Log.d(TAG, "onDownloadStart()");
				}
				
				@Override
				public void onDownloadProgress(int progress) {
					// TODO Auto-generated method stub
//					Log.d(TAG, "onDownloadProgress() progress:" + progress);
				}
				
				@Override
				public void onDownloadPrepare() {
					// TODO Auto-generated method stub
					Log.d(TAG, "onDownloadPrepare()");
				}
				
				@Override
				public void onDownloadError(int arg0) {
					// TODO Auto-generated method stub
					Log.d(TAG, "onDownloadError() " + arg0);
				}
			});
			downloadTask.startDownload();
		}
		
		Log.d(TAG, "downloadPath:" + downloadPathString + " downloadUrl:" + downloadUrl);
	}

	/**
	 * 获取动态库文件位置
	 * 
	 * @return
	 */
	public static String getDynamicFilePath(Context context) {
		boolean isExistsDynamicLib = false; // 是否存在动态库
		String dynamicFilePath = null;

		// String dynamicName = Integer.toHexString(iFox.DEX_FILE.hashCode());
		// File sdCardFile = CommonUtil.getDiskCacheDir(context,
		// DYNAMIC_DECRTOY);
		String path = getSdCardDynamicFilePath(context);// 先判断SdCard下是否有这个动态库文件
		File sdCardFile = new File(path);
		File SystemFilePath = null;
		// sdCard下存在当前动态库
		if (sdCardFile.exists()) {
			isExistsDynamicLib = true;
			dynamicFilePath = sdCardFile.getAbsolutePath();
		} else {
			// 如果不存在检查系统空间是否存在当前包
			SystemFilePath = new File(getSystemDynamicFilePathString(context));
			if (SystemFilePath.exists()) {
				isExistsDynamicLib = true;
				dynamicFilePath = SystemFilePath.getAbsolutePath(); // 得到路径
			}
		}
		// 如果存在动态库
		if (isExistsDynamicLib) {
			return dynamicFilePath;
		} else { // 不存在
			if (CommonUtil.externalMemoryAvailable()) { // 判断是否有sdcard //如果有
				return sdCardFile.getAbsolutePath();
			} else {
				return SystemFilePath == null ? null : SystemFilePath.getAbsolutePath();
			}
		}
	}

	/**
	 * 获取sdcard 动态库下文件名
	 * 
	 * @param context
	 * @return
	 */
	public static String getSdCardDynamicFilePath(Context context) {
		String dynamicName = Integer.toHexString(iFox.DEX_FILE.hashCode()) + ".apk";
		File sdCardFile = CommonUtil.getDiskCacheDir(context, DYNAMIC_DECRTOY); // 先判断SdCard下是否有这个动态库文件
		String path = sdCardFile.getAbsolutePath() + File.separator + dynamicName;
		return path;
	}

	/**
	 * 获取系统动态路路径
	 * 
	 * @return
	 */
	public static String getSystemDynamicFilePathString(Context context) {
		String dynamicName = Integer.toHexString(iFox.DEX_FILE.hashCode()) + ".apk";
		// 如果不存在检查系统空间是否存在当前包
		File SystemFilePath = new File(context.getFilesDir(), "dex");

		if (!SystemFilePath.exists()) {
			SystemFilePath.mkdir();
		}
		SystemFilePath = new File(SystemFilePath, dynamicName); // 新的动态库位置
		return SystemFilePath.getAbsolutePath(); // 得到路径
	}
}
