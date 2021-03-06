package com.rdc.gduthelper.utils;

import android.util.Log;

import com.rdc.gduthelper.bean.Lesson;
import com.rdc.gduthelper.bean.LessonTACR;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

/**
 * Created by seasonyuu on 16/1/8.
 */
public class LessonUtils {
	private static final String TAG = LessonUtils.class.getSimpleName();

	public static String getCompleteWeekTime(int[] weeksArray) {
		String CTime = "第";
		boolean flag = false;
		if (weeksArray == null || weeksArray.length == 0) {
			return null;
		}
		for (int i = 0, j = 0; i < weeksArray.length; i++) {
			if (i + 1 < weeksArray.length)
				if (weeksArray[i + 1] - weeksArray[i] == 1)
					flag = true;
				else
					flag = false;
			else
				flag = false;
			if (!flag) {
				if (i != j)
					CTime += weeksArray[j] + "-" + weeksArray[i] + ",";
				else
					CTime += weeksArray[j] + ",";
				j = i + 1;
			}
		}
		if (CTime.length() == 1)
			CTime += "1-" + weeksArray[weeksArray.length - 1] + ",";
		CTime = CTime.substring(0, CTime.length() - 1) + "周";
		return CTime;
	}

	/**
	 * 把形如 第1-2,4-5,7-9周 拆分开成为不同的项
	 *
	 * @param lessonTACR 要拆分的时间教室对象
	 * @return 拆分后的集合
	 */
	private static ArrayList<LessonTACR> splitLessonTACR(LessonTACR lessonTACR) {
		ArrayList<LessonTACR> lessonTACRs = new ArrayList<>();
		for (int i = 0, j = 0; i < lessonTACR.getWeek().length; i++) {
			if (i < lessonTACR.getWeek().length - 1
					&& lessonTACR.getWeek()[i + 1] - lessonTACR.getWeek()[i] != 1) {
				int newWeek[] = new int[i - j + 1];
				System.arraycopy(lessonTACR.getWeek(), j, newWeek, 0, i - j + 1);
				LessonTACR newLessonTACR = new LessonTACR();
				newLessonTACR.setLessonCode(lessonTACR.getLessonCode());
				newLessonTACR.setClassroom(lessonTACR.getClassroom());
				newLessonTACR.setNum(lessonTACR.getNum());
				newLessonTACR.setWeekday(lessonTACR.getWeekday());
				newLessonTACR.setWeek(newWeek);
				lessonTACRs.add(newLessonTACR);
				j = i + 1;
			} else if (i == lessonTACR.getWeek().length - 1) {
				int newWeek[] = new int[i - j + 1];
				System.arraycopy(lessonTACR.getWeek(), j, newWeek, 0, i - j + 1);
				LessonTACR newLessonTACR = new LessonTACR();
				newLessonTACR.setLessonCode(lessonTACR.getLessonCode());
				newLessonTACR.setClassroom(lessonTACR.getClassroom());
				newLessonTACR.setNum(lessonTACR.getNum());
				newLessonTACR.setWeekday(lessonTACR.getWeekday());
				newLessonTACR.setWeek(newWeek);
				lessonTACRs.add(newLessonTACR);
				j = i + 1;
			}
		}
		return lessonTACRs;
	}

