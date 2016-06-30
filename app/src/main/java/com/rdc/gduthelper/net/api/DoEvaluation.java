package com.rdc.gduthelper.net.api;

import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.bean.Evaluation;
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
 * Created by seasonyuu on 16/1/4.
 */
public class DoEvaluation extends BaseRunnable {
	private final String DEFAULT_1 = "5(优秀)";
	private final String DEFAULT_2 = "4(良好)";

	private String defaultEval;
	private Evaluation evaluation;

	private String score;
	private String lessonCode;

	public DoEvaluation(Evaluation evaluation, GGCallback callback) {
		this.evaluation = evaluation;
		this.lessonCode = evaluation.getLessonCode();
		this.score = GDUTHelperApp.getInstance().getResources().getString(evaluation.getScore());
		if (score.equals(DEFAULT_1))
			defaultEval = DEFAULT_2;
		else
			defaultEval = DEFAULT_1;
		this.callback = callback;
	}

	@Override
	public void run() {
		requestUrl = ApiHelper.getURl() + "xsjxpj.aspx?xkkh=" + lessonCode
				+ "&xh=" + GDUTHelperApp.userXh + "&gnmkdm=N12141";
		try {
			URL url = new URL(requestUrl);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.addRequestProperty("Cookie", GDUTHelperApp.cookie);
			httpURLConnection.addRequestProperty("Referer", requestUrl);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setInstanceFollowRedirects(true);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setUseCaches(false);
			String data = "__EVENTTARGET="
					+ "&__EVENTARGUMENT="
					+ "&__VIEWSTATE="
					+ URLEncoder.encode(GDUTHelperApp.viewState, "iso-8859-1")
					+ "&pjkc=" + lessonCode;
			for (int i = 2; i <= evaluation.getChoices(); i++) {
				for (int j = 1; j <= evaluation.getTeacherNums(); j++)
					if (i == 2) {
						data += "&DataGrid1%3A_ctl" + i + "%3AJS" + j + "="
								+ URLEncoder.encode(defaultEval, "gbk")
								+ "&DataGrid1%3A_ctl" + i + "%3Atxtjs" + j + "=";
					} else {
						data += "&DataGrid1%3A_ctl" + i + "%3AJS" + j + "="
								+ URLEncoder.encode(score, "gbk")
								+ "&DataGrid1%3A_ctl" + i + "%3Atxtjs" + j + "=";
					}
				//&DataGrid1%3A_ctl9%3AJS1=5%28%D3%C5%D0%E3%29
				//&DataGrid1%3A_ctl9%3Atxtjs1=
			}
			for (int i = 2; i <= 4; i++) {
				if (i == 2)
					data += "&dgPjc%3A_ctl" + i + "%3Ajc1="
							+ URLEncoder.encode(defaultEval, "gbk");
				else
					data += "&dgPjc%3A_ctl" + i + "%3Ajc1="
							+ URLEncoder.encode(score, "gbk");
				//&dgPjc%3A_ctl2%3Ajc1=5%28%D3%C5%D0%E3%29
			}
			data += "&pjxx=" +
					"&txt1=" +
					"&TextBox1=0" +
					"&Button2=+%CC%E1++%BD%BB+";

			DataOutputStream out = new DataOutputStream(httpURLConnection.getOutputStream());// 获得一个输出流,向服务器写数据
			out.writeBytes(data);
			out.flush();
			out.close();

			int responseCode = httpURLConnection.getResponseCode();// 调用此方法就不必再使用conn.connect()方法

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
			System.out.println(sb.toString());
			if (callback != null)
				callback.onCall(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
