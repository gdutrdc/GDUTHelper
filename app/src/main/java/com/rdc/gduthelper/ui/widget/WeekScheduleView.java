package com.rdc.gduthelper.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.bean.Lesson;
import com.rdc.gduthelper.bean.LessonTACR;
import com.rdc.gduthelper.bean.MaterialColors;
import com.rdc.gduthelper.utils.LessonUtils;
import com.rdc.gduthelper.utils.UIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by seasonyuu on 16/1/7.
 */
public class WeekScheduleView extends ViewGroup implements View.OnClickListener, View.OnLongClickListener {
	private static final String TAG = WeekScheduleView.class.getSimpleName();
	private Bitmap background;

	@Override
	public void onClick(View v) {
		if (onLessonsClickListener != null) {
			ArrayList<LessonTACR> tacrs = (ArrayList<LessonTACR>) v.getTag();
			HashMap<Lesson, LessonTACR> map = new HashMap<>();
			for (LessonTACR tacr : tacrs) {
				Lesson lesson = LessonUtils.findLesson(mLessons, tacr.getLessonCode());
				map.put(lesson, tacr);
			}
			onLessonsClickListener.onClick(v, map);
		}
	}

	@Override
	public boolean onLongClick(View v) {
		if (onLessonsClickListener != null) {
			ArrayList<LessonTACR> tacrs = (ArrayList<LessonTACR>) v.getTag();
			HashMap<Lesson, LessonTACR> map = new HashMap<>();
			for (LessonTACR tacr : tacrs) {
				Lesson lesson = LessonUtils.findLesson(mLessons, tacr.getLessonCode());
				map.put(lesson, tacr);
			}
			onLessonsClickListener.onLongClick(v, map);
			return true;
		}
		return false;
	}

	public interface OnLessonsClickListener {
		public void onClick(View lessonView, Map<Lesson, LessonTACR> lessonMap);

		public void onLongClick(View lessonView, Map<Lesson, LessonTACR> lessonMap);
	}

	private OnLessonsClickListener onLessonsClickListener;

	private int week = 0;

	private int cardColorIndex = 0;

	private HashMap<String, Integer> lessonColors;

	private ArrayList<TextView> lessonNums;

	private ArrayList<View> lessonViews;
	private int[] colors = new int[]{MaterialColors.getColor(MaterialColors.BLUE)};
	private int colorOut = Color.GRAY;

	private ArrayList<Lesson> mLessons;

