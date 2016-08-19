package com.rdc.gduthelper.ui;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.net.ApiHelper;
import com.rdc.gduthelper.net.BaseRunnable;
import com.rdc.gduthelper.net.api.GetCheckCode;
import com.rdc.gduthelper.net.api.GetPage;
import com.rdc.gduthelper.net.api.Login;
import com.rdc.gduthelper.utils.settings.Settings;

public class LoginActivity extends BaseActivity implements View.OnClickListener,
		CompoundButton.OnCheckedChangeListener, View.OnFocusChangeListener {
	private final String TAG = LoginActivity.class.getSimpleName();

	private EditText mEtUserXh;
	private EditText mEtPassword;
	private EditText mEtCheckCode;

	private Switch mSwUseDx;
	private AppCompatCheckBox mCbRememberUser;

	private Bitmap checkCodeBitmap;

	private Settings mSettings;

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
				GDUTHelperApp.getSettings().rememberUser(
						mEtUserXh.getText().toString(),
						mEtPassword.getText().toString()
				);
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
				if (mEtCheckCode.getText().toString().equals("")) {
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

		initPreferences();

		initEditText();

		showProgressDialog("正在加载");

		initConnection();
	}

	private void initPreferences() {
		mCbRememberUser = (AppCompatCheckBox) findViewById(R.id.login_remember_user);
		mSwUseDx = (Switch) findViewById(R.id.login_switch_use_dx);

		mSettings = GDUTHelperApp.getSettings();
		mCbRememberUser.setChecked(mSettings.needRememberUser());
		mSwUseDx.setChecked(mSettings.isUseDx());

		mCbRememberUser.setOnCheckedChangeListener(this);
		mSwUseDx.setOnCheckedChangeListener(this);
	}

	private void initEditText() {
		mEtUserXh = ((EditText) findViewById(R.id.login_user_xh));
		mEtPassword = ((EditText) findViewById(R.id.login_password));
		mEtCheckCode = (EditText) findViewById(R.id.login_check_code);

		mEtCheckCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					handled = true;
					if (mEtCheckCode.getText().toString().equals("")) {
						Toast.makeText(LoginActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
						return true;
					}
					showProgressDialog("正在登录");
					login();
				}
				return handled;
			}
		});

		mEtUserXh.setOnFocusChangeListener(this);
		mEtPassword.setOnFocusChangeListener(this);
		mEtCheckCode.setOnFocusChangeListener(this);

		if (GDUTHelperApp.getSettings().needRememberUser()) {
			String rememberData = GDUTHelperApp.getSettings().getRememberUser();
			if (rememberData != null) {
				String[] data = rememberData.split(";", 2);
				mEtUserXh.setText(data[0]);
				mEtPassword.setText(data[1]);
			}
		}
	}


	private void login() {
		if (GDUTHelperApp.cookie != null && GDUTHelperApp.viewState != null) {
			new Thread(new Login(
					mEtUserXh.getText().toString(),
					mEtPassword.getText().toString(),
					mEtCheckCode.getText().toString(),
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
		mEtCheckCode.setText("");
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
	public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
		switch (buttonView.getId()) {
			case R.id.login_remember_user:
				mSettings.setNeedRememberUser(isChecked);
				break;
			case R.id.login_switch_use_dx:
				new AlertDialog.Builder(this)
						.setTitle(R.string.tips)
						.setMessage(R.string.use_dx_tips)
						.setCancelable(false)
						.setPositiveButton(R.string.ensure, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								showProgressDialog("正在刷新");
								initConnection();
							}
						}).show();
				mSettings.setUseDx(mSwUseDx.isChecked());
				break;
		}
	}

	@Override
	public void onFocusChange(View view, boolean isFocus) {
		switch (view.getId()) {
			case R.id.login_user_xh:
				final ImageView ivUser = (ImageView) findViewById(R.id.login_iv_user_xh);
				if (isFocus) {
					animateColorFilter(ivUser, getResources().getColor(R.color.grey_500),
							getResources().getColor(R.color.pink_a200));
				} else {
					animateColorFilter(ivUser, getResources().getColor(R.color.pink_a200),
							getResources().getColor(R.color.grey_500));
				}
				break;
			case R.id.login_password:
				final ImageView ivPassword = (ImageView) findViewById(R.id.login_iv_password);
				if (isFocus) {
					animateColorFilter(ivPassword, getResources().getColor(R.color.grey_500),
							getResources().getColor(R.color.pink_a200));
				} else {
					animateColorFilter(ivPassword, getResources().getColor(R.color.pink_a200),
							getResources().getColor(R.color.grey_500));
				}
				break;
			case R.id.login_check_code:
				final ImageView ivCheckCode = (ImageView) findViewById(R.id.login_iv_check_code);
				if (isFocus) {
					animateColorFilter(ivCheckCode, getResources().getColor(R.color.grey_500),
							getResources().getColor(R.color.pink_a200));
				} else {
					animateColorFilter(ivCheckCode, getResources().getColor(R.color.pink_a200),
							getResources().getColor(R.color.grey_500));
				}
				break;
		}
	}

	private void animateColorFilter(final ImageView iv, int startColor, int endColor) {
		ValueAnimator animator = ValueAnimator.ofObject(new ColorTypeEvaluator(), startColor, endColor);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				iv.setColorFilter((Integer) valueAnimator.getAnimatedValue());
			}
		});
		animator.setTarget(iv);
		animator.start();
	}

	class ColorTypeEvaluator implements TypeEvaluator<Integer> {

		@Override
		public Integer evaluate(float v, Integer t0, Integer t1) {
			int red = (int) (Color.red(t0) + (Color.red(t1) - Color.red(t0)) * v);
			int green = (int) (Color.green(t0) + (Color.green(t1) - Color.green(t0)) * v);
			int blue = (int) (Color.blue(t0) + (Color.blue(t1) - Color.blue(t0)) * v);

			return Color.rgb(red, green, blue);
		}
	}
}