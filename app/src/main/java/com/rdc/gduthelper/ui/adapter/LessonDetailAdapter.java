package com.rdc.gduthelper.ui.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rdc.gduthelper.BR;
import com.rdc.gduthelper.R;
import com.rdc.gduthelper.bean.Lesson;
import com.rdc.gduthelper.bean.MaterialColors;

import java.util.ArrayList;

/**
 * Created by seasonyuu on 16/1/26.
 */
public class LessonDetailAdapter extends RecyclerView.Adapter<LessonDetailAdapter.LessonDetailHolder> {
	private Context context;

	private ArrayList<Lesson> lessons;

	public LessonDetailAdapter(Context context) {
		this.context = context;
		lessons = new ArrayList<>();
	}

	public ArrayList<Lesson> getLessons() {
		return lessons;
	}

	public void setLessons(ArrayList<Lesson> lessons) {
		this.lessons = lessons;
	}

	@Override
	public LessonDetailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new LessonDetailHolder(LayoutInflater.from(context)
				.inflate(R.layout.item_lesson_detail, parent, false));
	}

	@Override
	public void onBindViewHolder(LessonDetailHolder holder, int position) {
		holder.getBinding().setVariable(BR.lesson, lessons.get(position));
		holder.getBinding().executePendingBindings();
		CardView card = (CardView) holder.itemView.findViewById(R.id.lesson_detail_container);
		card.setCardElevation(position * 3.0f);
		card.setCardBackgroundColor(MaterialColors.getColor((int) (Math.random() * 4)));
	}

	@Override
	public int getItemCount() {
		return lessons.size();
	}

	public class LessonDetailHolder extends RecyclerView.ViewHolder {
		private ViewDataBinding binding;
		public View itemView;

		public LessonDetailHolder(View itemView) {
			super(itemView);
			this.itemView = itemView;
			binding = DataBindingUtil.bind(itemView);
		}

		public ViewDataBinding getBinding() {
			return binding;
		}
	}
}
