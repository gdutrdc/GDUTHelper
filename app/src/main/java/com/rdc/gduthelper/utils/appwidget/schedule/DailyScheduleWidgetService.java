package com.rdc.gduthelper.utils.appwidget.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.bean.Lesson;
import com.rdc.gduthelper.bean.LessonTACR;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by seasonyuu on 16-6-7.
 */

public class DailyScheduleWidgetService extends RemoteViewsService {
	private static final String TAG = DailyScheduleWidgetService.class.getSimpleName();
	private Map<LessonTACR, Lesson> todaysLessons = new HashMap<>(); // 由于序列化的缘故不得不这么做

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		ListRemoteViewsFactory factory = new ListRemoteViewsFactory();
		Bundle data = intent.getBundleExtra("data");
		Serializable serializable = data.getSerializable("todaysLessons");
		Log.e("service", serializable.getClass().getSimpleName());
		todaysLessons = (Map<LessonTACR, Lesson>) data.getSerializable("todaysLessons");
		return factory;
	}

	private class ListRemoteViewsFactory implements RemoteViewsFactory {

		@Override
		public void onCreate() {

		}

		@Override
		public void onDataSetChanged() {

		}

		@Override
		public void onDestroy() {

		}

		@Override
		public int getCount() {
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
