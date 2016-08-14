package com.rdc.gduthelper.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.bean.LessonTACR;
import com.rdc.gduthelper.ui.MultiChoiceDialog;
import com.rdc.gduthelper.utils.LessonUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by seasonyuu on 16-8-7.
 */

public class LessonTimesAdapter extends RecyclerView.Adapter<LessonTimesAdapter.LessonTimeHolder> {
	private ArrayList<LessonTACR> lessonTACRs;
	private Context context;

	public LessonTimesAdapter(Context context) {
		this.context = context;
	}

	public ArrayList<LessonTACR> getLessonTACRs() {
		return lessonTACRs;
	}

	public void setLessonTACRs(ArrayList<LessonTACR> lessonTACRs) {
		this.lessonTACRs = lessonTACRs;
	}

	@Override
	public LessonTimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(context).inflate(R.layout.item_add_other_lesson_time, parent, false);
		return new LessonTimeHolder(itemView);
	}

	@Override
	public void onBindViewHolder(LessonTimeHolder holder, int position) {
		Log.d("adapter", "onBind " + position);
		holder.setPosition(position);

		LessonTACR lessonTACR = lessonTACRs.get(position);
		holder.setWeeks(lessonTACR.getWeek());
		holder.setLessonNum(lessonTACR.getNum());
		holder.setClassroom(lessonTACR.getClassroom());
		holder.setWeekday(lessonTACR.getWeekday());
	}

	@Override
	public int getItemCount() {
		return lessonTACRs == null ? 0 : lessonTACRs.size();
	}

	class LessonTimeHolder extends RecyclerView.ViewHolder
			implements View.OnClickListener, AdapterView.OnItemSelectedListener, TextWatcher {
		private Button btnLessonNum;
		private Button btnWeeks;
		private LessonTACR lessonTACR;
		private EditText etClassroom;

		private Spinner spinnerWeekday;

		private int position;

		public LessonTimeHolder(View itemView) {
			super(itemView);
			btnLessonNum = (Button) itemView.findViewById(R.id.item_add_other_lesson_btn_lesson_num);
			btnWeeks = (Button) itemView.findViewById(R.id.item_add_other_lesson_btn_weeks);
			spinnerWeekday = (Spinner) itemView.findViewById(R.id.item_add_other_lesson_time_spinner_weekday);
			etClassroom = (EditText) itemView.findViewById(R.id.item_add_other_lesson_et_classroom);

			etClassroom.addTextChangedListener(this);

			ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item,
					android.R.id.text1, context.getResources().getStringArray(R.array.weekdays));
			adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
			spinnerWeekday.setAdapter(adapter);
			spinnerWeekday.setOnItemSelectedListener(this);

			btnLessonNum.setOnClickListener(this);
			btnWeeks.setOnClickListener(this);
			itemView.findViewById(R.id.item_add_other_lesson_time_delete).setOnClickListener(this);
		}

		public void setClassroom(String classroom) {
			etClassroom.setText(classroom);
		}

		public void setWeekday(int weekday) {
			spinnerWeekday.setSelection(weekday);
		}

		public void setPosition(int position) {
			this.position = position;
			lessonTACR = lessonTACRs.get(position);
		}

		public void setLessonNum(int[] lessonNum) {
			if (lessonNum != null && lessonNum.length > 0) {
				String text = "第";
				for (int num : lessonNum) {
					text += num + ",";
				}
				text = text.substring(0, text.length() - 1) + "节";
				btnLessonNum.setText(text);
			} else
				btnLessonNum.setText(R.string.please_choose_lesson_num);
		}

		public void setWeeks(int[] weeks) {
			String text = LessonUtils.getCompleteWeekTime(weeks);
			if (text == null) {
				btnWeeks.setText(R.string.please_choose_weeks);
			} else {
				btnWeeks.setText(text);
			}
		}

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
				case R.id.item_add_other_lesson_time_delete:
					if (getItemCount() > 1) {
						if (position < lessonTACRs.size())
							lessonTACRs.remove(position);
						notifyItemRemoved(position);
						notifyItemRangeChanged(position, getItemCount());
					} else {
						Snackbar.make((View) ((Activity) context).findViewById(R.id.toolbar).getParent(),
								"上课时间至少应保留一个", Snackbar.LENGTH_SHORT).show();
					}
					break;
				case R.id.item_add_other_lesson_btn_lesson_num:
					final MultiChoiceDialog lessonTimesDialog = new MultiChoiceDialog();
					lessonTimesDialog.setChoiceCount(12);
					lessonTimesDialog.setColumnCount(7);
					boolean[] choices = new boolean[12];
					if (lessonTACR.getNum() != null) {
						for (int num : lessonTACR.getNum())
							choices[num - 1] = true;
					}
					lessonTimesDialog.setChoices(choices);
					lessonTimesDialog.setOnMultiChoiceCallback(new MultiChoiceDialog.OnMultiChoiceCallback() {
						@Override
						public void onMultiChoice(int[] choices) {
							lessonTimesDialog.dismiss();
							lessonTACR.setNum(choices);
							setLessonNum(choices);
						}
					});
					lessonTimesDialog.show(((AppCompatActivity) context).getSupportFragmentManager(),
							context.getString(R.string.please_choose_lesson_num), new MultiChoiceDialog.OnMatchResultCallback() {
								@Override
								public String onMatchResult(int[] choices) {
									// 课程需要是连续的
									for (int i = 0; i < choices.length - 1; i++) {
										if (choices[i + 1] - choices[i] != 1)
											return "请选择连续的节数";
									}
									// 课程需要是12,34,5,67,89,101112中的若干项
									final String warning = "请选择 1,2;3,4;5;6,7;8,9;10,11,12 中的若干连续节数";
									String result = null;
									for (int choice : choices) {
										switch (choice) {
											case 1:
												result = findNum(choices, 2) ? null : warning;
												break;
											case 2:
												result = findNum(choices, 1) ? null : warning;
												break;
											case 3:
												result = findNum(choices, 4) ? null : warning;
												break;
											case 4:
												result = findNum(choices, 3) ? null : warning;
												break;
											case 6:
												result = findNum(choices, 7) ? null : warning;
												break;
											case 7:
												result = findNum(choices, 6) ? null : warning;
												break;
											case 8:
												result = findNum(choices, 9) ? null : warning;
												break;
											case 9:
												result = findNum(choices, 8) ? null : warning;
												break;
											case 10:
												result = findNum(choices, 11)
														&& findNum(choices, 12) ? null : warning;
												break;
											case 11:
												result = findNum(choices, 10)
														&& findNum(choices, 12) ? null : warning;
												break;
											case 12:
												result = findNum(choices, 10)
														&& findNum(choices, 11) ? null : warning;
												break;
										}
										if (result != null) return result;
									}
									return null;
								}
							});
					break;
				case R.id.item_add_other_lesson_btn_weeks:
					final MultiChoiceDialog weeksDialog = new MultiChoiceDialog();
					weeksDialog.setChoiceCount(24);
					weeksDialog.setColumnCount(7);
					boolean[] choicesWeek = new boolean[24];
					if (lessonTACR.getWeek() != null) {
						for (int num : lessonTACR.getWeek())
							choicesWeek[num - 1] = true;
					}
					weeksDialog.setChoices(choicesWeek);
					weeksDialog.setOnMultiChoiceCallback(new MultiChoiceDialog.OnMultiChoiceCallback() {
						@Override
						public void onMultiChoice(int[] choices) {
							weeksDialog.dismiss();
							lessonTACR.setWeek(choices);
							setWeeks(choices);
						}
					});
					weeksDialog.show(((AppCompatActivity) context).getSupportFragmentManager(),
							context.getString(R.string.please_choose_weeks), null);
					break;
			}
		}

		@Override
		public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
			lessonTACR.setWeekday(i);
		}

		@Override
		public void onNothingSelected(AdapterView<?> adapterView) {

		}

		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

		}

		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
			lessonTACR.setClassroom(charSequence.toString());
		}

		@Override
		public void afterTextChanged(Editable editable) {

		}

		private boolean findNum(int[] choices, int key) {
			for (int num : choices) {
				if (num == key)
					return true;
			}
			return false;
		}
	}
}


