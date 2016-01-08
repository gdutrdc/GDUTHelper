package com.rdc.gduthelper.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rdc.gduthelper.R;

/**
 * Created by seasonyuu on 16/1/7.
 */
public class ScheduleTextApdater extends BaseAdapter {
	private Context mContext;

	public ScheduleTextApdater(Context context) {
		mContext = context;
	}

	@Override
	public int getCount() {
		return 12;
	}

	@Override
	public Object getItem(int position) {
		return position + 1;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_schedult_text,
					parent, false);
		} else {

		}
		TextView tv = (TextView) convertView.findViewById(R.id.item_schedule_text);
		tv.setText(position + 1 + "");

		return convertView;
	}
}
