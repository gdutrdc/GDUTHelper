package com.rdc.gduthelper.utils.appwidget.schedule;

import android.appwidget.AppWidgetManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.bean.Lesson;
import com.rdc.gduthelper.bean.LessonTACR;
import com.rdc.gduthelper.utils.LessonUtils;
import com.rdc.gduthelper.utils.appwidget.WidgetConfigProvider;
import com.rdc.gduthelper.utils.database.ScheduleDBHelper;
import com.rdc.gduthelper.utils.settings.ScheduleConfig;
import com.rdc.gduthelper.utils.settings.Settings;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by seasonyuu on 16-6-7.
 */

public class DailyScheduleWidgetService extends RemoteViewsService {
	private static final String TAG = DailyScheduleWidgetService.class.getSimpleName();
	private Map<LessonTACR, Lesson> todaysLessons = new HashMap<>(); // 由于序列化的缘故不得不这么做

	private int appwidgetId = -1;

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		ListRemoteViewsFactory factory = new ListRemoteViewsFactory();
		Bundle data = intent.getBundleExtra("data");
		Serializable serializable = data.getSerializable("todaysLessons");
		todaysLessons = (Map<LessonTACR, Lesson>) data.getSerializable("todaysLessons");
		appwidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);
		return factory;
	}

	private class ListRemoteViewsFactory implements RemoteViewsFactory {

		@Override
		public void onCreate() {

		}

		@Override
		public void onDataSetChanged() {
			final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
			Uri uri = Uri.parse(WidgetConfigProvider.SCHEDULE_CONFIG_CONTENT_URI);
			String user = getSharedPreferences(
					getPackageName() + "_preferences", MODE_MULTI_PROCESS)
					.getString(Settings.REMEMBER_USER_DATA_KEY, null);
			if (user == null) {
				Log.e(TAG, "user = null");
				return;
			}
			Cursor cursor = getContentResolver()
					.query(uri, null, "id = ?", new String[]{user.split(";")[0]}, null);
			ScheduleConfig config = null;

			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				config = new ScheduleConfig();
				config.setId(cursor.getString(0));
				config.setTerm(cursor.getString(1));
				config.setFirstWeek(cursor.getString(2));
				config.setCardColors(cursor.getString(3));
			}
			cursor.close();
			if (config == null) {
				Log.e(TAG, "config is null while querying Uri(" + uri.toString() + ")");
				return;
			}

			ScheduleDBHelper helper = new ScheduleDBHelper(DailyScheduleWidgetService.this);
			ArrayList<Lesson> lessons = helper.getLessonList(config.getTerm(), config.getId());
			String[] weekdays = getResources().getStringArray(R.array.weekdays);

			Calendar firstWeek = Calendar.getInstance();
			try {
				firstWeek.setTime(format.parse(config.getFirstWeek()));
			} catch (ParseException e) {
				e.printStackTrace();
			}

			Calendar today = Calendar.getInstance();
			Uri widgetUri = Uri.parse(WidgetConfigProvider.WIDGET_CONFIG_CONTENT_URI);
			Cursor configCursor = getContentResolver()
					.query(widgetUri, null, "widget_id = ?", new String[]{appwidgetId + ""}, null);
			if (configCursor == null) {
				Log.e(TAG, "Cursor is null while querying Uri(" + widgetUri.toString() + ")");
				return;
			}
			if (configCursor.getCount() == 0) {
				ContentValues contentValues = new ContentValues();
				contentValues.put("widget_id", appwidgetId);
				contentValues.put("calendar", format.format(today.getTime()));
				getContentResolver().insert(widgetUri, contentValues);
			} else {
				configCursor.moveToFirst();
				String calendar = configCursor.getString(1);
				Date configDate = null;
				try {
					configDate = format.parse(calendar);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (configDate != null)
					today.setTime(configDate);
			}
			configCursor.close();
			DailyScheduleWidgetService.this.todaysLessons = LessonUtils
					.calculateTodaysLessons(firstWeek, today, lessons);
		}

		@Override
		public void onDestroy() {

		}

		@Override
		public int getCount() {
			Log.e(TAG, todaysLessons.size() + "");
			return todaysLessons.size();
		}

		@Override
		public RemoteViews getViewAt(int position) {
			RemoteViews itemView = new RemoteViews(getPackageName(), R.layout.item_widget_schedule_daily);
			LessonTACR lessonTACR = (LessonTACR) todaysLessons.keySet().toArray()[position];
			Lesson lesson = todaysLessons.get(lessonTACR);
			itemView.setTextViewText(R.id.item_schedule_daily_lesson, lesson.getLessonName());

			itemView.setTextViewText(R.id.item_schedule_daily_classroom, lessonTACR.getClassroom());
			itemView.setTextViewText(R.id.item_schedule_daily_num,
					lessonTACR.getNum()[0] + "-" + lessonTACR.getNum()[lessonTACR.getNum().length - 1]);
			return itemView;
		}

		@Override
		public RemoteViews getLoadingView() {
			return null;
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

	}

}
