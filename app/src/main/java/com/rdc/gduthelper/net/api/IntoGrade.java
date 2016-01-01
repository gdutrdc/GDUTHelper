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

/**
 * Created by seasonyuu on 15/8/28.
 */
public class IntoGrade extends BaseRunnable {

	public IntoGrade(GGCallback callback) {
		this.callback = callback;
	}

	@Override
	public void run() {
		String requestUrl = ApiHelper.getURl() + "xscj.aspx?xh=" + GDUTHelperApp.userXh + "&xm=" + GDUTHelperApp.userXm + "&gnmkdm=N121605";
		try {
			URL url = new URL(requestUrl);
			URLConnection urlConnection = url.openConnection();
			urlConnection.addRequestProperty("Cookie", GDUTHelperApp.cookie);
			urlConnection.addRequestProperty("Referer", ApiHelper.getURl() + "xs_main.aspx?xh=" + GDUTHelperApp.userXh);
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
					GDUTHelperApp.viewState = s.substring(begin, end);
					Log.d(IntoGrade.class.getSimpleName(), "get VIEW STATE");
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
					in.readLine();
				}
			}
			in.close();
			System.out.print(sb.toString());
			if (sb.toString().contains("<script language='javascript'>alert('")) {
				String warning = sb.toString();

				result = null;
				try {
					result = warning.split(">")[1].split(";")[0].split("'")[1];
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (result == null)
					result = "unknown error.";
			}
			if (callback != null)
				callback.onCall(result);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
