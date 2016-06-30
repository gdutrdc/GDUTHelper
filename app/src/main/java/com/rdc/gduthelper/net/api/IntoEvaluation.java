package com.rdc.gduthelper.net.api;

import android.util.Log;

import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.bean.Evaluation;
import com.rdc.gduthelper.net.ApiHelper;
import com.rdc.gduthelper.net.BaseRunnable;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

/**
 * Created by seasonyuu on 16/1/3.
 */
public class IntoEvaluation extends BaseRunnable {
	private Evaluation evaluation;

	public IntoEvaluation(Evaluation evaluation, GGCallback callback) {
		this.callback = callback;
		this.evaluation = evaluation;
	}

	@Override
	public void run() {
		if (evaluation.getLessonCode() == null || evaluation.getLessonCode().equals("xsyjfk.aspx")) {
			if (callback != null)
				callback.onCall("error");
			return;
		}
		String requestUrl = ApiHelper.getURl() + "xsjxpj.aspx?xkkh=" + evaluation.getLessonCode()
				+ "&xh=" + GDUTHelperApp.userXh + "&gnmkdm=N12141";
		try {
			URL url = new URL(requestUrl);
			URLConnection urlConnection = url.openConnection();
			urlConnection.addRequestProperty("Cookie", GDUTHelperApp
					.cookie);
			Log.d("URL", requestUrl);
			urlConnection.addRequestProperty("Referer",
					ApiHelper.getURl() + "xs_main.aspx?xh=" + GDUTHelperApp.userXh);
			InputStreamReader reader = new InputStreamReader(
					new BufferedInputStream(urlConnection.getInputStream()), "gbk");
			BufferedReader in = new BufferedReader(reader);
			String s;
			StringBuffer sb = new StringBuffer();
			int num = 0;
			HashMap<String, Boolean> mark = new HashMap<>();
			while ((s = in.readLine()) != null) {
				sb.append(s);
				sb.append("\n");
				if (s.contains("__VIEWSTATE")) {
					int begin = s.indexOf("value=\"") + 7;
					int end = s.indexOf("\" />");
					GDUTHelperApp.viewState = s.substring(begin, end);
				}
				if (s.contains("value=\"4(良好)\"")) {
					num++;
				}
				if (s.contains("DataGrid1__ctl2_JS")) {
					String[] data = s.split("\"");
					String key = data[1];
					mark.put(key, true);
				}
			}
			evaluation.setTeacherNums(mark.size());
			System.out.print(sb.toString());
			evaluation.setChoices(num);
			if (callback != null)
				callback.onCall(null);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
