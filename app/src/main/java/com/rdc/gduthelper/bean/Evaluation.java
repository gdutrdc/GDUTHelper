package com.rdc.gduthelper.bean;

import com.rdc.gduthelper.R;

/**
 * Created by seasonyuu on 16/1/2.
 */
public class Evaluation {
	private String lessonName;
	private String lessonCode;
	private int score;
	private int choices;
	private boolean showScore = false;
	private boolean isSelected = false;
	private int scoreColor;

	public int getChoices() {
		return choices;
	}

	public void setChoices(int choices) {
		this.choices = choices;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
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

	public int getScore() {
		switch (score) {
			case 1:
				return R.string.evaluation_score_1;
			case 2:
				return R.string.evaluation_score_2;
			case 3:
				return R.string.evaluation_score_3;
			case 4:
				return R.string.evaluation_score_4;
			case 5:
				return R.string.evaluation_score_5;
		}
		return R.string.evaluation_score_1;
	}

	public void setScore(int score) {
		setShowScore(score > 0);
		setScoreColor(MaterialColors.getColor(score));
		this.score = score;
	}
}
