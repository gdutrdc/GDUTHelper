package com.seasonyuu.getgrade.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import com.seasonyuu.getgrade.R;
import com.seasonyuu.getgrade.app.GGApplication;

/**
 * Created by seasonyuu on 15/9/3.
 */
public class SettingsActivity extends BaseActivity {
	private boolean needRefresh = false;
	private int themeId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.replace(R.id.settings_content, new SettingsFragment()).commit();
		}

		needRefresh = GGApplication.getInstance().isUseDx();
		themeId = GGApplication.getInstance().getThemeId();
	}

	public static class SettingsFragment extends PreferenceFragment {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preference_main);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		if (needRefresh != GGApplication.getInstance().isUseDx())
			intent.putExtra("need_refresh", true);
		if(themeId != GGApplication.getInstance().getThemeId())
			intent.putExtra("need_change_theme",true);
		setResult(0, intent);
		super.onBackPressed();
	}
}
