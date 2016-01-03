package com.rdc.gduthelper.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;
import com.rdc.gduthelper.R;
import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.bean.Evaluation;
import com.rdc.gduthelper.bean.Lesson;
import com.rdc.gduthelper.ui.adapter.EvaluationAdapter;

import java.util.ArrayList;


/**
 * Created by seasonyuu on 16/1/1.
 */
public class EvaluationActivity extends BaseActivity implements View.OnClickListener, EvaluationAdapter.OnItemClickListener {
	private RecyclerView mEvaluationList;
	private FloatingActionButton mFAB;
	private ViewGroup mScoreView;
	private EvaluationAdapter mAdapter;

	private FABToolbarLayout mFabToolbarLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_evaluation);

		initView();
	}

	private void initView() {
		if (GDUTHelperApp.getEvaluationList() == null) {
			ArrayList<Lesson> lessons = new ArrayList<>();
			Lesson l1 = new Lesson();
			l1.setLessonName("高等数学");
			l1.setLessonCode("1234567890");
			lessons.add(l1);
			Lesson l2 = new Lesson();
			l2.setLessonName("大学英语");
			l2.setLessonCode("1234567890");
			lessons.add(l2);
			Lesson l3 = new Lesson();
			l3.setLessonName("大学物理");
			l3.setLessonCode("1234567890");
			lessons.add(l3);
			Lesson l4 = new Lesson();
			l4.setLessonName("吧唧吧唧");
			l4.setLessonCode("1234567890");
			lessons.add(l4);
			GDUTHelperApp.setEvaluationList(lessons);
		}

		mEvaluationList = (RecyclerView) findViewById(R.id.evaluation_list);
		mEvaluationList.setLayoutManager(new LinearLayoutManager(this));
		mAdapter = new EvaluationAdapter(this);
		mEvaluationList.setAdapter(mAdapter);
		mAdapter.setOnItemClickListener(this);

		mFAB = (FloatingActionButton) findViewById(R.id.evaluation_fab);
		mFAB.setOnClickListener(this);

		mFabToolbarLayout = (FABToolbarLayout) findViewById(R.id.evaluation_fab_toolbar);

		mScoreView = (ViewGroup) findViewById(R.id.evaluation_score);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_evaluation, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.evaluation_select_all) {
			int mark = 0;
			ArrayList<Evaluation> evaluations = mAdapter.getEvaluationList();
			for (Evaluation evaluation : evaluations) {
				if (!evaluation.isSelected()) {
					mark++;
				}
			}
			for (Evaluation evaluation : evaluations)
				evaluation.setSelected(mark > 0);
			if (mark > 0)
				mFabToolbarLayout.show();
			else
				mFabToolbarLayout.hide();
			mAdapter.notifyDataSetChanged();
		}
		return super.onOptionsItemSelected(item);
	}

	private void doEvaluation() {
		showProgressDialog(R.string.loading);
	}

	@Override
	public void onClick(View v) {
		int score = 0;
		switch (v.getId()) {
			case R.id.evaluation_fab:
				int mark = 0;
				for (Evaluation evaluation : mAdapter.getEvaluationList()) {
					if (evaluation.isShowScore())
						mark++;
				}
				if (mark == 0)
					Toast.makeText(this, R.string.please_select, Toast.LENGTH_SHORT).show();
				if (mark == mAdapter.getEvaluationList().size()) {
					doEvaluation();
				} else
					mFabToolbarLayout.hide();
				break;
			case R.id.evaluation_score_1:
				score = 1;
				break;
			case R.id.evaluation_score_2:
				score = 2;
				break;
			case R.id.evaluation_score_3:
				score = 3;
				break;
			case R.id.evaluation_score_4:
				score = 4;
				break;
			case R.id.evaluation_score_5:
				score = 5;
				break;
		}
		if (score > 0) {
			int mark = 0;
			for (Evaluation evaluation : mAdapter.getEvaluationList()) {
				if (evaluation.isSelected()) {
					evaluation.setScore(score);
					evaluation.setSelected(false);
				}
				if (evaluation.isShowScore())
					mark++;
			}
			if (mark == mAdapter.getEvaluationList().size())
				mFabToolbarLayout.hide();
			else if (mark == 0)
				Toast.makeText(this, R.string.please_select, Toast.LENGTH_SHORT).show();

			mAdapter.notifyDataSetChanged();

		}
	}

	@Override
	public void onItemClick(int position) {
		if (mFAB.isShown())
			mFabToolbarLayout.show();

		Evaluation evaluation = mAdapter.getEvaluationList().get(position);
		evaluation.setSelected(!evaluation.isSelected());

		mAdapter.notifyDataSetChanged();
	}
}