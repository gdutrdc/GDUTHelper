package com.rdc.gduthelper.ui;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.bean.LevelExam;
import com.rdc.gduthelper.net.BaseRunnable;
import com.rdc.gduthelper.net.api.GetLevelExamGrade;
import com.rdc.gduthelper.ui.adapter.LevelExamsAdapter;

import java.util.ArrayList;

/**
 * Created by seasonyuu on 16-5-18.
 */
public class LevelExamActivity extends BaseActivity {
	private RecyclerView mRvLevelExams;

	private ArrayList<LevelExam> mLevelExams = new ArrayList<>();

	private LevelExamsAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_level_exam);

		mRvLevelExams = (RecyclerView) findViewById(R.id.level_exam_list);
		mAdapter = new LevelExamsAdapter(this);
		mRvLevelExams.setAdapter(mAdapter);

		showProgressDialog(R.string.loading);
		getLevelExamGrade();
	}

	private void getLevelExamGrade() {
		new Thread(new GetLevelExamGrade(new BaseRunnable.GGCallback() {
			@Override
			public void onCall(final Object obj) {
				mLevelExams = (ArrayList<LevelExam>) obj;
				cancelDialog();
				mRvLevelExams.post(new Runnable() {
					@Override
					public void run() {
						mAdapter.setLevelExams(mLevelExams);
						mAdapter.notifyDataSetChanged();
					}
				});
			}
		})).start();
	}


}
