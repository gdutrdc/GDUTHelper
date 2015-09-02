package com.seasonyuu.getgrade.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.seasonyuu.getgrade.R;
import com.seasonyuu.getgrade.app.GGApplication;
import com.seasonyuu.getgrade.bean.Grade;
import com.seasonyuu.getgrade.net.BaseRunnable;
import com.seasonyuu.getgrade.net.api.GetGrade;
import com.seasonyuu.getgrade.net.api.IntoGrade;
import com.seasonyuu.getgrade.ui.adapter.GradeListAdapter;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by seasonyuu on 15/8/28.
 */
public class GetGradeActivity extends AppCompatActivity {
	private final String TAG = GetGradeActivity.class.getSimpleName();

	private Handler handler;
	private GradeListAdapter adapter;
	private ListView lvGrade;
	private TextView tvGradePoint;
	private ProgressDialog progressDialog;

	private Spinner mYearSpinner;
	private Spinner mTermSpinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_grade);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		progressDialog = new ProgressDialog(this);
//		progressDialog.setCancelable(false);
		progressDialog.setMessage(getString(R.string.loading));
		progressDialog.show();

		mYearSpinner = (Spinner) findViewById(R.id.grade_year_spinner);
		mTermSpinner = (Spinner) findViewById(R.id.grade_term_spinner);

		tvGradePoint = (TextView) findViewById(R.id.grade_point);

		lvGrade = (ListView) findViewById(R.id.grade_list);
		adapter = new GradeListAdapter(this);
		lvGrade.setAdapter(adapter);

		((AppCompatRadioButton) findViewById(R.id.grade_get_term)).setChecked(true);

		initHandler();

		IntoGrade();
	}

	private void initHandler() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 0) {
					progressDialog.cancel();
					String[] spinnerData = ((String) msg.obj).split(";");
					Calendar calendar = Calendar.getInstance();
					int section = -1;

					ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(GetGradeActivity.this, R.layout.spinner_item);
					yearAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
					String[] years = spinnerData[0].split(",");
					for (int i = 0; i < years.length; i++) {
						yearAdapter.add(years[i]);
						if (section == -1 && years[i].contains(calendar.get(Calendar.YEAR) + "")) {
							section = i;
						}
					}
					mYearSpinner.setAdapter(yearAdapter);
					if (section != -1) {
						mYearSpinner.setSelection(section);
					}

					ArrayAdapter<String> termAdapter = new ArrayAdapter<String>(GetGradeActivity.this, R.layout.spinner_item);
					termAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
					for (String s : spinnerData[1].split(","))
						termAdapter.add(s);
					mTermSpinner.setAdapter(termAdapter);

				} else if (msg.what == 1) {
					progressDialog.cancel();
					if (msg.obj != null) {
						ArrayList<Grade> list = (ArrayList<Grade>) msg.obj;
						float points = 0, credits = 0;
						for (Grade grade : list) {
							points += grade.calculatePoint() * Float.parseFloat(grade.getLessonCredit());
							credits += Float.parseFloat(grade.getLessonCredit());
						}
						tvGradePoint.setText("平均绩点:" + String.format("%.2f", points / credits));
						adapter.setData(list);
						adapter.notifyDataSetChanged();
					}
				}

			}
		};

	}


	private void IntoGrade() {
		new Thread(new IntoGrade(GGApplication.userName, new BaseRunnable.GGCallback() {
			@Override
			public void onCall(Object obj) {
				Message msg = Message.obtain();
				msg.what = 0;
				msg.obj = obj;
				handler.sendMessage(msg);
			}
		})).start();
	}

	public void onClick(View v) {
		Runnable runnable = null;
		String year = null;
		String term = null;
		if (((AppCompatRadioButton) findViewById(R.id.grade_get_term)).isChecked()) {
			year = mYearSpinner.getSelectedItem().toString();
			term = mTermSpinner.getSelectedItem().toString();
		} else if (((AppCompatRadioButton) findViewById(R.id.grade_get_year)).isChecked()) {
			year = mYearSpinner.getSelectedItem().toString();
		} else if (((AppCompatRadioButton) findViewById(R.id.grade_get_all)).isChecked()) {

		}
		runnable = new GetGrade(year, term, new BaseRunnable.GGCallback() {
			@Override
			public void onCall(Object obj) {
				Message msg = Message.obtain();
				msg.what = 1;
				msg.obj = obj;
				handler.sendMessage(msg);
			}
		});
		if (runnable != null) {
			progressDialog.show();
			new Thread(runnable).start();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			startActivity(new Intent(this, LoginActivity.class));
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
