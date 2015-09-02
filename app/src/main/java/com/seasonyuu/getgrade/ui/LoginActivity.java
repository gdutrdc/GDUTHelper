package com.seasonyuu.getgrade.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.seasonyuu.getgrade.R;
import com.seasonyuu.getgrade.app.GGApplication;
import com.seasonyuu.getgrade.net.BaseRunnable;
import com.seasonyuu.getgrade.net.api.GetCheckCode;
import com.seasonyuu.getgrade.net.api.GetPage;
import com.seasonyuu.getgrade.net.api.Login;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
	private final String TAG = "ConnectionTest";

	private ArrayList<ImageView> iconList;
	private TextInputLayout mTILUserName;
	private TextInputLayout mTILPassword;
	private TextInputLayout mTILSecretCode;
	private ProgressDialog progressDialog;

	private Bitmap checkCodeBitmap;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0)
				getCheckCode();
			else if (msg.what == 1)
				if (checkCodeBitmap != null)
					((ImageView) findViewById(R.id.check_code)).setImageBitmap(checkCodeBitmap);
				else
					Log.e(TAG, "bitmap is null");
			else if (msg.what == 2) {
				progressDialog.cancel();
				startActivity(new Intent(LoginActivity.this, GetGradeActivity.class));
				finish();
			} else if (msg.what == 4) {
			}
		}

	};

	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.check_code:
				handler.sendEmptyMessage(0);
				break;
			case R.id.login:
				login();
				break;
			case R.id.main_read_check_code:
				Toast.makeText(this,"然而我还没有开发这个功能 (>...<)",Toast.LENGTH_SHORT).show();
				break;
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		progressDialog = new ProgressDialog(this);
//		progressDialog.setCancelable(false);
		progressDialog.setMessage(getString(R.string.loading));

		findViewById(R.id.main_read_check_code).setOnClickListener(this);
		findViewById(R.id.login).setOnClickListener(this);
		findViewById(R.id.check_code).setOnClickListener(this);

		initEditText();

		initConnection();
	}

	private void initEditText() {
		iconList = new ArrayList<>();
		iconList.add((ImageView) findViewById(R.id.main_iv_user));
		iconList.add((ImageView) findViewById(R.id.main_iv_password));
		iconList.add((ImageView) findViewById(R.id.main_iv_check_code));

		mTILUserName = ((TextInputLayout) findViewById(R.id.main_user_name));
		mTILUserName.setHint(getString(R.string.user_name));
		mTILPassword = ((TextInputLayout) findViewById(R.id.main_password));
		mTILPassword.setHint(getString(R.string.password));
		mTILSecretCode = (TextInputLayout) findViewById(R.id.main_check_code);
		mTILSecretCode.setHint(getString(R.string.check_code));
	}

	private void login() {
		progressDialog.show();
		new Thread(new Login(
				mTILUserName.getEditText().getText().toString(),
				mTILPassword.getEditText().getText().toString(),
				((EditText) findViewById(R.id.et_check_code)).getText().toString(),
				GGApplication.viewState, new BaseRunnable.GGCallback() {
			@Override
			public void onCall(Object obj) {
				if (obj == null) {
					handler.sendEmptyMessage(2);
					GGApplication.userName =
							mTILUserName.getEditText().getText().toString();
				} else {
					Log.e(TAG, obj.toString());
				}
			}
		})).start();
		Log.e(TAG, GGApplication.cookie + " " + GGApplication.viewState);
	}

	private void getCheckCode() {

		new Thread(new GetCheckCode(new BaseRunnable.GGCallback() {
			@Override
			public void onCall(Object obj) {
				checkCodeBitmap = (Bitmap) obj;
				handler.sendEmptyMessage(1);
			}
		})).start();
	}

	private void initConnection() {
		new Thread(new GetPage(new BaseRunnable.GGCallback() {
			@Override
			public void onCall(Object obj) {
				GGApplication.viewState = (String) obj;
				handler.sendEmptyMessage(0);
			}
		})).start();
	}

}
