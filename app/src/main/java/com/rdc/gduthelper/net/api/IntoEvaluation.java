package com.rdc.gduthelper.net.api;

import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.net.ApiHelper;
import com.rdc.gduthelper.net.BaseRunnable;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by seasonyuu on 16/1/3.
 */
public class IntoEvaluation extends BaseRunnable {
	private String lessonCode;

	public IntoEvaluation(String lessonCode, GGCallback callback) {
		this.callback = callback;
		this.lessonCode = lessonCode;
	}

	@Override
	public void run() {
		String requestUrl = ApiHelper.getURl() + "xsjxpj.aspx?xkkh=" + lessonCode
				+ "&xh=" + GDUTHelperApp.userXh + "&gnmkdm=N12141";
		try {
			URL url = new URL(requestUrl);
			URLConnection urlConnection = url.openConnection();
			urlConnection.addRequestProperty("Cookie", GDUTHelperApp.cookie);
			urlConnection.addRequestProperty("Referer",
					ApiHelper.getURl() + "xs_main.aspx?xh=" + GDUTHelperApp.userXh);
			InputStreamReader reader = new InputStreamReader(
					new BufferedInputStream(urlConnection.getInputStream()), "gbk");
			BufferedReader in = new BufferedReader(reader);
			String s;
			StringBuffer sb = new StringBuffer();
			while ((s = in.readLine()) != null) {
				sb.append(s);
				sb.append("\n");
				if (s.contains("__VIEWSTATE")) {
					int begin = s.indexOf("value=\"") + 7;
					int end = s.indexOf("\" />");
					GDUTHelperApp.viewState = s.substring(begin, end);
				}
			}
//			System.out.println(sb.toString());
			if (callback != null)
				callback.onCall(null);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
