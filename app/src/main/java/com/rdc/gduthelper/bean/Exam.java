package com.rdc.gduthelper.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
		try {
			Date date = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA).parse(examTime);
			this.examCount = (date.getTime() - new Date().getTime()) / 24 / 60 / 60 / 1000 + "天";
		} catch (ParseException e) {
			e.printStackTrace();
			examTime = examTime.split("周")[2];
			examTime = examTime.split("\\(")[1];
			examTime = examTime.split("\\)")[0];
			Date date = null;
			try {
				date = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).parse(examTime);
				this.examCount = (date.getTime() - new Date().getTime()) / 24 / 60 / 60 / 1000 + "天";
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
		}

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
		if (examType.equals("&nbsp;"))
			this.examType = " ";
		else
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
