package com.rdc.gduthelper.utils.appwidget.schedule;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.bean.Lesson;
import com.rdc.gduthelper.bean.LessonTACR;
import com.rdc.gduthelper.utils.LessonUtils;
import com.rdc.gduthelper.utils.appwidget.WidgetConfigProvider;
import com.rdc.gduthelper.utils.database.ScheduleDBHelper;
import com.rdc.gduthelper.utils.settings.ScheduleConfig;
import com.rdc.gduthelper.utils.settings.Settings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TreeMap;

/**
 * Created by seasonyuu on 16-6-6.
 */

public class DailyScheduleWidgetProvider extends AppWidgetProvider {
	private static final String TAG = DailyScheduleWidgetProvider.class.getSimpleName();

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		Uri uri = Uri.parse(WidgetConfigProvider.SCHEDULE_CONFIG_CONTENT_URI);
		String user = context.getSharedPreferences(
				context.getPackageName() + "_preferences", Context.MODE_MULTI_PROCESS)
				.getString(Settings.REMEMBER_USER_DATA_KEY, null);
		if (user == null)
			return;
		Cursor cursor = context.getContentResolver()
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

		if (config != null) {
			ScheduleDBHelper helper = new ScheduleDBHelper(context);
			ArrayList<Lesson> lessons = helper.getLessonList(config.getTerm(), config.getId());
			String[] weekdays = context.getResources().getStringArray(R.array.weekdays);
			for (int id : appWidgetIds) {
				RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
						R.layout.widget_schedule_daily);

				remoteViews.setTextViewText(R.id.widget_schedule_week, "第" +
						LessonUtils.calculateCurrentWeek(config.getFirstWeek()) + "周");
				remoteViews.setTextViewText(R.id.widget_schedule_weekday,
						weekdays[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1]);

				Intent adapter = new Intent(context, DailyScheduleWidgetService.class);

				Calendar firstWeek = Calendar.getInstance();
				try {
					firstWeek.setTime(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
							.parse(config.getFirstWeek()));
				} catch (ParseException e) {
					e.printStackTrace();
					break;
				}

				TreeMap<LessonTACR, Lesson> todaysLessons = LessonUtils
						.calculateTodaysLesson(firstWeek, Calendar.getInstance(), lessons);

				adapter.setExtrasClassLoader(TreeMap.class.getClassLoader());

				Bundle data = new Bundle();
				Log.e("todaysLessons",todaysLessons.getClass().getSimpleName());
				data.putSerializable("todaysLessons", todaysLessons);

				adapter.putExtra("data", data);
				adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
				adapter.setData(Uri.parse(adapter.toUri(Intent.URI_INTENT_SCHEME)));
				remoteViews.setRemoteAdapter(R.id.widget_schedule_daily_list, adapter);

				appWidgetManager.updateAppWidget(id, remoteViews);
			}
		}
	}
}
