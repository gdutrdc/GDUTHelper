package com.rdc.gduthelper.net.api;

import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.bean.Lesson;
import com.rdc.gduthelper.net.ApiHelper;
import com.rdc.gduthelper.net.BaseRunnable;
import com.rdc.gduthelper.utils.LessonUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by seasonyuu on 16/2/4.
 */
public class GetMain extends BaseRunnable {
	private static final String TAG = GetMain.class.getSimpleName();

	private String xh;
	private int responseCode;
	private String responseURL;
	private String viewState;

	public GetMain(String xh, GGCallback callback) {
		this.callback = callback;
		this.xh = xh;
	}

	@Override
	public void run() {
		requestUrl = ApiHelper.getURl() + "xs_main.aspx?xh=" + xh;
		try {
			URL url = new URL(requestUrl);
			URLConnection urlConnection = url.openConnection();
			urlConnection.addRequestProperty("Cookie", GDUTHelperApp.cookie);
			urlConnection.addRequestProperty("Referer", ApiHelper.getURl());
			urlConnection.setConnectTimeout(5 * 1000);
			if (responseCode == 200) {
				if (responseURL.equals(ApiHelper.getURl() + "xs_main.aspx?xh=" + xh)) {
					//登录成功
					InputStreamReader reader = new InputStreamReader(
							new BufferedInputStream(urlConnection.getInputStream()), "gbk");
					BufferedReader in = new BufferedReader(reader);
					String s;
					StringBuffer sb = new StringBuffer();
					while ((s = in.readLine()) != null) {
						sb.append(s);
						sb.append("\n");
						if (s.contains("<span id=\"xhxm\">")) {
							String[] temps = s.split(">");
							String text = temps[1].split("<")[0];
							GDUTHelperApp.userXm = URLEncoder.encode(
									text.substring(0, text.length() - 2), "gbk");
						}
						if (s.contains("<span class='down'> 教学质量评价</span>")) {
							String evaluationLine = LessonUtils.getEvaluationLine(s.split("<li class='top'>"));
							ArrayList<Lesson> lessonList = LessonUtils.getLessonList(evaluationLine);
							GDUTHelperApp.setEvaluationList(lessonList);
						}
					}
					if (callback != null) {
						callback.onCall(null);
					}

				} else if (responseURL.equals(ApiHelper.getURl() + "default2.aspx")) {
					InputStreamReader reader = new InputStreamReader(
							new BufferedInputStream(urlConnection.getInputStream()), "gbk");
					BufferedReader in = new BufferedReader(reader);
					String s;
					StringBuffer sb = new StringBuffer();
					while ((s = in.readLine()) != null) {
						sb.append(s);
						sb.append("\n");
					}
					int indexStart = sb.indexOf("<script language='javascript' defer>alert('")
							+ "<script language='javascript' defer>alert('".length();
					int indexEnd = sb.indexOf("');document.getElementById(");
					String failedTips = sb.substring(indexStart, indexEnd);
					if (callback != null)
						callback.onCall(failedTips);
				}
			} else if (callback != null) {
				callback.onCall(new Exception("Request Failed : code " + responseCode));
			}

		} catch (IOException e) {
			e.printStackTrace();
			if (callback != null)
				callback.onCall(e);
		}
	}
}
