package com.rdc.gduthelper.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;
import com.rdc.gduthelper.R;
import com.rdc.gduthelper.bean.Evaluation;
import com.rdc.gduthelper.ui.adapter.EvaluationAdapter;

/**
 * Created by seasonyuu on 16/1/1.
 */
public class EvaluationActivity extends BaseActivity implements View.OnClickListener {
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
		mEvaluationList = (RecyclerView) findViewById(R.id.evaluation_list);
		mEvaluationList.setLayoutManager(new LinearLayoutManager(this));
		mAdapter = new EvaluationAdapter(this);
		mEvaluationList.setAdapter(mAdapter);

		mFAB = (FloatingActionButton) findViewById(R.id.evaluation_fab);

		mFabToolbarLayout = (FABToolbarLayout) findViewById(R.id.evaluation_fab_toolbar);

		mScoreView = (ViewGroup) findViewById(R.id.evaluation_score);
		mScoreView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				mScoreView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				mFabToolbarLayout.hide();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_evaluation, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.evaluation_select_all) {
			for (Evaluation evaluation : mAdapter.getEvaluationList()) {
				evaluation.setEvaluated(!evaluation.isEvaluated());
			}
			mAdapter.notifyDataSetChanged();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		mFabToolbarLayout.hide();
	}
}