package com.rdc.gduthelper.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Arrays;

/**
 * means lesson time and classroom
 * <p/>
 * Created by seasonyuu on 15/8/28.
 */
public class LessonTACR implements Serializable, Parcelable {
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
		if (o == null)
			return false;
		if (o instanceof LessonTACR) {
			LessonTACR tacr = (LessonTACR) o;
			if (!tacr.getLessonCode().equals(getLessonCode()))
				return false;
			if (tacr.getWeekday() != getWeekday())
				return false;
			if (!tacr.getClassroom().equals(getClassroom()))
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

	@Override
	public String toString() {
		return "LessonTACR{" +
				"lessonCode='" + lessonCode + '\'' +
				", week=" + Arrays.toString(week) +
				", weekday=" + weekday +
				", num=" + Arrays.toString(num) +
				", classroom='" + classroom + '\'' +
				'}';
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.lessonCode);
		dest.writeIntArray(this.week);
		dest.writeInt(this.weekday);
		dest.writeIntArray(this.num);
		dest.writeString(this.classroom);
	}

	public LessonTACR(Parcel in) {
		this.lessonCode = in.readString();
		this.week = in.createIntArray();
		this.weekday = in.readInt();
		this.num = in.createIntArray();
		this.classroom = in.readString();
	}

	public static final Parcelable.Creator<LessonTACR> CREATOR = new Parcelable.Creator<LessonTACR>() {
		@Override
		public LessonTACR createFromParcel(Parcel source) {
			return new LessonTACR(source);
		}

		@Override
		public LessonTACR[] newArray(int size) {
			return new LessonTACR[size];
		}
	};
}
