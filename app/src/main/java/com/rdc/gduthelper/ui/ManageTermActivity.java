package com.rdc.gduthelper.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.bean.Lesson;
import com.rdc.gduthelper.bean.TermLessons;
import com.rdc.gduthelper.bean.YearSchedule;
import com.rdc.gduthelper.net.BaseRunnable;
import com.rdc.gduthelper.net.api.GetSchedule;
import com.rdc.gduthelper.net.api.IntoSchedule;
import com.rdc.gduthelper.ui.adapter.ManageTermAdapter;
import com.rdc.gduthelper.ui.widget.CustomToolbar;
import com.rdc.gduthelper.utils.UIUtils;
import com.rdc.gduthelper.utils.database.ScheduleDBHelper;
import com.rdc.gduthelper.utils.settings.ScheduleConfig;
import com.rdc.gduthelper.utils.settings.Settings;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class ManageTermActivity extends BaseActivity
		implements View.OnClickListener, ManageTermAdapter.OnChildClickListener,
		ManageTermAdapter.OnChildLongClickListener {
	private RecyclerView mRecyclerView;
	private ManageTermAdapter mAdapter;
	private List<YearSchedule> mYearSchedules;
	private FloatingActionButton mFAB;

	private AppCompatSpinner mSpinnerYear;
	private AppCompatSpinner mSpinnerTerm;

	private ArrayList<String> mYears;
	private ArrayList<String> mTerms;

	private Settings mSettings;

	private CustomToolbar mToolbarCAM;
	private CustomToolbar mToolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setWillNotPaintToolbar(false);
		setContentView(R.layout.activity_manage_term);
		mToolbar = (CustomToolbar) findViewById(R.id.toolbar);
		mRecyclerView = (RecyclerView) findViewById(R.id.manage_term_list);
		mFAB = (FloatingActionButton) findViewById(R.id.manage_term_add);
		mFAB.setOnClickListener(this);

		mSettings = GDUTHelperApp.getSettings();
		init();
	}

	private void init() {
		mYearSchedules = new ArrayList<>();
		ScheduleDBHelper helper = ScheduleDBHelper.getInstance(this);

		final String[] terms = helper.getOptionalTerms();
		if (terms != null) {
			for (String term : terms) {
				YearSchedule index = null;
				for (YearSchedule yearSchedule : mYearSchedules) {
					if (term.contains(yearSchedule.getTitle())) {
						index = yearSchedule;
						break;
					}
				}
				if (index == null) {
					index = new YearSchedule();
					index.setTitle(term.substring(0, term.length() - 2));
					mYearSchedules.add(index);
				}
				ArrayList<TermLessons> data = null;
				if (index.getChildItemList() == null) {
					data = new ArrayList<>();
					index.setTermLessonsList(data);
				} else
					data = (ArrayList<TermLessons>) index.getChildItemList();
				ArrayList<Lesson> lessons = helper.getLessonList(term);
				TermLessons termLessons = new TermLessons();
				termLessons.setLessons(lessons);
				termLessons.setTerm(term);

				data.add(termLessons);
			}
		}
		final Comparator<Object> comparator = new Comparator<Object>() {
			@Override
			public int compare(Object lhs, Object rhs) {
				int left = Integer.parseInt(((YearSchedule) lhs).getTitle().substring(0, 4));
				int right = Integer.parseInt(((YearSchedule) rhs).getTitle().substring(0, 4));
				return left > right ? 1 : left == right ? 0 : -1;
			}
		};
		if (mYearSchedules.size() > 0) {
			Object[] yearSchedules = mYearSchedules.toArray();
			Arrays.sort(yearSchedules, comparator);
			mYearSchedules = new ArrayList<>();
			for (int i = 0; i < yearSchedules.length; i++) {
				mYearSchedules.add((YearSchedule) yearSchedules[i]);
			}
		}
		initRecyclerView(mYearSchedules);
	}

	private void initRecyclerView(List<YearSchedule> yearSchedules) {
		mAdapter = new ManageTermAdapter(this, yearSchedules, findViewById(R.id.manage_term_empty_view));
		mAdapter.setWithCard(mSettings.isManageTermCardMode());

		String term = mSettings.getScheduleChooseTerm(ManageTermActivity.this);
		if (term != null) {
			int groupPosition = YearSchedule.getGroupPosition(term, yearSchedules);
			int childPosition = YearSchedule.getChildPosition(term, yearSchedules);
			if (groupPosition == -1 || childPosition == -1) {
				if (yearSchedules.size() > 0) {
					mAdapter.setSelectedPosition(0, 0, true);
					mSettings.setScheduleChooseTerm(this, mAdapter.getTerm(0, 0));
				} else {
					mSettings.setScheduleChooseTerm(this, null);
				}
			} else
				mAdapter.setSelectedPosition(groupPosition, childPosition, true);
		}

		mAdapter.setOnChildClickListener(this);
		mAdapter.setOnChildLongClickListener(this);

		mRecyclerView.setAdapter(mAdapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.manage_term_add:
				if (mAdapter.isInActionMode()) {
					setActionMode(false);
				}
				if (GDUTHelperApp.isLogin()) {
					intoSchedule();
					showProgressDialog(R.string.loading);
				} else {
					startActivity(new Intent(this, LoginActivity.class));
				}
				break;
		}
	}

	private int deleteListData() {
		int count = 0;
		List<YearSchedule> termData = YearSchedule.copyYearSchedules(mAdapter.getTermData());
		Iterator<YearSchedule> ysIterator = termData.iterator();
		while (ysIterator.hasNext()) {
			YearSchedule yearSchedule = ysIterator.next();
			Iterator<TermLessons> iterator = yearSchedule.getChildItemList().iterator();
			while (iterator.hasNext()) {
				TermLessons termLessons = iterator.next();
				if (mAdapter.getSelectedPosition(termLessons.getParentPosition(),
						termLessons.getChildPosition())) {
					iterator.remove();
					count++;
				}
			}
			if (yearSchedule.getChildItemList().size() == 0)
				ysIterator.remove();
		}

		initRecyclerView(termData);
		return count;
	}

	private void executeDelete() {
		ScheduleDBHelper helper = ScheduleDBHelper.getInstance(this);
		List<YearSchedule> currentData = (List<YearSchedule>) mAdapter.getParentItemList();
		final String[] terms = helper.getOptionalTerms();
		for (String term : terms) {
			boolean needDelete = false;
			for (YearSchedule yearSchedule : currentData) {
				if (term.contains(yearSchedule.getTitle())) {
					boolean flag = false;
					for (TermLessons termLessons : yearSchedule.getChildItemList()) {
						if (termLessons.getTerm().equals(term)) {
							flag = true;
							break;
						}
					}
					if (!flag) {
						needDelete = true;
					}
				} else {
					needDelete = true;
				}
			}
			if (!needDelete)
				needDelete = currentData.size() == 0;
			if (needDelete)
				helper.deleteTermLesson(term);
		}
		mYearSchedules = currentData;
	}

	@Override
	public void onChildClick(View v, int groupPosition, int childPosition) {
		if (mAdapter.isInActionMode()) {
			mAdapter.setSelectedPosition(groupPosition, childPosition,
					!mAdapter.getSelectedPosition(groupPosition, childPosition));
			if (mAdapter.getSelectedNum() > 0)
				mToolbarCAM.setTitle("已选择" + mAdapter.getSelectedNum() + "项");
			setActionMode(false);
		} else {
			mAdapter.setSelectedPosition(groupPosition, childPosition, true);
			mSettings.setScheduleChooseTerm(this, mAdapter.getTerm(groupPosition, childPosition));
		}

	}

	@Override
	public boolean onChildLongClick(View v, int groupPosition, int childPosition) {
		if (!mAdapter.isInActionMode()) {
			setActionMode(true);
			mAdapter.setSelectedPosition(groupPosition, childPosition, true);
		}
		return true;
	}

	private void intoSchedule() {
		new Thread(new IntoSchedule(new BaseRunnable.GGCallback() {
			@Override
			public void onCall(Object obj) {
				String[] spinnerData = ((String) obj).split(";");
				if (spinnerData.length > 1) {
					String[] years = spinnerData[0].split(",");
					mYears = new ArrayList<>();
					Collections.addAll(mYears, years);
					mTerms = new ArrayList<>();
					Collections.addAll(mTerms, spinnerData[1].split(","));

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
				new AlertDialog.Builder(ManageTermActivity.this)
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
						.setNegativeButton(R.string.cancel, null).show();
			}
		});

	}

	private void getSchedule(final String year, final String term) {
		new Thread(new GetSchedule(year, term, new BaseRunnable.GGCallback() {
			@Override
			public void onCall(Object obj) {
				ArrayList<Lesson> lessons = (ArrayList<Lesson>) obj;
				final ScheduleDBHelper helper = ScheduleDBHelper.getInstance(ManageTermActivity.this);
				helper.addLessonList(lessons, year + "-" + term);
				cancelDialog();

				ScheduleConfig config = mSettings.getScheduleConfig(ManageTermActivity.this, GDUTHelperApp.userXh);
				if (config != null) {
					config.setId(GDUTHelperApp.userXh);
					config.setTerm(year + "-" + term);
					config.setFirstWeek(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
							.format(Calendar.getInstance().getTime()));
					config.setCardColors(mSettings.getScheduleCardColors(ManageTermActivity.this));
					mSettings.updateScheduleConfig(ManageTermActivity.this, config);

				} else {
					config = new ScheduleConfig();
					config.setId(GDUTHelperApp.userXh);
					config.setTerm(year + "-" + term);
					config.setFirstWeek(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
							.format(Calendar.getInstance().getTime()));
					config.setCardColors(mSettings.getScheduleCardColors(ManageTermActivity.this));
					mSettings.saveScheduleConfig(ManageTermActivity.this, config);
				}
				mRecyclerView.post(new Runnable() {
					@Override
					public void run() {
						init();
					}
				});
			}
		})).start();
	}

	private void setActionMode(boolean enable) {
		mAdapter.setInActionMode(enable);
		if (enable) {
			((CollapsingToolbarLayout) findViewById(R.id.toolbar).getParent()).setTitle("");

			if (mToolbarCAM == null)
				initCAMToolbar();
			mToolbarCAM.setVisibility(View.VISIBLE);
			mToolbarCAM.animate().alpha(1).setDuration(200).start();
			mToolbarCAM.startAnimation(CustomToolbar.State.ARROW_TO_X, Color.BLACK);
			mToolbar.startAnimation(CustomToolbar.State.ARROW_TO_X);
			getWindow().setStatusBarColor(Color.GRAY);
		} else {
			((CollapsingToolbarLayout) findViewById(R.id.toolbar).getParent()).setTitle(getString(R.string.manage_term));

			if (mToolbarCAM != null) {
				mToolbarCAM.animate().alpha(0).setDuration(200).withEndAction(new Runnable() {
					@Override
					public void run() {
						mToolbarCAM.setVisibility(View.GONE);
						getWindow().setStatusBarColor(getResources().getColor(R.color.blue_700));
					}
				}).start();
				mToolbarCAM.startAnimation(CustomToolbar.State.X_TO_ARROW, Color.BLACK);
			}
			mToolbar.startAnimation(CustomToolbar.State.X_TO_ARROW);
		}

	}

	private void initCAMToolbar() {
		mToolbarCAM = new CustomToolbar(this);
		ViewGroup.LayoutParams params = new ViewGroup
				.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, findViewById(R.id.toolbar).getHeight());
		addContentView(mToolbarCAM, params);
		mToolbarCAM.setNavigationIcon(getDrawable(R.drawable.ic_arrow_back_black_24dp));
		mToolbarCAM.setBackgroundColor(getResources().getColor(R.color.white));
		mToolbarCAM.setClickable(true);
		mToolbarCAM.inflateMenu(R.menu.menu_context_manage_term);
		mToolbarCAM.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				setActionMode(false);
			}
		});
		mToolbarCAM.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				System.out.print(item.getItemId());
				switch (item.getItemId()) {
					case R.id.manage_term_delete:
						Snackbar snackbar = Snackbar.make(findViewById(R.id.manage_term_container),
								"", Snackbar.LENGTH_SHORT);
						snackbar.setAction(R.string.undo, new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								initRecyclerView(mYearSchedules);
							}
						});
						snackbar.setCallback(new Snackbar.Callback() {
							@Override
							public void onDismissed(Snackbar snackbar, int event) {
								if (event != Snackbar.Callback.DISMISS_EVENT_ACTION)
									executeDelete();
							}

							@Override
							public void onShown(Snackbar snackbar) {
								int num = deleteListData();
								setActionMode(false);
								snackbar.setText("删除了" + num + "条数据");
							}
						});
						snackbar.show();
						break;
				}
				return false;
			}
		});

		mToolbarCAM.setAlpha(0);
		mToolbarCAM.setTitle("已选择1项");
		mToolbarCAM.setElevation(UIUtils.convertDpToPixel(4, this));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mAdapter.isInActionMode()) {
				setActionMode(false);
				return true;
			}
			return super.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_manage_term, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.manage_term_switch_card_mode:
				mSettings.setManageTermCardMode(!mSettings.isManageTermCardMode());
				initRecyclerView(mYearSchedules);
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
