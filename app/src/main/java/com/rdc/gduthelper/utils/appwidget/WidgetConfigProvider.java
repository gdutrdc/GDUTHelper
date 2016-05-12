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

	public static final String CONFIG_CONTENT_URI = "content://" + AUTHORITY + "/config";

	private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	private SQLiteDatabase mConfigDatabase;

	static {
		sUriMatcher.addURI(AUTHORITY, "config", 0);
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
		return mConfigDatabase.query(ConfigHelper.DATABASE_TABLE,
				projection, selection, selectionArgs, null, null, sortOrder, null);
	}

	@Nullable
	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Nullable
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		try {
			mConfigDatabase.insert(ConfigHelper.DATABASE_TABLE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
		} catch (SQLiteConstraintException e) {
			// 若id已存在 会抛出该异常，但不影响使用
			e.printStackTrace();
		}
		return uri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count = mConfigDatabase.delete(ConfigHelper.DATABASE_TABLE, selection, selectionArgs);
		if (count > 0)
			getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int row = mConfigDatabase.update(ConfigHelper.DATABASE_TABLE, values, selection, selectionArgs);
		if (row > 0)
			getContext().getContentResolver().notifyChange(uri, null);
		return row;
	}

	private static class ConfigHelper extends SQLiteOpenHelper {
		private static final String DATABASE_NAME = "app_widget_configs";
		private static final String DATABASE_TABLE = "configs";
		private static final int VERSION = 1;
		static final String DATABASE_CREATE =
				"create table " + DATABASE_TABLE +
						" (widget_id integer primary key, "
						+ "selection text not null);";

		public ConfigHelper(Context context) {
			super(context, DATABASE_NAME, null, VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}
}
