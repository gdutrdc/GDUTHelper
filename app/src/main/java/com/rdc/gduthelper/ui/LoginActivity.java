package com.rdc.gduthelper.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.net.ApiHelper;
import com.rdc.gduthelper.net.BaseRunnable;
import com.rdc.gduthelper.net.api.GetCheckCode;
import com.rdc.gduthelper.net.api.GetPage;
import com.rdc.gduthelper.net.api.Login;
import com.rengwuxian.materialedittext.MaterialEditText;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
	private final String TAG = LoginActivity.class.getSimpleName();

	private MaterialEditText mEtUserXh;
	private MaterialEditText mEtPassword;
	private MaterialEditText mEtSecretCode;

	private Bitmap checkCodeBitmap;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.obj != null && msg.obj instanceof Exception) {
				cancelDialog();
				showWarning(((Exception) msg.obj).getMessage(), null);
				return;
			}
			if (msg.what == ApiHelper.GET_PAGE_RESULT) {
				cancelDialog();
				GDUTHelperApp.viewState = (String) msg.obj;
				getCheckCode();
			} else if (msg.what == ApiHelper.GET_CHECK_CODE_SUCCESS)
				if (msg.obj != null) {
					checkCodeBitmap = (Bitmap) msg.obj;
					((ImageView) findViewById(R.id.check_code)).setImageBitmap(checkCodeBitmap);
				} else {
					Log.d(TAG, "bitmap is null");
				}
			else if (msg.what == ApiHelper.LOGIN_SUCCESS) {
				cancelDialog();
				GDUTHelperApp.getInstance().rememberUser(
						mEtUserXh.getText().toString(),
						mEtPassword.getText().toString()
				);
				startActivity(new Intent(LoginActivity.this, MainActivity.class));
				finish();
			} else if (msg.what == ApiHelper.LOGIN_FAILED) {
				cancelDialog();
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
				showProgressDialog("正在加载");
				handler.sendEmptyMessage(ApiHelper.GET_PAGE_RESULT);
				break;
			case R.id.login:
				if (mEtSecretCode.getText().toString().equals("")) {
					Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				showProgressDialog("正在登录");
				login();
				break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		findViewById(R.id.login).setOnClickListener(this);
		findViewById(R.id.check_code).setOnClickListener(this);

		initEditText();

		showProgressDialog("正在加载");

		initConnection();
	}

	private void initEditText() {
		mEtUserXh = ((MaterialEditText) findViewById(R.id.login_user_xh));
		mEtPassword = ((MaterialEditText) findViewById(R.id.login_password));
		mEtSecretCode = (MaterialEditText) findViewById(R.id.login_check_code);

		mEtSecretCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					handled = true;
					if (mEtSecretCode.getText().toString().equals("")) {
						Toast.makeText(LoginActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
						return true;
					}
					showProgressDialog("正在登录");
					login();
				}
				return handled;
			}
		});

		if (GDUTHelperApp.getInstance().needRememberUser()) {
			String rememberData = GDUTHelperApp.getInstance().getRememberUser();
			if (rememberData != null) {
				String[] data = rememberData.split(";", 2);
				mEtUserXh.setText(data[0]);
				mEtPassword.setText(data[1]);
			}
		}
	}


	private void login() {
		if (GDUTHelperApp.cookie != null && GDUTHelperApp.viewState != null) {
			showProgressDialog("正在登陆");
			new Thread(new Login(
					mEtUserXh.getText().toString(),
					mEtPassword.getText().toString(),
					mEtSecretCode.getText().toString(),
					GDUTHelperApp.viewState, new BaseRunnable.GGCallback() {
				@Override
				public void onCall(Object obj) {
					if (obj == null) {
						handler.sendEmptyMessage(ApiHelper.LOGIN_SUCCESS);
						GDUTHelperApp.userXh =
								mEtUserXh.getText().toString();
					} else {
						Message msg = Message.obtain();
						msg.what = ApiHelper.LOGIN_FAILED;
						msg.obj = obj;
						handler.sendMessage(msg);
					}
				}
			})).start();
		} else {
			cancelDialog();
			showWarning("连接异常，请刷新页面", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					showProgressDialog("正在加载");
					initConnection();
				}
			});
		}
	}

	private void getCheckCode() {
		mEtSecretCode.setText("");
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
		getMenuInflater().inflate(R.menu.menu_login, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.login_refresh) {
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
				boolean needChangeTheme = data.getBooleanExtra("need_change_theme", false);
				if (!GDUTHelperApp.getInstance().needRememberUser()) {
					GDUTHelperApp.getInstance().rememberUser("", "");
				}
				if (needChangeTheme) {
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
}
