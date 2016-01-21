package com.rdc.gduthelper.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.bean.Lesson;
import com.rdc.gduthelper.bean.LessonTACR;
import com.rdc.gduthelper.bean.MaterialColors;
import com.rdc.gduthelper.utils.LessonUtils;
import com.rdc.gduthelper.utils.UIUtils;

import java.util.ArrayList;

/**
 * Created by seasonyuu on 16/1/7.
 */
public class WeekScheduleView extends ViewGroup {
	private static final String TAG = WeekScheduleView.class.getSimpleName();

	private int week = 0;

	private ArrayList<TextView> lessonNums;

	private ArrayList<View> lessonViews;
	private int colorIn = MaterialColors.getColor(MaterialColors.BLUE);
	private int colorOut = Color.GRAY;

	private ArrayList<Lesson> mLessons;

	public WeekScheduleView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WeekScheduleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		init();
	}

	public void setWeek(int week) {
		this.week = week;
		if (mLessons == null || mLessons.size() == 0) {
			return;
		}
		for (View view : lessonViews) {
			makeLessonView(view);
		}
	}

	private void init() {
		setWillNotDraw(false);

		lessonNums = new ArrayList<>();
		mLessons = new ArrayList<>();
		int lessonItemHeight = UIUtils.getScreenHeight(getContext()) / 12;
		if (lessonItemHeight < 48)
			lessonItemHeight = 48;
		for (int i = 1; i <= 12; i++) {
			TextView tv = new TextView(getContext());
			tv.setText(i + "");
			tv.setTextSize(12);
			tv.setBackgroundColor(getResources().getColor(R.color.grey_300));
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, lessonItemHeight);
			lessonNums.add(tv);
			addView(tv, params);
		}

		lessonViews = new ArrayList<>();
	}

	public ArrayList<Lesson> getLessons() {
		return mLessons;
	}

	public void setLessons(ArrayList<Lesson> lessons) {
		mLessons = lessons;
		for (View view : lessonViews)
			removeView(view);
		lessonViews = new ArrayList<>();
		for (int i = 0; i < mLessons.size(); i++) {
			Lesson lesson = mLessons.get(i);
			ArrayList<LessonTACR> tacrs = lesson.getLessonTACRs();
			for (LessonTACR tacr : tacrs) {
				View view = findViewWithTag(lessonViews, tacr);
				if (view == null) {
					view = LayoutInflater.from(getContext()).inflate(R.layout.item_lesson_week, null);
					addView(view);
					lessonViews.add(view);
				}
				ArrayList<LessonTACR> lessonTACRs = null;
				if (view.getTag() != null)
					lessonTACRs = (ArrayList<LessonTACR>) view.getTag();
				else {
					lessonTACRs = new ArrayList<>();
					view.setTag(lessonTACRs);
				}
				lessonTACRs.add(tacr);

				makeLessonView(view);
			}
		}
	}

	private void makeLessonView(View view) {
		ArrayList<LessonTACR> lessonTACRs = (ArrayList<LessonTACR>) view.getTag();

		boolean isShown = false;
		for (LessonTACR tacr1 : lessonTACRs) {
			if (LessonUtils.lessonInThisWeek(tacr1, week)) {
				isShown = true;
				int color = colorIn;
				((TextView) view.findViewById(R.id.item_schedule_text)).setText(
						LessonUtils.findLesson(
								mLessons, tacr1.getLessonCode()).getLessonName()
								+ "@" + tacr1.getClassroom());
				view.findViewById(R.id.item_schedule_dog_ear).setVisibility(GONE);
				((CardView) view).setCardBackgroundColor(color);
			}
		}
		if (!isShown) {
			LessonTACR tacr = lessonTACRs.get(0);
			Lesson lesson = LessonUtils.findLesson(mLessons, tacr.getLessonCode());
			((TextView) view.findViewById(R.id.item_schedule_text))
					.setText(lesson.getLessonName() + "@" + tacr.getClassroom());
			int color = colorOut;
			((CardView) view).setCardBackgroundColor(color);
		}
		view.findViewById(R.id.item_schedule_dog_ear)
				.setVisibility(lessonTACRs.size() > 1 ? VISIBLE : GONE);
	}

	private View findViewWithTag(ArrayList<View> views, LessonTACR tacr) {
		for (View v : views) {
			ArrayList<LessonTACR> lessonTACRs = (ArrayList<LessonTACR>) v.getTag();
			for (LessonTACR lessonTACR : lessonTACRs) {
				if (LessonUtils.isSameTime(lessonTACR, tacr)) {
					return v;
				}
			}
		}
		return null;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();

		Paint paint = new Paint();
		paint.setColor(Color.parseColor("#22888888"));
		paint.setStrokeWidth(1);
		int lineY = 0;
		int lineX = 0;
		for (int i = 0; i < 12; i++) {
			TextView tv = lessonNums.get(i);
			if (i != 0)
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

		if (mLessons.size() > 0) {
			int margin = getResources().getDimensionPixelOffset(R.dimen.lesson_item_margin);
			if (Build.VERSION.SDK_INT < 21)
				margin = 0;
			for (View view : lessonViews) {
				if (view != null && view.getParent() == this) {
					ArrayList<LessonTACR> tacrs = (ArrayList<LessonTACR>) view.getTag();
					LessonTACR tacr = tacrs.get(0);


					view.layout((int) (width * (0.09 + 0.13 * tacr.getWeekday() % 7)) + margin,
							(tacr.getNum()[0] - 1) * lessonItemHeight + margin,
							(int) (width * (0.09 + 0.13 * (tacr.getWeekday() % 7 + 1))),
							(tacr.getNum()[0] - 1 + tacr.getNum().length) * lessonItemHeight);

					LayoutParams lp1 = view.findViewById(R.id.item_schedule_text).getLayoutParams();
					lp1.width = (int) (getMeasuredWidth() * 0.13);
					view.findViewById(R.id.item_schedule_text).setLayoutParams(lp1);
				}
			}
		}
	}
}
