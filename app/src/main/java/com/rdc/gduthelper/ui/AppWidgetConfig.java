package com.rdc.gduthelper.ui;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.bean.Exam;
import com.rdc.gduthelper.bean.MaterialColors;
import com.rdc.gduthelper.utils.appwidget.WidgetService;
import com.rdc.gduthelper.utils.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by seasonyuu on 15/12/1.
 */
public class AppWidgetConfig extends BaseActivity implements AdapterView.OnItemSelectedListener {
	private int mAppWidgetId;
	private AppCompatSpinner mYearsSpinner;
	private AppCompatSpinner mTermsSpinner;
	private ListView mLvPreview;
	private ArrayList<Exam> mWholeList;
	private ArrayList<Exam> mPreviewList;
	private String xh = null;
	private ExamsAdapter mAdapter;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_widget_config);

		mYearsSpinner = (AppCompatSpinner) findViewById(R.id.get_exam_time_widget_spinner_years);
		mTermsSpinner = (AppCompatSpinner) findViewById(R.id.get_exam_time_widget_spinner_terms);
		mLvPreview = (ListView) findViewById(R.id.widget_exam_times);

		mYearsSpinner.setOnItemSelectedListener(this);
		mTermsSpinner.setOnItemSelectedListener(this);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(
					AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		preDealData();

		if (xh != null) {
			makePreView(mYearsSpinner.getSelectedItem().toString(),
					mTermsSpinner.getSelectedItem().toString());
		}
	}

	private void preDealData() {
		try {
			String s = GDUTHelperApp.getInstance().getRememberUser();
			if (s == null) {
				Toast.makeText(this, R.string.get_exam_time_no_local, Toast.LENGTH_SHORT).show();
				finish();
			} else {
				String[] data = s.split(";", 2);
				xh = data[0];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (xh != null) {
			DatabaseHelper helper = new DatabaseHelper(this);
			mWholeList = helper.getExamTimes(xh, null);
			if (mWholeList.size() == 0) {
				Toast.makeText(this, R.string.get_exam_time_no_local, Toast.LENGTH_SHORT).show();
				xh = null;
				finish();
			} else {
				Map<String, String> yearsMap = new HashMap<>();
				Map<String, String> termsMap = new HashMap<>();

				for (Exam exam : mWholeList) {
					String[] data = exam.getId().substring(1, 12).split("-");
					String xnd = data[0] + "-" + data[1];
					String xqd = data[2];
					yearsMap.put(xnd, xnd);
					termsMap.put(xqd, xqd);
				}
				ArrayList<String> years = new ArrayList<>();
				for (Object o : yearsMap.values().toArray()) {
					years.add(o.toString());
				}
				Collections.sort(years, new Comparator<String>() {
					@Override
					public int compare(String lhs, String rhs) {
						int l = Integer.parseInt(lhs.substring(0, 4));
						int r = Integer.parseInt(rhs.substring(0, 4));
						if (l > r)
							return 1;
						else if (l == r)
							return 0;
						else
							return -1;
					}
				});
				ArrayList<String> terms = new ArrayList<>();
				for (Object o : termsMap.values().toArray()) {
					terms.add(o.toString());
				}
				Collections.sort(terms, new Comparator<String>() {
					@Override
					public int compare(String lhs, String rhs) {
						int l = Integer.parseInt(lhs);
						int r = Integer.parseInt(rhs);
						if (l < r)
							return 1;
						else if (l == r)
							return 0;
						else
							return -1;
					}
				});

				ArrayAdapter<String> termsAdapter = new ArrayAdapter<>
						(this, R.layout.spinner_item,
								android.R.id.text1, terms);
				termsAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
				mTermsSpinner.setAdapter(termsAdapter);

				ArrayAdapter<String> yearsAdapter = new ArrayAdapter<>
						(this, R.layout.spinner_item,
								android.R.id.text1, years);
				yearsAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
				mYearsSpinner.setAdapter(yearsAdapter);
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.mi_widget_config_done) {
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
			RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_exam_time);

			Intent adapter = new Intent(this, WidgetService.class);
			adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
			adapter.setData(Uri.parse(adapter.toUri(Intent.URI_INTENT_SCHEME)));
			adapter.putExtra("selection", mYearsSpinner.getSelectedItem().toString() + "-"
					+ mTermsSpinner.getSelectedItem().toString());
			views.setRemoteAdapter(R.id.widget_exam_times, adapter);

			appWidgetManager.updateAppWidget(mAppWidgetId, views);
			Intent resultValue = new Intent();
			resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
			setResult(RESULT_OK, resultValue);
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	private void makePreView(String year, String term) {
		mPreviewList = new ArrayList<>();

		if (mAdapter == null) {
			mAdapter = new ExamsAdapter();
			mLvPreview.setAdapter(mAdapter);
		}

		String match = "(" + year + "-" + term + ")";
		for (Exam exam : mWholeList) {
			if (exam.getId().contains(match)) {
				mPreviewList.add(exam);
			}
		}

		mAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_widget_config, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		makePreView(mYearsSpinner.getSelectedItem().toString(),
				mTermsSpinner.getSelectedItem().toString());
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	private class ExamsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mPreviewList.size();
		}

		@Override
		public Object getItem(int position) {
			return mPreviewList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder(
						convertView = LayoutInflater.from(AppWidgetConfig.this)
								.inflate(R.layout.item_widget_exam_time, parent, false));
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();
			Exam exam = mPreviewList.get(position);
			holder.tvFirst.setText(exam.getLessonName());
			holder.tvSecond.setText(exam.getExamTime());
			holder.tvFirstBack.setText(exam.getExamType());
			holder.tvThird.setText(exam.getExamPosition()
					+ getResources().getString(R.string.get_exam_time_seat) + exam.getExamSeat());
			int count = exam.getExamCount();
			if (count >= 0 && count < 366) {
				holder.tvLeave.setText(getResources().getString(R.string.get_exam_time_leave));
				holder.tvCount.setText(
						count + getResources().getString(R.string.get_exam_time_day));
				if (count <= 5) {
					holder.tvCount.setTextColor(MaterialColors.getColor(MaterialColors.RED));
				} else if (count <= 10) {
					holder.tvCount.setTextColor(MaterialColors.getColor(MaterialColors.PINK));
				} else if (count <= 20) {
					holder.tvCount.setTextColor(MaterialColors.getColor(MaterialColors.ORANGE));
				} else
					holder.tvCount.setTextColor(MaterialColors.getColor(MaterialColors.AMBER));
			} else {
				holder.tvLeave.setText("");
				holder.tvCount.setText(count < 0 ?
						getResources().getString(R.string.get_exam_time_past)
						: getResources().getString(R.string.get_exam_time_no_data));

				holder.tvCount.setTextColor(
						MaterialColors.getColor(MaterialColors.BLUE_GREY));
			}
			return convertView;
		}

		class ViewHolder {
			TextView tvFirst, tvFirstBack, tvSecond, tvThird;
			TextView tvLeave, tvCount;

			public ViewHolder(View view) {
				tvFirst = (TextView) view.findViewById(R.id.widget_exam_times_first_line);
				tvSecond = (TextView) view.findViewById(R.id.widget_exam_times_second_line);
				tvThird = (TextView) view.findViewById(R.id.widget_exam_times_third_line);
				tvFirstBack = (TextView) view.findViewById(R.id.widget_exam_times_first_line_back);
				tvLeave = (TextView) view.findViewById(R.id.widget_exam_times_leave_tips);
				tvCount = (TextView) view.findViewById(R.id.widget_exam_times_leave_count);
			}
		}
	}
}
