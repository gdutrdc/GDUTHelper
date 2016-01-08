package com.rdc.gduthelper.bean;

/**
 * means lesson time and classroom
 *
 * Created by seasonyuu on 15/8/28.
 */
public class LessonTACR {
	private int[] week;
	private int weekday;
	private int[] num;
	private String classroom;

	public LessonTACR(){

	}

	public int[] getWeek() {
		return week;
	}

	public void setWeek(int[] week) {
		this.week = week;
	}

	public int getWeekday() {
		return weekday;
	}

	public void setWeekday(int weekday) {
		this.weekday = weekday;
	}

	public int[] getNum() {
		return num;
	}

	public void setNum(int[] num) {
		this.num = num;
	}

	public String getClassroom() {
		return classroom;
	}

	public void setClassroom(String classroom) {
		this.classroom = classroom;
	}
}
