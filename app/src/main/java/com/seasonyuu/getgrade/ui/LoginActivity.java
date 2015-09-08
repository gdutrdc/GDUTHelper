package com.seasonyuu.getgrade.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.seasonyuu.getgrade.R;
import com.seasonyuu.getgrade.app.GGApplication;
import com.seasonyuu.getgrade.net.ApiHelper;
import com.seasonyuu.getgrade.net.BaseRunnable;
import com.seasonyuu.getgrade.net.api.GetCheckCode;
import com.seasonyuu.getgrade.net.api.GetPage;
import com.seasonyuu.getgrade.net.api.Login;

import java.util.ArrayList;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
	private final String TAG = "ConnectionTest";

	private ArrayList<ImageView> iconList;

	private TextInputLayout mTILUserName;
	private TextInputLayout mTILPassword;
	private TextInputLayout mTILSecretCode;
	private CoordinatorLayout mSbContainer;

	private ProgressDialog progressDialog;

	private Bitmap checkCodeBitmap;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.obj != null && msg.obj instanceof Exception) {
				progressDialog.cancel();
				showWarning(((Exception) msg.obj).toString(), null);
				Log.e(TAG, "get Exception");
				return;
			}
			if (msg.what == ApiHelper.GET_PAGE_RESULT) {
				progressDialog.cancel();
				GGApplication.viewState = (String) msg.obj;
				getCheckCode();
			} else if (msg.what == ApiHelper.GET_CHECK_CODE_SUCCESS)
				if (msg.obj != null) {
					checkCodeBitmap = (Bitmap) msg.obj;
					((ImageView) findViewById(R.id.check_code)).setImageBitmap(checkCodeBitmap);
				} else {
					Log.e(TAG, "bitmap is null");
				}
			else if (msg.what == ApiHelper.LOGIN_SUCCESS) {
				progressDialog.cancel();
				GGApplication.getInstance().rememberUser(
						mTILUserName.getEditText().getText().toString(),
						mTILPassword.getEditText().getText().toString()
				);
				startActivity(new Intent(LoginActivity.this, GetGradeActivity.class));
				finish();
			} else if (msg.what == ApiHelper.LOGIN_FAILED) {
				progressDialog.cancel();
				showWarning((String) msg.obj, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						getCheckCode();
					}
				});
			}
		}

	};

	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.check_code:
			case R.id.login_change_check_code:
				progressDialog.show();
				handler.sendEmptyMessage(ApiHelper.GET_PAGE_RESULT);
				break;
			case R.id.login:
				if (mTILSecretCode.getEditText().getText().toString().equals("")) {
					Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				login();
				break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(false);
		getSupportActionBar().setLogo(R.mipmap.ic_launcher);

		progressDialog = new ProgressDialog(this);
//		progressDialog.setCancelable(false);
		progressDialog.setMessage(getString(R.string.loading));
		progressDialog.show();

		findViewById(R.id.login).setOnClickListener(this);
		findViewById(R.id.check_code).setOnClickListener(this);

		initSnackBar();

		initEditText();

		initConnection();
	}

	private void initSnackBar() {
		mSbContainer = (CoordinatorLayout) findViewById(R.id.login_sb_container);

	}

	private void initEditText() {
		iconList = new ArrayList<>();
		iconList.add((ImageView) findViewById(R.id.login_iv_user));
		iconList.add((ImageView) findViewById(R.id.login_iv_password));
		iconList.add((ImageView) findViewById(R.id.login_iv_check_code));

		mTILUserName = ((TextInputLayout) findViewById(R.id.login_user_name));
		mTILUserName.setHint(getString(R.string.user_name));
		mTILPassword = ((TextInputLayout) findViewById(R.id.login_password));
		mTILPassword.setHint(getString(R.string.password));
		mTILSecretCode = (TextInputLayout) findViewById(R.id.login_check_code);
		mTILSecretCode.setHint(getString(R.string.check_code));

		if (GGApplication.getInstance().needRememberUser()) {
			String rememberData = GGApplication.getInstance().getRememberUser();
			if (rememberData != null) {
				String[] data = rememberData.split(";", 2);
				mTILUserName.getEditText().setText(data[0]);
				mTILPassword.getEditText().setText(data[1]);
			}
		}
	}

	private void showProgressDialog(String message) {
		progressDialog.setMessage(message);
		progressDialog.show();
	}


	private void login() {
		if (GGApplication.cookie != null && GGApplication.viewState != null) {
			showProgressDialog("正在登陆");
			new Thread(new Login(
					mTILUserName.getEditText().getText().toString(),
					mTILPassword.getEditText().getText().toString(),
					mTILSecretCode.getEditText().getText().toString(),
					GGApplication.viewState, new BaseRunnable.GGCallback() {
				@Override
				public void onCall(Object obj) {
					if (obj == null) {
						handler.sendEmptyMessage(ApiHelper.LOGIN_SUCCESS);
						GGApplication.userName =
								mTILUserName.getEditText().getText().toString();
					} else {
						Message msg = Message.obtain();
						msg.what = ApiHelper.LOGIN_FAILED;
						msg.obj = obj;
						handler.sendMessage(msg);
					}
				}
			})).start();
		} else {
			progressDialog.cancel();
			showWarning("连接异常，请刷新页面", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					progressDialog.show();
					initConnection();
				}
			});
		}
	}

	private void getCheckCode() {
		mTILSecretCode.getEditText().setText("");
		new Thread(new GetCheckCode(new BaseRunnable.GGCallback() {
			@Override
			public void onCall(Object obj) {
				Message msg = Message.obtain();
				msg.obj = obj;
				msg.what = ApiHelper.GET_CHECK_CODE_SUCCESS;
				handler.sendMessage(msg);
			}
		})).start();
	}

	private void initConnection() {
		new Thread(new GetPage(new BaseRunnable.GGCallback() {
			@Override
			public void onCall(Object obj) {
				Message msg = Message.obtain();
				msg.what = ApiHelper.GET_PAGE_RESULT;
				msg.obj = obj;
				handler.sendMessage(msg);
			}
		})).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.main_settings)
			startActivityForResult(new Intent(this, SettingsActivity.class), 0);
		else if (item.getItemId() == R.id.login_refresh) {
			showProgressDialog("正在刷新");
			initConnection();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
			case 0:
				boolean needRefresh = data.getBooleanExtra("need_refresh", false);
				boolean needChangeTheme = data.getBooleanExtra("need_change_theme",false);
				if (!GGApplication.getInstance().needRememberUser()) {
					GGApplication.getInstance().rememberUser("", "");
				}
				if(needChangeTheme) {
					recreate();
					return;
				}
				if (needRefresh) {
					showProgressDialog("正在刷新");
					initConnection();
				}
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void showWarning(String message, DialogInterface.OnClickListener onClickListener) {
		new AlertDialog.Builder(this)
				.setMessage(message)
				.setTitle("警告")
				.setPositiveButton("确定", onClickListener)
				.show();
	}
}
