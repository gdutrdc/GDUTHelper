package com.rdc.gduthelper.net.api;

import com.rdc.gduthelper.app.GGApplication;
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

/**
 * Created by seasonyuu on 15/9/11.
 */
public class ChangePsw extends BaseRunnable {
	private String oldPassword;
	private String newPassword;

	public ChangePsw(String oldPassword, String newPassword, GGCallback callback) {
		this.callback = callback;
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
	}

	@Override
	public void run() {
		requestUrl = ApiHelper.getURl() + "/mmxg.aspx?xh=" + GGApplication.userXh + "&gnmkdm=N121502";
		try {
			HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(requestUrl).openConnection();
			httpURLConnection.addRequestProperty("Cookie", GGApplication.cookie);
			httpURLConnection.addRequestProperty("Referer", requestUrl);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setInstanceFollowRedirects(false);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setUseCaches(false);
			String data = "__VIEWSTATE="
					+ URLEncoder.encode(GGApplication.viewState, "iso-8859-1")
					+ "&TextBox2=" + oldPassword
					+ "&TextBox3=" + newPassword
					+ "&TextBox4=" + newPassword
					+ "&Button1=%D0%DE++%B8%C4";
			DataOutputStream out = new DataOutputStream(httpURLConnection.getOutputStream());// 获得一个输出流,向服务器写数据
			out.writeBytes(data);
			out.flush();
			out.close();
			int responseCode = httpURLConnection.getResponseCode();
			String result = "";
			if (responseCode == 200) {
				if (httpURLConnection.getURL().toString().equals(requestUrl)) {
					InputStreamReader reader = new InputStreamReader(new BufferedInputStream(httpURLConnection.getInputStream()), "gbk");
					BufferedReader in = new BufferedReader(reader);
					String s;
					while ((s = in.readLine()) != null) {
						if(s.contains("<script")&&result.length()==0){
							result += s.split(">")[1].split("'")[1];
							break;
						}
					}
					in.close();
				}
				if (callback != null)
					callback.onCall(result);
			}
		} catch (IOException e) {
			e.printStackTrace();
			if (callback != null)
				callback.onCall(e);
		}
	}
}
