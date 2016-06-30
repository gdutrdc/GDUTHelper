package com.rdc.gduthelper.utils.settings;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by seasonyuu on 15/12/28.
 */
public class Settings {
	public static final String REMEMBER_USER_KEY = GDUTHelperApp.getInstance().getString(R.string.remember_user_key);
	public static final String ZB_TEXT_KEY = GDUTHelperApp.getInstance().getString(R.string.zb_text_key);
	public static final String REMEMBER_USER_DATA_KEY = GDUTHelperApp.getInstance().getString(R.string.remember_user_data_key);
	public static final String SCHEDULE_BACKGROUND_KEY = GDUTHelperApp.getInstance().getString(R.string.schedule_background_key);
	public static final String COOKIE = "cookie";
	public static final String USE_DX_KEY = GDUTHelperApp.getInstance().getString(R.string.use_dx_key);

	private SharedPreferences mSharedPreferences;

	public Settings(Context context) {
		mSharedPreferences = context.getSharedPreferences(
				context.getPackageName() + "_preferences", Context.MODE_MULTI_PROCESS);
	}


	public boolean isUseDx() {
		return mSharedPreferences.getBoolean(USE_DX_KEY, false);
	}

	public void setUseDx(boolean enable) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putBoolean(USE_DX_KEY, enable);
		GDUTHelperApp.cookie = null;
		editor.apply();
	}

	public void setZBText(String text) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		if (text != null && text.length() > 0)
			editor.putString(ZB_TEXT_KEY, text);
		else
			editor.putString(ZB_TEXT_KEY, GDUTHelperApp.getInstance().getString(R.string.default_zb_text));
		editor.apply();
	}

	public String getZBText() {
		String result;
		result = mSharedPreferences.getString(ZB_TEXT_KEY, null);
		if (result != null)
			return result;
		return GDUTHelperApp.getInstance().getString(R.string.default_zb_text);
	}

	public boolean needRememberUser() {
		return mSharedPreferences.getBoolean(REMEMBER_USER_KEY, true);
	}

	public void setNeedRememberUser(boolean isNeed) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putBoolean(REMEMBER_USER_KEY, isNeed);
		if (!isNeed) {
			String user = getRememberUser();
			if (user != null)
				rememberUser(user.split(":")[0], "");
		}
		editor.apply();
	}

	public void rememberUser(String userName, String password) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putString(REMEMBER_USER_DATA_KEY, userName + ";" + password);
		editor.apply();
	}

	public String getRememberUser() {
		return mSharedPreferences.getString(REMEMBER_USER_DATA_KEY, null);
	}

	public WidgetConfigs getExamWidgetConfigs(Context context) {
		Uri uri = Uri.parse(WidgetConfigProvider.EXAM_CONFIG_CONTENT_URI);
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

	public void saveExamWidgetConfigs(Context context, WidgetConfigs configs) {
		Uri uri = Uri.parse(WidgetConfigProvider.EXAM_CONFIG_CONTENT_URI);

		for (Integer key : configs.keySet()) {
			ContentValues contentValues = new ContentValues();
			contentValues.put("widget_id", key);
			contentValues.put("selection", configs.get(key));

			context.getContentResolver().insert(uri, contentValues);
		}

	}

	public String getScheduleChooseTerm(Context context) {
		String user = getRememberUser();
		if (user != null) {
			ScheduleConfig config = getScheduleConfig(context, user.split(";")[0]);
			if (config != null)
				return config.getTerm();
		}
		return null;
	}

	public void setScheduleChooseTerm(Context context, String term) {
		ScheduleConfig config = new ScheduleConfig();
		String user = getRememberUser();
		if (user == null)
			return;
		config.setId(user.split(";")[0]);
		config.setTerm(term);
		updateScheduleConfig(context, config);
	}

	public void setScheduleFirstWeek(Context context, Calendar calendar) {
		if (calendar.get(Calendar.DAY_OF_WEEK) != 1)
			calendar.set(Calendar.DAY_OF_WEEK, 1);
		ScheduleConfig config = new ScheduleConfig();
		String user = getRememberUser();
		if (user == null)
			return;
		config.setId(user.split(";")[0]);
		config.setFirstWeek(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
				.format(calendar.getTime()));
		updateScheduleConfig(context, config);
	}

	public Calendar getScheduleFirstWeek(Context context) {
		Calendar calendar = Calendar.getInstance();
		String user = getRememberUser();
		if (user != null) {
			ScheduleConfig config = getScheduleConfig(context, user.split(";")[0]);
			if (config != null) {
				String firstWeekDate = config.getFirstWeek();
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
			}
		}
		return calendar;
	}

	public void saveScheduleConfig(Context context, ScheduleConfig config) {
		Uri uri = Uri.parse(WidgetConfigProvider.SCHEDULE_CONFIG_CONTENT_URI);

		ContentValues contentValues = new ContentValues();
		contentValues.put("id", config.getId());
		contentValues.put("term", config.getTerm());
		contentValues.put("first_week", config.getFirstWeek());
		contentValues.put("card_colors", config.getCardColors());

		context.getContentResolver().insert(uri, contentValues);
	}

	public void updateScheduleConfig(Context context, ScheduleConfig config) {
		Uri uri = Uri.parse(WidgetConfigProvider.SCHEDULE_CONFIG_CONTENT_URI);

		ContentValues contentValues = new ContentValues();
		if (config.getTerm() != null)
			contentValues.put("term", config.getTerm());
		if (config.getFirstWeek() != null)
			contentValues.put("first_week", config.getFirstWeek());
		if (config.getCardColors() != null)
			contentValues.put("card_colors", config.getCardColors());

		context.getContentResolver().update(uri, contentValues, "id = ?", new String[]{config.getId()});
	}

	public void deleteScheduleConfig(Context context, ScheduleConfig config) {
		Uri uri = Uri.parse(WidgetConfigProvider.SCHEDULE_CONFIG_CONTENT_URI);
		context.getContentResolver().delete(uri, "id = ?", new String[]{config.getId()});
	}

	public ArrayList<ScheduleConfig> getScheduleConfig(Context context) {
		Uri uri = Uri.parse(WidgetConfigProvider.SCHEDULE_CONFIG_CONTENT_URI);
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
		ArrayList<ScheduleConfig> configs = new ArrayList<ScheduleConfig>();
		while (cursor.moveToNext()) {
			ScheduleConfig config = new ScheduleConfig();
			config.setId(cursor.getString(0));
			config.setTerm(cursor.getString(1));
			config.setFirstWeek(cursor.getString(2));
			config.setCardColors(cursor.getString(3));

			configs.add(config);
		}
		cursor.close();
		return configs;
	}

	public ScheduleConfig getScheduleConfig(Context context, String xh) {
		Uri uri = Uri.parse(WidgetConfigProvider.SCHEDULE_CONFIG_CONTENT_URI);
		Cursor cursor = context.getContentResolver().query(uri, null, "id = ?", new String[]{xh}, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			ScheduleConfig config = new ScheduleConfig();
			config.setId(cursor.getString(0));
			config.setTerm(cursor.getString(1));
			config.setFirstWeek(cursor.getString(2));
			config.setCardColors(cursor.getString(3));

			cursor.close();
			return config;
		}
		return null;
	}

	public void setScheduleCurrentWeek(Context context, String currentWeek) {
		int week = Integer.parseInt(currentWeek);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.WEEK_OF_YEAR, -week + 1);
		setScheduleFirstWeek(context, calendar);
	}

	public String getScheduleCurrentWeek(Context context) {
		Calendar calendar = getScheduleFirstWeek(context);
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

	public void setScheduleCardColors(Context context, String colors) {
		ScheduleConfig config = new ScheduleConfig();
		String user = getRememberUser();
		if (user == null)
			return;
		config.setId(user.split(";")[0]);
		config.setCardColors(colors);
		updateScheduleConfig(context, config);
	}

	public String getScheduleCardColors(Context context) {
		String user = getRememberUser();
		if (user != null) {
			ScheduleConfig config = getScheduleConfig(context, user.split(";")[0]);
			if (config != null)
				return config.getCardColors();
		}
		return context.getResources().getString(R.string.default_colors);
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
