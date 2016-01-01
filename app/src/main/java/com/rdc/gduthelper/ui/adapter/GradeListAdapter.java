package com.rdc.gduthelper.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.bean.Lesson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by seasonyuu on 15/8/29.
 */
public class GradeListAdapter extends BaseAdapter {
	public static final int DEFAULT_SORT = 0;
	public static final int GRADE_CREDIT_DOWN_SORT = 1;
	public static final int GRADE_CREDIT_UP_SORT = 2;
	public static final int GRADE_DOWN_SORT = 3;
	public static final int GRADE_UP_SORT = 4;

	private Context context;
	private int mSortType = DEFAULT_SORT;

	private ArrayList<Lesson> list;
	private ArrayList<Lesson> sortList;

	public GradeListAdapter(Context context) {
		this.context = context;
		list = new ArrayList<>();
		sortList = (ArrayList<Lesson>) list.clone();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	public void setSortType(int sortType) {
		mSortType = sortType;
		sortData();
	}

	private void sortData() {
		if (list.size() == 0)
			return;
		if (mSortType != DEFAULT_SORT) {
			final Comparator<Object> comparator = new Comparator<Object>() {
				@Override
				public int compare(Object lhs0, Object rhs0) {
					Lesson lhs = (Lesson) lhs0;
					Lesson rhs = (Lesson) rhs0;
					double compareLeft = 0;
					double compareRight = 0;
					switch (mSortType) {
						case GRADE_CREDIT_UP_SORT:
							compareLeft = Double.parseDouble(lhs.getLessonCredit());
							compareRight = Double.parseDouble(rhs.getLessonCredit());
							break;
						case GRADE_CREDIT_DOWN_SORT:
							compareRight = Double.parseDouble(lhs.getLessonCredit());
							compareLeft = Double.parseDouble(rhs.getLessonCredit());
							break;
						case GRADE_DOWN_SORT:
							compareRight = lhs.calculatePoint();
							compareLeft = rhs.calculatePoint();
							break;
						case GRADE_UP_SORT:
							compareRight = rhs.calculatePoint();
							compareLeft = lhs.calculatePoint();
							break;
					}
					if (compareLeft == compareRight) {
						return 0;
					} else if (compareLeft > compareRight) {
						return 1;
					} else {
						return -1;
					}
				}
			};
			Object[] grades = sortList.toArray();
			Arrays.sort(grades, comparator);
			sortList = new ArrayList<>();
			for (int i = 0; i < grades.length; i++)
				sortList.add((Lesson) grades[i]);
		}
		notifyDataSetChanged();
	}

	public void setData(ArrayList<Lesson> list) {
		this.list = list;
		sortList = (ArrayList<Lesson>) list.clone();
	}

	@Override
	public Lesson getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.item_grade, parent, false);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (mSortType == DEFAULT_SORT)
			holder.setGrade(list.get(position));
		else
			holder.setGrade(sortList.get(position));

		return convertView;
	}

	public static class ViewHolder {
		private TextView tvLessonName;
		private TextView tvLessonType;
		private TextView tvLessonGrade;
		private TextView tvLessonCredit;

		public ViewHolder(View itemView) {
			tvLessonCredit = (TextView) itemView.findViewById(R.id.item_grade_credit);
			tvLessonGrade = (TextView) itemView.findViewById(R.id.item_grade_grade);
			tvLessonName = (TextView) itemView.findViewById(R.id.item_grade_name);
			tvLessonType = (TextView) itemView.findViewById(R.id.item_grade_type);
		}

		public void setGrade(Lesson lesson) {
			tvLessonName.setText(lesson.getLessonName());
			tvLessonGrade.setText(lesson.getLessonGrade());
			tvLessonType.setText(lesson.getLessonType());
			tvLessonCredit.setText(lesson.getLessonCredit());
		}
	}
}
