package com.rdc.gduthelper.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.bean.Lesson;
import com.rdc.gduthelper.net.BaseRunnable;
import com.rdc.gduthelper.net.api.GetSchedule;
import com.rdc.gduthelper.net.api.IntoSchedule;
import com.rdc.gduthelper.ui.widget.WeekScheduleView;
import com.rdc.gduthelper.utils.database.ScheduleDBHelper;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by seasonyuu on 16/1/4.
 */
public class GetScheduleActivity extends BaseActivity {
	private AppCompatSpinner mSpinnerYear;
	private AppCompatSpinner mSpinnerTerm;
	private AppCompatSpinner mSpinnerWeek;

	private ArrayList<String> mYears;
	private ArrayList<String> mTerms;

	private WeekScheduleView mWeekScheduleView;

	private boolean isNull;

	private FloatingActionButton mFAB;

	private int themeId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		themeId = GDUTHelperApp.getSettings().getThemeId();
		if (themeId == R.style.AppTheme_Blue)
			themeId = R.style.AppTheme_Blue_NoActionBar;
		else
			themeId = R.style.AppTheme_Pink_NoActionBar;
		setTheme(themeId);
		setContentView(R.layout.activity_get_schedule);

		initView();

		mFAB = (FloatingActionButton) findViewById(R.id.get_schedule_fab);
	}

	private void initView() {
		mSpinnerWeek = (AppCompatSpinner) findViewById(R.id.get_schedule_week_spinner);

		String[] s = new String[21];
		for (int i = 1; i <= s.length; i++)
			s[i - 1] = "第 " + i + " 周";
		ArrayAdapter<String> weekAdapter = new ArrayAdapter<>(this, R.layout.spinner_item);
		weekAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		weekAdapter.addAll(s);
		mSpinnerWeek.setAdapter(weekAdapter);

		mWeekScheduleView = (WeekScheduleView) findViewById(R.id.get_schedule_table);

	}


	@Override
	protected void onResume() {
		super.onResume();
		if (mWeekScheduleView != null)
			if (mWeekScheduleView.getLessons().size() == 0) {
				ScheduleDBHelper helper = new ScheduleDBHelper(this);
				ArrayList<Lesson> lessons = helper.getLessonList("2015-2016-1");
				if (lessons == null || lessons.size() == 0) {
					isNull = true;
					new AlertDialog.Builder(this)
							.setTitle(R.string.tips)
							.setMessage(R.string.no_local_schedule)
							.setPositiveButton(R.string.ensure, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									if (GDUTHelperApp.isLogin()) {
										intoSchedule();
										showProgressDialog(R.string.loading);
									} else {
										startActivity(new Intent(GetScheduleActivity.this, LoginActivity.class));
									}
								}
							}).show();
				} else {
					mWeekScheduleView.setLessons(lessons);
				}
			}else{
				mWeekScheduleView.requestLayout();
			}
	}

	private void intoSchedule() {
		new Thread(new IntoSchedule(new BaseRunnable.GGCallback() {
			@Override
			public void onCall(Object obj) {
				String[] spinnerData = ((String) obj).split(";");
				if (spinnerData.length == 1) {

				} else {
					String[] years = spinnerData[0].split(",");
					mYears = new ArrayList<>();
					for (int i = 0; i < years.length; i++) {
						mYears.add(years[i]);
					}
					mTerms = new ArrayList<>();
					for (String s : spinnerData[1].split(","))
						mTerms.add(s);

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							showAddSchedule();
						}
					});
				}
			}
		})).start();
	}

	private void getSchedule(final String year, final String term) {
		new Thread(new GetSchedule(year, term, new BaseRunnable.GGCallback() {
			@Override
			public void onCall(Object obj) {
				ArrayList<Lesson> lessons = (ArrayList<Lesson>) obj;
				final ScheduleDBHelper helper = new ScheduleDBHelper(GetScheduleActivity.this);
				helper.addLessonList(lessons, year + "-" + term);
				cancelDialog();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ArrayList<Lesson> lessons = helper.getLessonList("2015-2016-1");
						mWeekScheduleView.setLessons(lessons);
					}
				});
			}
		})).start();
	}

	private void showAddSchedule() {
		final View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_schedule, null);
		mSpinnerYear = (AppCompatSpinner) view.findViewById(R.id.add_schedule_year);
		mSpinnerTerm = (AppCompatSpinner) view.findViewById(R.id.add_schedule_term);
		Calendar calendar = Calendar.getInstance();
		int section = -1;

		ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		yearAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		for (int i = 0; i < mYears.size(); i++) {
			yearAdapter.add(mYears.get(i));
			if (section == -1 && mYears.get(i).contains(calendar.get(Calendar.YEAR) + "")) {
				section = i;
			}
		}
		mSpinnerYear.setAdapter(yearAdapter);
		if (section != -1) {
			mSpinnerYear.setSelection(section);
		}

		ArrayAdapter<String> termAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
		termAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		termAdapter.addAll(mTerms);
		mSpinnerTerm.setAdapter(termAdapter);

		cancelDialog();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				new AlertDialog.Builder(GetScheduleActivity.this)
						.setTitle(R.string.add_schedule)
						.setView(view)
						.setPositiveButton(R.string.ensure, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								showProgressDialog(R.string.loading);
								getSchedule(mSpinnerYear.getSelectedItem().toString(),
										mSpinnerTerm.getSelectedItem().toString());
							}
						})
						.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if (isNull) {
									finish();
								}
							}
						}).show();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_schedule, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.schedule_settings) {

		}
		return super.onOptionsItemSelected(item);
	}
}
