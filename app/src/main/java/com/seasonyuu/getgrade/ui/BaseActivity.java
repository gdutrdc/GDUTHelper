package com.seasonyuu.getgrade.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.seasonyuu.getgrade.R;
import com.seasonyuu.getgrade.app.GGApplication;

/**
 * Created by seasonyuu on 15/9/7.
 */
public class BaseActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		int themeId = GGApplication.getInstance().getThemeId();
		if (themeId != -1)
			setTheme(themeId);
		else
			getTheme().applyStyle(R.style.AppTheme_Blue, true);
		getTheme().applyStyle(themeId, true);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
	}
}
