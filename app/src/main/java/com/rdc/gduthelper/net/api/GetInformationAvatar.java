package com.rdc.gduthelper.net.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.net.ApiHelper;
import com.rdc.gduthelper.net.BaseRunnable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by seasonyuu on 15/9/9.
 */
public class GetInformationAvatar extends BaseRunnable {
	public GetInformationAvatar(GGCallback callback) {
		this.callback = callback;
	}

	@Override
	public void run() {
		String requestURl = ApiHelper.getURl() + "readimagexs.aspx?xh=" + GDUTHelperApp.userXh;
		try {
			URLConnection urlConnection = new URL(requestURl).openConnection();
			urlConnection.addRequestProperty("Cookie", GDUTHelperApp.cookie);

			InputStream inputStream = urlConnection.getInputStream();
			Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
			if (callback != null)
				callback.onCall(bitmap);
		} catch (IOException e) {
			e.printStackTrace();
			if (callback != null)
				callback.onCall(e);
		}
	}
}
