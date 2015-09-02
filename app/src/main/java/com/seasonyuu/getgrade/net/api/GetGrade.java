package com.seasonyuu.getgrade.net.api;

import android.util.Log;

import com.seasonyuu.getgrade.app.GGApplication;
import com.seasonyuu.getgrade.bean.Grade;
import com.seasonyuu.getgrade.net.BaseRunnable;

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
			HttpURLConnection httpURLConnection
					= (HttpURLConnection) new URL("http://jwgl.gdut.edu.cn/xscj.aspx?xh=" + GGApplication.userName + "&xm=%D3%E0%CE%FA%C8%BB&gnmkdm=N121605").openConnection();
			httpURLConnection.addRequestProperty("Cookie", GGApplication.cookie);
			httpURLConnection.addRequestProperty("Host", "jwgl.gdut.edu.cn");
			httpURLConnection.addRequestProperty("Accept-Encoding", "gzip, deflate");
			httpURLConnection.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			httpURLConnection.addRequestProperty("Cache-Control", "max-age=0");
			httpURLConnection.addRequestProperty("Referer", "http://jwgl.gdut.edu.cn/xscj.aspx?xh=" + GGApplication.userName + "&xm=%D3%E0%CE%FA%C8%BB&gnmkdm=N121605");
			httpURLConnection.addRequestProperty("Connection", "keep-alive");
			httpURLConnection.addRequestProperty("Origin", "http://jwgl.gdut.edu.cn");
			httpURLConnection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpURLConnection.addRequestProperty("Upgrade-Insecure-Requests", "1");
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setInstanceFollowRedirects(false);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setUseCaches(false);
			String data = "__VIEWSTATE="
					+ URLEncoder.encode(GGApplication.viewState, "iso-8859-1")
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
			Log.e(TAG, "getGrade in post complete");

			int responseCode = httpURLConnection.getResponseCode();// 调用此方法就不必再使用conn.connect()方法
			if (responseCode == 200) {
				if (httpURLConnection.getURL().toString().equals("http://jwgl.gdut.edu.cn/xs_main.aspx?xh=3113006101")) {
					Log.e(TAG, "getGrade success");
				} else if (httpURLConnection.getURL().toString().equals("http://jwgl.gdut.edu.cn/zdy.htm?aspxerrorpath=/default2.aspx")) {

				} else {
					Log.e(TAG, "the url is to \"" + httpURLConnection.getURL().toString() + "\"");
				}
				InputStreamReader reader = new InputStreamReader(httpURLConnection.getInputStream(), "gbk");
				BufferedReader in = new BufferedReader(reader);
				String s;
				StringBuffer sb = new StringBuffer();
				boolean isGrade = false;
				ArrayList<Grade> gradeList = new ArrayList<Grade>();
				while ((s = in.readLine()) != null) {
					sb.append(s);
					sb.append("\n");
					if (isGrade) {
						if (s.contains("</table>")) {
							isGrade = false;
							break;
						}
						String[] datas = s.split("</td>");
						Grade grade = new Grade();
						grade.setLessonCode(datas[0].substring(6, datas[0].length()));
						grade.setLessonName(datas[1].substring(4, datas[1].length()));
						grade.setLessonType(datas[2].substring(4, datas[2].length()));
						grade.setLessonGrade(datas[3].substring(4, datas[3].length()));
						grade.setLessonBelong(datas[4].substring(4, datas[4].length()));
						grade.setLessonCredit(datas[7].substring(4, datas[7].length()));
						gradeList.add(grade);
						in.readLine();
					}
					if (s.contains("<td>课程代码</td><td>课程名称</td><td>课程性质</td><td>成绩</td><td>课程归属</td><td>补考成绩</td><td>重修成绩</td><td>学分</td><td>辅修标记</td>")) {
						isGrade = true;
						in.readLine();
					}
				}
				in.close();

				if (callback != null)
					callback.onCall(gradeList);
			} else {
				Log.e(TAG, "the url is to \"" + httpURLConnection.getURL().toString() + "\"");
				Log.i(TAG, "访问失败" + responseCode);
			}
			Log.e(TAG, "getGrade in complete");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}