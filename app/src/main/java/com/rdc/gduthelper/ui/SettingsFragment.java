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
	private Settings mSettings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference_main);
		mSettings = GDUTHelperApp.getSettings();

	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		return false;
	}
}