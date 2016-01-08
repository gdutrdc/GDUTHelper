package com.rdc.gduthelper.utils.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.bean.Lesson;
import com.rdc.gduthelper.bean.LessonTACR;
import com.rdc.gduthelper.utils.LessonUtils;

import java.util.ArrayList;

/**
 * Created by seasonyuu on 16/1/4.
 */
public class ScheduleDBHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "sched.db";
	private static final int VERSION = 1;
	private static final String TABLE_LESSONS = "lessons";
	private static final String TABLE_LESSON_TIMES = "lesson_times";

	public ScheduleDBHelper(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table " + TABLE_LESSONS + "("
				+ Column.LESSON_CODE + " text,"
				+ Column.XH + " text,"
				+ Column.LESSON_NAME + " text,"
				+ Column.LESSON_TEACHER + " text,"
				+ Column.LESSON_TYPE + " text,"
				+ Column.LESSON_TIME + " text,"
				+ Column.LESSON_CLASSROOM + " text,"
				+ Column.LESSON_CREDIT + " text,"
				+ Column.SELECTION + " text,"
				+ "primary key("
				+ Column.XH + ","
				+ Column.LESSON_CODE + ","
				+ Column.SELECTION
				+ "))");
		db.execSQL("create table " + TABLE_LESSON_TIMES + "("
				+ Column.XH + " text,"
				+ Column.SELECTION + " text,"
				+ Column.LESSON_CODE + " text,"
				+ Column.WEEK + " text,"
				+ Column.NUM + " text,"
				+ Column.WEEKDAY + " text,"
				+ Column.LESSON_CLASSROOM + " text,"
				+ "primary key("
				+ Column.XH + ","
				+ Column.LESSON_CODE + ","
				+ Column.SELECTION + ","
				+ Column.WEEK + ","
				+ Column.WEEKDAY + ","
				+ Column.NUM
				+ "),"
				+ "foreign key(" + Column.XH + ") references " + TABLE_LESSONS + "(" + Column.XH + ")"
				+ " on delete cascade,"
				+ "foreign key(" + Column.LESSON_CODE + ") references " + TABLE_LESSONS + "(" + Column.LESSON_CODE + ")"
				+ " on delete cascade,"
				+ "foreign key(" + Column.SELECTION + ") references " + TABLE_LESSONS + "(" + Column.SELECTION + ")"
				+ " on delete cascade"
				+ ")");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	/**
	 * 获取指定要求的课程列表
	 *
	 * @param selection 形如 2014-2015-1 为null则返回所有课程
	 * @return 指定的课程列表
	 */
	public ArrayList<Lesson> getLessonList(String selection) {
		ArrayList<Lesson> result = new ArrayList<>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = null;
		String xh = GDUTHelperApp.userXh;
		if (xh == null) {
			String up = GDUTHelperApp.getSettings().getRememberUser();
			if (up == null) {
				return result;
			}
			String[] data = up.split(";", 2);
			xh = data[0];
		}

		if (selection == null)
			return result;
		cursor = db.query(TABLE_LESSONS, null,
				Column.SELECTION + " = ? and " + Column.XH + " = ?", new String[]{selection
						, xh}, null, null, null);
		while (cursor.moveToNext()) {
			Lesson lesson = new Lesson();
			lesson.setLessonCode(cursor.getString(cursor.getColumnIndex(Column.LESSON_CODE)));
			lesson.setLessonName(cursor.getString(cursor.getColumnIndex(Column.LESSON_NAME)));
			lesson.setLessonClassroom(cursor.getString(cursor.getColumnIndex(Column.LESSON_CLASSROOM)));
			lesson.setLessonTeacher(cursor.getString(cursor.getColumnIndex(Column.LESSON_TEACHER)));
			lesson.setLessonTime(cursor.getString(cursor.getColumnIndex(Column.LESSON_TIME)));
			lesson.setLessonCredit(cursor.getString(cursor.getColumnIndex(Column.LESSON_CREDIT)));
			lesson.setLessonType(cursor.getString(cursor.getColumnIndex(Column.LESSON_TYPE)));
			Cursor c = db.query(TABLE_LESSON_TIMES, null,
					Column.SELECTION + " = ? and " + Column.XH + " = ? and " + Column.LESSON_CODE + " = ?",
					new String[]{selection, xh, lesson.getLessonCode()}, null, null, null);
			ArrayList<LessonTACR> tacrs = new ArrayList<>();
			while (c.moveToNext()) {
				LessonTACR tacr = new LessonTACR();
				tacr.setClassroom(c.getString(c.getColumnIndex(Column.LESSON_CLASSROOM)));

				String[] numData = c.getString(c.getColumnIndex(Column.NUM)).split(",");
				int[] num = new int[numData.length];
				for (int i = 0; i < num.length; i++)
					num[i] = Integer.parseInt(numData[i]);
				tacr.setNum(num);

				tacr.setWeekday(Integer.parseInt(c.getString(c.getColumnIndex(Column.WEEKDAY))));

				String[] weekData = c.getString(c.getColumnIndex(Column.WEEK)).split(",");
				int[] week = new int[weekData.length];
				for (int i = 0; i < week.length; i++)
					week[i] = Integer.parseInt(weekData[i]);
				tacr.setWeek(week);

				tacrs.add(tacr);
			}
			lesson.setLessonTACRs(tacrs);
			result.add(lesson);
		}
		cursor.close();
		db.close();
		return result;
	}

	public void addLessonList(ArrayList<Lesson> lessons, String selection) {
		SQLiteDatabase db = getWritableDatabase();
		for (Lesson lesson : lessons) {
			ContentValues contentValues = new ContentValues();
			contentValues.put(Column.XH, GDUTHelperApp.userXh);
			contentValues.put(Column.LESSON_CODE, lesson.getLessonCode());
			contentValues.put(Column.LESSON_NAME, lesson.getLessonName());
			contentValues.put(Column.LESSON_TYPE, lesson.getLessonType());
			contentValues.put(Column.LESSON_TEACHER, lesson.getLessonTeacher());
			contentValues.put(Column.LESSON_TIME, lesson.getLessonTime());
			contentValues.put(Column.LESSON_CLASSROOM, lesson.getLessonClassroom());
			contentValues.put(Column.LESSON_CREDIT, lesson.getLessonCredit());
			contentValues.put(Column.SELECTION, selection);
			db.insert(TABLE_LESSONS, null, contentValues);

			for (LessonTACR tacr : LessonUtils.readTimeAndClassroom(lesson)) {
				ContentValues foreignValue = new ContentValues();
				foreignValue.put(Column.XH, GDUTHelperApp.userXh);
				foreignValue.put(Column.LESSON_CODE, lesson.getLessonCode());
				foreignValue.put(Column.SELECTION, selection);
				foreignValue.put(Column.WEEKDAY, tacr.getWeekday());
				String weekdata = "";
				for (int week : tacr.getWeek()) {
					weekdata += week + ",";
				}
				weekdata = weekdata.substring(0, weekdata.length() - 1);
				foreignValue.put(Column.WEEK, weekdata);
				String numdata = "";
				for (int num : tacr.getNum())
					numdata += num + ",";
				foreignValue.put(Column.NUM, numdata.substring(0, numdata.length() - 1));

				foreignValue.put(Column.LESSON_CLASSROOM, tacr.getClassroom());

				db.insert(TABLE_LESSON_TIMES, null, foreignValue);
			}
		}
		db.close();
	}

	private static class Column {
		public static final String XH = "xh";
		public static final String LESSON_CODE = "lesson_code";
		public static final String LESSON_NAME = "lesson_name";
		public static final String LESSON_CLASSROOM = "lesson_classroom";
		public static final String LESSON_TEACHER = "lesson_teacher";
		public static final String LESSON_TYPE = "lesson_type";
		public static final String LESSON_TIME = "lesson_time";
		public static final String SELECTION = "selection";
		public static final String LESSON_CREDIT = "lesson_credit";

		public static final String WEEK = "week";
		public static final String WEEKDAY = "weekday";
		public static final String NUM = "num";
	}
}
