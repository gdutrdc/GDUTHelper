package com.seasonyuu.getgrade.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.seasonyuu.getgrade.R;
import com.seasonyuu.getgrade.net.ApiHelper;
import com.seasonyuu.getgrade.net.BaseRunnable;
import com.seasonyuu.getgrade.net.api.ChangePsw;
import com.seasonyuu.getgrade.net.api.IntoChangePsw;
import com.seasonyuu.getgrade.utils.CheckPswUtils;

/**
 * Created by seasonyuu on 15/9/11.
 */
public class ChangePswActivity extends BaseActivity implements View.OnClickListener {
	private TextView pswTips;
	private MaterialEditText mEtOld;
	private MaterialEditText mEtNew;
	private MaterialEditText mEtEnsure;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.obj != null && msg.obj instanceof Exception) {
				showWarning(((Exception) msg.obj).getMessage(), null);
				return;
			}
			if (msg.what == ApiHelper.INTO_CHANGE_PSW) {
				pswTips.setText(msg.obj.toString());
				cancelDialog();
			} else if (msg.what == ApiHelper.CHANGE_PSW_SUCCESS) {
				cancelDialog();
				if (msg.obj.equals("修改成功！"))
					new AlertDialog.Builder(ChangePswActivity.this)
							.setTitle("提示")
							.setMessage(msg.obj.toString())
							.setPositiveButton("确定", null)
							.show();
				else
					showWarning(msg.obj.toString(), null);
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_psw);

		showProgressDialog("正在进入");

		pswTips = (TextView) findViewById(R.id.change_psw_tips);
		mEtEnsure = (MaterialEditText) findViewById(R.id.change_psw_ensure);
		mEtNew = (MaterialEditText) findViewById(R.id.change_psw_new);
		mEtOld = (MaterialEditText) findViewById(R.id.change_psw_old);

		mEtNew.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {

				String error = CheckPswUtils.getPswError(s.toString());
				if (error != null) {
					mEtNew.setError(error);
				} else {
					mEtNew.setHelperText(CheckPswUtils.getPswSafeRate(s.toString()));
					mEtNew.setError(null);
				}
			}
		});

		init();

		findViewById(R.id.change_psw_commit).setOnClickListener(this);
	}

	private void init() {
		new Thread(new IntoChangePsw(new BaseRunnable.GGCallback() {
			@Override
			public void onCall(Object obj) {
				Message msg = Message.obtain();
				msg.what = ApiHelper.INTO_CHANGE_PSW;
				msg.obj = obj;
				handler.sendMessage(msg);
			}
		})).start();
	}

	private void changePsw(String oldPassword, String newPassword) {
		new Thread(new ChangePsw(oldPassword, newPassword, new BaseRunnable.GGCallback() {
			@Override
			public void onCall(Object obj) {
				Message msg = Message.obtain();
				msg.what = ApiHelper.CHANGE_PSW_SUCCESS;
				msg.obj = obj;
				handler.sendMessage(msg);
			}
		})).start();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private boolean isEditTextEmpty(EditText editText) {
		return editText.getText().toString().length() == 0;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.change_psw_commit:
				if (isEditTextEmpty(mEtNew) || isEditTextEmpty(mEtOld)) {
					mEtNew.setError(CheckPswUtils.getPswError(mEtNew.getText().toString()));
					return;
				}
				if (!mEtNew.getText().toString().equals(mEtEnsure.getText().toString())) {
					mEtEnsure.setError("两次输入的密码不相同");
					return;
				} else {
					mEtEnsure.setError(null);
					if (mEtNew.getError() != null && mEtNew.getError().length() > 0) {
						Toast.makeText(this, "新的密码不符合规则，请仔细检查", Toast.LENGTH_SHORT).show();
						return;
					}
					showProgressDialog("正在提交");
					changePsw(mEtOld.getText().toString(), mEtNew.getText().toString());

				}
				break;
		}
	}
}
