package com.wx.scan.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

@SuppressWarnings("unused")
public class SimUtil {
	private static final String SPLIT = ",";

	public static String getImsiString(Context context) {
		String imsi = getMtkDoubleSim(context);
		LOG.test("getMtkDoubleSim:imsi====>   " + imsi);
		if (!TextUtils.isEmpty(imsi) && imsi.length() > 3) {
			return imsi;
		}
		imsi = getMtkSecondDoubleSim(context);
		LOG.test("getMtkSecondDoubleSim====>   " + imsi);
		if (!TextUtils.isEmpty(imsi) && imsi.length() > 3) {
			return imsi;
		}
		imsi = getSpreadDoubleSim(context);
		LOG.test("getSpreadDoubleSim====>   " + imsi);
		if (!TextUtils.isEmpty(imsi) && imsi.length() > 3) {
			return imsi;
		}
		imsi = getQualcommDoubleSim(context);
		LOG.test("getQualcommDoubleSim====>   " + imsi);
		if (!TextUtils.isEmpty(imsi) && imsi.length() > 3) {
			return imsi;
		}
		imsi = getIMSI(context);
		LOG.test("getIMSI====>   " + imsi);
		if (!TextUtils.isEmpty(imsi) && imsi.length() > 3) {
			return imsi;
		}
		return "null";
	}

	public static String getIMSI(Context context) {
		final TelephonyManager mTelephonyMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = mTelephonyMgr.getSubscriberId();
		return imsi;
	}

	private static String getMtkDoubleSim(Context context) {
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			Class<?> c = Class.forName("com.android.internal.telephony.Phone");
			int simId_1 = 0;
			int simId_2 = 1;
			Method m1 = TelephonyManager.class.getDeclaredMethod(
					"getDeviceIdGemini", int.class);
			String imei1 = (String) m1.invoke(tm, simId_1);
			String imei2 = (String) m1.invoke(tm, simId_2);

			if (!TextUtils.isEmpty(imei1) && !TextUtils.isEmpty(imei2)) {
				return imei1 + SPLIT + imei2;
			} else {
				return null;
			}
		} catch (Exception e) {
			LOG.test("getMtkDoubleSim  e: " + LOG.getStackTrace(e));
		}
		return null;
	}

	private static String getMtkSecondDoubleSim(Context context) {
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			Class<?> c = Class.forName("com.android.internal.telephony.Phone");
			int simId_1 = 0;
			int simId_2 = 1;
			Method mx = TelephonyManager.class.getMethod("getDefault",
					int.class);
			TelephonyManager tm1 = (TelephonyManager) mx.invoke(tm, simId_1);
			TelephonyManager tm2 = (TelephonyManager) mx.invoke(tm, simId_2);
			String imei1 = tm1.getDeviceId();
			String imei2 = tm2.getDeviceId();
			if (!TextUtils.isEmpty(imei1) && !TextUtils.isEmpty(imei2)) {
				return imei1 + SPLIT + imei2;
			} else {
				return null;
			}
		} catch (Exception e) {
			LOG.test("getMtkSecondDoubleSim  e: " + LOG.getStackTrace(e));
		}
		return null;
	}

	private static String getSpreadDoubleSim(Context context) {
		try {
			Class<?> c = Class
					.forName("com.android.internal.telephony.PhoneFactory");
			Method m = c.getMethod("getServiceName", String.class, int.class);
			String spreadTmService = (String) m.invoke(c,
					Context.TELEPHONY_SERVICE, 1);
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String imei1 = tm.getDeviceId();
			TelephonyManager tm1 = (TelephonyManager) context
					.getSystemService(spreadTmService);
			String imei2 = tm1.getDeviceId();

			if (!TextUtils.isEmpty(imei1) && !TextUtils.isEmpty(imei2)) {
				return imei1 + SPLIT + imei2;
			} else {
				return null;
			}
		} catch (Exception e) {
			LOG.test("getSpreadDoubleSim  e: " + LOG.getStackTrace(e));
		}
		return null;
	}

	public static String getQualcommDoubleSim(Context context) {
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			Class<?> cx = Class
					.forName("android.telephony.MSimTelephonyManager");
			Object obj = context.getSystemService("phone_msim");
			int simId_1 = 0;
			int simId_2 = 1;
			Method md = cx.getMethod("getDeviceId", int.class);
			String imei1 = (String) md.invoke(obj, simId_1);
			String imei2 = (String) md.invoke(obj, simId_2);

			if (!TextUtils.isEmpty(imei1) && !TextUtils.isEmpty(imei2)) {
				return imei1 + SPLIT + imei2;
			} else {
				return null;
			}
		} catch (Exception e) {
			LOG.test("getQualcommDoubleSim  e: " + LOG.getStackTrace(e));
		}
		return null;
	}
}
