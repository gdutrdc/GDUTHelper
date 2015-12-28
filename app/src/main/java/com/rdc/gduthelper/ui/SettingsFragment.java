package com.rdc.gduthelper.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.utils.Settings;

/**
 * Created by seasonyuu on 15/12/28.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
	private Preference mPrefTheme;

	private Settings mSettings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference_main);
		mSettings = GDUTHelperApp.getSettings();

		mPrefTheme = findPreference(getResources().getString(R.string.theme_key));
		mPrefTheme.setSummary(getResources().getStringArray(R.array.theme_array)
				[mSettings.getInt(getResources().getString(R.string.theme_key), 0)]);
		mPrefTheme.setOnPreferenceClickListener(this);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		switch (preference.getKey()) {
			case Settings.THEME:
				new AlertDialog.Builder(getActivity())
						.setTitle(R.string.theme)
						.setSingleChoiceItems(getResources().getStringArray(R.array.theme_array),
								mSettings.getInt(Settings.THEME, 0),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										mSettings.putInt(Settings.THEME, which);
										mPrefTheme.setSummary(getResources()
												.getStringArray(R.array.theme_array)[which]);
										dialog.dismiss();
									}
								}).show();
				break;
		}
		return false;
	}
}