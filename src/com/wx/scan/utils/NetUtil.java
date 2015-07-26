package com.wx.scan.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.wx.scan.App;

public class NetUtil {

	public static Context getContext() {
		return App.get();
	}

	/** 判断是否有网络连接 */
	public static boolean isNetworkConnected() {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) getContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (mNetworkInfo != null) {
			return mNetworkInfo.isAvailable();
		}
		return false;
	}

	/** 判断WIFI网络是否可用 */
	public static boolean isWifiConnected() {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) getContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWiFiNetworkInfo = mConnectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (mWiFiNetworkInfo != null) {
			return mWiFiNetworkInfo.isAvailable();
		}
		return false;
	}

	/** 判断移动网络是否可用 */
	public static boolean isMobileConnected() {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) getContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mMobileNetworkInfo = mConnectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mMobileNetworkInfo != null) {
			return mMobileNetworkInfo.isAvailable();
		}
		return false;
	}

	/** 获取当前网络连接的类型信息 */
	public static int getNetType() {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) getContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
			return mNetworkInfo.getType();
		}
		return -1;
	}

	/** 打开网络设置 */
	public static void startNetSetting(Context context) {
		Intent intent = null;
		if (android.os.Build.VERSION.SDK_INT > 10) {
			intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
		} else {
			intent = new Intent();
			ComponentName component = new ComponentName("com.android.settings",
					"com.android.settings.WirelessSettings");
			intent.setComponent(component);
			intent.setAction("android.intent.action.VIEW");
		}
		context.startActivity(intent);
	}

	/** 获取IP地址 */
	public static String getIP() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (Exception ex) {
			LOG.test(LOG.getStackTrace(ex));
		}
		return null;
	}

	//
	// public String getLocalIpAddress() {
	//
	//

	//
	//
	// 16 }

	private static final String NETWORK_TYPE_LTE = "LTE"; // 4G
	private static final String NETWORK_TYPE_WIFI = "WIFI";// WIFI
	private static final String NETWORK_TYPE_GPRS = "GPRS"; // 2G
	private static final String NETWORK_TYPE_IDEN = "IDEN"; // 2G
	private static final String NETWORK_TYPE_EDGE = "EDGE"; // 2.5G
	private static final String NETWORK_TYPE_UMTS = "UMTS"; // 3G
	private static final String NETWORK_TYPE_EVDO_0 = "EVDO_0";// 3G
	private static final String NETWORK_TYPE_HSPA = "HSPA"; // 3G->4G
	private static final String NETWORK_TYPE_HSUPA = "HSUPA"; // 3G->4G
	private static final String NETWORK_TYPE_HSDPA = "HSDPA"; // 3G->4G
	private static final String NETWORK_TYPE_EVDO_A = "EVDO_A";// 3G->4G
	private static final String NETWORK_TYPE_1xRTT = "1xRTT"; // CDMA2000 1xRTT
	private static final String NETWORK_TYPE_CDMA = "CDMA"; // CDMAONE 2G
	private static final String NETWORK_TYPE_HSPAP = "HSPAP";
	private static final String NETWORK_TYPE_EHRPD = "EHRPD";
	private static final String NETWORK_TYPE_EVDO_B = "EVDO_B";
	private static final String NETWORK_TYPE_UNKNOWN = "UNKNOWN";
	private static final String NETWORK_TYPE_NONE = "NONE";

	/** 网络类型（文字形式） */
	public static String getNetTypeString() {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) getContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			if (info.getType() == ConnectivityManager.TYPE_WIFI) {
				return NETWORK_TYPE_WIFI;
			}
			int type = info.getSubtype();
			switch (type) {
			case TelephonyManager.NETWORK_TYPE_1xRTT:
				return NETWORK_TYPE_1xRTT;
			case TelephonyManager.NETWORK_TYPE_CDMA:
				return NETWORK_TYPE_CDMA;
			case TelephonyManager.NETWORK_TYPE_EDGE:
				return NETWORK_TYPE_EDGE;
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
				return NETWORK_TYPE_EVDO_0;
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
				return NETWORK_TYPE_EVDO_A;
			case TelephonyManager.NETWORK_TYPE_GPRS:
				return NETWORK_TYPE_GPRS;
			case TelephonyManager.NETWORK_TYPE_HSDPA:
				return NETWORK_TYPE_HSDPA;
			case TelephonyManager.NETWORK_TYPE_HSPA:
				return NETWORK_TYPE_HSPA;
			case TelephonyManager.NETWORK_TYPE_HSUPA:
				return NETWORK_TYPE_HSUPA;
			case TelephonyManager.NETWORK_TYPE_IDEN:
				return NETWORK_TYPE_IDEN;
			case TelephonyManager.NETWORK_TYPE_UMTS:
				return NETWORK_TYPE_UMTS;
			case TelephonyManager.NETWORK_TYPE_HSPAP:
				return NETWORK_TYPE_HSPAP;
			case TelephonyManager.NETWORK_TYPE_EHRPD:
				return NETWORK_TYPE_EHRPD;
			case TelephonyManager.NETWORK_TYPE_LTE:
				return NETWORK_TYPE_LTE;
			case TelephonyManager.NETWORK_TYPE_EVDO_B:
				return NETWORK_TYPE_EVDO_B;
			case TelephonyManager.NETWORK_TYPE_UNKNOWN:
				return NETWORK_TYPE_UNKNOWN;
			default:
				return NETWORK_TYPE_UNKNOWN;
			}
		} else {
			return NETWORK_TYPE_NONE;
		}
	}

}
