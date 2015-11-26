package com.rdc.gduthelper.bean;

/**
 * Created by seasonyuu on 15/11/26.
 */
public class Exam {
	private String id;//选课课号
	private String lessonName;//课程名称
	private String studentName;//姓名
	private String examTime;//考试时间
	private String examPosition;//考试地点
	private String examType;//考试形式
	private String campus;//校区
	private String examSeat;//座位号
	private String examCount;//考试倒计时

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLessonName() {
		return lessonName;
	}

	public void setLessonName(String lessonName) {
		this.lessonName = lessonName;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getExamTime() {
		return examTime;
	}

	public void setExamTime(String examTime) {
		this.examTime = examTime;
	}

	public String getExamPosition() {
		return examPosition;
	}

	public void setExamPosition(String examPosition) {
		this.examPosition = examPosition;
	}

	public String getExamType() {
		return examType;
	}

	public void setExamType(String examType) {
		this.examType = examType;
	}

	public String getCampus() {
		return campus;
	}

	public void setCampus(String campus) {
		this.campus = campus;
	}

	public String getExamSeat() {
		return examSeat;
	}

	public void setExamSeat(String examSeat) {
		this.examSeat = examSeat;
	}

	public String getExamCount() {
		return examCount;
	}

	public void setExamCount(String examCount) {
		this.examCount = examCount;
	}
}
