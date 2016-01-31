package com.rdc.gduthelper.ui.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.View;

import com.rdc.gduthelper.BR;
import com.rdc.gduthelper.R;
import com.rdc.gduthelper.bean.Lesson;

import java.util.ArrayList;

import tk.zielony.materialrecents.RecentsAdapter;

/**
 * Created by seasonyuu on 16/1/26.
 */
public class LessonDetailAdapter implements RecentsAdapter {
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
	public View getView(int position) {
		View view = View.inflate(context, R.layout.item_lesson_detail, null);
		ViewDataBinding binding = DataBindingUtil.bind(view);
		binding.setVariable(BR.lesson, lessons.get(position));
		return view;
	}

	@Override
	public int getCount() {
		return 0;
	}
}
