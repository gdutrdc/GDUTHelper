package com.rdc.gduthelper.net.api;

import com.rdc.gduthelper.net.BaseRunnable;
import com.rdc.gduthelper.utils.Key;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by seasonyuu on 16-7-18.
 */

public class CheckUpdate extends BaseRunnable {
	private String versionName;

	public CheckUpdate(String versionName, GGCallback callback) {
		this.versionName = versionName;
		this.callback = callback;
	}

	@Override
	public void run() {
		String updateURL = Key.CUSTOM_SERVER + "/gduthelper/update.php?version=" + versionName;
		try {
			URLConnection urlConnection = new URL(updateURL).openConnection();
			urlConnection.setConnectTimeout(5 * 1000);
			InputStreamReader reader = new InputStreamReader(new BufferedInputStream(urlConnection.getInputStream()), "utf-8");
			BufferedReader in = new BufferedReader(reader);

			String s = in.readLine();
			JSONObject jsonObject = new JSONObject(s);
			if (callback != null) {
				callback.onCall(jsonObject);
			}
			in.close();
		} catch (IOException | JSONException e) {
			if (callback != null) {
				callback.onCall(null);
			}
			e.printStackTrace();
		}
	}
}
