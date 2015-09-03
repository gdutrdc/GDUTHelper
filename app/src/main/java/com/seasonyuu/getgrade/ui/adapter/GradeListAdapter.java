package com.seasonyuu.getgrade.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.seasonyuu.getgrade.R;
import com.seasonyuu.getgrade.bean.Grade;

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

	private ArrayList<Grade> list;
	private ArrayList<Grade> sortList;

	public GradeListAdapter(Context context) {
		this.context = context;
		list = new ArrayList<>();
		sortList = (ArrayList<Grade>) list.clone();
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
					Grade lhs = (Grade) lhs0;
					Grade rhs = (Grade) rhs0;
					float compareLeft = 0;
					float compareRight = 0;
					switch (mSortType) {
						case GRADE_CREDIT_UP_SORT:
							compareLeft = Float.parseFloat(lhs.getLessonCredit());
							compareRight = Float.parseFloat(rhs.getLessonCredit());
							break;
						case GRADE_CREDIT_DOWN_SORT:
							compareRight = Float.parseFloat(lhs.getLessonCredit());
							compareLeft = Float.parseFloat(rhs.getLessonCredit());
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
				sortList.add((Grade) grades[i]);
		}
		notifyDataSetChanged();
	}

	public void setData(ArrayList<Grade> list) {
		this.list = list;
		sortList = (ArrayList<Grade>) list.clone();
	}

	@Override
	public Grade getItem(int position) {
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

		public void setGrade(Grade grade) {
			tvLessonName.setText(grade.getLessonName());
			tvLessonGrade.setText(grade.getLessonGrade());
			tvLessonType.setText(grade.getLessonType());
			tvLessonCredit.setText(grade.getLessonCredit());
		}
	}
}
