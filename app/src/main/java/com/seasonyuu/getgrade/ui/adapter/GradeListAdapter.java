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

/**
 * Created by seasonyuu on 15/8/29.
 */
public class GradeListAdapter extends BaseAdapter {
	private Context context;

	private ArrayList<Grade> list;

	public GradeListAdapter(Context context) {
		this.context = context;
		list = new ArrayList<>();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	public void setData(ArrayList<Grade> list) {
		this.list = list;
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
		holder.setGrade(list.get(position));
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
