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
			urlConnection.addRequestProperty("Cookie", GDUTHelperApp.cookie);
//			urlConnection.addRequestProperty("Accept", "image/webp,*/*;q=0.8");
//			urlConnection.addRequestProperty("Accept-Encoding", "gzip, deflate, sdch");
//			urlConnection.addRequestProperty("Connection", "keep-alive");
//			urlConnection.addRequestProperty("Upgrade-Insecure-Requests", "1");
			urlConnection.addRequestProperty("Referer", ApiHelper.getURl() + "default2.aspx");
//			urlConnection.addRequestProperty("Host", ApiHelper.getHost());
			InputStream inputStream = urlConnection.getInputStream();
			Bitmap checkCodeBitmap = BitmapFactory.decodeStream(inputStream);
			if (callback != null)
				callback.onCall(checkCodeBitmap);
		} catch (IOException e) {
			e.printStackTrace();
			if (callback != null)
				callback.onCall(e);
		}
	}
}
