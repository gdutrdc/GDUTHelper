package com.rdc.gduthelper.ui;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.bean.Lesson;
import com.rdc.gduthelper.net.BaseRunnable;
import com.rdc.gduthelper.net.api.GetGrade;
import com.rdc.gduthelper.net.api.IntoGrade;
import com.rdc.gduthelper.ui.adapter.GradeListAdapter;
import com.rdc.gduthelper.utils.LessonUtils;
import com.rdc.gduthelper.utils.UIUtils;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by seasonyuu on 15/8/28.
 */
public class GetGradeActivity extends BaseActivity {
	private final String TAG = GetGradeActivity.class.getSimpleName();
	private boolean firstLoad = true;

	private Handler handler;
	private GradeListAdapter adapter;
	private ListView lvGrade;
	private TextView tvGradePoint;
	private ProgressDialog progressDialog;
	private AlertDialog zbDialog;
	private View zbDialogView;

	private Spinner mYearSpinner;
	private Spinner mTermSpinner;

	private ArrayList<MenuItem> menuItemList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_grade);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		progressDialog = new ProgressDialog(this);
//		progressDialog.setCancelable(false);
		progressDialog.setMessage(getString(R.string.loading));
		progressDialog.show();

		tvGradePoint = (TextView) findViewById(R.id.grade_point);

		lvGrade = (ListView) findViewById(R.id.grade_list);
		View view = new View(this);
		view.setMinimumHeight((int) UIUtils.convertDpToPixel(28, this));
		lvGrade.addFooterView(view);
		adapter = new GradeListAdapter(this);
		lvGrade.setAdapter(adapter);
		lvGrade.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				if (position < adapter.getCount() && position >= 0) {
					Lesson lesson = adapter.getItem(position);
					String message = GDUTHelperApp.getSettings().getZBText();
					StringBuffer sb = new StringBuffer(message);
					String[] matches = new String[]{"$name", "$grade"};
					for (String match : matches) {
						String data = null;
						if (match.equals("$name"))
							data = lesson.getLessonName();
						else if (match.equals("$grade"))
							data = lesson.getLessonGrade();
						while (sb.indexOf(match) != -1) {
							sb.replace(sb.indexOf(match), sb.indexOf(match) + match.length(), data);
						}
					}

					ClipboardManager clipboard = (ClipboardManager)
							getSystemService(Context.CLIPBOARD_SERVICE);
					ClipData clip = ClipData.newPlainText("", sb.toString());
					clipboard.setPrimaryClip(clip);
					Toast.makeText(GetGradeActivity.this, "\"" + sb.toString() + "\"已复制到剪贴板",
							Toast.LENGTH_SHORT).show();
				}
				return true;
			}
		});

		mYearSpinner = (Spinner) findViewById(R.id.grade_year_spinner);
		mTermSpinner = (Spinner) findViewById(R.id.grade_term_spinner);

		((AppCompatRadioButton) findViewById(R.id.grade_get_all)).setChecked(true);

		initHandler();

		IntoGrade();
	}

	private void initHandler() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 0) {
					progressDialog.cancel();
					String[] spinnerData = ((String) msg.obj).split(";");
					if (spinnerData.length == 1) {
						new AlertDialog.Builder(GetGradeActivity.this)
								.setTitle(R.string.tips)
								.setMessage(msg.obj.toString())
								.setNegativeButton(R.string.ensure,
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												finish();
											}
										}).show();
					} else {
						Calendar calendar = Calendar.getInstance();
						int section = -1;

						ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(GetGradeActivity.this, R.layout.spinner_item);
						yearAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
						String[] years = spinnerData[0].split(",");
						for (int i = 0; i < years.length; i++) {
							yearAdapter.add(years[i]);
							if (section == -1 && years[i].contains(calendar.get(Calendar.YEAR) + "")) {
								section = i;
							}
						}
						mYearSpinner.setAdapter(yearAdapter);
						if (section != -1) {
							mYearSpinner.setSelection(section);
						}

						ArrayAdapter<String> termAdapter = new ArrayAdapter<String>(GetGradeActivity.this, R.layout.spinner_item);
						termAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
						for (String s : spinnerData[1].split(","))
							termAdapter.add(s);
						mTermSpinner.setAdapter(termAdapter);

						if (firstLoad) {
							firstLoad = false;
							Runnable runnable = new GetGrade(null, null, new BaseRunnable.GGCallback() {
								@Override
								public void onCall(Object obj) {
									Message msg = Message.obtain();
									msg.what = 1;
									msg.obj = obj;
									handler.sendMessage(msg);
								}
							});
							new Thread(runnable).start();
						}
					}
				} else if (msg.what == 1) {
					progressDialog.cancel();
					if (msg.obj != null) {
						ArrayList<Lesson> list = (ArrayList<Lesson>) msg.obj;
						if (list.size() > 0) {
							double points = 0, credits = 0;
							for (Lesson lesson : list) {
								double point = Double.parseDouble(
										String.format("%.2f", LessonUtils.calculatePoint(lesson) * Double.parseDouble(lesson.getLessonCredit())));
								points += point;
								if (point == 0 && !lesson.getLessonBelong().equals("&nbsp;"))
									continue;
								credits += Double.parseDouble(lesson.getLessonCredit());
							}
							tvGradePoint.setText("平均绩点: " + String.format("%.3f", Double.parseDouble(
									String.format("%.2f", points)) / credits));
						} else {
							tvGradePoint.setText("平均绩点: 0");
						}
						adapter.setData(list);
						adapter.notifyDataSetChanged();
					}
				}

			}
		};

	}


	private void IntoGrade() {
		new Thread(new IntoGrade(new BaseRunnable.GGCallback() {
			@Override
			public void onCall(Object obj) {
				Message msg = Message.obtain();
				msg.what = 0;
				msg.obj = obj;
				handler.sendMessage(msg);
			}
		})).start();
	}

	public void onClick(View v) {
		((ListView) findViewById(R.id.grade_list)).smoothScrollToPosition(0);

		Runnable runnable = null;
		String year = null;
		String term = null;
		if (((AppCompatRadioButton) findViewById(R.id.grade_get_term)).isChecked()) {
			year = mYearSpinner.getSelectedItem().toString();
			term = mTermSpinner.getSelectedItem().toString();
		} else if (((AppCompatRadioButton) findViewById(R.id.grade_get_year)).isChecked()) {
			year = mYearSpinner.getSelectedItem().toString();
		} else if (((AppCompatRadioButton) findViewById(R.id.grade_get_all)).isChecked()) {

		}
		runnable = new GetGrade(year, term, new BaseRunnable.GGCallback() {
			@Override
			public void onCall(Object obj) {
				Message msg = Message.obtain();
				msg.what = 1;
				msg.obj = obj;
				handler.sendMessage(msg);
			}
		});
		progressDialog.show();
		new Thread(runnable).start();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.grade_menu:
				return super.onOptionsItemSelected(item);
			case android.R.id.home:
				onBackPressed();
				return true;
			case R.id.grade_zb:
				if (zbDialog == null) {
					zbDialogView = View.inflate(this, R.layout.dialog_zb, null);
					((TextInputLayout) zbDialogView.findViewById(R.id.dialog_zb_til)).setHint("替换文本");
					zbDialog = new AlertDialog.Builder(this).create();
					zbDialog.setView(zbDialogView);
					zbDialog.setTitle("装个逼");
					final DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
								case DialogInterface.BUTTON_POSITIVE:
									GDUTHelperApp.getSettings().setZBText(
											((EditText) zbDialogView.findViewById(R.id.dialog_zb_content)).getText().toString());
									break;
								case DialogInterface.BUTTON_NEGATIVE:
									break;
							}
						}
					};
					zbDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", onClickListener);
					zbDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", onClickListener);
				}
				((EditText) zbDialogView.findViewById(R.id.dialog_zb_content))
						.setText(GDUTHelperApp.getSettings().getZBText());
				zbDialog.show();
				return true;
			case R.id.grade_default_sort:
				adapter.setSortType(GradeListAdapter.DEFAULT_SORT);
				break;
			case R.id.grade_down_sort:
				adapter.setSortType(GradeListAdapter.GRADE_DOWN_SORT);
				break;
			case R.id.grade_up_sort:
				adapter.setSortType(GradeListAdapter.GRADE_UP_SORT);
				break;
			case R.id.grade_credit_down_sort:
				adapter.setSortType(GradeListAdapter.GRADE_CREDIT_DOWN_SORT);
				break;
			case R.id.grade_credit_up_sort:
				adapter.setSortType(GradeListAdapter.GRADE_CREDIT_UP_SORT);
				break;

		}
		clearMenuIcon();
		item.setIcon(R.drawable.ic_done_grey_500_24dp);
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_grade, menu);
		menuItemList = new ArrayList<>();
		MenuItem mi = menu.findItem(R.id.grade_menu);
		Menu m = mi.getSubMenu();
		for (int i = 0; i < m.size(); i++) {
			menuItemList.add(m.getItem(i));
		}
		return super.onCreateOptionsMenu(menu);
	}

	private void clearMenuIcon() {
		for (MenuItem item : menuItemList) {
			item.setIcon(null);
		}
	}
}
