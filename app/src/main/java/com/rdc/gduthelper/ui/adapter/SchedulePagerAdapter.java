package com.rdc.gduthelper.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.rdc.gduthelper.ui.DayScheduleFragment;
import com.rdc.gduthelper.ui.WeekScheduleFragment;

import java.util.ArrayList;

/**
 * Created by seasonyuu on 16/1/7.
 */
public class SchedulePagerAdapter extends FragmentPagerAdapter {
	private ArrayList<Fragment> mFragmentList;

	private String[] mTitles = {"周视图", "日视图"};

	public SchedulePagerAdapter(FragmentManager fm) {
		super(fm);
		mFragmentList = new ArrayList<>();
		mFragmentList.add(new WeekScheduleFragment());
		mFragmentList.add(new DayScheduleFragment());
	}

	@Override
	public Fragment getItem(int position) {
		return mFragmentList.get(position);
	}

	@Override
	public int getCount() {
		return mFragmentList.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mTitles[position];
	}
}
