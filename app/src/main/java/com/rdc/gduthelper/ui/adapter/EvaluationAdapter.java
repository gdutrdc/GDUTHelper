package com.rdc.gduthelper.ui.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rdc.gduthelper.BR;
import com.rdc.gduthelper.R;
import com.rdc.gduthelper.bean.Evaluation;
import com.rdc.gduthelper.bean.MaterialColors;

import java.util.ArrayList;

/**
 * Created by seasonyuu on 16/1/2.
 */
public class EvaluationAdapter extends RecyclerView.Adapter<EvaluationAdapter.EvaluationHolder> {
	private Context mContext;
	private ArrayList<Evaluation> mEvaluationList;

	public EvaluationAdapter(Context context) {
		mContext = context;
		mEvaluationList = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			Evaluation evaluation = new Evaluation();
			evaluation.setLessonName("编译原理");
			evaluation.setLessonCode("(2013-2014)");
			evaluation.setShowScore(i > 0);
			evaluation.setScoreColor(MaterialColors.getColor(i));
			evaluation.setScore(i);
			mEvaluationList.add(evaluation);
		}
	}

	public void setEvaluationList(ArrayList<Evaluation> evaluationList) {
		mEvaluationList = evaluationList;
	}

	public ArrayList<Evaluation> getEvaluationList(){
		return mEvaluationList;
	}

	@Override
	public EvaluationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new EvaluationHolder(LayoutInflater.from(mContext)
				.inflate(R.layout.item_evaluation, parent, false));
	}

	@Override
	public void onBindViewHolder(EvaluationHolder holder, int position) {
		holder.position = position;

		holder.getBinding().setVariable(BR.eval, mEvaluationList.get(position));
		holder.getBinding().executePendingBindings();
	}

	@Override
	public int getItemCount() {
		return mEvaluationList.size();
	}

	public class EvaluationHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		private ViewDataBinding binding;
		public int position;

		public EvaluationHolder(View itemView) {
			super(itemView);
			binding = DataBindingUtil.bind(itemView);
			itemView.setOnClickListener(this);
		}

		public ViewDataBinding getBinding() {
			return binding;
		}

		@Override
		public void onClick(View v) {
			Evaluation evaluation = mEvaluationList.get(position);
			evaluation.setEvaluated(!evaluation.isEvaluated());

			notifyDataSetChanged();
		}
	}
}
