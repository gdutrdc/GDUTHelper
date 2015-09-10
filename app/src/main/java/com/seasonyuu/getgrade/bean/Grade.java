package com.seasonyuu.getgrade.bean;

/**
 * Created by seasonyuu on 15/8/28.
 */
public class Grade implements Cloneable {
	private String lessonCode;//课程代码
	private String lessonName;//课程名称
	private String lessonType;//课程性质
	private String lessonBelong;//课程归属
	private String lessonCredit;//课程学分
	private String lessonGrade;//课程成绩

	public String getLessonCredit() {
		return lessonCredit;
	}

	public void setLessonCredit(String lessonCredit) {
		this.lessonCredit = lessonCredit;
	}

	public String getLessonBelong() {
		return lessonBelong;
	}

	public void setLessonBelong(String lessonBelong) {
		this.lessonBelong = lessonBelong;
	}

	public String getLessonName() {
		return lessonName;
	}

	public void setLessonName(String lessonName) {
		this.lessonName = lessonName;
	}

	public String getLessonType() {
		return lessonType;
	}

	public void setLessonType(String lessonType) {
		this.lessonType = lessonType;
	}

	public String getLessonGrade() {
		return lessonGrade;
	}

	public void setLessonGrade(String lessonGrade) {
		this.lessonGrade = lessonGrade;
	}

	public String getLessonCode() {
		return lessonCode;
	}

	public void setLessonCode(String lessonCode) {
		this.lessonCode = lessonCode;
	}

	@Override
	public String toString() {
		return lessonCode + "\t" + lessonName + "\t" + lessonGrade;
	}

	public double calculatePoint() {
		double point = 0;
		try {
			point = Double.parseDouble(lessonGrade) / 10 - 5;
			if (point < 1)
				point = 0;
		} catch (NumberFormatException e) {
			if (lessonGrade.equals("优秀")) {
				return 4.5;
			} else if (lessonGrade.equals("良好")) {
				return 3.5;
			} else if (lessonGrade.equals("中等")) {
				return 2.5;
			} else if (lessonGrade.equals("及格")) {
				return 1.5;
			} else if (lessonGrade.equals("不及格")) {
				return 0;
			}
		}
		return point;
	}

}
