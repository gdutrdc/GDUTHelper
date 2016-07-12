package com.rdc.gduthelper.bean;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seasonyuu on 16-7-8.
 */

public class YearSchedule implements ParentListItem {
	private String title;
	private List<TermLessons> termLessonsList;


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTermLessonsList(List<TermLessons> termLessonsList) {
		this.termLessonsList = termLessonsList;
	}

	public void initPosition(int position) {
		for (TermLessons termLessons : termLessonsList) {
			termLessons.setParentPosition(position);
			termLessons.setChildPosition(termLessonsList.indexOf(termLessons));
		}
	}

	@Override
	public List<TermLessons> getChildItemList() {
		return termLessonsList;
	}

	@Override
	public boolean isInitiallyExpanded() {
		return true;
	}

	/**
	 * 获取指定学期在列表中的组位置
	 *
	 * @param term          学期
	 * @param yearSchedules 列表数据
	 * @return 组位置
	 */
	public static int getGroupPosition(String term, List<YearSchedule> yearSchedules) {
		int position = -1;
		for (int i = 0; i < yearSchedules.size(); i++) {
			if (term.contains(yearSchedules.get(i).getTitle())) {
				position = i;
				break;
			}
		}
		return position;
	}

	/**
	 * 获取指定学期在列表中的组位置
	 *
	 * @param term          学期
	 * @param yearSchedules 列表数据
	 * @return 子位置
	 */
	public static int getChildPosition(String term, List<YearSchedule> yearSchedules) {
		int position = -1;
		for (int i = 0; i < yearSchedules.size(); i++) {
			if (term.contains(yearSchedules.get(i).getTitle())) {
				for (int j = 0; j < yearSchedules.get(i).getChildItemList().size(); j++) {
					if (yearSchedules.get(i).getChildItemList().get(j).getTerm().equals(term)) {
						position = j;
						break;
					}
				}
			}
		}
		return position;
	}

	public static List<YearSchedule> copyYearSchedules(List<YearSchedule> source) {
		List<YearSchedule> result = new ArrayList<>();
		for (YearSchedule yearSchedule : source) {
			YearSchedule newYearSchedule = new YearSchedule();
			List<TermLessons> newChildItemList = new ArrayList<>();
			for (TermLessons termLessons : yearSchedule.getChildItemList()) {
				TermLessons newTermLessons = new TermLessons();
				newTermLessons.setParentPosition(termLessons.getParentPosition());
				newTermLessons.setChildPosition(termLessons.getChildPosition());
				newTermLessons.setTerm(termLessons.getTerm() + "");
				newTermLessons.setLessons(new ArrayList<>(termLessons.getLessons()));

				newChildItemList.add(newTermLessons);
			}
			newYearSchedule.setTermLessonsList(newChildItemList);
			newYearSchedule.setTitle(yearSchedule.getTitle() + "");// 使之成为新对象
			result.add(newYearSchedule);
		}
		return result;
	}
}
