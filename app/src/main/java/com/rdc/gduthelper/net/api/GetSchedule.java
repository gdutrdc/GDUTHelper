package com.rdc.gduthelper.net.api;

import android.util.Log;

import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.bean.Lesson;
import com.rdc.gduthelper.net.ApiHelper;
import com.rdc.gduthelper.net.BaseRunnable;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by seasonyuu on 16/1/4.
 */
public class GetSchedule extends BaseRunnable {
	private String year;
	private String term;

	public GetSchedule(String year, String term, GGCallback callback) {
		this.callback = callback;
		this.year = year;
		this.term = term;
	}

	@Override
	public void run() {
		HttpURLConnection httpURLConnection = null;
		try {
			requestUrl = ApiHelper.getURl() + "xsxkqk.aspx?xh=" + GDUTHelperApp.userXh + "&xm="
					+ GDUTHelperApp.userXm + "&gnmkdm=N121615";
			httpURLConnection = (HttpURLConnection) new URL(requestUrl).openConnection();
			httpURLConnection.addRequestProperty("Cookie", GDUTHelperApp.cookie);
			httpURLConnection.addRequestProperty("Referer", requestUrl);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setInstanceFollowRedirects(false);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setUseCaches(false);
			String data = "__EVENTTARGET=ddlXQ"
					+ "&__EVENTARGUMENT="
					+ "&__VIEWSTATE="
					+ URLEncoder.encode(GDUTHelperApp.viewState, "gbk")
					+ "&ddlXN=" + year //学年
					+ "&ddlXQ=" + term //学期
					;
			DataOutputStream out = new DataOutputStream(httpURLConnection.getOutputStream());// 获得一个输出流,向服务器写数据
			out.writeBytes(data);
			out.flush();
			out.close();
			int responseCode = httpURLConnection.getResponseCode();
			InputStreamReader reader = new InputStreamReader(httpURLConnection.getInputStream(), "gbk");
			BufferedReader in = new BufferedReader(reader);
			String s;
			StringBuffer sb = new StringBuffer();
			boolean isLesson = false;
			ArrayList<Lesson> lessonList = new ArrayList<>();
			while ((s = in.readLine()) != null) {
				sb.append(s);
				sb.append("\n");
			}
			Log.e(GetSchedule.class.getSimpleName(), httpURLConnection.getURL().toString());
			System.out.print(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
