package com.rdc.gduthelper.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by seasonyuu on 15/8/28.
 */
public class Lesson implements Serializable, Parcelable {
	private String lessonCode;//课程代码
	private String lessonName;//课程名称
	private String lessonType;//课程性质
	private String lessonBelong;//课程归属
	private String lessonCredit;//课程学分
	private String lessonGrade;//课程成绩
	private String lessonTime;//上课时间
	private String lessonClassroom;//上课地点
	private String lessonTeacher;//教师姓名
	private ArrayList<LessonTACR> lessonTACRs;//上课时间与地点集合

	public ArrayList<LessonTACR> getLessonTACRs() {
		return lessonTACRs;
	}

	public void setLessonTACRs(ArrayList<LessonTACR> lessonTACRs) {
		this.lessonTACRs = lessonTACRs;
	}

	public String getLessonTime() {
		return lessonTime;
	}

	public void setLessonTime(String lessonTime) {
		this.lessonTime = lessonTime;
	}

	public String getLessonClassroom() {
		return lessonClassroom;
	}

	public void setLessonClassroom(String lessonClassroom) {
		if(lessonClassroom.equals("&nbsp;"))
			lessonClassroom = "";
		this.lessonClassroom = lessonClassroom;
	}

	public String getLessonTeacher() {
		return lessonTeacher;
	}

	public void setLessonTeacher(String lessonTeacher) {
		this.lessonTeacher = lessonTeacher;
	}

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
		if(lessonType.equals("&nbsp;"))
			lessonType = "";
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
		return "课程代码:" + lessonCode + ",课程名字:" + lessonName +
				",課程教師:" + lessonTeacher + ",上课时间:" + lessonTime
				+ ",上课地点:" + lessonClassroom + ",课程学分:" + lessonCredit
				+ ",课程性质:" + lessonType + ",课程成绩:" + lessonGrade;
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.lessonCode);
		dest.writeString(this.lessonName);
		dest.writeString(this.lessonType);
		dest.writeString(this.lessonBelong);
		dest.writeString(this.lessonCredit);
		dest.writeString(this.lessonGrade);
		dest.writeString(this.lessonTime);
		dest.writeString(this.lessonClassroom);
		dest.writeString(this.lessonTeacher);
		dest.writeTypedList(this.lessonTACRs);
	}

	public Lesson() {
	}

	protected Lesson(Parcel in) {
		this.lessonCode = in.readString();
		this.lessonName = in.readString();
		this.lessonType = in.readString();
		this.lessonBelong = in.readString();
		this.lessonCredit = in.readString();
		this.lessonGrade = in.readString();
		this.lessonTime = in.readString();
		this.lessonClassroom = in.readString();
		this.lessonTeacher = in.readString();
		this.lessonTACRs = in.createTypedArrayList(LessonTACR.CREATOR);
	}

	public static final Parcelable.Creator<Lesson> CREATOR = new Parcelable.Creator<Lesson>() {
		@Override
		public Lesson createFromParcel(Parcel source) {
			return new Lesson(source);
		}

		@Override
		public Lesson[] newArray(int size) {
			return new Lesson[size];
		}
	};
}
