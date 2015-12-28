package com.rdc.gduthelper.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.bean.WidgetConfigs;

import java.io.IOException;

/**
 * Created by seasonyuu on 15/12/28.
 */
public class Settings {
	public static final String REMEMBER_USER = "remember_user";
	public static final String ZB_TEXT = "ZBText";
	public static final String REMEMBER_USER_DATA = "remember_user_data";
	public static final String THEME = "theme";
	public static final String WIDGET_CONFIGS = "widget_configs";

	private SharedPreferences mSharedPreferences;

	public Settings(Context context) {
		mSharedPreferences = context
				.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
	}


	public boolean isUseDx() {
		return mSharedPreferences.getBoolean(GDUTHelperApp.getInstance().getString(R.string.use_dx_key), false);
	}

	public void setZBText(String text) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		if (text != null && text.length() > 0)
			editor.putString(ZB_TEXT, text);
		else
			editor.putString(ZB_TEXT, GDUTHelperApp.getInstance().getString(R.string.default_zb_text));
		editor.apply();
	}

	public String getZBText() {
		String result;
		result = mSharedPreferences.getString(ZB_TEXT, null);
		if (result != null)
			return result;
		return GDUTHelperApp.getInstance().getString(R.string.default_zb_text);
	}

	public boolean needRememberUser() {
		return mSharedPreferences.getBoolean(REMEMBER_USER, true);
	}

	public void rememberUser(String userName, String password) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putString(REMEMBER_USER_DATA, userName + ";" + password);
		editor.apply();
	}

	public String getRememberUser() {
		return mSharedPreferences.getString(REMEMBER_USER_DATA, null);
	}

	public int getThemeId() {
		int themeId = mSharedPreferences.getInt(THEME, -1);
		switch (themeId) {
			case 0:
				themeId = R.style.AppTheme_Blue;
				break;
			case 1:
				themeId = R.style.AppTheme_Pink;
				break;
			default:
				themeId = R.style.AppTheme_Blue;
				break;
		}
		return themeId;
	}

	public WidgetConfigs getAppWidgetConfigs() {
		String data = mSharedPreferences.getString(WIDGET_CONFIGS, null);
		if (data == null)
			return null;
		else {
			try {
				return (WidgetConfigs) SerializeUtil.deSerialization(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public void saveAppWidgetConfigs(WidgetConfigs configs) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		try {
			editor.putString(WIDGET_CONFIGS, SerializeUtil.serialize(configs));
		} catch (IOException e) {
			e.printStackTrace();
		}
		editor.apply();
	}

	public String getString(String key, String defaultValue) {
		return mSharedPreferences.getString(key, defaultValue);
	}

	public int getInt(String key, int defaultValue) {
		return mSharedPreferences.getInt(key, defaultValue);
	}

	public void putInt(String key, int value) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putInt(key, value);
		editor.apply();
	}
}
