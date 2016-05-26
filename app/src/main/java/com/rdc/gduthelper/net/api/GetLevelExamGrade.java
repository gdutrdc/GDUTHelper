package com.rdc.gduthelper.net.api;

import android.util.Log;

import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.bean.LevelExam;
import com.rdc.gduthelper.net.ApiHelper;
import com.rdc.gduthelper.net.BaseRunnable;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by seasonyuu on 16-5-18.
 */
public class GetLevelExamGrade extends BaseRunnable {
	private String requestURL = ApiHelper.getURl() + "xsdjkscx.aspx?xh=" + GDUTHelperApp.userXh
			+ "&xm=" + GDUTHelperApp.userXm + "&gnmkdm=N121606";

	public GetLevelExamGrade(GGCallback callback) {
		this.callback = callback;
	}

	@Override
	public void run() {
		try {
			URLConnection urlConnection = new URL(requestURL).openConnection();
			urlConnection.setConnectTimeout(5 * 1000);

			urlConnection.addRequestProperty("Cookie", GDUTHelperApp.cookie);
			urlConnection.addRequestProperty("Referer", ApiHelper.getURl());

			InputStreamReader reader = new InputStreamReader(new BufferedInputStream(urlConnection.getInputStream()), "gbk");
			BufferedReader in = new BufferedReader(reader);

			String s;
			ArrayList<LevelExam> resultList = new ArrayList<>();
			boolean isIn = false;
			while ((s = in.readLine()) != null) {
				if (s.contains("<tr class=\"datelisthead\">")) {
					isIn = true;
					in.readLine();
					in.readLine();
					s = in.readLine();
				}
				if (isIn) {
					if (s.contains("</table>")) {
						isIn = false;
						break;
					}
					String[] data = s.split("<");
					LevelExam levelExam = new LevelExam();
					levelExam.setYear(data[1].substring(3, data[1].length()));
					levelExam.setTerm(data[3].substring(3, data[3].length()));
					levelExam.setName(data[5].substring(3, data[5].length()));
					levelExam.setExamId(data[7].substring(3, data[7].length()));
					levelExam.setExamTime(data[9].substring(3, data[9].length()));
					levelExam.setExamGrade(data[11].substring(3, data[11].length()));
					levelExam.setExamGradeLis(data[13].substring(3, data[13].length()));
					levelExam.setExamGradeRea(data[15].substring(3, data[15].length()));
					levelExam.setExamGradeWri(data[17].substring(3, data[17].length()));
					levelExam.setExamGradeCom(data[19].substring(3, data[19].length()));
					resultList.add(levelExam);
					in.readLine();

				}
			}
			in.close();
			if (callback != null) {
				callback.onCall(resultList);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
