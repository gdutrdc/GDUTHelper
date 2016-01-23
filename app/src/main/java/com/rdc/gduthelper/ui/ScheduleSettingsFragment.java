package com.rdc.gduthelper.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.ui.widget.ChooseColorsDialog;
import com.rdc.gduthelper.utils.Settings;
import com.rdc.gduthelper.utils.database.ScheduleDBHelper;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

/**
 * Created by seasonyuu on 16/1/21.
 */
public class ScheduleSettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
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
		mPrefChooseTerm.setSummary(mSettings.getScheduleChooseTerm());
		mPrefChooseTerm.setTitle(R.string.schedule_choose_term);
		mPrefChooseTerm.setOnPreferenceClickListener(this);

		mPrefChooseWeek = findPreference(getResources().getString(R.string.schedule_current_week_key));
		mPrefChooseWeek.setTitle(R.string.schedule_current_week);
		String week = mSettings.getScheduleCurrentWeek();
		if (week != null)
			mPrefChooseWeek.setSummary(week);
		else {
			//默认为第一周
			mPrefChooseWeek.setSummary("1");
			mSettings.setScheduleCurrentWeek("1");
		}
		mPrefChooseWeek.setOnPreferenceClickListener(this);

		mPrefScheBackgroud = findPreference(getResources().getString(R.string.schedule_background_key));
		mPrefScheBackgroud.setTitle(R.string.schedule_background);

		mPrefScheCardColors = findPreference(getResources().getString(R.string.schedule_card_colors_key));
		mPrefScheCardColors.setTitle(R.string.schedule_card_colors);
		mPrefScheCardColors.setSummary(R.string.schedule_card_colors_tips);
		mPrefScheCardColors.setOnPreferenceClickListener(this);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		switch (preference.getKey()) {
			case Settings.SCHEDULE_CHOOSE_TERM:
				ScheduleDBHelper helper = new ScheduleDBHelper(getActivity());
				final String[] terms = helper.getOptionalTerms();
				new AlertDialog.Builder(getActivity())
						.setItems(terms, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								mSettings.setScheduleChooseTerm(terms[which]);
								mPrefChooseTerm.setSummary(terms[which]);
							}
						})
						.setTitle(R.string.choose_terms)
						.setPositiveButton(R.string.cancel, null)
						.show();
				break;
			case Settings.SCHEDULE_CURRENT_WEEK:
				final String[] weeks = new String[22];
				weeks[0] = "0(若还未进入学期则当前周为0)";
				for (int i = 1; i < weeks.length; i++) {
					weeks[i] = i + "";
				}
				new AlertDialog.Builder(getActivity())
						.setItems(weeks, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								mSettings.setScheduleCurrentWeek(which + "");
								mPrefChooseWeek.setSummary(weeks[which]);
							}
						})
						.setTitle(R.string.choose_terms)
						.setPositiveButton(R.string.cancel, null)
						.setNegativeButton(R.string.schedule_choose_first_week, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Calendar c = Calendar.getInstance();
								DatePickerDialog pickerDialog = DatePickerDialog.newInstance(
										new DatePickerDialog.OnDateSetListener() {
											@Override
											public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
												Calendar calendar = Calendar.getInstance();
												calendar.set(Calendar.YEAR, year);
												calendar.set(Calendar.MONTH, monthOfYear);
												calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
												Log.e("Tag", year + " " + monthOfYear + " " + dayOfMonth);
												mSettings.setScheduleFirstWeek(calendar);

												mPrefChooseWeek.setSummary(mSettings.getScheduleCurrentWeek());
											}
										}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
								);
								pickerDialog.show(getFragmentManager(), "DatePicker");
							}
						})
						.show();
				break;
			case Settings.SCHEDULE_CARD_COLORS:
				final ChooseColorsDialog chooseColorsDialog = new ChooseColorsDialog(getActivity());
				String colors = mSettings.getScheduleCardColors();
				boolean[] choosed = new boolean[chooseColorsDialog.getChoosedColors().length];
				if (colors == null) {
					int themeId = mSettings.getThemeId();
					int choose = 0;
					if (themeId == R.style.AppTheme_Blue)
						choose = 5;
					else if (themeId == R.style.AppTheme_Pink)
						choose = 1;
					choosed[choose] = true;
				} else {
					String[] choosedColors = colors.split(",");
					for (int i = 0; i < choosedColors.length; i++) {
						int j = Integer.parseInt(choosedColors[i]);
						choosed[j] = true;
					}
				}
				chooseColorsDialog.setChoosedColors(choosed);
				chooseColorsDialog.setOnClickListener(new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == DialogInterface.BUTTON_POSITIVE) {
							boolean[] choosedColors = chooseColorsDialog.getChoosedColors();
							String colors = "";
							for (int i = 0; i < choosedColors.length; i++) {
								if (choosedColors[i])
									colors += i + ",";
							}
							mSettings.setScheduleCardColors(colors.substring(0, colors.length() - 1));
						}
					}
				});
				chooseColorsDialog.show();
				break;
		}
		return false;
	}


}
