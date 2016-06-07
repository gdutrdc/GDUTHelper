package com.rdc.gduthelper.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.bean.Lesson;
import com.rdc.gduthelper.bean.LessonTACR;
import com.rdc.gduthelper.net.BaseRunnable;
import com.rdc.gduthelper.net.api.GetSchedule;
import com.rdc.gduthelper.net.api.IntoSchedule;
import com.rdc.gduthelper.ui.adapter.LessonDetailAdapter;
import com.rdc.gduthelper.ui.widget.WeekScheduleView;
import com.rdc.gduthelper.utils.BitmapUtils;
import com.rdc.gduthelper.utils.LessonUtils;
import com.rdc.gduthelper.utils.settings.ScheduleConfig;
import com.rdc.gduthelper.utils.settings.Settings;
import com.rdc.gduthelper.utils.UIUtils;
import com.rdc.gduthelper.utils.database.ScheduleDBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;


/**
 * Created by seasonyuu on 16/1/4.
 */
public class GetScheduleActivity extends BaseActivity
		implements AdapterView.OnItemSelectedListener, WeekScheduleView.OnLessonsClickListener, LessonDetailAdapter.OnButtonClickListener {
	private AppCompatSpinner mSpinnerYear;
	private AppCompatSpinner mSpinnerTerm;
	private AppCompatSpinner mSpinnerWeek;

	private ArrayList<String> mYears;
	private ArrayList<String> mTerms;

	private WeekScheduleView mWeekScheduleView;

	private RecyclerView mLessonDetailsView;
	private LessonDetailAdapter mLessonDetailAdapter;
	private View mBtnCloseDetail;

	private boolean isNull;
	private ScrollView mScheduleContainer;

	private FloatingActionButton mFAB;

	private int themeId;
	private int fabLocation = 0;

	private Settings mSettings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_schedule);

		initView();

		mSettings = GDUTHelperApp.getSettings();
	}

	private void initView() {
		mLessonDetailsView = (RecyclerView) findViewById(R.id.get_schedule_lessons);
		mLessonDetailAdapter = new LessonDetailAdapter(this);
		mLessonDetailAdapter.setOnButtonClickListener(this);
		mLessonDetailsView.setLayoutManager(new LinearLayoutManager(this));
		mLessonDetailsView.setAdapter(mLessonDetailAdapter);
		mBtnCloseDetail = findViewById(R.id.get_schedule_btn_close_lesson_details);
		mBtnCloseDetail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setLessonsDetailVisible(false, v.getX() + v.getWidth() / 2, v.getY() + v.getHeight() / 2);
			}
		});
		mSpinnerWeek = (AppCompatSpinner) findViewById(R.id.get_schedule_week_spinner);
		mSpinnerWeek.setOnItemSelectedListener(this);

		mWeekScheduleView = (WeekScheduleView) findViewById(R.id.get_schedule_table);
		mWeekScheduleView.setOnLessonsClickListener(this);
		mScheduleContainer = (ScrollView) mWeekScheduleView.getParent();
		mFAB = (FloatingActionButton) findViewById(R.id.get_schedule_fab);
		mScheduleContainer.getViewTreeObserver()
				.addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
					int scrollDist = 0;
					final float MINIMUM = 25;
					boolean isVisible = true;
					int lastTime = 0;

					@Override
					public void onScrollChanged() {
						int dy = mScheduleContainer.getScrollY() - lastTime;
						lastTime = mScheduleContainer.getScrollY();
						if (isVisible && scrollDist > MINIMUM) {
							hide();
							scrollDist = 0;
							isVisible = false;
						} else if (!isVisible && scrollDist < -MINIMUM) {
							show();
							scrollDist = 0;
							isVisible = true;
						}
						if ((isVisible && dy > 0) || (!isVisible && dy < 0)) {
							scrollDist += dy;
						}
					}

					private void show() {
						mFAB.animate().translationY(0)
								.setInterpolator(new DecelerateInterpolator(2)).start();
					}

					private void hide() {
						mFAB.animate().translationY(
								mFAB.getHeight() + fabLocation)
								.setInterpolator(new AccelerateInterpolator(2)).start();
					}
				});
		fabLocation = ((RelativeLayout.LayoutParams) mFAB.getLayoutParams()).bottomMargin;

	}

	private void updateDate() {
		int currentWeek = mSpinnerWeek.getSelectedItemPosition();
		Calendar calendar = mSettings.getScheduleFirstWeek(this);
		if (calendar == null)
			return;
		if (calendar.compareTo(Calendar.getInstance()) < 0) {
			calendar = Calendar.getInstance();
		} else
			calendar.add(Calendar.WEEK_OF_YEAR, currentWeek - 1);
		((TextView) findViewById(R.id.get_schedule_tv_month))
				.setText(calendar.get(Calendar.MONTH) + 1 + "月");

		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		((TextView) findViewById(R.id.get_schedule_tv_sun_date))
				.setText(calendar.get(Calendar.DAY_OF_MONTH) + "");
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		((TextView) findViewById(R.id.get_schedule_tv_mon_date))
				.setText(calendar.get(Calendar.DAY_OF_MONTH) + "");
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		((TextView) findViewById(R.id.get_schedule_tv_tue_date))
				.setText(calendar.get(Calendar.DAY_OF_MONTH) + "");
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		((TextView) findViewById(R.id.get_schedule_tv_wed_date))
				.setText(calendar.get(Calendar.DAY_OF_MONTH) + "");
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		((TextView) findViewById(R.id.get_schedule_tv_thu_date))
				.setText(calendar.get(Calendar.DAY_OF_MONTH) + "");
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		((TextView) findViewById(R.id.get_schedule_tv_fri_date))
				.setText(calendar.get(Calendar.DAY_OF_MONTH) + "");
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		((TextView) findViewById(R.id.get_schedule_tv_sat_date))
				.setText(calendar.get(Calendar.DAY_OF_MONTH) + "");

	}

	@Override
	protected void onResume() {
		super.onResume();
		initData();
	}

	private void initData() {
		if (mWeekScheduleView != null) {
			Bitmap bitmap = BitmapUtils.getBitmap(this);
			mWeekScheduleView.setScheduleBackground(bitmap);

			String colors = mSettings.getScheduleCardColors(this);
			if (colors == null) {
				colors = getResources().getString(R.string.default_colors);
				GDUTHelperApp.getSettings().setScheduleCardColors(this, colors);
			}
			int[] allColors = getResources().getIntArray(R.array.colors);
			int[] temp = new int[allColors.length];
			int[] target = null;
			String[] choosedColors = colors.split(",");
			for (int i = 0; i < choosedColors.length; i++) {
				int j = Integer.parseInt(choosedColors[i]);
				temp[i] = allColors[j];
			}
			target = new int[choosedColors.length];
			for (int i = 0; i < target.length; i++) {
				target[i] = temp[i];
			}
			mWeekScheduleView.setColors(target);
			refreshData();
		}
		if (mSpinnerWeek != null) {
			String[] s = new String[22];
			s[0] = "非上课时间";
			for (int i = 1; i < s.length; i++)
				s[i] = "第 " + i + " 周";
			ArrayAdapter<String> weekAdapter = new ArrayAdapter<>(this, R.layout.spinner_item);
			weekAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
			weekAdapter.addAll(s);
			mSpinnerWeek.setAdapter(weekAdapter);
			String current = mSettings.getScheduleCurrentWeek(this);
			int week = 0;
			if (current != null)
				week = Integer.parseInt(current);
			if (week >= s.length || week < 0)
				week = 0;
			mSpinnerWeek.setSelection(week);
		}
	}

	private void refreshData() {
		ScheduleDBHelper helper = new ScheduleDBHelper(this);
		String term = mSettings.getScheduleChooseTerm(this);
		ArrayList<Lesson> lessons = null;
		if (term != null) {
			lessons = helper.getLessonList(term);
		}
		if (lessons == null || lessons.size() == 0) {
			isNull = true;
			new AlertDialog.Builder(this)
					.setTitle(R.string.tips)
					.setMessage(R.string.no_local_schedule)
					.setPositiveButton(R.string.ensure, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (GDUTHelperApp.isLogin()) {
								intoSchedule();
								showProgressDialog(R.string.loading);
							} else {
								startActivity(new Intent(GetScheduleActivity.this,
										LoginActivity.class));
							}
						}
					}).show();
		} else {
			mWeekScheduleView.setLessons(lessons);
		}
	}

	private void intoSchedule() {
		new Thread(new IntoSchedule(new BaseRunnable.GGCallback() {
			@Override
			public void onCall(Object obj) {
				String[] spinnerData = ((String) obj).split(";");
				if (spinnerData.length == 1) {

				} else {
					String[] years = spinnerData[0].split(",");
					mYears = new ArrayList<>();
					for (int i = 0; i < years.length; i++) {
						mYears.add(years[i]);
					}
					mTerms = new ArrayList<>();
					for (String s : spinnerData[1].split(","))
						mTerms.add(s);

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							showAddSchedule();
						}
					});
				}
			}
		})).start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			boolean needRefresh = data.getBooleanExtra("need_refresh", false);
			if (needRefresh)
				refreshData();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void getSchedule(final String year, final String term) {
		new Thread(new GetSchedule(year, term, new BaseRunnable.GGCallback() {
			@Override
			public void onCall(Object obj) {
				ArrayList<Lesson> lessons = (ArrayList<Lesson>) obj;
				final ScheduleDBHelper helper = new ScheduleDBHelper(GetScheduleActivity.this);
				helper.addLessonList(lessons, year + "-" + term);
				cancelDialog();

				ScheduleConfig config = mSettings.getScheduleConfig(GetScheduleActivity.this, GDUTHelperApp.userXh);
				if (config != null) {
					config.setId(GDUTHelperApp.userXh);
					config.setTerm(year + "-" + term);
					config.setFirstWeek(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
							.format(Calendar.getInstance().getTime()));
					config.setCardColors(mSettings.getScheduleCardColors(GetScheduleActivity.this));
					mSettings.updateScheduleConfig(GetScheduleActivity.this, config);

				} else {
					config = new ScheduleConfig();
					config.setId(GDUTHelperApp.userXh);
					config.setTerm(year + "-" + term);
					config.setFirstWeek(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
							.format(Calendar.getInstance().getTime()));
					config.setCardColors(mSettings.getScheduleCardColors(GetScheduleActivity.this));
					mSettings.saveScheduleConfig(GetScheduleActivity.this, config);
				}
				mWeekScheduleView.post(new Runnable() {
					@Override
					public void run() {
						initData();
//						ArrayList<Lesson> lessons = helper.getLessonList(year + "-" + term);
//						mWeekScheduleView.setLessons(lessons);
					}
				});
			}
		})).start();
	}

	private void showAddSchedule() {
		final View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_schedule, null);
		mSpinnerYear = (AppCompatSpinner) view.findViewById(R.id.add_schedule_year);
		mSpinnerTerm = (AppCompatSpinner) view.findViewById(R.id.add_schedule_term);
		Calendar calendar = Calendar.getInstance();
		int section = -1;

		ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		yearAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		for (int i = 0; i < mYears.size(); i++) {
			yearAdapter.add(mYears.get(i));
			if (section == -1 && mYears.get(i).contains(calendar.get(Calendar.YEAR) + "")) {
				section = i;
			}
		}
		mSpinnerYear.setAdapter(yearAdapter);
		if (section != -1) {
			mSpinnerYear.setSelection(section);
		}

		ArrayAdapter<String> termAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
		termAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		termAdapter.addAll(mTerms);
		mSpinnerTerm.setAdapter(termAdapter);

		cancelDialog();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				new AlertDialog.Builder(GetScheduleActivity.this)
						.setTitle(R.string.add_schedule)
						.setView(view)
						.setPositiveButton(R.string.ensure, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								showProgressDialog(R.string.loading);
								getSchedule(mSpinnerYear.getSelectedItem().toString(),
										mSpinnerTerm.getSelectedItem().toString());
							}
						})
						.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if (isNull) {
									finish();
								}
							}
						}).show();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_schedule, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.schedule_settings) {
			startActivityForResult(new Intent(this, ScheduleSettingsActivity.class), 0);
		}
		return super.onOptionsItemSelected(item);
	}

	public void onClick(View v) {
		String[] choices = {getResources().getString(R.string.new_term),
				getResources().getString(R.string.other_lesson)};
		new AlertDialog.Builder(this)
				.setItems(choices, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							if (GDUTHelperApp.isLogin()) {
								intoSchedule();
								showProgressDialog(R.string.loading);
							} else {
								startActivity(new Intent(GetScheduleActivity.this,
										LoginActivity.class));
							}
						} else {

						}
					}
				})
				.setTitle(R.string.add)
				.setPositiveButton(R.string.cancel, null)
				.show();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if (mWeekScheduleView != null)
			mWeekScheduleView.setWeek(position);
		updateDate();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	private void setLessonsDetailVisible(boolean isVisible, float dx, float dy) {
		if (mLessonDetailsView == null)
			return;
		if (isVisible) {
			mBtnCloseDetail.setVisibility(View.VISIBLE);
			mLessonDetailsView.setVisibility(View.VISIBLE);
			findViewById(R.id.get_schedule_lessons_container).setVisibility(View.VISIBLE);
			SupportAnimator animator = ViewAnimationUtils
					.createCircularReveal(
							findViewById(R.id.get_schedule_lessons_container), (int) dx,
							(int) dy,
							0,
							UIUtils.getScreenHeight(GetScheduleActivity.this));

			animator.setInterpolator(new AccelerateDecelerateInterpolator());
			animator.setDuration(300);
			animator.start();
		} else {
			SupportAnimator animator = ViewAnimationUtils
					.createCircularReveal(
							findViewById(R.id.get_schedule_lessons_container), (int) dx,
							(int) dy,
							UIUtils.getScreenHeight(GetScheduleActivity.this), 0);

			animator.setInterpolator(new AccelerateDecelerateInterpolator());
			animator.setDuration(300);
			animator.addListener(new SupportAnimator.AnimatorListener() {
				@Override
				public void onAnimationStart() {

				}

				@Override
				public void onAnimationEnd() {
					mBtnCloseDetail.setVisibility(View.GONE);
					mLessonDetailsView.setVisibility(View.GONE);
					findViewById(R.id.get_schedule_lessons_container).setVisibility(View.GONE);
				}

				@Override
				public void onAnimationCancel() {

				}

				@Override
				public void onAnimationRepeat() {

				}
			});
			animator.start();
		}
	}


	@Override
	public void onClick(View lessonView, final Map<Lesson, LessonTACR> lessonMap) {
		ArrayList<Lesson> lessons = new ArrayList<>();
		for (Lesson lesson : lessonMap.keySet()) {
			lesson.setLessonTime(LessonUtils.transformTime(lessonMap.get(lesson)));
			lesson.setLessonClassroom(lessonMap.get(lesson).getClassroom());
			lessons.add(lesson);
		}

		mLessonDetailAdapter.setLessonColors(mWeekScheduleView.getLessonColors());
		mLessonDetailAdapter.setLessons(lessons);
		mLessonDetailAdapter.notifyDataSetChanged();

		setLessonsDetailVisible(true, lessonView.getX() + lessonView.getWidth() / 2,
				lessonView.getY() + lessonView.getHeight() / 2
						- ((ScrollView) mWeekScheduleView.getParent()).getScaleY());
	}

	@Override
	public void onLongClick(View lessonView, final Map<Lesson, LessonTACR> lessonMap) {
		new AlertDialog.Builder(this)
				.setTitle(R.string.delete_lesson)
				.setMessage(R.string.delete_lesson_tips)
				.setPositiveButton(R.string.ensure, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Set<Lesson> lessons = lessonMap.keySet();
						if (lessons.size() > 1) {
							Toast.makeText(GetScheduleActivity.this,
									R.string.more_than_one_lesson, Toast.LENGTH_SHORT).show();
						} else {
							ScheduleDBHelper helper = new ScheduleDBHelper(GetScheduleActivity.this);
							for (Lesson lesson : lessons)
								helper.deleteLesson(lesson);
						}
						initData();
					}
				})
				.setNegativeButton(R.string.cancel, null)
				.show();

	}

	@Override
	public void onBackPressed() {
		if (mLessonDetailsView.isShown())
			setLessonsDetailVisible(false, mLessonDetailsView.getWidth() / 2,
					mLessonDetailsView.getHeight() / 2);
		else
			super.onBackPressed();
	}

	@Override
	public void onEditClick(View itemView, int position) {

	}

	@Override
	public void onDeleteClick(View itemView, final int position) {
		new AlertDialog.Builder(this)
				.setTitle(R.string.delete_lesson)
				.setMessage(R.string.delete_lesson_tips)
				.setPositiveButton(R.string.ensure, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ArrayList<Lesson> lessons = mLessonDetailAdapter.getLessons();
						Lesson lesson = lessons.get(position);
						ScheduleDBHelper helper = new ScheduleDBHelper(GetScheduleActivity.this);
						helper.deleteLesson(lesson);
						initData();
						lessons.remove(lesson);

						mLessonDetailAdapter.notifyItemRemoved(position);

						if (lessons.size() == 0) {
							setLessonsDetailVisible(false, mLessonDetailsView.getWidth() / 2,
									mLessonDetailsView.getHeight() / 2);
						}
					}
				})
				.setNegativeButton(R.string.cancel, null)
				.show();
	}
}