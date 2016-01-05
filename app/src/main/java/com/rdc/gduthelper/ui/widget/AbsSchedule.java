package com.rdc.gduthelper.ui.widget;

import com.rdc.gduthelper.bean.Lesson;

import java.util.ArrayList;

/**
 * Created by seasonyuu on 16/1/4.
 */
public abstract class AbsSchedule {
	private ArrayList<Lesson> mLessonList;

	public Lesson getLesson(int week, int day, int number) {
		return null;
	}

	public ArrayList<Lesson> getLessonList(int week) {
		return null;
	}

	public ArrayList<Lesson> getLessonList(int week, int day) {
		return null;
	}

	public ArrayList<Lesson> getLessonList() {
		return mLessonList;
	}

	public void setLessonList(ArrayList<Lesson> lessonList) {
		this.mLessonList = lessonList;
	}
}
