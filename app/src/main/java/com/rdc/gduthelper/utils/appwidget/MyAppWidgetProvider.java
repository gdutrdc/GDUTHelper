package com.rdc.gduthelper.utils.appwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.bean.WidgetConfigs;

/**
 * Created by seasonyuu on 15/12/1.
 */
public class MyAppWidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		WidgetConfigs configs = GDUTHelperApp.getInstance().getAppWidgetConfigs();
		if (configs == null)
			return;
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
}
