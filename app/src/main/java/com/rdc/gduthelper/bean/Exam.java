package com.rdc.gduthelper.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by seasonyuu on 15/11/26.
 */
public class Exam implements Parcelable, Serializable {
	private String id;//选课课号
	private String lessonName;//课程名称
	private String studentName;//姓名
	private String examTime;//考试时间
	private String examPosition;//考试地点
	private String examType;//考试形式
	private String campus;//校区
	private String examSeat;//座位号
	private int examCount;//考试倒计时

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
		if (examTime.equals("&nbsp;") || examTime.equals(" ")) {
			this.examTime = " ";
			setExamCount(366);
		} else {
			this.examTime = examTime;
			try {
				Date date = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA).parse(examTime);
				long count = date.getTime() - new Date().getTime() + 5 * 24 * 60 * 60 * 1000;
				if (count > 0)
					this.examCount = (int) (count / 24 / 60 / 60 / 1000 + 1);
				else if (count > -((long) 24) * 60 * 60 * 1000)
					this.examCount = 0;
				else
					this.examCount = -1;
			} catch (ParseException e) {
				try {
					examTime = examTime.split("周")[2];
					examTime = examTime.split("\\(")[1];
					examTime = examTime.split("\\)")[0];
					Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).parse(examTime);
					long count = date.getTime() - new Date().getTime() + 5 * 24 * 60 * 60 * 1000;
					if (count > 0)
						this.examCount = (int) (count / 24 / 60 / 60 / 1000 + 1);
					else if (count > -((long) 24) * 60 * 60 * 1000)
						this.examCount = 0;
					else
						this.examCount = -1;
				} catch (Exception e1) {
				}
			}
		}

	}

	public String getExamPosition() {
		return examPosition;
	}

	public void setExamPosition(String examPosition) {
		if (examPosition.equals("&nbsp;") || examPosition.equals(" "))
			this.examPosition = " ";
		else
			this.examPosition = examPosition;
	}

	public String getExamType() {
		return examType;
	}

	public void setExamType(String examType) {
		if (examType.equals("&nbsp;") || examType.equals(" "))
			this.examType = " ";
		else
			this.examType = examType;
	}

	public String getCampus() {
		return campus;
	}

	public void setCampus(String campus) {
		if (campus.equals("&nbsp;") || campus.equals(" "))
			this.campus = " ";
		else
			this.campus = campus;
	}

	public String getExamSeat() {
		return examSeat;
	}

	public void setExamSeat(String examSeat) {
		if (examSeat.equals("&nbsp;") || examSeat.equals(" "))
			this.examSeat = " ";
		else
			this.examSeat = examSeat;
	}

	public int getExamCount() {
		return examCount;
	}

	public void setExamCount(int examCount) {
		this.examCount = examCount;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.id);
		dest.writeString(this.lessonName);
		dest.writeString(this.studentName);
		dest.writeString(this.examTime);
		dest.writeString(this.examPosition);
		dest.writeString(this.examType);
		dest.writeString(this.campus);
		dest.writeString(this.examSeat);
		dest.writeInt(this.examCount);
	}

	public Exam() {
	}

	protected Exam(Parcel in) {
		this.id = in.readString();
		this.lessonName = in.readString();
		this.studentName = in.readString();
		this.examTime = in.readString();
		this.examPosition = in.readString();
		this.examType = in.readString();
		this.campus = in.readString();
		this.examSeat = in.readString();
		this.examCount = in.readInt();
	}

	public static final Parcelable.Creator<Exam> CREATOR = new Parcelable.Creator<Exam>() {
		public Exam createFromParcel(Parcel source) {
			return new Exam(source);
		}

		public Exam[] newArray(int size) {
			return new Exam[size];
		}
	};


}
