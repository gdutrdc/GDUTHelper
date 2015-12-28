package com.rdc.gduthelper.utils.appwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.bean.WidgetConfigs;
import com.rdc.gduthelper.utils.SerializeUtil;

/**
 * Created by seasonyuu on 15/12/1.
 */
public class MyAppWidgetProvider extends AppWidgetProvider {

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);

		Log.e(MyAppWidgetProvider.class.getSimpleName(), intent.getAction());
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		Log.e(MyAppWidgetProvider.class.getSimpleName(), "onUpdate called");
		SharedPreferences sp = context.getSharedPreferences(
				context.getPackageName() + "_preferences", Context.MODE_PRIVATE);
		String data = sp.getString("widget_configs", null);
		WidgetConfigs configs = null;
		if (data == null) {
			return;
		} else {
			try {
				configs = (WidgetConfigs) SerializeUtil.deSerialization(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String KEY = "widgetvalue";
		SharedPreferences.Editor editor = sp.edit();
		String value = sp.getString(KEY, null);
		if (value == null) value = "";
		if (configs == null) {
			Log.e(MyAppWidgetProvider.class.getSimpleName(), "configs null");
			editor.putString(KEY, value + "null;");
			editor.apply();
			return;
		} else {
			String[] values = value.split(";");
			if (!values[values.length - 1].equals(configs.toString())) {
				editor.putString(KEY, value + configs.toString() + ";");
				editor.apply();
			}
		}
		for (int id : appWidgetIds) {
			String selection = configs.getConfig(id);

			Intent adapter = new Intent(context, WidgetService.class);
			adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
			adapter.setData(Uri.parse(adapter.toUri(Intent.URI_INTENT_SCHEME)));
			adapter.putExtra("selection", selection);

			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_exam_time);
			views.setRemoteAdapter(R.id.widget_exam_times, adapter);

			appWidgetManager.updateAppWidget(id, views);
		}
	}

	@Override
	public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);

		Log.e(MyAppWidgetProvider.class.getSimpleName(), "onAppWidgetOptionsChanged called");
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);

		Log.e(MyAppWidgetProvider.class.getSimpleName(), "onDeleted called");
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);

		Log.e(MyAppWidgetProvider.class.getSimpleName(), "onDisabled called");
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		Log.e(MyAppWidgetProvider.class.getSimpleName(), "onEnabled called");
	}

	@Override
	public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
		super.onRestored(context, oldWidgetIds, newWidgetIds);
		Log.e(MyAppWidgetProvider.class.getSimpleName(), "onRestored called");
	}
}
