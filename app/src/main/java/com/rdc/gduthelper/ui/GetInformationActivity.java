package com.rdc.gduthelper.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.bean.Information;
import com.rdc.gduthelper.net.ApiHelper;
import com.rdc.gduthelper.net.BaseRunnable;
import com.rdc.gduthelper.net.api.GetInformation;
import com.rdc.gduthelper.net.api.GetInformationAvatar;

/**
 * Created by seasonyuu on 15/9/9.
 */
public class GetInformationActivity extends BaseActivity implements View.OnClickListener {
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.obj != null && msg.obj instanceof Exception) {
				showWarning(((Exception) msg.obj).getMessage(), null);
				return;
			}
			if (msg.what == ApiHelper.GET_INFORMATION_AVATAR) {
				((ImageView) findViewById(R.id.get_information_avatar))
						.setImageBitmap((Bitmap) msg.obj);
				cancelDialog();
			} else if (msg.what == ApiHelper.GET_INFORMATION_SUCCESS) {
				mInformation = (Information) msg.obj;
				showInformation(mInformation);
				getAvatar();
			}
		}
	};
	private Information mInformation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setWillNotPaintToolbar(false);
		setContentView(R.layout.activity_get_information);

		((CollapsingToolbarLayout) findViewById(R.id.get_information_collapsing_toolbar))
				.setTitle(getString(R.string.get_information));

		findViewById(R.id.get_information_appbar).setOnClickListener(this);

		showProgressDialog("正在加载");
		getInformation();

	}

	private void showInformation(Information information) {
		((CollapsingToolbarLayout) findViewById(R.id.get_information_collapsing_toolbar)).setTitle(information.getName());

		((TextView) findViewById(R.id.get_information_account)).setText(information.getAccount());
		((TextView) findViewById(R.id.get_information_birthday)).setText(information.getBirthday());
		((TextView) findViewById(R.id.get_information_clazz)).setText(information.getClazz());
		((TextView) findViewById(R.id.get_information_college)).setText(information.getCollege());
		((TextView) findViewById(R.id.get_information_degree)).setText(information.getDegree());
		((TextView) findViewById(R.id.get_information_department)).setText(information.getDepartment());
		((TextView) findViewById(R.id.get_information_dormitory)).setText(information.getDormitory());
		((TextView) findViewById(R.id.get_information_enrollment_date)).setText(information.getEnrollmentDate());
		((TextView) findViewById(R.id.get_information_grade)).setText(information.getGrade());
		((TextView) findViewById(R.id.get_information_middle_school)).setText(information.getMiddleSchool());
		((TextView) findViewById(R.id.get_information_nation)).setText(information.getNation());
		((TextView) findViewById(R.id.get_information_sex)).setText(information.getSex());

	}

	private void getInformation() {
		new Thread(new GetInformation(new BaseRunnable.GGCallback() {
			@Override
			public void onCall(Object obj) {
				Message msg = Message.obtain();
				msg.what = ApiHelper.GET_INFORMATION_SUCCESS;
				msg.obj = obj;
				handler.sendMessage(msg);
			}
		})).start();
	}

	private void getAvatar() {
		new Thread(new GetInformationAvatar(new BaseRunnable.GGCallback() {
			@Override
			public void onCall(Object obj) {
				Message msg = Message.obtain();
				msg.what = ApiHelper.GET_INFORMATION_AVATAR;
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

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.get_information_appbar:
				Intent intent = new Intent(this, PictureActivity.class);
				intent.putExtra("avatar",
						((BitmapDrawable) ((ImageView) findViewById(R.id.get_information_avatar)).getDrawable()).getBitmap());
				startActivity(intent);
				break;
		}
	}
}
