package com.rdc.gduthelper.ui;

import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.bean.Exam;
import com.rdc.gduthelper.bean.User;
import com.rdc.gduthelper.net.BaseRunnable;
import com.rdc.gduthelper.net.api.GetExamTime;
import com.rdc.gduthelper.net.api.GetSchedule;
import com.rdc.gduthelper.net.api.IntoGetExamTime;
import com.rdc.gduthelper.ui.adapter.ExamTimeAdapter;
import com.rdc.gduthelper.utils.SerializeUtil;
import com.rdc.gduthelper.utils.database.ExamTimeDBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 查询考试时间
 * <p/>
 * Created by seasonyuu on 15/11/26.
 */
public class GetExamTimeActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {
	private RecyclerView mRvExamTimes;
	private ExamTimeAdapter mExamTimeAdapter;
	private AppCompatSpinner mYearsSpinner;
	private AppCompatSpinner mTermsSpinner;

	private ArrayList<Exam> mWholeList;

	private int mPreYear = 0;
	private int mPreTerm = 0;

	private static final int MODE_OFFLINE = 0;
	private static final int MODE_ONLINE = 1;

	private int mReadMode = 0;

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

		if (GDUTHelperApp.isLogin())
			mReadMode = MODE_ONLINE;
		else
			mReadMode = MODE_OFFLINE;

