package com.rdc.gduthelper.net.api;

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
 * Created by seasonyuu on 16/1/4.
 */
public class IntoSchedule extends BaseRunnable {

	public IntoSchedule(GGCallback callback) {
		this.callback = callback;
	}

	@Override
	public void run() {
		String requestUrl = ApiHelper.getURl() + "xsxkqk.aspx?xh="
				+ GDUTHelperApp.userXh + "&xm=" + GDUTHelperApp.userXm + "&gnmkdm=N121615";
		try {
			URL url = new URL(requestUrl);
			URLConnection urlConnection = url.openConnection();
			urlConnection.addRequestProperty("Cookie", GDUTHelperApp.cookie);
			urlConnection.addRequestProperty("Referer", ApiHelper.getURl() + "xs_main.aspx?xh=" + GDUTHelperApp.userXh);
			InputStreamReader reader = new InputStreamReader(new BufferedInputStream(urlConnection.getInputStream()), "gbk");
			BufferedReader in = new BufferedReader(reader);
			String s;
			boolean gotYear = false, gotTerm = false;
			String result = "";
			StringBuffer sb = new StringBuffer();
			while ((s = in.readLine()) != null) {
				sb.append(s);
				sb.append("\n");
				if (s.contains("__VIEWSTATE")) {
					int begin = s.indexOf("value=\"") + 7;
					int end = s.indexOf("\" />");
					GDUTHelperApp.viewState = s.substring(begin, end);
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
				if (s.contains("<select name=\"ddlXN\" onchange=\"__doPostBack('ddlXN','')\" language=\"javascript\"")) {
					gotYear = true;
				}
				if (s.contains("<select name=\"ddlXQ\" onchange=\"__doPostBack('ddlXQ','')\" language=\"javascript\"")) {
					gotTerm = true;
				}
			}
			in.close();
			if (callback != null)
				callback.onCall(result);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
