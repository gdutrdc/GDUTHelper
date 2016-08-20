package com.rdc.gduthelper.utils.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.bean.Exam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by seasonyuu on 15/12/1.
 */
public class ExamTimeDBHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "gdut_helper.db";
	private static final int VERSION = 1;
	private static final String TABLE_EXAM_TIME = "exam_time";


	public ExamTimeDBHelper(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table " + TABLE_EXAM_TIME + "("
				+ Column.ID + " integer primary key autoincrement,"
				+ Column.XH + " varchar(12),"
				+ Column.LESSON_NAME + " text,"
				+ Column.EXAM_TIME + " text,"
				+ Column.STUDENT_NAME + " text,"
				+ Column.EXAM_ID + " text,"
				+ Column.EXAM_POSITION + " text,"
				+ Column.EXAM_SEAT + " varchar(5),"
				+ Column.EXAM_TYPE + " text,"
				+ Column.CAMPUS + " text"
				+ ")");
	}

	public void insertExamTime(Exam exam) {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(TABLE_EXAM_TIME, Column.EXAM_ID + "=? and " + Column.XH + "=?",
				new String[]{exam.getId(), GDUTHelperApp.userXh});
		ContentValues cv = new ContentValues();
		cv.put(Column.XH, GDUTHelperApp.userXh);
		cv.put(Column.EXAM_ID, exam.getId());
		cv.put(Column.CAMPUS, exam.getCampus());
		cv.put(Column.EXAM_POSITION, exam.getExamPosition());
		cv.put(Column.EXAM_SEAT, exam.getExamSeat());
		cv.put(Column.EXAM_TIME, exam.getExamTime());
		cv.put(Column.EXAM_TYPE, exam.getExamType());
		cv.put(Column.LESSON_NAME, exam.getLessonName());
		cv.put(Column.STUDENT_NAME, exam.getStudentName());
		db.insert(TABLE_EXAM_TIME, null, cv);
		db.close();
	}

	public void deleteExamTimes(String xh) {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(TABLE_EXAM_TIME, Column.XH + " = ?", new String[]{xh});
		db.close();
	}

	/**
	 * @param xh        学号
	 * @param selection 学年-学期 形如 2013-2014-1
	 * @return
	 */
	public ArrayList<Exam> getExamTimes(String xh, String selection) {
		ArrayList<Exam> list = new ArrayList<>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_EXAM_TIME, null
				, Column.XH + "=?", new String[]{xh}, null, null, null);
		while (cursor.moveToNext()) {
			String examId = cursor.getString(cursor.getColumnIndex(Column.EXAM_ID));
			if (selection != null && !examId.contains(selection))
				continue;
			Exam exam = new Exam();
			exam.setId(examId);
			exam.setExamSeat(cursor.getString(cursor.getColumnIndex(Column.EXAM_SEAT)));
			exam.setCampus(cursor.getString(cursor.getColumnIndex(Column.CAMPUS)));
			exam.setExamType(cursor.getString(cursor.getColumnIndex(Column.EXAM_TYPE)));
			exam.setExamPosition(cursor.getString(cursor.getColumnIndex(Column.EXAM_POSITION)));
			exam.setExamTime(cursor.getString(cursor.getColumnIndex(Column.EXAM_TIME)));
			exam.setLessonName(cursor.getString(cursor.getColumnIndex(Column.LESSON_NAME)));
			exam.setStudentName(cursor.getString(cursor.getColumnIndex(Column.STUDENT_NAME)));
			list.add(exam);
		}
		db.close();
		Collections.sort(list, new Comparator<Exam>() {
			@Override
			public int compare(Exam lhs, Exam rhs) {
				if (lhs.getExamCount() < rhs.getExamCount() || rhs.getExamCount() < 0)
					return -1;
				else if (lhs.getExamCount() > rhs.getExamCount() || lhs.getExamCount() < 0)
					return 1;
				return 0;
			}
		});
		cursor.close();
		return list;
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	private static class Column {
		private static final String XH = "xh";
		private static final String ID = "_id";
		private static final String LESSON_NAME = "lesson_name";
		private static final String EXAM_TIME = "exam_time";
		private static final String EXAM_ID = "exam_id";
		private static final String STUDENT_NAME = "student_name";
		private static final String EXAM_POSITION = "exam_position";
		private static final String EXAM_TYPE = "exam_type";
		private static final String CAMPUS = "campus";
		private static final String EXAM_SEAT = "exam_seat";
	}
}
