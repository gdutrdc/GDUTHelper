package com.seasonyuu.getgrade.net.api;

import android.util.Log;

import com.seasonyuu.getgrade.app.GGApplication;
import com.seasonyuu.getgrade.net.ApiHelper;
import com.seasonyuu.getgrade.net.BaseRunnable;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

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
			httpURLConnection.addRequestProperty("Cookie", GGApplication.cookie);
			httpURLConnection.addRequestProperty("Host", ApiHelper.getHost());
			httpURLConnection.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			httpURLConnection.addRequestProperty("Referer", ApiHelper.getURl() + "default2.aspx");
			httpURLConnection.addRequestProperty("Connection", "keep-alive");
			httpURLConnection.addRequestProperty("Origin", ApiHelper.getURl());
			httpURLConnection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpURLConnection.addRequestProperty("Upgrade-Insecure-Requests", "1");
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setInstanceFollowRedirects(false);
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
			if (responseCode == 200 || httpURLConnection.getURL().toString().equals(ApiHelper.getURl() + "xs_main.aspx?xh=" + userName)) {
				Log.d(TAG, "login success");

				InputStreamReader reader = new InputStreamReader(httpURLConnection.getInputStream(), "gbk");
				BufferedReader in = new BufferedReader(reader);
				String s;
				while ((s = in.readLine()) != null) {
					if (s.contains("<script language='javascript' defer>alert")) {

					}
				}
				in.close();

				if (callback != null)
					callback.onCall(null);

				Log.e(TAG, "the url is to \"" + httpURLConnection.getURL().toString() + "\"");

			} else {
				if (responseCode == 302 || callback != null)
					callback.onCall(null);
				Log.d(TAG, "访问失败" + responseCode);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
