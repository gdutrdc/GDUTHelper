package com.rdc.gduthelper.bean;

/**
 * Created by seasonyuu on 16-5-18.
 */
public class LevelExam {
	private String year;
	private String term;
	private String name;
	private String examId; //准考证号
	private String examTime;
	private String examGrade; //成绩
	private String examGradeLis; //听力成绩
	private String examGradeRea; //阅读成绩
	private String examGradeWri; //写作成绩
	private String examGradeCom; //综合成绩

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		if (year.equals("&nbsp;"))
			year = " ";
		this.year = year;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		if (term.equals("&nbsp;"))
			term = " ";
		this.term = term;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name.equals("&nbsp;"))
			name = " ";
		this.name = name;
	}

	public String getExamId() {
		return examId;
	}

	public void setExamId(String examId) {
		if (examId.equals("&nbsp;"))
			examId = " ";
		this.examId = examId;
	}

	public String getExamTime() {
		return examTime;
	}

	public void setExamTime(String examTime) {
		if (examTime.equals("&nbsp;"))
			examTime = " ";
		this.examTime = examTime;
	}

	public String getExamGrade() {
		return examGrade;
	}

	public void setExamGrade(String examGrade) {
		if (examGrade.equals("&nbsp;"))
			examGrade = " ";
		this.examGrade = examGrade;
	}

	public String getExamGradeLis() {
		return examGradeLis;
	}

	public void setExamGradeLis(String examGradeLis) {
		if (examGradeLis.equals("&nbsp;"))
			examGradeLis = " ";
		this.examGradeLis = examGradeLis;
	}

	public String getExamGradeRea() {
		return examGradeRea;
	}

	public void setExamGradeRea(String examGradeRea) {
		if (examGradeRea.equals("&nbsp;"))
			examGradeRea = " ";
		this.examGradeRea = examGradeRea;
	}

	public String getExamGradeWri() {
		return examGradeWri;
	}

	public void setExamGradeWri(String examGradeWri) {
		if (examGradeWri.equals("&nbsp;"))
			examGradeWri = " ";
		this.examGradeWri = examGradeWri;
	}

	public String getExamGradeCom() {
		return examGradeCom;
	}

	public void setExamGradeCom(String examGradeCom) {
		if (examGradeCom.equals("&nbsp;"))
			examGradeCom = " ";
		this.examGradeCom = examGradeCom;
	}

	@Override
	public String toString() {
		return "LevelExam{" +
				"year='" + year + '\'' +
				", term='" + term + '\'' +
				", name='" + name + '\'' +
				", examId='" + examId + '\'' +
				", examTime='" + examTime + '\'' +
				", examGrade='" + examGrade + '\'' +
				", examGradeLis='" + examGradeLis + '\'' +
				", examGradeRea='" + examGradeRea + '\'' +
				", examGradeWri='" + examGradeWri + '\'' +
				", examGradeCom='" + examGradeCom + '\'' +
				'}';
	}
}
