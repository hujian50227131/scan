package com.wx.scan.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

import android.util.Log;

/** 调试日志 */
public class LOG {
	/** 调试日志开关 */
	public static final boolean DEBUG = true;
	public static final String TAG = "hj";

	public static void e(String s) {
		if (!DEBUG) {
			return;
		}
		Log.e(TAG, "" + s);
	}

	/** 获得异常信息 */
	public static String getStackTrace(Throwable t) {
		if (t == null) {
			return "Exception message is null";
		}
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		t.printStackTrace(pw);
		pw.flush();
		sw.flush();
		return sw.toString();
	}

	public static void log(String teg, String s) {
		e(teg + "    --    " + s);
	}

	public static void test(String s) {
		log("TEST", s);
	}
}
