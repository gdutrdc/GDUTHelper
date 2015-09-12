package com.seasonyuu.getgrade.net.api;

import android.util.Log;

import com.seasonyuu.getgrade.app.GGApplication;
import com.seasonyuu.getgrade.net.ApiHelper;
import com.seasonyuu.getgrade.net.BaseRunnable;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by seasonyuu on 15/9/11.
 */
public class IntoChangePsw extends BaseRunnable {
	public IntoChangePsw(GGCallback callback) {
		this.callback = callback;
	}

	@Override
	public void run() {
		String requestUrl = ApiHelper.getURl() + "mmxg.aspx?xh=" + GGApplication.userXh + "&xm=" + GGApplication.userXm + "&gnmkdm=N121502";
		try {
			URL url = new URL(requestUrl);
			URLConnection urlConnection = url.openConnection();
			urlConnection.addRequestProperty("Cookie", GGApplication.cookie);
			urlConnection.addRequestProperty("Referer", ApiHelper.getURl() + "xs_main.aspx?xh=" + GGApplication.userXh);
			InputStreamReader reader = new InputStreamReader(new BufferedInputStream(urlConnection.getInputStream()), "gbk");
			BufferedReader in = new BufferedReader(reader);
			String s;
			StringBuffer sb = new StringBuffer();
			String result = "";
			boolean getTips = false;
			while ((s = in.readLine()) != null) {
				sb.append(s);
				sb.append("\n");
				if (s.contains("__VIEWSTATE")) {
					int begin = s.indexOf("value=\"") + 7;
					int end = s.indexOf("\" />");
					GGApplication.viewState = s.substring(begin, end);
					Log.d(IntoGrade.class.getSimpleName(), "get VIEW STATE");
				}
				if (s.contains("登录密码修改规则说明：")) {
					getTips = true;
				}
				if (getTips) {
					String s1 = s.split(">")[1].split("<")[0];
					result += s1 + "\n";
					if (s.contains("</TD>"))
						getTips = false;
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