package com.seasonyuu.getgrade.bean;

/**
 * Created by seasonyuu on 15/9/9.
 */
public class Information {
	private String account;//学号
	private String name;//姓名
	private String sex;//性别
	private String enrollmentDate;//入学日期
	private String middleSchool;//毕业中学
	private String dormitory;//宿舍号
	private String birthday;//出生日期
	private String nation;//民族
	private String grade;//年级
	private String college;//学院
	private String department;//系
	private String clazz;//班级
	private String degree;//学历

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEnrollmentDate() {
		return enrollmentDate;
	}

	public void setEnrollmentDate(String enrollmentDate) {
		this.enrollmentDate = enrollmentDate;
	}

	public String getMiddleSchool() {
		return middleSchool;
	}

	public void setMiddleSchool(String middleSchool) {
		this.middleSchool = middleSchool;
	}

	public String getDormitory() {
		return dormitory;
	}

	public void setDormitory(String dormitory) {
		this.dormitory = dormitory;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getNation() {
		return nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getCollege() {
		return college;
	}

	public void setCollege(String college) {
		this.college = college;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	@Override
	public String toString() {
		return "学号:" + getAccount() + "," +
				"姓名:" + getName() + "," +
				"性别:" + getSex() + "," +
				"入学日期:" + getEnrollmentDate() + "," +
				"出生日期:" + getBirthday() + "," +
				"毕业中学:" + getMiddleSchool() + "," +
				"民族:" + getNation() + "," +
				"宿舍号:" + getDormitory() + "," +
				"学历层次:" + getDegree() + "," +
				"学院:" + getCollege() + "," +
				"系:" + getDepartment() + "," +
				"行政班:" + getClazz() + "," +
				"当前所在级:" + getGrade();
	}
}
