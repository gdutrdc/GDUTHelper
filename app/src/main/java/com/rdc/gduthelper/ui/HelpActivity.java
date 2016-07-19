package com.rdc.gduthelper.ui;

import android.os.Bundle;

import com.rdc.gduthelper.R;

/**
 * Created by seasonyuu on 16-5-15.
 */
public class HelpActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.replace(R.id.help_content, new HelpFragment()).commit();
		}
	}

}