	public static void fillLessonTACRs(Lesson lesson, ArrayList<LessonTACR> lessonTACRs) {
		ArrayList<LessonTACR> newLessonTACRs = new ArrayList<>();
		for (LessonTACR lessonTACR : lessonTACRs) {
			newLessonTACRs.addAll(splitLessonTACR(lessonTACR));
		}
		lessonTACRs = newLessonTACRs;
		lesson.setLessonTACRs(lessonTACRs);
		String lessonTime = "";
		String classrooms = "";
		String[] weekdays = new String[]{"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
		for (LessonTACR lessonTACR : lessonTACRs) {
			ArrayList<String> lessonNum = new ArrayList<>();
			// 分別拆成1,2;3,4;5;6,7;8,9;10,11,12
			String temp = "";
			int[] num = lessonTACR.getNum();
			for (int i = 0; i <= num.length; i++) {
				if (i < num.length) {
					if (num[i] == 3 || num[i] == 5 || num[i] == 6 || num[i] == 8 || num[i] == 10) {
						if (temp.length() != 0)
							lessonNum.add(temp.substring(0, temp.length() - 1));
						temp = "";
					}
				} else if (i == num.length) {
					lessonNum.add(temp.substring(0, temp.length() - 1));
					break;
				}
				temp += num[i] + ",";

			}
			for (String numText : lessonNum) {
				lessonTime += weekdays[lessonTACR.getWeekday()];
				lessonTime += "第" + numText;
				lessonTime += "节";
				if (lessonTACR.getWeek().length != 1)
					lessonTime += "{" + getCompleteWeekTime(lessonTACR.getWeek()) + "}";
				else
					lessonTime += "{第" + lessonTACR.getWeek()[0] + "-" + lessonTACR.getWeek()[0] + "周}";

				lessonTime += ";";

				classrooms += lessonTACR.getClassroom() + ";";
			}


		}
		lessonTime = lessonTime.substring(0, lessonTime.length() - 1);
		classrooms = classrooms.substring(0, classrooms.length() - 1);

		lesson.setLessonTime(lessonTime);
		lesson.setLessonClassroom(classrooms);
	}

	public static ArrayList<LessonTACR> readTimeAndClassroom(Lesson lesson) {
		ArrayList<LessonTACR> lessonTACRs = new ArrayList<>();
		if (lesson.getLessonTime().length() == 0 || lesson.getLessonClassroom().length() == 0)
			return lessonTACRs;
		String[] times = lesson.getLessonTime().split(";");
		String[] classrooms = lesson.getLessonClassroom().split(";");
		for (int i = 0; i < times.length; i++) {
			LessonTACR tacr = new LessonTACR();
			tacr.setLessonCode(lesson.getLessonCode());
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
					wday = 0;
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

			int flag = -1;
			for (int ii = 0; ii < lessonTACRs.size(); ii++) {
				LessonTACR lessonTACR = lessonTACRs.get(ii);
				if (isSameTime(lessonTACR, tacr))
					flag = ii;
			}
			if (flag == -1)
				lessonTACRs.add(tacr);
			else {
				LessonTACR tacr1 = lessonTACRs.get(flag);
				int[] newWeeks = new int[tacr.getWeek().length + tacr1.getWeek().length];
				for (int ii = 0; ii < newWeeks.length; ii++) {
					newWeeks[ii] = ii >= tacr.getWeek().length ?
							tacr1.getWeek()[ii - tacr.getWeek().length] : tacr.getWeek()[ii];
				}
				for (int ii = 0; ii < newWeeks.length; ii++)
					for (int j = 0; j < ii; j++) {
						if (newWeeks[ii] < newWeeks[j]) {
							int temp = newWeeks[ii];
							newWeeks[ii] = newWeeks[j];
							newWeeks[j] = temp;
						}
					}
				tacr1.setWeek(newWeeks);
			}
		}

		return lessonTACRs;
	}

	public static boolean isSameTime(LessonTACR tacr0, LessonTACR tacr1) {
		if (tacr0.getWeekday() == tacr1.getWeekday()) {
			for (int j = 0; j < tacr0.getNum().length && j < tacr1.getNum().length; j++) {
				if (tacr0.getNum()[j] != tacr1.getNum()[j]) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public static String calculateCurrentWeek(String firstWeekDate, Calendar today) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(today.getTime());
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(firstWeekDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		calendar.setTime(date);
		int firstWeek = calendar.get(Calendar.WEEK_OF_YEAR);
		int currentWeek = today.get(Calendar.WEEK_OF_YEAR) - firstWeek + 1;
		if (currentWeek < 0) {
			calendar.add(Calendar.YEAR, -1);
			currentWeek = currentWeek + calendar.getMaximum(Calendar.WEEK_OF_YEAR) - 1;
		}
		return currentWeek + "";
	}

	/**
	 * 由于一天中可能同一门课要上数次，故课程对象不能作为key，而时间是唯一的
	 *
	 * @param firstWeek
	 * @param today
	 * @param lessons
	 * @return
	 */
	public static TreeMap<LessonTACR, Lesson> calculateTodaysLessons(
			Calendar firstWeek, Calendar today, ArrayList<Lesson> lessons) {
		TreeMap<LessonTACR, Lesson> result = new TreeMap<>(new Comparator<LessonTACR>() {
			@Override
			public int compare(LessonTACR lhs, LessonTACR rhs) {
				if (lhs.getNum()[0] > rhs.getNum()[0])
					return 1;
				else if (lhs.getNum()[0] == rhs.getNum()[0])
					return 0;
				else return -1;
			}
		});

		int currentWeek = (int) ((today.getTimeInMillis() - firstWeek.getTimeInMillis())
				/ 1000 / 60 / 60 / 24 / 7 + 1);
		if (currentWeek < 0)
			return result;
		for (Lesson lesson : lessons) {
			ArrayList<LessonTACR> lessonTACRs = readTimeAndClassroom(lesson);
			boolean flag = false;
			for (LessonTACR lessonTACR : lessonTACRs) {
				for (int week : lessonTACR.getWeek())
					if (week == currentWeek) {
						flag = true;
						break;
					}
				if (!flag)
					continue;
				flag = lessonTACR.getWeekday() == today.get(Calendar.DAY_OF_WEEK) - 1;
				if (flag) {
					result.put(lessonTACR, lesson);
				}
			}
		}
		return result;

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


	public static Lesson findLesson(ArrayList<Lesson> lessons, String lessonCode) {
		for (Lesson lesson : lessons) {
			if (lesson.getLessonCode().equals(lessonCode))
				return lesson;
		}
		return null;
	}

	public static boolean lessonInThisWeek(LessonTACR tacr, int week) {
		int[] weeks = tacr.getWeek();
		for (int ii = 0; ii < weeks.length; ii++) {
			if (weeks[ii] == week) {
				return true;
			}
		}
		return false;
	}

	public static String transformTime(LessonTACR tacr) {
		String result = "";
		switch (tacr.getWeekday()) {
			case 1:
				result += "周一";
				break;
			case 2:
				result += "周二";
				break;
			case 3:
				result += "周三";
				break;
			case 4:
				result += "周四";
				break;
			case 5:
				result += "周五";
				break;
			case 6:
				result += "周六";
				break;
			case 7:
				result += "周日";
				break;
		}
		result += "第";
		for (int i = 0; i < tacr.getNum().length; i++) {
			result += tacr.getNum()[i];
			if (i != tacr.getNum().length - 1)
				result += ",";
		}
		result += "节@第";
		int[] weekArr = tacr.getWeek();
		boolean isShort = true;
		if (weekArr.length < 2)
			isShort = false;
		else
			for (int i = 0; i < weekArr.length - 1; i++) {
				if (weekArr[i + 1] - weekArr[i] != 2)
					isShort = false;
			}
		if (isShort) {
			result += weekArr[0] + "-" + weekArr[weekArr.length - 1] + "周|";
			if (weekArr[0] % 2 == 0)
				result += "双周";
			else
				result += "单周";
		} else {
			boolean flag;
			String temp = "";
			for (int i = 0, j = 0; i < weekArr.length - 1; i++) {
				flag = weekArr[i + 1] - weekArr[i] == 1;
				if (!flag) {
					if (i != j)
						temp += weekArr[j] + "-" + weekArr[i] + ",";
					else
						temp += weekArr[j] + ",";
					j = i + 1;
				}
			}
			if (temp.length() == 0) {
				if (weekArr.length == 1)
					temp += weekArr[0] + ",";
				else
					temp += weekArr[0] + "-" + weekArr[weekArr.length - 1] + ",";
			}
			result += temp.substring(0, temp.length() - 1) + "周";
		}
		return result;
	}


	public static String getEvaluationLine(String[] array) {
		for (String s : array) {
			if (s.contains("教学质量评价")) {
				return s;
			}
		}
		return null;
	}

	public static boolean isLessonCode(String lessonCode) {
		if (lessonCode.length() != 33)
			return false;
		String[] data = lessonCode.split("-");
		if (data.length != 6)
			return false;
		return true;
	}

	public static ArrayList<Lesson> getLessonList(String s) {
		ArrayList<Lesson> lessonList = new ArrayList<>();
		String[] split = s.split("<li>");
		for (String string : split) {
			if (!string.contains("xsjxpj.aspx?xkkh"))
				continue;
			Lesson lesson = new Lesson();
			int indexXk = string.indexOf("xkkh=") + 5;
			lesson.setLessonCode(string.substring(indexXk, string.indexOf("&xh=")));
			lesson.setLessonName(string.substring(string.indexOf("GetMc('") + 7, string.indexOf("');\"")));
			if (isLessonCode(lesson.getLessonCode()))
				lessonList.add(lesson);
		}
		return lessonList;
	}

	public static String checkNull(Lesson lesson) {
		if (lesson.getLessonName() == null || lesson.getLessonName().length() == 0)
			return "课程名称为空";
		if (lesson.getLessonTeacher() == null || lesson.getLessonTeacher().length() == 0)
			return "课程教师为空";
		for (LessonTACR lessonTACR : lesson.getLessonTACRs()) {
			if (lessonTACR.getNum() == null || lessonTACR.getNum().length == 0)
				return "存在某项上课时间中上课节数为空";
			if (lessonTACR.getWeek() == null || lessonTACR.getWeek().length == 0)
				return "存在某项上课时间中上课周数为空";
			if (lessonTACR.getClassroom() == null || lessonTACR.getClassroom().length() == 0)
				return "存在某项上课时间中教室为空";
		}
		return null;
	}
}
