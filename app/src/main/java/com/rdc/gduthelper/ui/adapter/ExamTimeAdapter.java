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
import com.rdc.gduthelper.bean.Exam;

import java.util.ArrayList;

/**
 * Created by seasonyuu on 15/11/27.
 */
public class ExamTimeAdapter extends RecyclerView.Adapter<ExamTimeAdapter.ExamTimeHolder> {
	private Context mContext;
	private ArrayList<Exam> exams;

	public ExamTimeAdapter(Context context) {
		mContext = context;
	}

	public ArrayList<Exam> getExams() {
		return exams;
	}

	public void setExams(ArrayList<Exam> exams) {
		if (exams != null)
			this.exams = exams;
		else
			this.exams = new ArrayList<>();
	}

	@Override
	public ExamTimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ExamTimeHolder(
				LayoutInflater.from(mContext)
						.inflate(R.layout.item_exam_time, parent, false));
	}

	@Override
	public void onBindViewHolder(ExamTimeHolder holder, int position) {
		holder.getBinding().setVariable(BR.exam, exams.get(position));
		holder.getBinding().executePendingBindings();
	}

	@Override
	public int getItemCount() {
		return exams == null ? 0 : exams.size();
	}

	public class ExamTimeHolder extends RecyclerView.ViewHolder {
		private ViewDataBinding binding;

		public ExamTimeHolder(View itemView) {
			super(itemView);
			binding = DataBindingUtil.bind(itemView);
		}

		public ViewDataBinding getBinding() {
			return binding;
		}
	}
}
