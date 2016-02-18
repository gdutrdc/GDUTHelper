package com.rdc.gduthelper.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by seasonyuu on 16/2/16.
 */
public class BitmapUtils {
	public static String IMG_PATH;
	public static final String IMG_NAME = "ScheduleBackground.jpg";

	public static void saveBitmap(Context context, Bitmap bitmap) throws IOException {
		if (IMG_PATH == null)
			IMG_PATH = getImgPath(context);
		File file = new File(IMG_PATH + IMG_NAME);
		if (file.exists())
			file.delete();
		else {
			File noMedia = new File(IMG_PATH + ".nomedia");
			noMedia.createNewFile();
		}
		file.createNewFile();
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
		fileOutputStream.flush();
		fileOutputStream.close();
	}

	public static Bitmap getBitmap(Context context) {
		Bitmap bitmap = null;
		if (IMG_PATH == null)
			IMG_PATH = getImgPath(context);
		File file = new File(IMG_PATH + IMG_NAME);
		if (file.exists())
			bitmap = BitmapFactory.decodeFile(file.getPath());
		return bitmap;
	}

	public static String getImgPath(Context context) {
		return context.getExternalFilesDir(null) + "/";
	}

	public static void deleteBitmap(Context context) {
		if (IMG_PATH == null)
			IMG_PATH = getImgPath(context);
		File file = new File(IMG_PATH + IMG_NAME);
		if (file.exists())
			file.delete();
		Log.e("Bitmap", "had deleted");
	}
}
