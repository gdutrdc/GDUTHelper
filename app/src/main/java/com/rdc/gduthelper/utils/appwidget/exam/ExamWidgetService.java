package com.rdc.gduthelper.utils.appwidget.exam;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.bean.Exam;
import com.rdc.gduthelper.bean.MaterialColors;
import com.rdc.gduthelper.bean.User;
import com.rdc.gduthelper.utils.database.ExamTimeDBHelper;

import java.util.ArrayList;

/**
 * Created by seasonyuu on 15/12/1.
 */
public class ExamWidgetService extends RemoteViewsService {
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new ExamListRemoteViewsFactory(intent);
	}

	private class ExamListRemoteViewsFactory implements RemoteViewsFactory {
		private ArrayList<Exam> mExams;

		ExamListRemoteViewsFactory(Intent intent) {
			String selection = intent.getStringExtra("selection");
			User user = GDUTHelperApp.getSettings().getLastUser(getApplicationContext());
			String xh = user.getXh();
			ExamTimeDBHelper helper = ExamTimeDBHelper.getInstance(ExamWidgetService.this);
			mExams = helper.getExamTimes(xh, selection);
		}

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
			return mExams.size();
		}

		@Override
		public RemoteViews getViewAt(int position) {

			RemoteViews itemView = new RemoteViews(getPackageName(), R.layout.item_widget_exam_time);
			Exam exam = mExams.get(position);
			itemView.setTextViewText(R.id.widget_exam_times_first_line, exam.getLessonName());
			itemView.setTextViewText(R.id.widget_exam_times_first_line_back, exam.getExamType());
			itemView.setTextViewText(R.id.widget_exam_times_second_line, exam.getExamTime());
			itemView.setTextViewText(R.id.widget_exam_times_third_line, exam.getExamPosition()
					+ getResources().getString(R.string.get_exam_time_seat)
					+ exam.getExamSeat());
			int count = exam.getExamCount();
			if (count >= 0 && count < 366) {
				itemView.setTextViewText(R.id.widget_exam_times_leave_tips, getResources().getString(R.string.get_exam_time_leave));
				itemView.setTextViewText(R.id.widget_exam_times_leave_count,
						count +
								getResources().getString(R.string.get_exam_time_day));
				if (count <= 5) {
					itemView.setTextColor(R.id.widget_exam_times_leave_count,
							MaterialColors.getColor(MaterialColors.RED));
				} else if (count <= 10) {
					itemView.setTextColor(R.id.widget_exam_times_leave_count,
							MaterialColors.getColor(MaterialColors.PINK));
				} else if (count <= 20) {
					itemView.setTextColor(R.id.widget_exam_times_leave_count,
							MaterialColors.getColor(MaterialColors.ORANGE));
				} else
					itemView.setTextColor(R.id.widget_exam_times_leave_count,
							MaterialColors.getColor(MaterialColors.AMBER));
			} else {
				itemView.setTextViewText(R.id.widget_exam_times_leave_tips, "");
				itemView.setTextViewText(R.id.widget_exam_times_leave_count,
						count < 0 ?
								getResources().getString(R.string.get_exam_time_past) :
								getResources().getString(R.string.get_exam_time_no_data));

				itemView.setTextColor(R.id.widget_exam_times_leave_count,
						MaterialColors.getColor(MaterialColors.BLUE_GREY));
			}
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
