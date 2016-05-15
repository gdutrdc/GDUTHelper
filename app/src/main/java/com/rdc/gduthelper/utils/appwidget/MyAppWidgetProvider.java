package com.rdc.gduthelper.utils.appwidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.bean.WidgetConfigs;
import com.rdc.gduthelper.utils.SerializeUtil;
import com.rdc.gduthelper.utils.Settings;

import java.util.Calendar;

/**
 * Created by seasonyuu on 15/12/1.
 */
public class MyAppWidgetProvider extends AppWidgetProvider {
	private static final String TAG = MyAppWidgetProvider.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		Uri uri = Uri.parse(WidgetConfigProvider.CONFIG_CONTENT_URI);
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
		WidgetConfigs configs = new WidgetConfigs();
		if (cursor == null)
			return;
		while (cursor.moveToNext()) {
			configs.putConfig(cursor.getInt(0), cursor.getString(1));
		}
		cursor.close();
		if (configs.size() == 0)
			return;
		for (int id : appWidgetIds) {
			String selection = configs.getConfig(id);
			if (selection == null)
				continue;
			addNotify(context, id);

			Intent adapter = new Intent(context, WidgetService.class);
			adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
			adapter.setData(Uri.parse(adapter.toUri(Intent.URI_INTENT_SCHEME)));

			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_exam_time);
			views.setRemoteAdapter(id, R.id.widget_exam_times, adapter);

			appWidgetManager.updateAppWidget(id, views);
			AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(id, R.id.widget_exam_times);
		}
	}

	@Override
	public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);

		onUpdate(context, appWidgetManager, new int[]{appWidgetId});
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Uri uri = Uri.parse(WidgetConfigProvider.CONFIG_CONTENT_URI);
		context.getContentResolver().delete(uri, "widget_id = ?", new String[]{appWidgetIds[0] + ""});
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
	}

	private void addNotify(Context context, int appWidgetId) {
		Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		intent.setClass(context.getApplicationContext(), MyAppWidgetProvider.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		Bundle bundle = new Bundle();
		bundle.putIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{appWidgetId});
		intent.putExtras(bundle);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.add(Calendar.DAY_OF_YEAR, 1);

		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
	}

	@Override
	public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
		super.onRestored(context, oldWidgetIds, newWidgetIds);
	}
}
