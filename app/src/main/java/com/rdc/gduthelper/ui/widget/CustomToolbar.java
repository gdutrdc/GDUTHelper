package com.rdc.gduthelper.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import com.rdc.gduthelper.R;

/**
 * Created by seasonyuu on 2016/12/29.
 */

public class CustomToolbar extends Toolbar {
	public enum State {
		ARROW_TO_X, X_TO_ARROW
	}

	public CustomToolbar(Context context) {
		super(context);
	}

	public CustomToolbar(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void startAnimation(State state) {
		startAnimation(state, Color.WHITE);
	}

	public void startAnimation(State state, int color) {
		if (state == State.ARROW_TO_X) {
			AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) getContext().getDrawable(R.drawable.arrow_to_x);
			drawable.setTint(color);
			setNavigationIcon(drawable);
			drawable.start();
		} else if (state == State.X_TO_ARROW) {
			AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) getContext().getDrawable(R.drawable.x_to_arrow);
			drawable.setTint(color);
			setNavigationIcon(drawable);
			drawable.start();
		}
	}

}
