package com.rdc.gduthelper.ui.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.CardView;
import android.view.View;

import com.rdc.gduthelper.BR;
import com.rdc.gduthelper.R;
import com.rdc.gduthelper.bean.Lesson;

import java.util.ArrayList;
import java.util.HashMap;

import tk.zielony.materialrecents.RecentsAdapter;

/**
 * Created by seasonyuu on 16/1/26.
 */
public class LessonDetailAdapter implements RecentsAdapter {
	private Context context;

	private ArrayList<Lesson> lessons;

	private HashMap<String, Integer> lessonColors;

	public LessonDetailAdapter(Context context) {
		this.context = context;
		lessons = new ArrayList<>();
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
	public View getView(int position) {
		View view = View.inflate(context, R.layout.item_lesson_detail, null);
		ViewDataBinding binding = DataBindingUtil.bind(view);
		binding.setVariable(BR.lesson, lessons.get(position));
		int color = 0;
		if (lessonColors.get(lessons.get(position).getLessonCode()) != null)
			color = lessonColors.get(lessons.get(position).getLessonCode());
		((CardView)view).setCardBackgroundColor(color);
		return view;
	}

	@Override
	public int getCount() {
		return lessons.size();
	}
}
