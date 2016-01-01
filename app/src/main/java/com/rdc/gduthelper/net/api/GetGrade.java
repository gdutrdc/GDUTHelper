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
 * Created by seasonyuu on 15/8/29.
 */
public class GetGrade extends BaseRunnable {
	private final String TAG = GetGrade.class.getSimpleName();

	public static final int BY_TERM = 0x1; //通过学期查询
	public static final int BY_YEAR = 0x2; //通过学年查询
	public static final int BY_ALL = 0x4; //在校学习成绩查询

	private int type;
	private String year;
	private String term;

	public GetGrade(String year, String term, GGCallback callback) {
		this.callback = callback;
		this.type = BY_TERM;
		this.term = term;
		this.year = year;
		if (year != null) {
			if (term != null)
				type = BY_TERM;
			else
				type = BY_YEAR;
		} else
			type = BY_ALL;
	}


	@Override
	public void run() {
		try {
			HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(
					ApiHelper.getURl() + "xscj.aspx?xh=" + GDUTHelperApp.userXh + "&xm=" + GDUTHelperApp.userXm + "&gnmkdm=N121605").openConnection();
			httpURLConnection.addRequestProperty("Cookie", GDUTHelperApp.cookie);
			httpURLConnection.addRequestProperty("Referer", ApiHelper.getURl()
					+ "xscj.aspx?xh=" + GDUTHelperApp.userXh + "&xm=" + GDUTHelperApp.userXm + "&gnmkdm=N121605");
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setInstanceFollowRedirects(false);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setUseCaches(false);
			String data = "__VIEWSTATE="
					+ URLEncoder.encode(GDUTHelperApp.viewState, "iso-8859-1")
					+ "&ddlXN=" + year //学年
					+ "&ddlXQ=" + term //学期
					+ "&txtQSCJ=0"
					+ "&txtZZCJ=100";
			switch (type) {
				case BY_TERM:
					data += "&Button1=%B0%B4%D1%A7%C6%DA%B2%E9%D1%AF";
					break;
				case BY_YEAR:
					data += "&Button5=%B0%B4%D1%A7%C4%EA%B2%E9%D1%AF";
					break;
				case BY_ALL:
					data += "&Button2=%D4%DA%D0%A3%D1%A7%CF%B0%B3%C9%BC%A8%B2%E9%D1%AF";
					break;
			}

			DataOutputStream out = new DataOutputStream(httpURLConnection.getOutputStream());// 获得一个输出流,向服务器写数据
			out.writeBytes(data);
			out.flush();
			out.close();
			Log.d(TAG, "getGrade in post complete");

			int responseCode = httpURLConnection.getResponseCode();// 调用此方法就不必再使用conn.connect()方法
			if (responseCode == 200) {
				String url = httpURLConnection.getURL().toString();
				if (url.equals(ApiHelper.getURl() + "xs_main.aspx?xh=3113006101")) {
					Log.d(TAG, "getGrade success");
				} else if (url.equals(ApiHelper.getURl() + "zdy.htm?aspxerrorpath=/default2.aspx")) {
					if (callback != null)
						callback.onCall("校园网大姨妈啦！");
				} else if (url.equals(ApiHelper.getURl() + "xs_main.aspx?xh=3113006101")) {

				} else {
					Log.d(TAG, "the url is to \"" + httpURLConnection.getURL().toString() + "\"");
				}
				InputStreamReader reader = new InputStreamReader(httpURLConnection.getInputStream(), "gbk");
				BufferedReader in = new BufferedReader(reader);
				String s;
				StringBuffer sb = new StringBuffer();
				boolean isGrade = false;
				ArrayList<Lesson> lessonList = new ArrayList<Lesson>();
				while ((s = in.readLine()) != null) {
					sb.append(s);
					sb.append("\n");
					if (isGrade) {
						if (s.contains("</table>")) {
							isGrade = false;
							break;
						}
						String[] datas = s.split("</td>");
						Lesson lesson = new Lesson();
						lesson.setLessonCode(datas[0].substring(6, datas[0].length()));
						lesson.setLessonName(datas[1].substring(4, datas[1].length()));
						lesson.setLessonType(datas[2].substring(4, datas[2].length()));
						lesson.setLessonGrade(datas[3].substring(4, datas[3].length()));
						lesson.setLessonBelong(datas[4].substring(4, datas[4].length()));
						lesson.setLessonCredit(datas[7].substring(4, datas[7].length()));
						lessonList.add(lesson);
						in.readLine();
					}
					if (s.contains("<td>课程代码</td><td>课程名称</td><td>课程性质</td><td>成绩</td><td>课程归属</td><td>补考成绩</td><td>重修成绩</td><td>学分</td><td>辅修标记</td>")) {
						isGrade = true;
						in.readLine();
					}
				}
				in.close();

				if (callback != null)
					callback.onCall(lessonList);
			} else {
				Log.i(TAG, "the url is to \"" + httpURLConnection.getURL().toString() + "\"");
				Log.i(TAG, "failed code " + responseCode);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}