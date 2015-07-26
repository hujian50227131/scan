package com.wx.scan.utils;

import java.util.List;
import java.util.UUID;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.wx.scan.App;

/** 手机信息工具类 */
public class AppUtil {
	/** imei */
	public static String imei;

	public static Context getContext() {
		return App.get();
	}

	/** 获取imei */
	public static String getImei() {
		if (!TextUtils.isEmpty(imei))
			return imei;

		PreferenceUtil pu = App.getPreferenceUtil();

		imei = pu.getString("LABRARY_APP_UTIL_IMEI", null);
		if (!TextUtils.isEmpty(imei))
			return imei;

		TelephonyManager tm = (TelephonyManager) getContext().getSystemService(
				Context.TELEPHONY_SERVICE);
		imei = tm.getDeviceId();
		if (!TextUtils.isEmpty(imei))
			return imei;

		imei = Secure.getString(getContext().getContentResolver(),
				Secure.ANDROID_ID);
		// android2.2或者是某些山寨手机会返回一个固定的值，弃用
		if (!TextUtils.isEmpty(imei) && !"9774d56d682e549c".equals(imei))
			imei = null;

		if (!TextUtils.isEmpty(imei))
			return imei;

		imei = UUID.randomUUID().toString();
		pu.setString("LABRARY_APP_UTIL_IMEI", imei);

		// return "866500026525744";
		return imei;
	}

	/** 版本名称 */
	public static String getVerName() {
		PackageInfo packageInfo = getPackageInfoSelf();
		if (packageInfo != null) {
			return packageInfo.versionName;
		}
		return null;
	}

	/** 版本号 */
	public static int getVerCode() {
		PackageInfo packageInfo = getPackageInfoSelf();
		if (packageInfo != null) {
			return packageInfo.versionCode;
		}
		return 0;
	}

	/** 获得自身的包信息 */
	public static PackageInfo getPackageInfoSelf() {

		try {
			return getContext().getPackageManager().getPackageInfo(
					getContext().getPackageName(), 0);
		} catch (NameNotFoundException e) {
			return null;
		}
	}

	/** 获取手机型号，如 "HTC Desire"等 */
	public static String getModel() {
		return Build.MODEL;
	}

	/** 获取系统版本号 */
	public static int getSDK() {
		return Build.VERSION.SDK_INT;
	}

	/** 获取包名 */
	public static String getPackageName() {
		return getContext().getPackageName();
	}

	/** 判断是否有该权限 */
	public static boolean hasPermission(String permission) {
		if (TextUtils.isEmpty(permission)) {
			return false;
		}
		PackageManager pm = getContext().getPackageManager();
		String pkgName = getContext().getPackageName();
		return PackageManager.PERMISSION_GRANTED == pm.checkPermission(
				permission, pkgName);
	}

	/** 是否安装了此应用 */
	public static boolean isInstalled(String packageName) {
		try {
			return getContext().getPackageManager().getPackageInfo(packageName,
					0) != null;
		} catch (NameNotFoundException e) {
		}
		return false;
	}

	/** 打印所有已安装包名 */
	public static void logAllPackage() {
		List<PackageInfo> packageInfoList = getContext().getPackageManager()
				.getInstalledPackages(0); // 返回已安装的包信息列表
		for (int i = 0; i < packageInfoList.size(); i++) {
			PackageInfo info = packageInfoList.get(i);
			LOG.log("logAllPackage:  ",
					info.packageName
							+ "======   "
							+ info.applicationInfo.loadLabel(
									getContext().getPackageManager())
									.toString());
		}
	}

	/** app是否在后台运行 */
	public static boolean isBackgroundRunning(Context context,
			String processName) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		KeyguardManager keyguardManager = (KeyguardManager) context
				.getSystemService(Context.KEYGUARD_SERVICE);
		if (activityManager == null)
			return false;
		List<ActivityManager.RunningAppProcessInfo> processList = activityManager
				.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo process : processList) {
			if (process.processName.startsWith(processName)) {
				boolean isBackground = process.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND
						&& process.importance != RunningAppProcessInfo.IMPORTANCE_VISIBLE;
				boolean isLockedState = keyguardManager
						.inKeyguardRestrictedInputMode();
				if (isBackground || isLockedState)
					return true;
				else
					return false;
			}
		}
		return false;
	}

}
