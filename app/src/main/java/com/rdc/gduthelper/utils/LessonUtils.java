package com.rdc.gduthelper.utils;

import com.rdc.gduthelper.bean.Lesson;
import com.rdc.gduthelper.bean.LessonTACR;

import java.util.ArrayList;

/**
 * Created by seasonyuu on 16/1/8.
 */
public class LessonUtils {

	public static ArrayList<LessonTACR> readTimeAndClassroom(Lesson lesson) {
		ArrayList<LessonTACR> lessonTACRs = new ArrayList<>();
		if (lesson.getLessonTime().length() == 0 || lesson.getLessonClassroom().length() == 0)
			return lessonTACRs;
		String[] times = lesson.getLessonTime().split(";");
		String[] classrooms = lesson.getLessonClassroom().split(";");
		for (int i = 0; i < times.length; i++) {
			LessonTACR tacr = new LessonTACR();
			String time = times[i];
			String[] data = time.split("\\{");
			String weekday = data[0].substring(0, 2);
			int wday = 0;
			switch (weekday) {
				case "周一":
					wday = 1;
					break;
				case "周二":
					wday = 2;
					break;
				case "周三":
					wday = 3;
					break;
				case "周四":
					wday = 4;
					break;
				case "周五":
					wday = 5;
					break;
				case "周六":
					wday = 6;
					break;
				case "周日":
					wday = 7;
					break;
			}
			tacr.setWeekday(wday);

			String[] num = data[0].substring(3, data[0].length() - 1)
					.split(",");
			int[] n = new int[num.length];
			for (int j = 0; j < n.length; j++)
				n[j] = Integer.parseInt(num[j]);
			tacr.setNum(n);

			String[] week = data[1].substring(0, data[1].length() - 1).split(
					"\\|");
			boolean needCut = week.length > 1;
			boolean cutSingle = false;
			String[] wdata = week[0].substring(1, week[0].length() - 1).split(
					"-");
			int wlength = Integer.parseInt(wdata[1])
					- Integer.parseInt(wdata[0]) + 1;
			if (needCut) {
				wlength = wlength / 2 + 1;
				switch (week[1]) {
					case "单周}":
						cutSingle = false;
						break;
					case "双周}":
						cutSingle = true;
						break;
				}
			}
			int[] w = new int[wlength];
			int start = Integer.parseInt(wdata[0]);
			for (int j = 0; j < w.length; j++) {
				if (needCut) {
					if (cutSingle) {
						if (start % 2 == 1)
							start++;
					} else {
						if (start % 2 == 0)
							start++;
					}
					w[j] = start++;
				} else
					w[j] = start++;
			}
			tacr.setWeek(w);

			tacr.setClassroom(classrooms[i]);

			lessonTACRs.add(tacr);
		}
		return lessonTACRs;
	}

	public static double calculatePoint(Lesson lesson) {
		String lessonGrade = lesson.getLessonGrade();
		double point = 0;
		try {
			point = Double.parseDouble(lessonGrade) / 10 - 5;
			if (point < 1)
				point = 0;
		} catch (NumberFormatException e) {
			if (lessonGrade.equals("优秀")) {
				return 4.5;
			} else if (lessonGrade.equals("良好")) {
				return 3.5;
			} else if (lessonGrade.equals("中等")) {
				return 2.5;
			} else if (lessonGrade.equals("及格")) {
				return 1.5;
			} else if (lessonGrade.equals("不及格")) {
				return 0;
			}
		}
		return point;
	}
}