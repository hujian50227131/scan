package com.wx.scan.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.wx.scan.App;

@SuppressLint("CommitPrefEdits")
public class PreferenceUtil {

	private SharedPreferences mSharedPreferences;

	private Editor mEditor;

	public static PreferenceUtil get() {
		return App.getPreferenceUtil();
	}

	public PreferenceUtil(Context c, String name) {
		mSharedPreferences = c.getSharedPreferences(name, 0);
		mEditor = mSharedPreferences.edit();
	}

	public void onDestroy() {
		mSharedPreferences = null;
		mEditor = null;
	}

	public void setLong(String key, long l) {
		mEditor.putLong(key, l);
		mEditor.commit();
	}

	public long getLong(String key, long defaultlong) {
		return mSharedPreferences.getLong(key, defaultlong);
	}

	public void setBoolean(String key, boolean value) {
		mEditor.putBoolean(key, value);
		mEditor.commit();
	}

	public boolean getBoolean(String key, Boolean defaultboolean) {
		return mSharedPreferences.getBoolean(key, defaultboolean);
	}

	public void setInt(String key, int value) {
		mEditor.putInt(key, value);
		mEditor.commit();
	}

	public int getInt(String key, int defaultInt) {
		return mSharedPreferences.getInt(key, defaultInt);
	}

	public String getString(String key, String defaultInt) {
		return mSharedPreferences.getString(key, defaultInt);
	}

	public void setString(String key, String value) {
		mEditor.putString(key, value);
		mEditor.commit();
	}

	public void remove(String key) {
		mEditor.remove(key);
		mEditor.commit();
	}

}
