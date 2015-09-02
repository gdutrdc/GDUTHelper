package com.seasonyuu.getgrade.net.api;

import android.util.Log;

import com.seasonyuu.getgrade.app.GGApplication;
import com.seasonyuu.getgrade.net.BaseRunnable;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by seasonyuu on 15/8/28.
 */
public class IntoGrade extends BaseRunnable {
	private String userName;

	public IntoGrade(String userName, GGCallback callback) {
		this.userName = userName;
		this.callback = callback;
	}

	@Override
	public void run() {
		String address = "http://jwgl.gdut.edu.cn/xscj.aspx?xh=" + userName + "&xm=%D3%E0%CE%FA%C8%BB&gnmkdm=N121605";
		try {
			URL url = new URL(address);
			URLConnection urlConnection = url.openConnection();
			urlConnection.addRequestProperty("Cookie", GGApplication.cookie);
			urlConnection.addRequestProperty("Host", "jwgl.gdut.edu.cn");
			urlConnection.addRequestProperty("Referer", "http://jwgl.gdut.edu.cn/xscj.aspx?xh=3113006101&xm=%D3%E0%CE%FA%C8%BB&gnmkdm=N121605\n ");
			urlConnection.addRequestProperty("Connection", "keep-alive");
			urlConnection.addRequestProperty("Origin", "http://jwgl.gdut.edu.cn");
			urlConnection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			urlConnection.addRequestProperty("Upgrade-Insecure-Requests", "1");
			urlConnection.setAllowUserInteraction(false);
			InputStreamReader reader = new InputStreamReader(new BufferedInputStream(urlConnection.getInputStream()), "gbk");
			BufferedReader in = new BufferedReader(reader);
			String s;
			StringBuffer sb = new StringBuffer();
			boolean gotYear = false, gotTerm = false;
			String result = "";
			while ((s = in.readLine()) != null) {
				sb.append(s);
				sb.append("\n");
				if (s.contains("__VIEWSTATE")) {
					int begin = s.indexOf("value=\"") + 7;
					int end = s.indexOf("\" />");
					GGApplication.viewState = s.substring(begin, end);
				}
				if (gotYear) {
					if (s.contains("</select>")) {
						gotYear = false;
						result = result.substring(0, result.length() - 1) + ";";
					} else {
						String[] temp = s.split(">");
						if (temp.length > 1 && temp[1].length() >= 9)
							result += temp[1].substring(0, 9) + ",";
					}
				}
				if (gotTerm) {
					if (s.contains("</select>")) {
						gotTerm = false;
					} else {
						String[] temp = s.split(">");
						if (temp.length > 1 && temp[1].length() >= 1) {
							result += temp[1].substring(0, 1) + ",";
						}
					}
				}
				if (s.contains("<p class=\"search_con\">学年：<select name=\"ddlXN\" id=\"ddlXN\">")) {
					gotYear = true;
					in.readLine();
				}
				if (s.contains("<span id=\"Label2\">学期：</span>")) {
					gotTerm = true;
					Log.e("Tag", "got term");
					in.readLine();
				}
			}
			in.close();
			System.out.print(sb.toString());
			if (callback != null)
				callback.onCall(result);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
