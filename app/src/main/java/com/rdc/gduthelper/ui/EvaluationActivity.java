package com.rdc.gduthelper.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;
import com.rdc.gduthelper.R;

/**
 * Created by seasonyuu on 16/1/1.
 */
public class EvaluationActivity extends BaseActivity implements View.OnClickListener {
	private RecyclerView mEvaluationList;
	private FloatingActionButton mFAB;
	private ViewGroup mScoreView;

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

		mFAB = (FloatingActionButton) findViewById(R.id.evaluation_fab);

		mFabToolbarLayout = (FABToolbarLayout) findViewById(R.id.evaluation_fab_toolbar);

		mScoreView = (ViewGroup) findViewById(R.id.evaluation_score);
	}

	@Override
	public void onClick(View v) {
		mFabToolbarLayout.hide();
	}
}