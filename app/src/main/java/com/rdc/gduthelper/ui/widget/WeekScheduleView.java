package com.rdc.gduthelper.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.utils.UIUtils;

import java.util.ArrayList;

/**
 * Created by seasonyuu on 16/1/7.
 */
public class WeekScheduleView extends ViewGroup {
	private ArrayList<TextView> lessonNums;

	private ArrayList<View> lessonViews;

	public WeekScheduleView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WeekScheduleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		init();
	}

	private void init() {
		setWillNotDraw(false);

		lessonNums = new ArrayList<>();
		for (int i = 1; i <= 12; i++) {
			TextView tv = new TextView(getContext());
			tv.setText(i + "");
			tv.setTextSize(12);
			tv.setBackgroundColor(getResources().getColor(R.color.grey_300));
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
					(int) UIUtils.convertDpToPixel(48, getContext()));
			lessonNums.add(tv);
			addView(tv, params);
		}

		lessonViews = new ArrayList<>();
		for (int i = 0; i < 7; i++) {
			View lesson0 = LayoutInflater.from(getContext()).inflate(R.layout.item_lesson_week, null);
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			addView(lesson0, params);
			lessonViews.add(lesson0);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();

		Paint paint = new Paint();
		paint.setColor(Color.GRAY);
		paint.setStrokeWidth(1);
		int lineY = 0;
		int lineX = 0;
		for (int i = 0; i < 12; i++) {
			TextView tv = lessonNums.get(i);
			canvas.drawLine(0, lineY, width, lineY, paint);
			lineY += tv.getMeasuredHeight();
		}
		canvas.drawLine((int) (width * 0.09), 0, (int) (width * .09), height, paint);

		super.onDraw(canvas);
	}

	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new MarginLayoutParams(getContext(), attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		/**
		 * 获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
		 */
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

		int height = lessonNums.get(0).getLayoutParams().height * lessonNums.size();

		measureChildren(widthMeasureSpec, heightMeasureSpec);

		for (TextView tv : lessonNums) {
			LayoutParams lp = tv.getLayoutParams();
			lp.width = (int) (sizeWidth * 0.09);
			tv.setLayoutParams(lp);
		}

		int lessonWidth = (int) (sizeWidth * 0.13);
		for (View v : lessonViews) {
			LayoutParams lp = v.getLayoutParams();
			lp.width = lessonWidth;
			v.setLayoutParams(lp);
		}

		setMeasuredDimension(sizeWidth, (heightMode == MeasureSpec.EXACTLY) ? sizeHeight
				: height);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();

		int lessonItemHeight = 0;

		for (int i = 0; i < lessonNums.size(); i++) {
			TextView tv = lessonNums.get(i);
			tv.layout(l, t, (int) (l + width * 0.09), t + tv.getMeasuredHeight());
			tv.setGravity(Gravity.CENTER);
			t += tv.getMeasuredHeight();
			lessonItemHeight = tv.getMeasuredHeight();
		}

		for (int i = 0; i < lessonViews.size(); i++) {
			View view = lessonViews.get(i);
			view.layout((int) (width * (0.09 + 0.13 * i)) + 2, lessonItemHeight * i + 2,
					(int) (width * (0.09 + 0.13 * (i + 1))) - 2, lessonItemHeight * (2 + i) - 2);
		}
	}
}
