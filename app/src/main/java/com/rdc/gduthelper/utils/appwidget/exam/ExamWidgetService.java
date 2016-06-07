package com.rdc.gduthelper.utils.appwidget.exam;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by seasonyuu on 15/12/1.
 */
public class ExamWidgetService extends RemoteViewsService {
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		ExamListRemoteViewsFactory factory = new ExamListRemoteViewsFactory(this, intent);
		return factory;
	}
}
