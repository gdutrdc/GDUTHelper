package com.rdc.gduthelper.utils.appwidget;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by seasonyuu on 16-5-11.
 */
public class WidgetConfigProvider extends ContentProvider {
	private static final String TAG = WidgetConfigProvider.class.getSimpleName();

	public static final String AUTHORITY = "com.rdc.gduthelper.appwidget.provider";

	public static final String EXAM_CONFIG_CONTENT_URI = "content://" + AUTHORITY + "/exam_config";
	public static final String SCHEDULE_CONFIG_CONTENT_URI = "content://" + AUTHORITY + "/schedule_config";

	private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	private SQLiteDatabase mConfigDatabase;

	static {
		sUriMatcher.addURI(AUTHORITY, "exam_config", 0);
		sUriMatcher.addURI(AUTHORITY, "schedule_config", 1);
	}

	@Override
	public boolean onCreate() {
		ConfigHelper helper = new ConfigHelper(getContext());
		mConfigDatabase = helper.getWritableDatabase();
		return mConfigDatabase != null;
	}

	@Nullable
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		switch (sUriMatcher.match(uri)) {
			case 0:
				return mConfigDatabase.query(ConfigHelper.DATABASE_TABLE_EXAM,
						projection, selection, selectionArgs, null, null, sortOrder, null);
			case 1:
				return mConfigDatabase.query(ConfigHelper.DATABASE_TABLE_SCHEDULE,
						projection, selection, selectionArgs, null, null, sortOrder, null);
		}
		return null;
	}

	@Nullable
	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Nullable
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		switch (sUriMatcher.match(uri)) {
			case 0: {
				try {
					mConfigDatabase.insert(ConfigHelper.DATABASE_TABLE_EXAM, null, values);
					getContext().getContentResolver().notifyChange(uri, null);
				} catch (SQLiteConstraintException e) {
					// 若id已存在 会抛出该异常，但不影响使用
					e.printStackTrace();
				}
			}
			case 1: {
				try {
					mConfigDatabase.insert(ConfigHelper.DATABASE_TABLE_SCHEDULE, null, values);
					getContext().getContentResolver().notifyChange(uri, null);
				} catch (SQLiteConstraintException e) {
					// 若id已存在 会抛出该异常，但不影响使用
					e.printStackTrace();
				}
			}
		}
		return uri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		switch (sUriMatcher.match(uri)) {
			case 0: {
				int count = mConfigDatabase.delete(ConfigHelper.DATABASE_TABLE_EXAM, selection, selectionArgs);
				if (count > 0)
					getContext().getContentResolver().notifyChange(uri, null);
				return count;
			}
			case 1: {
				int count = mConfigDatabase.delete(ConfigHelper.DATABASE_TABLE_SCHEDULE, selection, selectionArgs);
				if (count > 0)
					getContext().getContentResolver().notifyChange(uri, null);
				return count;
			}
		}
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

		switch (sUriMatcher.match(uri)) {
			case 0: {
				int row = mConfigDatabase.update(ConfigHelper.DATABASE_TABLE_EXAM, values, selection, selectionArgs);
				if (row > 0)
					getContext().getContentResolver().notifyChange(uri, null);
				return row;
			}
			case 1: {
				int row = mConfigDatabase.update(ConfigHelper.DATABASE_TABLE_SCHEDULE, values, selection, selectionArgs);
				if (row > 0)
					getContext().getContentResolver().notifyChange(uri, null);
				return row;
			}
		}
		return 0;
	}

	private static class ConfigHelper extends SQLiteOpenHelper {
		private static final String DATABASE_NAME = "app_widget_configs";
		private static final String DATABASE_TABLE_EXAM = "exam_configs";
		private static final String DATABASE_TABLE_SCHEDULE = "schedule_configs";
		private static final int VERSION = 1;
		static final String DATABASE_CREATE_EXAM =
				"create table " + DATABASE_TABLE_EXAM
						+ " (widget_id integer primary key, "
						+ "selection text not null);";
		static final String DATABASE_CREATE_SCHEDULE =
				"create table " + DATABASE_TABLE_SCHEDULE
						+ " (id text not null primary key, " // 学号
						+ " term text not null," // 学年学期
						+ " first_week text not null," // 第一周
						+ " card_colors text not null);"; // 卡片颜色

		ConfigHelper(Context context) {
			super(context, DATABASE_NAME, null, VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE_EXAM);
			db.execSQL(DATABASE_CREATE_SCHEDULE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}
}
