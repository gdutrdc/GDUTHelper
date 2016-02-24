package com.rdc.gduthelper.ui.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.rdc.gduthelper.BR;
import com.rdc.gduthelper.R;
import com.rdc.gduthelper.bean.Lesson;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by seasonyuu on 16/1/26.
 */
public class LessonDetailAdapter extends RecyclerView.Adapter<LessonDetailAdapter.LessonHolder> {
	private Context context;

	private ArrayList<Lesson> lessons;

	private HashMap<String, Integer> lessonColors;
	private HashMap<Integer, LessonHolder> holders;

	public interface OnButtonClickListener {
		public void onEditClick(View itemView, int position);

		public void onDeleteClick(View itemView, int position);
	}

	public OnButtonClickListener onButtonClickListener;

	public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
		this.onButtonClickListener = onButtonClickListener;
	}

	public LessonDetailAdapter(Context context) {
		this.context = context;
		lessons = new ArrayList<>();
		holders = new HashMap<>();
	}

	public HashMap<String, Integer> getLessonColors() {
		return lessonColors;
	}

	public void setLessonColors(HashMap<String, Integer> lessonColors) {
		this.lessonColors = lessonColors;
	}

	public ArrayList<Lesson> getLessons() {
		return lessons;
	}

	public void setLessons(ArrayList<Lesson> lessons) {
		this.lessons = lessons;
	}

	@Override
	public LessonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LessonHolder holder = new LessonHolder(LayoutInflater.from(context)
				.inflate(R.layout.item_lesson_detail, parent, false));
		return holder;
	}

	public Lesson getItem(int position) {
		return lessons.get(position);
	}

	@Override
	public void onBindViewHolder(LessonHolder holder, int position) {
		int color = 0;
		if (lessonColors.get(lessons.get(position).getLessonCode()) != null) {
			color = lessonColors.get(lessons.get(position).getLessonCode());
		}
		((CardView) holder.itemView).setCardBackgroundColor(color);

		holder.getBinding().setVariable(BR.lesson, lessons.get(position));
		holder.getBinding().executePendingBindings();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemCount() {
		return lessons.size();
	}

	public class LessonHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		private ViewDataBinding binding;

		public LessonHolder(View itemView) {
			super(itemView);
			binding = DataBindingUtil.bind(itemView);
			itemView.findViewById(R.id.item_lesson_detail_edit).setOnClickListener(this);
			itemView.findViewById(R.id.item_lesson_detail_delete).setOnClickListener(this);
		}

		public ViewDataBinding getBinding() {
			return binding;
		}

		@Override
		public void onClick(View v) {
			if (onButtonClickListener != null) {
				switch (v.getId()) {
					case R.id.item_lesson_detail_edit:
						onButtonClickListener.onEditClick(itemView, getAdapterPosition());
						break;
					case R.id.item_lesson_detail_delete:
						onButtonClickListener.onDeleteClick(itemView, getAdapterPosition());
						break;
				}
			}
		}
	}
}
