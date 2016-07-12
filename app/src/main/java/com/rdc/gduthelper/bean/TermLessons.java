package com.rdc.gduthelper.bean;

import java.util.ArrayList;

/**
 * Created by seasonyuu on 16-7-8.
 */

public class TermLessons {
	private ArrayList<Lesson> lessons;
	private String term;
	private int parentPosition;
	private int childPosition;

	public int getChildPosition() {
		return childPosition;
	}

	public void setChildPosition(int childPosition) {
		this.childPosition = childPosition;
	}

	public int getParentPosition() {
		return parentPosition;
	}

	public void setParentPosition(int parentPosition) {
		this.parentPosition = parentPosition;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public ArrayList<Lesson> getLessons() {
		return lessons;
	}

	public void setLessons(ArrayList<Lesson> lessons) {
		this.lessons = lessons;
	}
}
