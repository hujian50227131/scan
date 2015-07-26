package com.wx.scan;

import android.app.Application;

import com.wx.scan.utils.PreferenceUtil;

public class App extends Application {
	public static App app;
	public static PreferenceUtil pu;

	public void onCreate() {
		//908
		app = this;
		pu = new PreferenceUtil(this, "SCAN_PRDFERENCE_NAME");
	}

	public static App get() {
		return app;
	}

	public static PreferenceUtil getPreferenceUtil() {
		return pu;
	} 
}
