package com.rdc.gduthelper.app;

import android.app.Application;

import com.rdc.gduthelper.bean.Lesson;
import com.rdc.gduthelper.utils.Settings;

import java.util.ArrayList;

/**
 * Created by seasonyuu on 15/8/28.
 */
public class GDUTHelperApp extends Application {
	public static String cookie;
	public static String userXh;
	public static String viewState;
	public static String userXm;

	private static GDUTHelperApp mApplication;

	private static ArrayList<Lesson> evaluationList;

	private static Settings mSettings;

	@Override
	public void onCreate() {
		super.onCreate();
		mApplication = this;
		mSettings = new Settings(this);
	}

	public static ArrayList<Lesson> getEvaluationList() {
		return evaluationList;
	}

	public static void setEvaluationList(ArrayList<Lesson> list) {
		evaluationList = list;
	}

	public static GDUTHelperApp getInstance() {
		return mApplication;
	}

	public static Settings getSettings() {
		return mSettings;
	}

	public static boolean isLogin() {
		return userXh != null && cookie != null;
	}
}
