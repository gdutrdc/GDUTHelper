package com.rdc.gduthelper.net.api;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.bean.Exam;
import com.rdc.gduthelper.net.ApiHelper;
import com.rdc.gduthelper.net.BaseRunnable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by seasonyuu on 15/11/26.
 */
public class IntoGetExamTime extends BaseRunnable {
	public IntoGetExamTime(GGCallback callback) {
		this.callback = callback;
	}

	@Override
	public void run() {
		String requestUrl = ApiHelper.getURl() + "xskscx.aspx?xh="
				+ GDUTHelperApp.userXh + "&xm=" + GDUTHelperApp.userXm + "&gnmkdm=N121604";
		try {
			URL url = new URL(requestUrl);
			URLConnection urlConnection = url.openConnection();
			urlConnection.addRequestProperty("Cookie", GDUTHelperApp.cookie);
			urlConnection.addRequestProperty("Referer", ApiHelper.getURl() + "xs_main.aspx?xh=" + GDUTHelperApp.userXh);
			InputStreamReader reader = new InputStreamReader(new BufferedInputStream(urlConnection.getInputStream()), "gbk");
			BufferedReader in = new BufferedReader(reader);
			String s;
			StringBuffer sb = new StringBuffer();
			boolean gotYear = false, gotTerm = false, isExam = false;
			ArrayList<String> years = null, terms = null;
			ArrayList<Exam> exams = null;
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
					if (years == null)
						years = new ArrayList<>();
					if (s.contains("</select>")) {
						gotYear = false;
					} else {
						String[] temp = s.split(">");
						if (temp.length > 1 && temp[1].length() >= 9)
							years.add(temp[1].substring(0, 9));
					}
				}
				if (gotTerm) {
					if (terms == null)
						terms = new ArrayList<>();
					if (s.contains("</select>")) {
						gotTerm = false;
					} else {
						String[] temp = s.split(">");
						if (temp.length > 1 && temp[1].length() >= 1) {
							terms.add(temp[1].substring(0, 1));
						}
					}
				}
				if (isExam) {
					if (s.contains("</table>")) {
						isExam = false;
						break;
					}
					if(exams == null)
						exams = new ArrayList<>();
					String[] datas = s.split("</td>");
					Exam exam = new Exam();
					exam.setId(datas[0].substring(8, datas[0].length()));
					exam.setLessonName(datas[1].substring(4, datas[1].length()));
					exam.setStudentName(datas[2].substring(4, datas[2].length()));
					exam.setExamTime(datas[3].substring(4, datas[3].length()));
					exam.setExamPosition(datas[4].substring(4, datas[4].length()));
					exam.setExamType(datas[5].substring(4, datas[5].length()));
					exam.setCampus(datas[6].substring(4, datas[6].length()));
					exams.add(exam);
					in.readLine();
				}
				if (s.contains("<td>选课课号</td><td>课程名称</td><td>姓名</td><td>考试时间</td><td>考试地点</td><td>考试形式</td><td>座位号</td><td>校区</td>")) {
					isExam = true;
					in.readLine();
				}
				if (s.contains("<select name=\"xnd\"")) {
					gotYear = true;
				}
				if (s.contains("<select name=\"xqd\"")) {
					gotTerm = true;
				}
			}
			in.close();
			System.out.print(sb.toString());
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject();
				jsonObject.put("years", JSON.toJSONString(years));
				jsonObject.put("terms", JSON.toJSONString(terms));
				jsonObject.put("exams", JSON.toJSONString(exams));
			} catch (JSONException e) {
				jsonObject = null;
				e.printStackTrace();
			}
			if (callback != null && jsonObject != null)
				callback.onCall(jsonObject);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
