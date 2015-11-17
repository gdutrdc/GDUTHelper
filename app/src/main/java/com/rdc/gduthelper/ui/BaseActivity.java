package com.rdc.gduthelper.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.app.GDUTHelperApp;

/**
 * Created by seasonyuu on 15/9/7.
 */
public class BaseActivity extends AppCompatActivity {
	private ProgressDialog progressDialog;
	private AlertDialog warningDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		int themeId = GDUTHelperApp.getInstance().getThemeId();
		if (themeId != -1)
			getTheme().applyStyle(themeId, true);
		else
			getTheme().applyStyle(R.style.Blue, true);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);

		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setHomeButtonEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home)
			finish();
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void setContentView(View view) {
		super.setContentView(view);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
	}

	public void showProgressDialog(String message) {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
//  		progressDialog.setCancelable(false);
			progressDialog.show();
		}
		progressDialog.setMessage(message);
		progressDialog.show();
	}

	public void showWarning(String message, DialogInterface.OnClickListener onClickListener) {
		if (warningDialog == null)
			warningDialog = new AlertDialog.Builder(this)
					.setMessage(message)
					.setTitle("警告")
					.setPositiveButton("确定", onClickListener)
					.create();
		else {
			warningDialog.setMessage(message);
			warningDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", onClickListener);
		}
		warningDialog.show();
	}

	public void cancelDialog() {
		if (progressDialog != null && progressDialog.isShowing())
			progressDialog.cancel();
		if (warningDialog != null && warningDialog.isShowing())
			warningDialog.cancel();
	}
}