		intoGetExamTime();
	}

	private void intoGetExamTime() {
		if (mReadMode == MODE_ONLINE)
			new Thread(new IntoGetExamTime(new BaseRunnable.GGCallback() {
				@Override
				public void onCall(Object obj) {
					JSONObject jsonObject = (JSONObject) obj;
					try {
						final ArrayList<String> terms = (ArrayList<String>) SerializeUtil.deSerialization(jsonObject.getString("terms"));
						final ArrayList<String> years = (ArrayList<String>) SerializeUtil.deSerialization(jsonObject.getString("years"));
						final ArrayList<Exam> exams =
								(ArrayList<Exam>) SerializeUtil.deSerialization(jsonObject.getString("exams"));

						final int whichYear = jsonObject.getInt("which_year");
						final int whichTerm = jsonObject.getInt("which_term");

						if (exams == null) {
							// 此时可能有两种情况，第一种是当前学期还没有任何考试时间可以查询，
							// 另一种可能是在寒暑假期间，新学期信息还未插入教务系统数据库
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									initSpinner(years, whichYear, terms, whichTerm);
									mPreTerm = whichTerm;
									mPreYear = whichYear;

									if (mExamTimeAdapter == null) {
										mExamTimeAdapter = new ExamTimeAdapter(GetExamTimeActivity.this);
										mRvExamTimes.setAdapter(mExamTimeAdapter);
									}
									getExamTime();
								}
							});
						} else {
							Collections.sort(exams, new Comparator<Exam>() {
								@Override
								public int compare(Exam lhs, Exam rhs) {
									if (lhs.getExamCount() < rhs.getExamCount() || rhs.getExamCount() < 0)
										return -1;
									else if (lhs.getExamCount() > rhs.getExamCount() || lhs.getExamCount() < 0)
										return 1;
									return 0;
								}
							});
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									initSpinner(years, whichYear, terms, whichTerm);
									mPreTerm = whichTerm;
									mPreYear = whichYear;

									if (mExamTimeAdapter == null) {
										mExamTimeAdapter = new ExamTimeAdapter(GetExamTimeActivity.this);
										mRvExamTimes.setAdapter(mExamTimeAdapter);
									}
									cancelDialog();
									mExamTimeAdapter.setExams(exams);
									ExamTimeDBHelper helper = ExamTimeDBHelper.getInstance(GetExamTimeActivity.this);
									for (Exam exam : exams) {
										helper.insertExamTime(exam);
									}
									mExamTimeAdapter.notifyDataSetChanged();
								}
							});
						}
					} catch (JSONException | ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
				}
			})).start();
		else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					ExamTimeDBHelper helper = ExamTimeDBHelper.getInstance(GetExamTimeActivity.this);
					User user = GDUTHelperApp.getSettings().getLastUser(GetExamTimeActivity.this);
					if (user == null) {
						noLocalToast();
					} else {
						String xh = user.getXh();
						mWholeList = helper.getExamTimes(xh, null);
						if (mWholeList.size() == 0)
							noLocalToast();
						else {
							Map<String, String> yearsMap = new HashMap<>();
							Map<String, String> termsMap = new HashMap<>();

							for (Exam exam : mWholeList) {
								String[] ss = exam.getId().substring(1, 12).split("-");
								String xnd = ss[0] + "-" + ss[1];
								String xqd = ss[2];
								yearsMap.put(xnd, xnd);
								termsMap.put(xqd, xqd);
							}
							final ArrayList<String> years = new ArrayList<>();
							for (Object o : yearsMap.values().toArray()) {
								years.add(o.toString());
							}
							Collections.sort(years, new Comparator<String>() {
								@Override
								public int compare(String lhs, String rhs) {
									int l = Integer.parseInt(lhs.substring(0, 4));
									int r = Integer.parseInt(rhs.substring(0, 4));
									if (l < r)
										return 1;
									else if (l == r)
										return 0;
									else
										return -1;
								}
							});
							final ArrayList<String> terms = new ArrayList<>();
							for (Object o : termsMap.values().toArray()) {
								terms.add(o.toString());
							}
							Collections.sort(terms, new Comparator<String>() {
								@Override
								public int compare(String lhs, String rhs) {
									int l = Integer.parseInt(lhs);
									int r = Integer.parseInt(rhs);
									if (l > r)
										return 1;
									else if (l == r)
										return 0;
									else
										return -1;
								}
							});

							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									initSpinner(years, -1, terms, -1);

									getExamTime();
								}
							});

						}
					}
				}
			}).start();
		}
	}

	/**
	 * 为Spinner设置数据以及选中项
	 *
	 * @param years      年份数据
	 * @param whichYear  选中年份位置，-1表示不更改
	 * @param terms      学期数据
	 * @param whichTerms 选中学期位置，-1表示不更改
	 */
	private void initSpinner(final List<String> years, final int whichYear, final List<String> terms, final int whichTerms) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ArrayAdapter<String> termsAdapter = new ArrayAdapter<String>
						(GetExamTimeActivity.this, R.layout.spinner_item,
								android.R.id.text1, terms);
				termsAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
				mTermsSpinner.setAdapter(termsAdapter);
				if (whichTerms != -1)
					mTermsSpinner.setSelection(whichTerms);

				ArrayAdapter<String> yearsAdapter = new ArrayAdapter<String>
						(GetExamTimeActivity.this, R.layout.spinner_item,
								android.R.id.text1, years);
				yearsAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
				mYearsSpinner.setAdapter(yearsAdapter);
				if (whichYear != -1)
					mYearsSpinner.setSelection(whichYear);
			}
		});
	}

	private void noLocalToast() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(GetExamTimeActivity.this,
						R.string.get_exam_time_no_local, Toast.LENGTH_SHORT).show();
				cancelDialog();
			}
		});
	}

	private void getExamTime() {
		String year = mYearsSpinner.getSelectedItem().toString();
		String term = mTermsSpinner.getSelectedItem().toString();
		if (mReadMode == MODE_ONLINE)
			new Thread(new GetExamTime(year, term, new BaseRunnable.GGCallback() {
				@Override
				public void onCall(Object obj) {
					ArrayList<Exam> list = null;
					try {
						list = (ArrayList<Exam>) SerializeUtil.deSerialization(obj.toString());
					} catch (IOException | ClassNotFoundException e) {
						e.printStackTrace();
					}
					mExamTimeAdapter.setExams(list);
					ExamTimeDBHelper helper = ExamTimeDBHelper.getInstance(GetExamTimeActivity.this);
					if (list != null)
						for (Exam exam : list) {
							helper.insertExamTime(exam);
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
		else {
			String match = "(" + year + "-" + term + ")";
			ArrayList<Exam> list = new ArrayList<>();
			for (Exam exam : mWholeList) {
				if (exam.getId().contains(match)) {
					list.add(exam);
				}
			}

			if (mExamTimeAdapter == null) {
				mExamTimeAdapter = new ExamTimeAdapter(GetExamTimeActivity.this);
				mRvExamTimes.setAdapter(mExamTimeAdapter);
			}
			cancelDialog();
			mExamTimeAdapter.setExams(list);
			mExamTimeAdapter.notifyDataSetChanged();
		}
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
