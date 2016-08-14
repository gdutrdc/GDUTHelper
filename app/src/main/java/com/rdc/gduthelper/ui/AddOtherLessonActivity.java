package com.rdc.gduthelper.ui;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.bean.Lesson;
import com.rdc.gduthelper.bean.LessonTACR;
import com.rdc.gduthelper.ui.adapter.LessonTimesAdapter;
import com.rdc.gduthelper.utils.LessonUtils;
import com.rdc.gduthelper.utils.UIUtils;
import com.rdc.gduthelper.utils.database.ScheduleDBHelper;

import java.util.ArrayList;

/**
 * Created by seasonyuu on 16-7-31.
 */

public class AddOtherLessonActivity extends BaseActivity {
	private Toolbar mToolbar;

	private EditText mEtLessonName;
	private EditText mEtLessonTeacher;

	private RecyclerView mRvLessonTimes;

	private Lesson mLesson;

	private int type = TYPE_NEW; // 新增课程模式、修改课程模式
	public static final int TYPE_NEW = 0;
	public static final int TYPE_MODIFY = 1;

	private TextView mTvLessonCode;
	private LessonTimesAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_other_lesson);

		mToolbar = (Toolbar) findViewById(R.id.toolbar);

		mTvLessonCode = (TextView) findViewById(R.id.add_other_lesson_tv_lesson_code);
		mEtLessonName = (EditText) findViewById(R.id.add_other_lesson_et_lesson_name);
		mEtLessonTeacher = (EditText) findViewById(R.id.add_other_lesson_et_lesson_teacher);
		mRvLessonTimes = (RecyclerView) findViewById(R.id.add_other_lesson_rv_lesson_times);

		findViewById(R.id.add_other_lesson_btn_add_lesson_time)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						LessonTACR lessonTACR = new LessonTACR();
						if (mAdapter.getLessonTACRs() == null) {
							ArrayList<LessonTACR> lessonTACRs = new ArrayList<>();
							mAdapter.setLessonTACRs(lessonTACRs);
						}
						mAdapter.getLessonTACRs().add(lessonTACR);

						mAdapter.notifyDataSetChanged();

						mRvLessonTimes.postDelayed(new Runnable() {
							@Override
							public void run() {
								mRvLessonTimes.smoothScrollToPosition(mAdapter.getItemCount() - 1);
							}
						}, 200);

					}
				});

		initData();

		mRvLessonTimes.setLayoutManager(new LinearLayoutManager(this));
		mAdapter = new LessonTimesAdapter(this);
		ArrayList<LessonTACR> lessonTACRs = new ArrayList<>();
		LessonTACR lessonTACR = new LessonTACR();
		lessonTACR.setClassroom(mLesson.getLessonClassroom());
		lessonTACR.setLessonCode(mLesson.getLessonCode());
		lessonTACRs.add(lessonTACR);
		mAdapter.setLessonTACRs(lessonTACRs);
		mRvLessonTimes.setAdapter(mAdapter);
		mRvLessonTimes.addOnScrollListener(new RecyclerView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				if (recyclerView.computeVerticalScrollOffset() > UIUtils.convertDpToPixel(4, AddOtherLessonActivity.this)) {
					findViewById(R.id.add_other_lesson_shadow).setVisibility(View.VISIBLE);
				} else {
					findViewById(R.id.add_other_lesson_shadow).setVisibility(View.GONE);
				}
			}
		});

	}

	private void initData() {
		type = getIntent().getIntExtra("type", TYPE_NEW);
		if (type == TYPE_NEW) {
			mLesson = new Lesson();
			mLesson.setLessonBelong("");
			mLesson.setLessonCredit("");
			mLesson.setLessonGrade("");
			mLesson.setLessonType("");

			String lessonCode = "(" + GDUTHelperApp.getSettings().getScheduleChooseTerm(this) + ")";
			lessonCode += System.currentTimeMillis();
			mLesson.setLessonCode(lessonCode);
			ArrayList<LessonTACR> lessonTACRs = new ArrayList<>();
			LessonTACR lessonTACR = new LessonTACR();
			lessonTACR.setLessonCode(lessonCode);
			lessonTACRs.add(lessonTACR);
			mLesson.setLessonTACRs(lessonTACRs);

			mTvLessonCode.setText(lessonCode + "(自动生成)");
		} else {
			mLesson = (Lesson) getIntent().getSerializableExtra("lesson");

			mTvLessonCode.setText(mLesson.getLessonCode());
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_add_other_lesson, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.mi_done) {
			if (type == TYPE_NEW) {
				ArrayList<LessonTACR> lessonTACRs = mAdapter.getLessonTACRs();
				for (LessonTACR lessonTACR : lessonTACRs) {
					lessonTACR.setLessonCode(mLesson.getLessonCode());
				}
				mLesson.setLessonTeacher(mEtLessonTeacher.getText().toString());
				mLesson.setLessonName(mEtLessonName.getText().toString());

				mLesson.setLessonTACRs(lessonTACRs);

				String check1 = LessonUtils.checkNull(mLesson);
				if (check1 != null)
					showWarning(check1, null);
				else {
					LessonUtils.fillLessonTACRs(mLesson, lessonTACRs);

					String check2 = LessonUtils.checkNull(mLesson);
					if (check2 == null) {
						ScheduleDBHelper helper = new ScheduleDBHelper(this);
						helper.addLesson(mLesson, GDUTHelperApp.getSettings().getScheduleChooseTerm(this));
						finish();
					} else {
						showWarning(check2, null);
					}
				}
			} else {

			}

		}
		return super.onOptionsItemSelected(item);
	}

	private void showSnackBar(String text) {
		Snackbar.make((View) mToolbar.getParent(), text, Snackbar.LENGTH_SHORT).show();
	}
}
