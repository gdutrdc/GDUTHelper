package com.seasonyuu.getgrade.app;

import android.app.Application;
import android.content.SharedPreferences;

/**
 * Created by seasonyuu on 15/8/28.
 */
public class GGApplication extends Application {
	public static String cookie;
	public static String userName;
	public static String viewState;
	private SharedPreferences sp;

	@Override
	public void onCreate() {
		super.onCreate();
		sp = getSharedPreferences("settings", MODE_PRIVATE);
	}


}
