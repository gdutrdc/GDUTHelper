package com.rdc.gduthelper.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;

/**
 * Created by seasonyuu on 16/1/26.
 */
public class LessonDetailAdapter extends PagerAdapter {
	private Context context;

	public LessonDetailAdapter(Context context){
		this.context = context;
	}

	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return false;
	}
}
