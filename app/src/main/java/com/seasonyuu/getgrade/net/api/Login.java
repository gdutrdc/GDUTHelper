package com.seasonyuu.getgrade.net.api;

import android.util.Log;

import com.seasonyuu.getgrade.app.GGApplication;
import com.seasonyuu.getgrade.net.ApiHelper;
import com.seasonyuu.getgrade.net.BaseRunnable;

import java.io.BufferedInputStream;
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
//			httpURLConnection.addRequestProperty("Host", ApiHelper.getHost());
//			httpURLConnection.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			httpURLConnection.addRequestProperty("Referer", ApiHelper.getURl() + "default2.aspx");
//			httpURLConnection.addRequestProperty("Connection", "keep-alive");
//			httpURLConnection.addRequestProperty("Origin", ApiHelper.getURl());
//			httpURLConnection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpURLConnection.addRequestProperty("Upgrade-Insecure-Requests", "1");
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setUseCaches(false);
//			httpURLConnection.setInstanceFollowRedirects(false);
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
					System.out.print(sb.toString());
					if (callback != null)
						callback.onCall(failedTips);
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
}
