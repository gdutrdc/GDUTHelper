package com.rdc.gduthelper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.utils.Settings;

/**
 * Created by seasonyuu on 16/1/21.
 */
public class ScheduleSettingsActivity extends BaseActivity {
	private String term;
	private String week;

	private Settings mSettings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		if (getSupportActionBar() != null)
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.replace(R.id.settings_content, new ScheduleSettingsFragment()).commit();
		}

		mSettings = GDUTHelperApp.getSettings();
		term = mSettings.getScheduleChooseTerm();
		week = mSettings.getScheduleCurrentWeek();
		if (week == null)
			week = "1";
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		if (term.equals(mSettings.getScheduleChooseTerm()) && week.equals(mSettings.getScheduleCurrentWeek())) {

		} else
			intent.putExtra("need_refresh", true);
		setResult(0, intent);
		super.onBackPressed();
	}

}
