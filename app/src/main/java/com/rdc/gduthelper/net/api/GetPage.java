package com.rdc.gduthelper.net.api;

import android.util.Log;

import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.net.ApiHelper;
import com.rdc.gduthelper.net.BaseRunnable;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by seasonyuu on 15/8/28.
 */
public class GetPage extends BaseRunnable {

	public GetPage(GGCallback callback) {
		this.callback = callback;
	}

	@Override
	public void run() {
		String address = ApiHelper.getURl();
		String viewState = null;
		try {
			URL url = new URL(address);
			URLConnection urlConnection = url.openConnection();
			InputStreamReader reader = new InputStreamReader(new BufferedInputStream(urlConnection.getInputStream()), "gbk");
			BufferedReader in = new BufferedReader(reader);
			List<String> cookies = urlConnection.getHeaderFields().get("Set-Cookie");
			GDUTHelperApp.cookie = cookies.get(0).split(";", 2)[0];
			urlConnection.setConnectTimeout(5 * 1000);

			String s;
			while ((s = in.readLine()) != null) {
				if (s.contains("__VIEWSTATE")) {
					int begin = s.indexOf("value=\"") + 7;
					int end = s.indexOf("\" />");
					viewState = s.substring(begin, end);
					Log.d(GetPage.class.getSimpleName(), "get VIEW STATE");
				}
			}
			in.close();
			if (callback != null)
				callback.onCall(viewState);

		} catch (IOException e) {
			e.printStackTrace();
			if (callback != null)
				callback.onCall(e);
		}
	}
}
