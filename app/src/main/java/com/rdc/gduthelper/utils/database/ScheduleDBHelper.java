package com.rdc.gduthelper.utils.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rdc.gduthelper.bean.Lesson;

import java.util.ArrayList;

/**
 * Created by seasonyuu on 16/1/4.
 */
public class ScheduleDBHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "sched.db";
	private static final int VERSION = 1;
	private static final String TABLE_NAME = "lessons";

	public ScheduleDBHelper(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table " + TABLE_NAME + "("
				+ Column.LESSON_CODE + " text primary key,"
				+ Column.LESSON_NAME + " text,"
				+ Column.LESSON_TEACHER + " text,"
				+ Column.LESSON_TYPE + " text,"
				+ Column.LESSON_TIME + " text,"
				+ Column.LESSON_CLASSROOM + " text,"
				+ Column.LESSON_CREDIT + " text,"
				+ Column.SELECTION + " text"
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
		if (selection != null)
			cursor = db.query(TABLE_NAME, null, "time = ?", new String[]{selection}, null, null, null);
		else
			cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
		while (cursor.moveToNext()) {
			Lesson lesson = new Lesson();
			lesson.setLessonCode(cursor.getString(cursor.getColumnIndex(Column.LESSON_CODE)));
			lesson.setLessonName(cursor.getString(cursor.getColumnIndex(Column.LESSON_NAME)));
			lesson.setLessonClassroom(cursor.getString(cursor.getColumnIndex(Column.LESSON_CLASSROOM)));
			lesson.setLessonTeacher(cursor.getString(cursor.getColumnIndex(Column.LESSON_TEACHER)));
			lesson.setLessonTime(cursor.getString(cursor.getColumnIndex(Column.LESSON_TIME)));
			lesson.setLessonCredit(cursor.getString(cursor.getColumnIndex(Column.LESSON_CREDIT)));
			lesson.setLessonType(cursor.getString(cursor.getColumnIndex(Column.LESSON_TYPE)));
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
			contentValues.put(Column.LESSON_CODE, lesson.getLessonCode());
			contentValues.put(Column.LESSON_NAME, lesson.getLessonName());
			contentValues.put(Column.LESSON_TYPE, lesson.getLessonType());
			contentValues.put(Column.LESSON_TEACHER, lesson.getLessonTeacher());
			contentValues.put(Column.LESSON_TIME, lesson.getLessonTime());
			contentValues.put(Column.LESSON_CLASSROOM, lesson.getLessonClassroom());
			contentValues.put(Column.LESSON_CREDIT, lesson.getLessonCredit());
			contentValues.put(Column.SELECTION, selection);
			db.insert(TABLE_NAME, null, contentValues);
		}
		db.close();
	}

	private static class Column {
		public static final String LESSON_CODE = "lesson_code";
		public static final String LESSON_NAME = "lesson_name";
		public static final String LESSON_CLASSROOM = "lesson_classroom";
		public static final String LESSON_TEACHER = "lesson_teacher";
		public static final String LESSON_TYPE = "lesson_type";
		public static final String LESSON_TIME = "lesson_time";
		public static final String SELECTION = "selection";
		public static final String LESSON_CREDIT = "lesson_credit";
	}
}
