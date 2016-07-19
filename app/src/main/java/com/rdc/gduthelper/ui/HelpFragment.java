package com.rdc.gduthelper.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v7.app.AlertDialog;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.net.BaseRunnable;
import com.rdc.gduthelper.net.api.CheckUpdate;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * Created by seasonyuu on 16-7-19.
 */

public class HelpFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
	private Preference mPrefAppVersion;
	private Preference mPrefCheckUpdate;
	private Preference mPrefGitHubSource;
	private Preference mPrefLicenses;
	private Preference mPrefFAQ;
	private Preference mPrefFallBack;
	private HelpHandler mHandler = new HelpHandler(this);

	private static class HelpHandler extends Handler {
		private WeakReference<HelpFragment> wrFragment;
		private ProgressDialog progressDialog;
		static final int SHOW_PROGRESS_DIALOG = 0;
		static final int SHOW_UPDATE_DIALOG = 1;

		HelpHandler(HelpFragment fragment) {
			wrFragment = new WeakReference<>(fragment);
		}

		@Override
		public void handleMessage(Message msg) {
			if (wrFragment.get() == null) {
				return;
			}

			final Activity activity = wrFragment.get().getActivity();
			if (msg.what == SHOW_PROGRESS_DIALOG) {
				if (progressDialog == null) {
					progressDialog = new ProgressDialog(activity);
					progressDialog.setMessage(activity.getString(R.string.loading));
				}
				progressDialog.show();
			} else if (msg.what == SHOW_UPDATE_DIALOG) {
				if (progressDialog != null)
					if (progressDialog.isShowing()) {
						progressDialog.dismiss();
						progressDialog = null;
					} else {
						return;
					}

				try {
					JSONObject jsonObject = (JSONObject) msg.obj;
					String changeLog = jsonObject.getString("change_log");
					final String url = jsonObject.getString("newest_version");
					new AlertDialog.Builder(activity)
							.setTitle("新版本可下载")
							.setMessage(changeLog)
							.setPositiveButton("开始下载", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {
									Uri uri = Uri.parse(url);
									activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
								}
							})
							.setNegativeButton(R.string.next_time, null)
							.show();
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.help);

		initPreferences();
	}

	private void initPreferences() {
		mPrefAppVersion = findPreference(getString(R.string.app_name));
		mPrefCheckUpdate = findPreference(getString(R.string.check_update));
		mPrefFallBack = findPreference(getString(R.string.fallback));
		mPrefFAQ = findPreference(getString(R.string.faq));
		mPrefLicenses = findPreference(getString(R.string.licenses));
		mPrefGitHubSource = findPreference(getString(R.string.github_source));

		mPrefAppVersion.setOnPreferenceClickListener(this);
		mPrefCheckUpdate.setOnPreferenceClickListener(this);
		mPrefFallBack.setOnPreferenceClickListener(this);
		mPrefFAQ.setOnPreferenceClickListener(this);
		mPrefLicenses.setOnPreferenceClickListener(this);
		mPrefGitHubSource.setOnPreferenceClickListener(this);

		try {
			mPrefAppVersion.setSummary(
					getActivity().getPackageManager().getPackageInfo(
							getActivity().getApplication().getPackageName(), 0).versionName);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}


	}

	private void checkUpdate() {
		new Thread(new CheckUpdate(mPrefAppVersion.getSummary().toString(), new BaseRunnable.GGCallback() {
			@Override
			public void onCall(Object obj) {
				if (obj != null) {
					Message msg = Message.obtain();
					msg.what = HelpHandler.SHOW_UPDATE_DIALOG;
					msg.obj = obj;
					mHandler.sendMessage(msg);
				}
			}
		})).start();
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference.equals(mPrefLicenses)) {
			Intent licenses = new Intent(getActivity(), WebViewActivity.class);
			licenses.putExtra("title", getResources().getString(R.string.licenses));
			licenses.putExtra("url", "file:///android_asset/licenses.html");
			startActivity(licenses);
			return true;
		} else if (preference.equals(mPrefCheckUpdate)) {
			mHandler.sendEmptyMessage(HelpHandler.SHOW_PROGRESS_DIALOG);
			checkUpdate();
			return true;
		} else if (preference.equals(mPrefGitHubSource) || preference.equals(mPrefFallBack)) {
			String url = preference.getSummary().toString();
			Uri uri = Uri.parse(url);
			getActivity().startActivity(new Intent(Intent.ACTION_VIEW, uri));
			return true;
		}
		return false;
	}
}
