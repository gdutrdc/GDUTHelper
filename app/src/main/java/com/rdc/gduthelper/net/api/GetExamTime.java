package com.rdc.gduthelper.net.api;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.bean.Exam;
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
 * Created by seasonyuu on 15/11/26.
 */
public class GetExamTime extends BaseRunnable {
	private static final String TAG = GetExamTime.class.getSimpleName();

	private String year;
	private String term;

	public GetExamTime(String year, String term, GGCallback callback) {
		this.callback = callback;
		this.year = year;
		this.term = term;
	}

	@Override
	public void run() {
		try {
			String requestUrl = ApiHelper.getURl() + "xskscx.aspx?xh="
					+ GDUTHelperApp.userXh + "&xm=" + GDUTHelperApp.userXm + "&gnmkdm=N121604";
			HttpURLConnection httpURLConnection = (HttpURLConnection)
					new URL(requestUrl).openConnection();
			httpURLConnection.addRequestProperty("Cookie", GDUTHelperApp.cookie);
			httpURLConnection.addRequestProperty("Referer", ApiHelper.getURl()
					+ "xskscx.aspx?xh=" + GDUTHelperApp.userXh + "&xm=" + GDUTHelperApp.userXm + "&gnmkdm=N121604");
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setInstanceFollowRedirects(false);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setUseCaches(false);
			String data = "__EVENTTARGET=" + "xnd"
					+ "&__EVENTARGUMENT="
					+ "&__VIEWSTATE="
					+ URLEncoder.encode(GDUTHelperApp.viewState, "iso-8859-1")
					+ "&xnd=" + year //学年
					+ "&xqd=" + term //学期
					;

			DataOutputStream out = new DataOutputStream(httpURLConnection.getOutputStream());// 获得一个输出流,向服务器写数据
			out.writeBytes(data);
			out.flush();
			out.close();

			int responseCode = httpURLConnection.getResponseCode();// 调用此方法就不必再使用conn.connect()方法
			if (responseCode == 200) {

				if (httpURLConnection.getURL().toString().equals(ApiHelper.getURl() + "xs_main.aspx?xh=3113006101")) {
					Log.d(TAG, "getGrade success");
				} else if (httpURLConnection.getURL().toString().equals(ApiHelper.getURl() + "zdy.htm?aspxerrorpath=/default2.aspx")) {

				} else {
					Log.d(TAG, "the url is to \"" + httpURLConnection.getURL().toString() + "\"");
				}
				InputStreamReader reader = new InputStreamReader(httpURLConnection.getInputStream(), "gbk");
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
						if (exams == null)
							exams = new ArrayList<>();
						String[] datas = s.split("</td>");
						Exam exam = new Exam();
						exam.setId(datas[0].substring(8, datas[0].length()));
						exam.setLessonName(datas[1].substring(4, datas[1].length()));
						exam.setStudentName(datas[2].substring(4, datas[2].length()));
						exam.setExamTime(datas[3].substring(4, datas[3].length()));
						exam.setExamPosition(datas[4].substring(4, datas[4].length()));
						exam.setExamType(datas[5].substring(4, datas[5].length()));
						exam.setExamSeat(datas[6].substring(4, datas[6].length()));
						exam.setCampus(datas[7].substring(4, datas[7].length()));
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
				System.out.print(sb.toString());
				in.close();

				if (callback != null)
					callback.onCall(JSON.toJSONString(exams));
			} else {
				Log.i(TAG, "the url is to \"" + httpURLConnection.getURL().toString() + "\"");
				Log.i(TAG, "访问失败 - " + responseCode);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
