package com.rdc.gduthelper.bean;

/**
 * Created by seasonyuu on 16/1/2.
 */
public class Evaluation {
	private String lessonName;
	private String lessonCode;
	private String score;
	private boolean showScore;
	private int scoreColor;
	private boolean isEvaluated;

	public boolean isEvaluated() {
		return isEvaluated;
	}

	public void setEvaluated(boolean evaluated) {
		isEvaluated = evaluated;
	}

	public boolean isShowScore() {
		return showScore;
	}

	public void setShowScore(boolean showScore) {
		this.showScore = showScore;
	}

	public int getScoreColor() {
		return scoreColor;
	}

	public void setScoreColor(int scoreColor) {
		this.scoreColor = scoreColor;
	}

	public String getLessonName() {
		return lessonName;
	}

	public void setLessonName(String lessonName) {
		this.lessonName = lessonName;
	}

	public String getLessonCode() {
		return lessonCode;
	}

	public void setLessonCode(String lessonCode) {
		this.lessonCode = lessonCode;
	}

	public String getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score + "";
	}
}
