package com.rdc.gduthelper.ui;

import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.rdc.gduthelper.R;
import com.rdc.gduthelper.bean.Exam;
import com.rdc.gduthelper.net.BaseRunnable;
import com.rdc.gduthelper.net.api.GetExamTime;
import com.rdc.gduthelper.net.api.IntoGetExamTime;
import com.rdc.gduthelper.ui.adapter.ExamTimeAdapter;
import com.rdc.gduthelper.utils.database.DatabaseHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by seasonyuu on 15/11/26.
 */
public class GetExamTimeActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {
	private RecyclerView mRvExamTimes;
	private ExamTimeAdapter mExamTimeAdapter;
	private AppCompatSpinner mYearsSpinner;
	private AppCompatSpinner mTermsSpinner;

	private int mPreYear = 0;
	private int mPreTerm = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_exam_time);

		mRvExamTimes = (RecyclerView) findViewById(R.id.get_exam_time_recycler_view);
		mYearsSpinner = (AppCompatSpinner) findViewById(R.id.get_exam_time_spinner_years);
		mTermsSpinner = (AppCompatSpinner) findViewById(R.id.get_exam_time_spinner_terms);

		mYearsSpinner.setOnItemSelectedListener(this);
		mTermsSpinner.setOnItemSelectedListener(this);

		mRvExamTimes.setLayoutManager(new LinearLayoutManager(this));
		showProgressDialog("加载中");

		intoGetExamTime();
	}

	private void intoGetExamTime() {
		new Thread(new IntoGetExamTime(new BaseRunnable.GGCallback() {
			@Override
			public void onCall(Object obj) {
				JSONObject jsonObject = (JSONObject) obj;
				try {
					final JSONArray terms = JSON.parseArray(jsonObject.getString("terms"));
					final JSONArray years = JSON.parseArray(jsonObject.getString("years"));
					final ArrayList<com.alibaba.fastjson.JSONObject> exams =
							JSON.parseObject(jsonObject.getString("exams"), ArrayList.class);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							ArrayAdapter<Object> termsAdapter = new ArrayAdapter<Object>
									(GetExamTimeActivity.this, R.layout.spinner_item,
											android.R.id.text1, terms.toArray());
							termsAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
							mTermsSpinner.setAdapter(termsAdapter);

							ArrayAdapter<Object> yearsAdapter = new ArrayAdapter<Object>
									(GetExamTimeActivity.this, R.layout.spinner_item,
											android.R.id.text1, years.toArray());
							yearsAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
							mYearsSpinner.setAdapter(yearsAdapter);

							if (mExamTimeAdapter == null) {
								mExamTimeAdapter = new ExamTimeAdapter(GetExamTimeActivity.this);
								mRvExamTimes.setAdapter(mExamTimeAdapter);
							}
							cancelDialog();
							mExamTimeAdapter.setExams(exams);
							DatabaseHelper helper = new DatabaseHelper(GetExamTimeActivity.this);
							for (com.alibaba.fastjson.JSONObject jsonObject : exams) {
								helper.insertExamTime(JSON.parseObject(jsonObject.toString(), Exam.class));
							}
							mExamTimeAdapter.notifyDataSetChanged();
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		})).start();
	}

	private void getExamTime() {
		new Thread(new GetExamTime(mYearsSpinner.getSelectedItem().toString(),
				mTermsSpinner.getSelectedItem().toString(), new BaseRunnable.GGCallback() {
			@Override
			public void onCall(Object obj) {
				ArrayList<com.alibaba.fastjson.JSONObject> list =
						JSON.parseObject(obj.toString(), ArrayList.class);
				mExamTimeAdapter.setExams(list);
				DatabaseHelper helper = new DatabaseHelper(GetExamTimeActivity.this);
				if (list != null)
					for (com.alibaba.fastjson.JSONObject jsonObject : list) {
						helper.insertExamTime(JSON.parseObject(jsonObject.toString(), Exam.class));
					}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mExamTimeAdapter.notifyDataSetChanged();
					}
				});
				cancelDialog();
			}
		})).start();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if (position != (parent.getId() == R.id.get_exam_time_spinner_years ? mPreYear : mPreTerm)) {
			showProgressDialog("正在加载");
			if (parent.getId() == R.id.get_exam_time_spinner_years)
				mPreYear = position;
			else
				mPreTerm = position;
			getExamTime();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}
}
