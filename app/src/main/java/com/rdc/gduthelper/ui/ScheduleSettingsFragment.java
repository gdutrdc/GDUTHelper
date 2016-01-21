package com.rdc.gduthelper.ui;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.utils.Settings;

/**
 * Created by seasonyuu on 16/1/21.
 */
public class ScheduleSettingsFragment extends PreferenceFragment {
	private Preference mPrefChooseTerm;
	private Preference mPrefChooseWeek;

	private Preference mPrefScheBackgroud;
	private Preference mPrefScheCardColors;

	private Settings mSettings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.schedule_settings);

		mSettings = GDUTHelperApp.getSettings();

		mPrefChooseTerm = findPreference(getResources().getString(R.string.schedule_choose_term_key));
		mPrefChooseTerm.setSummary("2015-2016-1");
		mPrefChooseTerm.setTitle(R.string.schedule_choose_term);

		mPrefChooseWeek = findPreference(getResources().getString(R.string.schedule_choose_week_key));
		mPrefChooseWeek.setTitle(R.string.schedule_choose_week);
		mPrefChooseWeek.setSummary("10");

		mPrefScheBackgroud = findPreference(getResources().getString(R.string.schedule_background_key));
		mPrefScheBackgroud.setTitle(R.string.schedule_background);

		mPrefScheCardColors = findPreference(getResources().getString(R.string.schedule_card_colors_key));
		mPrefScheCardColors.setTitle(R.string.schedule_card_colors);
		mPrefScheCardColors.setSummary(R.string.schedule_card_colors_tips);
	}
}
