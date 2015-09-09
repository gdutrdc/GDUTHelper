package com.seasonyuu.getgrade.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.seasonyuu.getgrade.R;
import com.seasonyuu.getgrade.app.GGApplication;

/**
 * Created by seasonyuu on 15/9/8.
 */
public class MainActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View view = View.inflate(this, R.layout.activity_main, null);
		setContentView(view);

		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(false);


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.main_settings:
				startActivityForResult(new Intent(this, SettingsActivity.class), 0);
				break;
			case R.id.main_help:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			boolean needRefresh = data.getBooleanExtra("need_refresh", false);
			boolean needChangeTheme = data.getBooleanExtra("need_change_theme", false);
			if (!GGApplication.getInstance().needRememberUser()) {
				GGApplication.getInstance().rememberUser("", "");
			}
			if (needChangeTheme) {
				recreate();
				return;
			}
			if (needRefresh) {
				new AlertDialog.Builder(this)
						.setMessage("您当前修改了要访问的URL，请重新登录")
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								startActivity(new Intent(MainActivity.this, LoginActivity.class));
								finish();
							}
						})
						.setCancelable(false)
						.show();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.main_get_grade:
				startActivity(new Intent(this, GetGradeActivity.class));
				break;
			case R.id.main_get_information:
				startActivity(new Intent(this, GetInformationActivity.class));
				break;
		}
	}
}
