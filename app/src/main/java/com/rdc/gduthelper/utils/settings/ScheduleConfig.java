package com.rdc.gduthelper.utils.settings;

/**
 * Created by seasonyuu on 16-6-6.
 */

public class ScheduleConfig {
	private String id; // 学号
	private String term; // 学年学期
	private String cardColors; // 卡片颜色
	private String firstWeek; // 第一周

	public String getFirstWeek() {
		return firstWeek;
	}

	public void setFirstWeek(String firstWeek) {
		this.firstWeek = firstWeek;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getCardColors() {
		return cardColors;
	}

	public void setCardColors(String cardColors) {
		this.cardColors = cardColors;
	}
}
