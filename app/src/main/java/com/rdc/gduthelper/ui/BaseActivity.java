package com.rdc.gduthelper.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.rdc.gduthelper.R;
import com.rdc.gduthelper.app.GDUTHelperApp;

/**
 * Created by seasonyuu on 15/9/7.
 */
public class BaseActivity extends AppCompatActivity {
	private ProgressDialog progressDialog;
	private AlertDialog warningDialog;

	private boolean mWillNotPaintToolbar = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	protected void setWillNotPaintToolbar(boolean willNotPaintToolbar) {
		mWillNotPaintToolbar = willNotPaintToolbar;
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);

		View toolbar = findViewById(R.id.toolbar);
		if (toolbar != null) {
			if (mWillNotPaintToolbar) {
				toolbar.setBackgroundColor(getResources().getColor(R.color.blue_500));
			}
			MaterialMenuDrawable drawable = new MaterialMenuDrawable(this,
					Color.WHITE, MaterialMenuDrawable.Stroke.THIN);
			drawable.setIconState(MaterialMenuDrawable.IconState.ARROW);
			((Toolbar) toolbar).setNavigationIcon(drawable);
			setSupportActionBar((Toolbar) toolbar);
		}

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
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setHomeButtonEnabled(true);
		}
	}

	public void showProgressDialog(int stringId) {
		showProgressDialog(getResources().getString(stringId));
	}

	public void showProgressDialog(String message) {
		if (progressDialog != null && progressDialog.isShowing())
			progressDialog.dismiss();
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(message);
		progressDialog.show();
	}

	public void showWarning(String message, DialogInterface.OnClickListener onClickListener) {
		if (warningDialog != null && warningDialog.isShowing())
			warningDialog.dismiss();
		warningDialog = new AlertDialog.Builder(this)
				.setMessage(message)
				.setTitle("警告")
				.setPositiveButton(R.string.ensure, onClickListener)
				.create();
		warningDialog.show();
	}

	public void cancelDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			Log.d("BaseActivity", "dismiss progress dialog");
		}
		if (warningDialog != null)
			warningDialog.dismiss();
	}
}
