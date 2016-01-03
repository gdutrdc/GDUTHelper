package com.rdc.gduthelper.net.api;

import android.util.Log;

import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.net.ApiHelper;
import com.rdc.gduthelper.net.BaseRunnable;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by seasonyuu on 16/1/3.
 */
public class DoEvaluation extends BaseRunnable {
	private final String DEFAULT_1 = "5(优秀)";
	private final String DEFAULT_2 = "4(良好)";

	private String defaultEval;

	private String score;
	private String lessonCode;

	public DoEvaluation(String lessonCode, String score, GGCallback callback) {
		this.lessonCode = lessonCode;
		this.score = score;
		if (score.equals(DEFAULT_1))
			defaultEval = DEFAULT_2;
		else
			defaultEval = DEFAULT_1;
		this.callback = callback;
	}

	@Override
	public void run() {
		String requestUrl = ApiHelper.getURl() + "xsjxpj.aspx?xkkh=" + lessonCode
				+ "&xh=" + GDUTHelperApp.userXh + "&gnmkdm=N12141";
		try {
			URL url = new URL(requestUrl);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.addRequestProperty("Cookie", GDUTHelperApp.cookie);
			httpURLConnection.addRequestProperty("Referer", requestUrl);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setInstanceFollowRedirects(false);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setUseCaches(false);
			String data = "__EVENTTARGET="
					+ "&__EVENTARGUMENT="
					+ "&__VIEWSTATE="
					+ URLEncoder.encode(GDUTHelperApp.viewState, "iso-8859-1")
					+ "&pjkc=" + lessonCode;
			Log.e(DoEvaluation.class.getSimpleName(), lessonCode);
			for (int i = 2; i <= 18; i++) {
				if (i == 2)
					data += "&DataGrid1:_ctl" + i + ":JS1="
							+ URLEncoder.encode(defaultEval, "iso-8859-1")
							+ "&DataGrid1:_ctl" + i + ":txtjs1=";
				else
					data += "&DataGrid1:_ctl" + i + ":JS1="
							+ URLEncoder.encode(score, "iso-8859-1")
							+ "&DataGrid1:_ctl" + i + ":txtjs1=";
			}
			for (int i = 2; i <= 4; i++) {
				if (i == 2)
					data += "&dgPjc:_ctl" + i + ":jc1="
							+ URLEncoder.encode(defaultEval, "iso-8859-1");
				else
					data += "&dgPjc:_ctl" + i + ":jc1="
							+ URLEncoder.encode(score, "iso-8859-1");
			}
			data += "&pjxx=" +
					"&txt1=" +
					"&TextBox1=0" +
					"&Button1=%B1%A3++%B4%E6";

			DataOutputStream out = new DataOutputStream(httpURLConnection.getOutputStream());// 获得一个输出流,向服务器写数据
			out.writeBytes(URLEncoder.encode(data, "iso-8859-1"));
			out.flush();
			out.close();

			int responseCode = httpURLConnection.getResponseCode();// 调用此方法就不必再使用conn.connect()方法
			if (responseCode == 200) {

				InputStreamReader reader = new InputStreamReader(
						new BufferedInputStream(httpURLConnection.getInputStream()), "gbk");
				BufferedReader in = new BufferedReader(reader);

				String s;
				StringBuffer sb = new StringBuffer();
				while ((s = in.readLine()) != null) {
					sb.append(s);
					sb.append("\n");
					if (s.contains("__VIEWSTATE")) {
						int begin = s.indexOf("value=\"") + 7;
						int end = s.indexOf("\" />");
						GDUTHelperApp.viewState = s.substring(begin, end);
					}
				}
				if (callback != null)
					callback.onCall(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
