package com.rdc.gduthelper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.app.GDUTHelperApp;

/**
 * Created by seasonyuu on 15/9/3.
 */
public class SettingsActivity extends BaseActivity {
	private boolean needRefresh = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		if (getSupportActionBar() != null)
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.replace(R.id.settings_content, new SettingsFragment()).commit();
		}

		needRefresh = GDUTHelperApp.getSettings().isUseDx();
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
		if (needRefresh != GDUTHelperApp.getSettings().isUseDx())
			intent.putExtra("need_refresh", true);
		setResult(0, intent);
		super.onBackPressed();
	}
}
