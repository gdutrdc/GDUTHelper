package com.rdc.gduthelper.net.api;

import android.util.Log;

import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.bean.Lesson;
import com.rdc.gduthelper.net.ApiHelper;
import com.rdc.gduthelper.net.BaseRunnable;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by seasonyuu on 15/8/28.
 */
public class Login extends BaseRunnable {
	private final String TAG = Login.class.getSimpleName();
	private String viewState;
	private String secretCode;
	private String userName;
	private String password;
	private GGCallback callback;

	public Login(String userName, String password, String secretCode, String viewState, GGCallback callback) {
		this.userName = userName;
		this.password = password;
		this.secretCode = secretCode;
		this.viewState = viewState;
		this.callback = callback;
	}

	@Override
	public void run() {
		try {
			HttpURLConnection httpURLConnection
					= (HttpURLConnection) new URL(ApiHelper.getURl() + "default2.aspx").openConnection();
			httpURLConnection.addRequestProperty("Cookie", GDUTHelperApp.cookie);
			httpURLConnection.addRequestProperty("Referer", ApiHelper.getURl() + "default2.aspx");
			httpURLConnection.addRequestProperty("Upgrade-Insecure-Requests", "1");
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setUseCaches(false);
			String data = "__VIEWSTATE="
					+ URLEncoder.encode(viewState, "iso-8859-1")
					+ "&txtUserName=" + userName
					+ "&TextBox2=" + password
					+ "&txtSecretCode=" + secretCode
					+ "&RadioButtonList1=%D1%A7%C9%FA"
					+ "&Button1="
					+ "&lbLanguage="
					+ "&hidPdrs="
					+ "&hidsc=";

			httpURLConnection.connect();

			DataOutputStream out = new DataOutputStream(httpURLConnection.getOutputStream());// 获得一个输出流,向服务器写数据
			out.writeBytes(data);
			out.flush();
			out.close();

			int responseCode = httpURLConnection.getResponseCode();// 调用此方法就不必再使用conn.connect()方法
			String responseURL = httpURLConnection.getURL().toString();
			Log.d(TAG, "The response URL = " + responseURL);
			if (responseCode == 200) {
				if (responseURL.equals(ApiHelper.getURl() + "xs_main.aspx?xh=" + userName)) {
					//登录成功
					Log.d(TAG, "login success");
					InputStreamReader reader = new InputStreamReader(
							new BufferedInputStream(httpURLConnection.getInputStream()), "gbk");
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
							String evaluationLine = getEvaluationLine(s.split("<li class='top'>"));
							ArrayList<Lesson> lessonList = getLessonList(evaluationLine);
							GDUTHelperApp.setEvaluationList(lessonList);
						}
					}
					if (callback != null) {
						callback.onCall(null);
					}

				} else if (responseURL.equals(ApiHelper.getURl() + "default2.aspx")) {
					InputStreamReader reader = new InputStreamReader(
							new BufferedInputStream(httpURLConnection.getInputStream()), "gbk");
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
				} else if (responseURL.equals(ApiHelper.getURl()
						+ "zdy.htm?aspxerrorpath=/default2.aspx")) {
					if (callback != null)
						callback.onCall("教务系统大姨妈了 = =");
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

	private String getEvaluationLine(String[] array) {
		for (String s : array) {
			if (s.contains("教学质量评价")) {
				return s;
			}
		}
		return null;
	}

	private ArrayList<Lesson> getLessonList(String s) {
		ArrayList<Lesson> lessonList = new ArrayList<>();
		String[] split = s.split("<li>");
		for (String string : split) {
			if (!string.contains("xsjxpj.aspx?xkkh"))
				continue;
			Lesson lesson = new Lesson();
			int indexXk = string.indexOf("xkkh=") + 5;
			lesson.setLessonCode(string.substring(indexXk, indexXk + 33));
			lesson.setLessonName(string.substring(string.indexOf("GetMc('") + 7, string.indexOf("');\"")));
			lessonList.add(lesson);
		}
		return lessonList;
	}
}
