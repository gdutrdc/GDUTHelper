package com.rdc.gduthelper.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.view.ContextThemeWrapper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.app.GDUTHelperApp;
import com.rdc.gduthelper.utils.UIUtils;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;

/**
 * Created by seasonyuu on 16-7-31.
 */

public class MultiChoiceDialog extends DialogFragment {
	private static final int DEFAULT_CHOICE_COUNT = 20;
	private int mChoiceCount;
	private String mTitle;
	private final int SIZE_16DP =
			(int) UIUtils.convertDpToPixel(16, GDUTHelperApp.getInstance());

	private int mColumnCount;
	private OnMultiChoiceCallback mOnMultiChoiceCallback;
	private OnMatchResultCallback mOnMatchResultCallback;
	private TextView mTvAlert;
	private boolean[] mChoices;

	public interface OnMultiChoiceCallback {
		void onMultiChoice(int choices[]);
	}

	public interface OnMatchResultCallback {
		/**
		 * 根据返回的选择结果，选择提示文字，在按下确认时执行该回调
		 *
		 * @param choices 选择结果
		 * @return 提示文本，为null时表示符合规则并正常返回结果
		 */
		String onMatchResult(int[] choices);
	}

	public boolean[] getChoices() {
		return mChoices;
	}

	public void setChoices(boolean[] choices) {
		mChoices = choices;
	}

	public int getColumnCount() {
		return mColumnCount;
	}

	public void setColumnCount(int columnCount) {
		this.mColumnCount = columnCount;
	}

	public MultiChoiceDialog() {
		mChoiceCount = DEFAULT_CHOICE_COUNT;
	}

	public int getChoiceCount() {
		return mChoiceCount;
	}

	public void setChoiceCount(int choiceCount) {
		this.mChoiceCount = choiceCount;
	}

	public void show(FragmentManager fragmentManager, String title, OnMatchResultCallback onMatchResultCallback) {
		mTitle = title;
		mOnMatchResultCallback = onMatchResultCallback;
		show(fragmentManager, "MultiChoiceDialog");
	}

	public OnMultiChoiceCallback getOnMultiChoiceCallback() {
		return mOnMultiChoiceCallback;
	}

	public void setOnMultiChoiceCallback(OnMultiChoiceCallback onMultiChoiceCallback) {
		mOnMultiChoiceCallback = onMultiChoiceCallback;
	}


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		LinearLayout containerLayout = new LinearLayout(getActivity());
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		containerLayout.setLayoutParams(params);
		containerLayout.setMinimumWidth((int) UIUtils.convertDpToPixel(480, getActivity()));
		containerLayout.setOrientation(LinearLayout.VERTICAL);

