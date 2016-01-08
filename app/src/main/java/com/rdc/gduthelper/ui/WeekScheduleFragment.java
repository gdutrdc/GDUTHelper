package com.rdc.gduthelper.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rdc.gduthelper.R;

/**
 * Created by seasonyuu on 16/1/7.
 */
public class WeekScheduleFragment extends Fragment {
	private View contentView;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater,
	                         @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		contentView = LayoutInflater.from(getActivity())
				.inflate(R.layout.fragment_week_schedule, container, false);
		return contentView;
	}
}
