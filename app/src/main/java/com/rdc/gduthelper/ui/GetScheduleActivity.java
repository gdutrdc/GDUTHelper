package com.rdc.gduthelper.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.bean.Lesson;
import com.rdc.gduthelper.ui.widget.AddScheduleDialog;
import com.rdc.gduthelper.utils.database.ScheduleDBHelper;

import java.util.ArrayList;

/**
 * Created by seasonyuu on 16/1/4.
 */
public class GetScheduleActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_schedule);

		ScheduleDBHelper helper = new ScheduleDBHelper(this);
		ArrayList<Lesson> lessons = helper.getLessonList(null);
		if (lessons == null || lessons.size() == 0) {
			new AlertDialog.Builder(this)
					.setTitle(R.string.tips)
					.setMessage(R.string.no_local_schedule)
					.setPositiveButton(R.string.ensure, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (!GDUTHelperApp.isLogin()) {
								dialog.dismiss();
								showAddSchedule();
							} else {
								startActivity(new Intent(GetScheduleActivity.this, LoginActivity.class));
								finish();
							}
						}
					}).show();
		}
	}

	private void showAddSchedule() {
		AddScheduleDialog dialog = new AddScheduleDialog(this);
		dialog.setOnButtonClickListener(new AddScheduleDialog.OnButtonClickListener() {
			@Override
			public void onEnsure(DialogInterface dialog) {

			}

			@Override
			public void onCancel(DialogInterface dialog) {
				finish();
			}
		});
		dialog.show();
	}
}
