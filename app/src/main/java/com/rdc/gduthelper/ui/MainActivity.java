package com.rdc.gduthelper.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.bean.Lesson;
import com.rdc.gduthelper.net.BaseRunnable;
import com.rdc.gduthelper.net.api.IntoMain;
import com.rdc.gduthelper.utils.settings.Settings;
import com.rdc.gduthelper.utils.UIUtils;

import java.util.ArrayList;

/**
 * Created by seasonyuu on 15/9/8.
 */
public class MainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(false);
			getSupportActionBar().setHomeButtonEnabled(false);
		}

		startSplashAnimation();
	}

	private void startSplashAnimation() {
		final int duration = 400;
		final int startDelay = 400;
		findViewById(R.id.main_title).animate()
				.alpha(0)
				.translationY(-getResources().getDimension(R.dimen.main_box_size))
				.setDuration(duration)
				.setStartDelay(startDelay)
				.start();

		findViewById(R.id.main_background).animate()
				.translationY(-UIUtils.getScreenHeight(this))
				.setDuration(duration)
				.setStartDelay(startDelay)
				.start();

		int delay = 0;
		ViewGroup mainLayout = (ViewGroup) findViewById(R.id.main_layout);
		int y = getResources().getDimensionPixelOffset(R.dimen.main_box_size);
		for (int i = 1; i < mainLayout.getChildCount(); i++) {
			View child = mainLayout.getChildAt(i);
			child.setTranslationY(y);
			child.animate()
					.alpha(1)
					.translationY(0)
					.setStartDelay(delay + duration + startDelay)
					.setDuration(200)
					.start();
			delay += 50;
		}
	}

	private void invalidateData() {
		ArrayList<Lesson> evaluationList = GDUTHelperApp.getEvaluationList();
		if (evaluationList != null && evaluationList.size() > 0) {
			ViewGroup evaluation = (ViewGroup) findViewById(R.id.main_evaluation);
			final View textView = evaluation.getChildAt(0);
			textView.post(new Runnable() {
				@Override
				public void run() {
					AlphaAnimation animation = new AlphaAnimation(0, 1f);
					animation.setRepeatMode(Animation.REVERSE);
					animation.setRepeatCount(-1);
					animation.setDuration(500);
					textView.startAnimation(animation);
				}
			});
		} else {
			ViewGroup evaluation = (ViewGroup) findViewById(R.id.main_evaluation);
			final View textView = evaluation.getChildAt(0);
			textView.post(new Runnable() {
				@Override
				public void run() {
					textView.clearAnimation();
				}
			});
		}
		supportInvalidateOptionsMenu();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (GDUTHelperApp.cookie == null) {
			Settings settings = GDUTHelperApp.getSettings();
			String cookie = settings.getCookie();
			String reUP = settings.getRememberUser();
			if (cookie != null && reUP != null) {
				GDUTHelperApp.cookie = cookie;
				String[] data = reUP.split(";", 2);
				GDUTHelperApp.userXh = data[0];
			}
		}
		new Thread(new IntoMain(new BaseRunnable.GGCallback() {
			@Override
			public void onCall(Object obj) {
				invalidateData();
			}
		})).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		MenuItem menuItem = menu.findItem(R.id.main_logout);
		if (!GDUTHelperApp.isLogin()) {
			menuItem.setTitle(R.string.login);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.main_help:
				startActivity(new Intent(this, HelpActivity.class));
				break;
			case R.id.main_logout:
				startActivity(new Intent(this, LoginActivity.class));
				GDUTHelperApp.userXh = null;
				GDUTHelperApp.userXm = null;
				GDUTHelperApp.setEvaluationList(new ArrayList<Lesson>());
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			boolean needRefresh = data.getBooleanExtra("need_refresh", false);
			boolean needChangeTheme = data.getBooleanExtra("need_change_theme", false);
			if (!GDUTHelperApp.getSettings().needRememberUser()) {
				GDUTHelperApp.getSettings().rememberUser("", "");
			}
			if (needChangeTheme) {
				recreate();
				return;
			}
			if (needRefresh) {
				new AlertDialog.Builder(this)
						.setTitle(R.string.tips)
						.setMessage(R.string.url_changed)
						.setPositiveButton(R.string.ensure, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								startActivity(new Intent(MainActivity.this, LoginActivity.class));
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
			case R.id.main_get_exam_time:
				startActivity(new Intent(this, GetExamTimeActivity.class));
				return;
			case R.id.main_get_schedule:
				startActivity(new Intent(this, GetScheduleActivity.class));
				return;
		}
		if (!GDUTHelperApp.isLogin()) {
			startActivity(new Intent(this, LoginActivity.class));
			return;
		}
		switch (view.getId()) {
			case R.id.main_get_grade:
				startActivity(new Intent(this, GetGradeActivity.class));
				break;
			case R.id.main_get_information:
				startActivity(new Intent(this, GetInformationActivity.class));
				break;
			case R.id.main_change_password:
				startActivity(new Intent(this, ChangePswActivity.class));
				break;
			case R.id.main_evaluation:
				if (GDUTHelperApp.getEvaluationList() != null
						&& GDUTHelperApp.getEvaluationList().size() > 0)
					startActivity(new Intent(this, EvaluationActivity.class));
				else
					new AlertDialog.Builder(this)
							.setTitle(R.string.tips)
							.setMessage(R.string.no_need_evaluation)
							.setPositiveButton(R.string.ensure, null)
							.show();
				break;
			case R.id.main_get_level_exam_grade:
				startActivity(new Intent(this, LevelExamActivity.class));
				break;
		}
	}
}
