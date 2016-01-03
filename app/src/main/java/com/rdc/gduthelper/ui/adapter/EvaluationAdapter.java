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
import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.bean.Evaluation;
import com.rdc.gduthelper.bean.Lesson;

import java.util.ArrayList;

/**
 * Created by seasonyuu on 16/1/2.
 */
public class EvaluationAdapter extends RecyclerView.Adapter<EvaluationAdapter.EvaluationHolder> {
	private Context mContext;
	private ArrayList<Evaluation> mEvaluationList;

	public interface OnItemClickListener {
		public void onItemClick(int position);
	}

	private OnItemClickListener onItemClickListener;

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public EvaluationAdapter(Context context) {
		mContext = context;
		mEvaluationList = new ArrayList<>();
		ArrayList<Lesson> lessons = GDUTHelperApp.getEvaluationList();
		for (Lesson lesson : lessons) {
			Evaluation evaluation = new Evaluation();
			evaluation.setLessonCode(lesson.getLessonCode());
			evaluation.setLessonName(lesson.getLessonName());
			mEvaluationList.add(evaluation);
		}
	}

	public void setEvaluationList(ArrayList<Evaluation> evaluationList) {
		mEvaluationList = evaluationList;
	}

	public ArrayList<Evaluation> getEvaluationList() {
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

	private void onItemClick(int position) {
		if (onItemClickListener != null)
			onItemClickListener.onItemClick(position);
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
			onItemClick(position);
		}
	}
}
