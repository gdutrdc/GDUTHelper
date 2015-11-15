package com.rdc.gduthelper.app;

import android.app.Application;
import android.content.SharedPreferences;

import com.rdc.gduthelper.R;

/**
 * Created by seasonyuu on 15/8/28.
 */
public class GGApplication extends Application {
	public static String cookie;
	public static String userXh;
	public static String viewState;
	public static String userXm;
	private SharedPreferences sp;

	private static GGApplication mApplication;

	@Override
	public void onCreate() {
		super.onCreate();
		mApplication = this;
		sp = getSharedPreferences(getPackageName() + "_preferences", MODE_PRIVATE);
	}

	public static GGApplication getInstance() {
		return mApplication;
	}

	public boolean isUseDx() {
		return sp.getBoolean(getString(R.string.use_dx_key), false);
	}

	public void setZBText(String text) {
		SharedPreferences.Editor editor = sp.edit();
		if (text != null && text.length() > 0)
			editor.putString("ZBText", text);
		else
			editor.putString("ZBText", getString(R.string.default_zb_text));
		editor.commit();
	}

	public String getZBText() {
		String result;
		result = sp.getString("ZBText", null);
		if (result != null)
			return result;
		return getString(R.string.default_zb_text);
	}

	public boolean needRememberUser() {
		return sp.getBoolean("remember_user", true);
	}

	public void rememberUser(String userName, String password) {
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("remember_user_data", userName + ";" + password);
		editor.commit();
	}

	public String getRememberUser() {
		return sp.getString("remember_user_data", null);
	}

	public int getThemeId() {
		int themeId = Integer.parseInt(sp.getString("theme", "-1"));
		switch (themeId) {
			case 0:
				themeId = R.style.AppTheme_Blue;
				break;
			case 1:
				themeId = R.style.AppTheme_Pink;
				break;
		}
		return themeId;
	}

}
