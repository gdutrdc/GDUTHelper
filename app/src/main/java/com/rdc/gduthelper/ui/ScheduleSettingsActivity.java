package com.rdc.gduthelper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.utils.settings.Settings;

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
