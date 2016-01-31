package com.rdc.gduthelper.bean;

/**
 * means lesson time and classroom
 * <p/>
 * Created by seasonyuu on 15/8/28.
 */
public class LessonTACR {
	private String lessonCode;
	private int[] week;
	private int weekday;
	private int[] num;
	private String classroom;

	public LessonTACR() {

	}

	public String getLessonCode() {
		return lessonCode;
	}

	public void setLessonCode(String lessonCode) {
		this.lessonCode = lessonCode;
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

	@Override
	public boolean equals(Object o) {
		if (o instanceof LessonTACR) {
			LessonTACR tacr = (LessonTACR) o;
			if (!tacr.getLessonCode().equals(getLessonCode()))
				return false;
			if (tacr.getWeekday() != getWeekday())
				return false;
			if (tacr.getWeek().length == getWeek().length)
				for (int i = 0; i < getWeek().length; i++) {
					if (getWeek()[i] != tacr.getWeek()[i])
						return false;
				}
			else
				return false;
			if (tacr.getNum().length != getNum().length)
				return false;
			else {
				for (int i = 0; i < getNum().length; i++)
					if (getNum()[i] != tacr.getNum()[i])
						return false;
			}
			return true;
		} else
			return super.equals(o);
	}
}
