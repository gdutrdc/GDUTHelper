package com.rdc.gduthelper.utils.appwidget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by seasonyuu on 15/12/1.
 */
public class WidgetService extends RemoteViewsService {
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		ListRemoteViewsFactory factory = new ListRemoteViewsFactory(this, intent);

		return factory;
	}
}
