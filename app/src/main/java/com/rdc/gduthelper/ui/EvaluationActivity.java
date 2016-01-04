package com.rdc.gduthelper.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
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
import com.rdc.gduthelper.net.BaseRunnable;
import com.rdc.gduthelper.net.api.SaveEvaluation;
import com.rdc.gduthelper.net.api.IntoEvaluation;
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

	private int done = 0;

	private FABToolbarLayout mFabToolbarLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_evaluation);

		initView();
	}

	private void initView() {
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

	private void saveEvaluation() {
		showProgressDialog(R.string.loading);
		done = 0;
		evaluate(0);
	}

	private void doEvaluation() {
		new Thread(new SaveEvaluation(mAdapter.getEvaluationList().get(mAdapter.getItemCount() - 1),
				new BaseRunnable.GGCallback() {
					@Override
					public void onCall(Object obj) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								cancelDialog();
								ArrayList<Lesson> list = new ArrayList<>();
								GDUTHelperApp.setEvaluationList(list);
								new AlertDialog
										.Builder(EvaluationActivity.this)
										.setTitle(R.string.tips)
										.setMessage(R.string.all_eval_done)
										.setPositiveButton(R.string.ensure,
												new DialogInterface
														.OnClickListener() {
													@Override
													public void onClick(
															DialogInterface dialog, int which) {
														finish();
													}
												})
										.show();
							}
						});
					}
				})).start();
	}

	private void evaluate(int position) {
		final Evaluation evaluation = mAdapter.getEvaluationList().get(position);
		new Thread(new IntoEvaluation(evaluation, new BaseRunnable.GGCallback() {
			@Override
			public void onCall(Object obj) {
				new Thread(new SaveEvaluation(evaluation, new BaseRunnable.GGCallback() {
					@Override
					public void onCall(Object obj) {
						done++;
						if (done == mAdapter.getItemCount()) {
							doEvaluation();
						} else
							evaluate(done);
					}
				})).start();
			}
		})).start();
	}

	@Override
	public void onClick(View v) {
		int score = 0;
		switch (v.getId()) {
			case R.id.evaluation_fab:
				int mark = 0;
				ArrayList<Evaluation> evaluations = mAdapter.getEvaluationList();
				for (Evaluation evaluation : evaluations) {
					if (evaluation.isShowScore())
						mark++;
				}
				if (mark == 0) {
					for (Evaluation evaluation : evaluations) {
						evaluation.setSelected(true);
					}
					mAdapter.notifyDataSetChanged();

					mFabToolbarLayout.show();
				} else if (mark == evaluations.size()) {
					saveEvaluation();
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