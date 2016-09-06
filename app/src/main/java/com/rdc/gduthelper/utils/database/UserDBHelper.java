package com.rdc.gduthelper.utils.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rdc.gduthelper.bean.User;
import com.rdc.gduthelper.utils.DESUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seasonyuu on 16-8-20.
 */

public class UserDBHelper extends SQLiteOpenHelper {
	private static final String NAME = "user.db";
	private static final int VERSION = 1;
	private static final String TABLE_NAME = "user";

	private static UserDBHelper sHelper;

	private UserDBHelper(Context context) {
		super(context, NAME, null, VERSION);
	}

	public synchronized static UserDBHelper getInstance(Context context) {
		if (sHelper == null)
			sHelper = new UserDBHelper(context.getApplicationContext());
		return sHelper;
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		String sql = "create table " + TABLE_NAME + "("
				+ Column.XH + " text,"
				+ Column.PASSWORD + " text"
				+ ")";
		sqLiteDatabase.execSQL(sql);
	}

	public void putUser(User user) {
		deleteUser(user.getXh());

		SQLiteDatabase db = getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(Column.XH, user.getXh());
		if (user.getPassword() != null && user.getPassword().length() > 0)
			contentValues.put(Column.PASSWORD, DESUtils.encrypt(user.getPassword()));
		else
			contentValues.put(Column.PASSWORD, "");
		db.insert(TABLE_NAME, null, contentValues);

		db.close();
	}

	public User getUser(String xh) {
		if (xh == null)
			return null;
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, Column.XH + " = ?", new String[]{xh},
				null, null, null);
		User user = null;
		if (cursor.getCount() > 0) {
			cursor.moveToNext();
			String userXh = cursor.getString(cursor.getColumnIndex(Column.XH));
			String password = cursor.getString(cursor.getColumnIndex(Column.PASSWORD));
			user = new User(userXh, password.length() != 0 ? DESUtils.decrypt(password) : "");
		}
		cursor.close();
		db.close();
		return user;
	}

	public List<User> getUsers() {
		ArrayList<User> users = new ArrayList<>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
		while (cursor.moveToNext()) {
			String xh = cursor.getString(cursor.getColumnIndex(Column.XH));
			String password = cursor.getString(cursor.getColumnIndex(Column.PASSWORD));
			User user = new User(xh, password.length() != 0 ? DESUtils.decrypt(password) : "");
			users.add(user);
		}
		cursor.close();
		db.close();
		return users;
	}

	public void deleteUser(String xh) {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(TABLE_NAME, Column.XH + " = ?", new String[]{xh});
		db.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

	}

	private final static class Column {
		static final String XH = "xh";
		static final String PASSWORD = "password";
	}
}
