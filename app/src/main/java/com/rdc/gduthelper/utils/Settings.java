package com.rdc.gduthelper.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.bean.WidgetConfigs;
import com.rdc.gduthelper.utils.appwidget.WidgetConfigProvider;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by seasonyuu on 15/12/28.
 */
public class Settings {
	public static final String REMEMBER_USER = "remember_user";
	public static final String ZB_TEXT = "ZBText";
	public static final String REMEMBER_USER_DATA = "remember_user_data";
	public static final String WIDGET_CONFIGS = "widget_configs";
	public static final String SCHEDULE_CHOOSE_TERM = "schedule_choose_term";
	public static final String SCHEDULE_FIRST_WEEK = "schedule_first_week";
	public static final String SCHEDULE_CURRENT_WEEK = "schedule_current_week";
	public static final String SCHEDULE_CARD_COLORS = "schedule_card_colors";
	public static final String SCHEDULE_BACKGROUND = "schedule_background";
	public static final String COOKIE = "cookie";

	private SharedPreferences mSharedPreferences;

	public Settings(Context context) {
		mSharedPreferences = context.getSharedPreferences(
				context.getPackageName() + "_preferences", Context.MODE_MULTI_PROCESS);
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

	public WidgetConfigs getAppWidgetConfigs(Context context) {
		Uri uri = Uri.parse(WidgetConfigProvider.CONFIG_CONTENT_URI);
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
		WidgetConfigs configs = new WidgetConfigs();
		if (cursor == null)
			return null;
		while (cursor.moveToNext()) {
			configs.putConfig(cursor.getInt(0), cursor.getString(1));
		}
		cursor.close();
		return configs;
	}

	public void saveAppWidgetConfigs(Context context, WidgetConfigs configs) {
		Uri uri = Uri.parse(WidgetConfigProvider.CONFIG_CONTENT_URI);

		for (Integer key : configs.keySet()) {
			ContentValues contentValues = new ContentValues();
			contentValues.put("widget_id", key);
			contentValues.put("selection", configs.get(key));

			context.getContentResolver().insert(uri, contentValues);
		}

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

	public String getScheduleChooseTerm() {
		return mSharedPreferences.getString(SCHEDULE_CHOOSE_TERM, null);
	}

	public void setScheduleChooseTerm(String term) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putString(SCHEDULE_CHOOSE_TERM, term);
		editor.apply();
	}

	public void setScheduleFirstWeek(Calendar calendar) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		if (calendar.get(Calendar.DAY_OF_WEEK) != 1)
			calendar.set(Calendar.DAY_OF_WEEK, 1);
		editor.putString(SCHEDULE_FIRST_WEEK, new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
				.format(calendar.getTime()));
		editor.apply();
	}

	public Calendar getScheduleFirstWeek() {
		Calendar calendar = Calendar.getInstance();
		String firstWeekDate = mSharedPreferences.getString(SCHEDULE_FIRST_WEEK, null);
		if (firstWeekDate == null)
			return null;
		else {
			try {
				Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(firstWeekDate);
				calendar.setTime(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return calendar;
	}

	public void setScheduleCurrentWeek(String currentWeek) {
		int week = Integer.parseInt(currentWeek);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.WEEK_OF_YEAR, -week + 1);
		setScheduleFirstWeek(calendar);
	}

	public String getScheduleCurrentWeek() {
		Calendar calendar = getScheduleFirstWeek();
		if (calendar == null)
			return null;
		int firstWeek = calendar.get(Calendar.WEEK_OF_YEAR);
		int currentWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR) - firstWeek + 1;
		if (currentWeek < 0) {
			//小于0说明第一周在去年
			calendar.add(Calendar.YEAR, -1);
			currentWeek = currentWeek + calendar.getMaximum(Calendar.WEEK_OF_YEAR) - 1;
		}
		return currentWeek + "";
	}

	public void setScheduleCardColors(String colors) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putString(SCHEDULE_CARD_COLORS, colors);
		editor.apply();
	}

	public String getScheduleCardColors() {
		return mSharedPreferences.getString(SCHEDULE_CARD_COLORS, null);
	}

	public String getCookie() {
		return mSharedPreferences.getString(COOKIE, null);
	}

	public void setCookie(String cookie) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putString(COOKIE, cookie);
		editor.apply();
	}
}
