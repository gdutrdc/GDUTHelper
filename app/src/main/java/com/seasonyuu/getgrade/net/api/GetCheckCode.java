package com.seasonyuu.getgrade.net.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.seasonyuu.getgrade.app.GGApplication;
import com.seasonyuu.getgrade.net.ApiHelper;
import com.seasonyuu.getgrade.net.BaseRunnable;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by seasonyuu on 15/8/28.
 */
public class GetCheckCode extends BaseRunnable {
	final String checkCode = ApiHelper.getURl() + "CheckCode.aspx";

	public GetCheckCode(GGCallback callback) {
		this.callback = callback;
	}

	@Override
	public void run() {
		try {
			URL url = new URL(checkCode);
			URLConnection urlConnection = url.openConnection();
			urlConnection.addRequestProperty("Cookie", GGApplication.cookie);
			urlConnection.addRequestProperty("Referer", ApiHelper.getURl() + "default2.aspx");
			urlConnection.addRequestProperty("Connection", "keep-alive");
			urlConnection.addRequestProperty("Host", ApiHelper.getHost());
			Bitmap checkCodeBitmap = BitmapFactory.decodeStream(urlConnection.getInputStream());
			if (callback != null)
				callback.onCall(checkCodeBitmap);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