	public WeekScheduleView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WeekScheduleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		init();
	}

	public OnLessonsClickListener getOnLessonsClickListener() {
		return onLessonsClickListener;
	}

	public void setOnLessonsClickListener(OnLessonsClickListener onLessonsClickListener) {
		this.onLessonsClickListener = onLessonsClickListener;
	}

	public HashMap<String, Integer> getLessonColors() {
		return lessonColors;
	}

	public int[] getColors() {
		return colors;
	}

	public void setColors(int[] colors) {
		this.colors = colors;
		cardColorIndex = 0;
		lessonColors = new HashMap<>();
		setLessons(mLessons);
	}

	public void setWeek(int week) {
		this.week = week;
		if (mLessons == null || mLessons.size() == 0) {
			return;
		}
		cardColorIndex = 0;
		for (View view : lessonViews) {
			makeLessonView(view);
		}
	}

	private void init() {
		setWillNotDraw(false);

		lessonNums = new ArrayList<>();
		mLessons = new ArrayList<>();
		lessonColors = new HashMap<>();
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

	public void setScheduleBackground(Bitmap bitmap) {
		background = bitmap;
		invalidate();
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
			int color;
			if (lessonColors.get(tacr1.getLessonCode()) == null) {
				color = colors[cardColorIndex % colors.length];
				lessonColors.put(tacr1.getLessonCode(), color);
				cardColorIndex++;
			} else
				color = lessonColors.get(tacr1.getLessonCode());

			LayoutParams lp1 = view.findViewById(R.id.item_schedule_text).getLayoutParams();
			CardView cardView = (CardView) view;
			lp1.width = (int) ((getWidth() * 0.13) - (cardView.getMaxCardElevation()
					+ (1 - Math.cos(45)) * cardView.getRadius()));
			lp1.height = (int) (tacr1.getNum().length * UIUtils.getScreenHeight(getContext()) / 12
					- cardView.getMaxCardElevation() * 1.5
					- (1 - Math.cos(45)) * cardView.getRadius());
			cardView.findViewById(R.id.item_schedule_text).setLayoutParams(lp1);

			if (LessonUtils.lessonInThisWeek(tacr1, week)) {
				isShown = true;

				((TextView) view.findViewById(R.id.item_schedule_text)).setText(
						LessonUtils.findLesson(
								mLessons, tacr1.getLessonCode()).getLessonName()
								+ "@" + tacr1.getClassroom());

				cardView.setCardBackgroundColor(color);
				((FrameLayout) view.findViewById(R.id.item_schedule_container))
						.setForeground(makeCoverDrawable(color, 8, Gravity.BOTTOM));
			}
		}
		if (!isShown) {
			LessonTACR tacr = lessonTACRs.get(0);
			Lesson lesson = LessonUtils.findLesson(mLessons, tacr.getLessonCode());
			((TextView) view.findViewById(R.id.item_schedule_text))
					.setText(lesson.getLessonName() + "@" + tacr.getClassroom());
			int color = colorOut;
			((CardView) view).setCardBackgroundColor(color);
			((FrameLayout) view.findViewById(R.id.item_schedule_container))
					.setForeground(makeCoverDrawable(color, 8, Gravity.BOTTOM));
		}
		view.findViewById(R.id.item_schedule_dog_ear)
				.setVisibility(lessonTACRs.size() > 1 ? VISIBLE : GONE);
		view.setOnClickListener(this);
		view.setOnLongClickListener(this);
	}

	private Drawable makeCoverDrawable(int baseColor, int numStops, int gravity) {
		numStops = Math.max(numStops, 2);

		PaintDrawable paintDrawable = new PaintDrawable();
		paintDrawable.setShape(new RectShape());

		final int[] stopColors = new int[numStops];
		int red = Color.red(baseColor);
		int green = Color.green(baseColor);
		int blue = Color.blue(baseColor);
		int alpha = Color.alpha(baseColor);

		for (int i = 0; i < numStops; i++) {
			double x = 1f / Math.pow(numStops - i, 2);
//			float opacity = MathUtil.constrain(0, 1, Math.pow(x,3));
			stopColors[i] = Color.argb((int) (alpha * x) % 256, red, green, blue);
		}

		final float x0, x1, y0, y1;
		switch (gravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
			case Gravity.LEFT:
				x0 = 1;
				x1 = 0;
				break;
			case Gravity.RIGHT:
				x0 = 0;
				x1 = 1;
				break;
			default:
				x0 = 0;
				x1 = 0;
				break;
		}
		switch (gravity & Gravity.VERTICAL_GRAVITY_MASK) {
			case Gravity.TOP:
				y0 = 1;
				y1 = 0;
				break;
			case Gravity.BOTTOM:
				y0 = 0;
				y1 = 1;
				break;
			default:
				y0 = 0;
				y1 = 0;
				break;
		}

		paintDrawable.setShaderFactory(new ShapeDrawable.ShaderFactory() {
			@Override
			public Shader resize(int width, int height) {
				return new LinearGradient(
						width * x0,
						height * y0,
						width * x1,
						height * y1,
						stopColors, null,
						Shader.TileMode.CLAMP
				);
			}
		});

		return paintDrawable;
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
		paint.setStrokeWidth(1);
		if (background == null) {
			paint.setColor(Color.parseColor("#22888888"));
			int lineY = 0;
			int lineX = 0;
			for (int i = 0; i < 12; i++) {
				TextView tv = lessonNums.get(i);
				if (i != 0)
					canvas.drawLine(0, lineY, width, lineY, paint);
				lineY += tv.getMeasuredHeight();
			}
		} else {
			Rect rect = new Rect();
			rect.set((int) (width * 0.09), 0, width, height);
			canvas.drawBitmap(background, null, rect, paint);
		}

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
			for (View view : lessonViews) {
				if (view != null && view.getParent() == this) {
					ArrayList<LessonTACR> tacrs = (ArrayList<LessonTACR>) view.getTag();
					LessonTACR tacr = tacrs.get(0);

					view.layout((int) (width * (0.09 + 0.13 * tacr.getWeekday() % 7)),
							(tacr.getNum()[0] - 1) * lessonItemHeight,
							(int) (width * (0.09 + 0.13 * (tacr.getWeekday() % 7 + 1))),
							(tacr.getNum()[0] - 1 + tacr.getNum().length) * lessonItemHeight);
				}
			}
		}
	}
}