		TextView tvTitle = new TextView(getActivity());
		tvTitle.setText(mTitle);
		tvTitle.setTextSize(20);
		tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
		tvTitle.setTextColor(Color.BLACK);
		tvTitle.setPadding(SIZE_16DP / 2 * 3,
				SIZE_16DP / 2 * 3, SIZE_16DP / 2 * 3, SIZE_16DP / 4 * 5);
		containerLayout.addView(tvTitle, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

		final MultiChoiceView multiChoiceView = new MultiChoiceView(
				getActivity(), mChoiceCount, mColumnCount, mChoices);
		LinearLayout.LayoutParams choiceViewParams = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		choiceViewParams.setMargins(SIZE_16DP / 2 * 3, 0, SIZE_16DP / 2 * 3, SIZE_16DP / 2 * 3);
		containerLayout.addView(multiChoiceView, choiceViewParams);

		mTvAlert = new TextView(getActivity());
		mTvAlert.setGravity(Gravity.CENTER);
		mTvAlert.setPadding(SIZE_16DP, 0, SIZE_16DP, 0);
		mTvAlert.setMaxLines(3);
		containerLayout.addView(mTvAlert, new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

		final int ButtonStyle = R.style.FlatButton;
		Button button = new Button(new ContextThemeWrapper(getActivity(), ButtonStyle), null, ButtonStyle);
		button.setText(R.string.ensure);
		button.setTextColor(getResources().getColor(R.color.teal_500));
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				boolean[] choices = multiChoiceView.getSelectedItems();
				int times = 0;
				for (boolean choice : choices) {
					if (choice)
						times++;
				}
				int[] result = new int[times];
				times = 0;
				for (int i = 0; i < choices.length; i++) {
					if (choices[i])
						result[times++] = i + 1;
				}
				if (mOnMatchResultCallback != null) {
					String text = mOnMatchResultCallback.onMatchResult(result);
					if (text == null)
						if (mOnMultiChoiceCallback != null) {
							mOnMultiChoiceCallback.onMultiChoice(result);
							return;
						}
					if (mTvAlert.getAnimation() != null)
						mTvAlert.getAnimation().cancel();
					mTvAlert.setText(text);
					mTvAlert.setTextColor(Color.RED);
					AlphaAnimation animation = new AlphaAnimation(0, 1);
					animation.setDuration(1200);
					animation.setRepeatMode(Animation.REVERSE);
					animation.setRepeatCount(1);
					animation.setInterpolator(new FastOutSlowInInterpolator());
					animation.setAnimationListener(new Animation.AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {

						}

						@Override
						public void onAnimationEnd(Animation animation) {
							mTvAlert.setText("");
							mTvAlert.setAlpha(1);
						}

						@Override
						public void onAnimationRepeat(Animation animation) {

						}
					});
					mTvAlert.startAnimation(animation);
				} else if (mOnMultiChoiceCallback != null) {
					mOnMultiChoiceCallback.onMultiChoice(result);
				}
			}
		});
		LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		buttonParams.gravity = Gravity.RIGHT | Gravity.END;
		buttonParams.setMargins(SIZE_16DP / 2, SIZE_16DP / 2, SIZE_16DP / 2, SIZE_16DP / 2);
		containerLayout.addView(button, buttonParams);

		return containerLayout;
	}

	private class MultiChoiceView extends View {
		private int choiceCount;
		private int buttonHeight;

		private int columnCount;

		private final int MIN_WIDTH =
				(int) UIUtils.convertDpToPixel(32, GDUTHelperApp.getInstance());

		private final int SELECTED_COLOR = Color.parseColor("#30000000");

		private boolean[] selectedItems;

		private int lastTouch = -1;

		public boolean[] getSelectedItems() {
			return selectedItems;
		}

		MultiChoiceView(Context context, int choiceCount, int columnCount, boolean[] choices) {
			super(context);
			this.choiceCount = choiceCount;
			this.columnCount = columnCount;

			if (choices == null || choiceCount != choices.length)
				selectedItems = new boolean[choiceCount];
			else selectedItems = choices;

			buttonHeight = (int) UIUtils.convertDpToPixel(36, context);

			setClickable(true);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			int width = 0;
			int height = 0;

			int widthMode = MeasureSpec.getMode(widthMeasureSpec);
			int widthSize = MeasureSpec.getSize(widthMeasureSpec);
			if (widthMode == EXACTLY) {
				width = widthSize;
			} else if (widthMode == AT_MOST) {
				width = Math.min(MIN_WIDTH * columnCount, widthSize);
			} else {
				width = MIN_WIDTH * columnCount;
			}

			int heightMode = MeasureSpec.getMode(heightMeasureSpec);
			int heightSize = MeasureSpec.getSize(heightMeasureSpec);
			int lines = selectedItems.length / columnCount
					+ (selectedItems.length % columnCount == 0 ? 0 : 1);
			if (heightMode == EXACTLY) {
				height = heightSize;
			} else if (heightMode == AT_MOST) {
				height = Math.min(buttonHeight * lines, heightSize);
			} else {
				height = buttonHeight * lines;
			}

			setMeasuredDimension(width + getPaddingLeft() + getPaddingRight(),
					height + getPaddingTop() + getPaddingBottom());
		}

		@Override
		protected void onDraw(Canvas canvas) {
			Paint bgPaint = new Paint();
			bgPaint.setColor(SELECTED_COLOR);
			Paint textPaint = new Paint();
			textPaint.setColor(Color.BLACK);
			textPaint.setAntiAlias(true);
			textPaint.setTextSize(UIUtils.convertDpToPixel(14, getContext()));
			textPaint.setTextAlign(Paint.Align.CENTER);
			int width = (getMeasuredWidth() - getPaddingRight() - getPaddingLeft()) / columnCount;
			int startY = getPaddingTop();
			int startX = getPaddingLeft();
			for (int i = 0; i < choiceCount; i++) {
				String text = i + 1 + "";
				Rect targetRect = new Rect(0, 0, width, buttonHeight);
				Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
				int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;

				if (selectedItems[i])
					canvas.drawRect(startX + i % columnCount * width,
							startY + i / columnCount * buttonHeight,
							startX + (i % columnCount + 1) * width,
							startY + (i / columnCount + 1) * buttonHeight,
							bgPaint);

				canvas.drawText(text,
						startX + i % columnCount * width + targetRect.centerX(),
						startY + i / columnCount * buttonHeight + baseline,
						textPaint);
			}
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN)
				lastTouch = -1;
			float x = event.getX();
			float y = event.getY();
			if (x > getPaddingLeft() && x < getMeasuredWidth() - getPaddingRight()
					&& y > getPaddingTop() && y < getMeasuredHeight() - getPaddingBottom()) {
				Log.d("onTouch", x + " " + y);
				int width = (getMeasuredWidth() - getPaddingRight() - getPaddingLeft()) / columnCount;
				int startY = getPaddingTop();
				int startX = getPaddingLeft();
				int index = 0;
				int line = (int) ((y - startY) / buttonHeight);
				index = (int) ((x - startX) / width + line * columnCount);
				if (index == lastTouch || index >= selectedItems.length) {
					return super.onTouchEvent(event);
				}

				selectedItems[index] = !selectedItems[index];
				lastTouch = index;
				invalidate();
			}
			return super.onTouchEvent(event);
		}
	}
}
