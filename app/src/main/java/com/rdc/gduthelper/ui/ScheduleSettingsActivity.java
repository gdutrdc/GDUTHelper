package com.rdc.gduthelper.ui;

import android.os.Bundle;

import com.rdc.gduthelper.R;

/**
 * Created by seasonyuu on 16/1/21.
 */
public class ScheduleSettingsActivity extends BaseActivity {
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
	}
}
