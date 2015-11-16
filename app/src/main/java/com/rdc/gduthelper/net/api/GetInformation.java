package com.rdc.gduthelper.net.api;

import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.bean.Information;
import com.rdc.gduthelper.net.ApiHelper;
import com.rdc.gduthelper.net.BaseRunnable;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by seasonyuu on 15/9/9.
 */
public class GetInformation extends BaseRunnable {
	public GetInformation(GGCallback callback) {
		this.callback = callback;
	}

	@Override
	public void run() {
		try {
			String requestUrl = ApiHelper.getURl() + "xsgrxx.aspx?xh=" + GDUTHelperApp.userXh + "&xm=" + GDUTHelperApp.userXm + "&gnmkdm=N121501";
			URLConnection urlConnection;
			URL url = new URL(requestUrl);
			urlConnection = url.openConnection();
			urlConnection.addRequestProperty("Cookie", GDUTHelperApp.cookie);
			urlConnection.addRequestProperty("Referer", ApiHelper.getURl() + "xs_main.aspx?xh=" + GDUTHelperApp.userXh);
			InputStreamReader reader = new InputStreamReader(new BufferedInputStream(urlConnection.getInputStream()), "gbk");
			BufferedReader in = new BufferedReader(reader);
			String s;
			Information information = new Information();
			while ((s = in.readLine()) != null) {
				if (s.contains("id=\"xh\""))
					information.setAccount(matchSpanValue(s));
				if (s.contains("id=\"xm\""))
					information.setName(matchSpanValue(s));
				if (s.contains("id=\"lbl_xb\""))
					information.setSex(matchSpanValue(s));
				if (s.contains("id=\"lbl_rxrq\""))
					information.setEnrollmentDate(matchSpanValue(s));
				if (s.contains("id=\"lbl_csrq\""))
					information.setBirthday(matchSpanValue(s));
				if (s.contains("id=\"byzx\""))
					information.setMiddleSchool(matchInputValue(s));
				if (s.contains("id=\"lbl_mz\""))
					information.setNation(matchSpanValue(s));
				if (s.contains("id=\"ssh\""))
					information.setDormitory(matchInputValue(s));
				if (s.contains("id=\"lbl_xy\""))
					information.setCollege(matchSpanValue(s));
				if (s.contains("id=\"lbl_xi\""))
					information.setDepartment(matchSpanValue(s));
				if (s.contains("id=\"lbl_xzb\""))
					information.setClazz(matchSpanValue(s));
				if (s.contains("id=\"lbl_dqszj\""))
					information.setGrade(matchSpanValue(s));
				if (s.contains("id=\"lbl_CC\""))
					information.setDegree(matchSpanValue(s));
			}
			if (callback != null)
				callback.onCall(information);
		} catch (IOException e) {
			e.printStackTrace();
			if (callback != null)
				callback.onCall(e);
		}

	}

	private String matchSpanValue(String s) {
		return s.split(">")[2].split("<")[0];
	}

	private String matchInputValue(String s) {
		return s.split("value=\"")[1].split("\"")[0];
	}
}
