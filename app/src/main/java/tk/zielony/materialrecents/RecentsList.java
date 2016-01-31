package tk.zielony.materialrecents;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.nineoldandroids.view.ViewHelper;
import com.rdc.gduthelper.utils.UIUtils;

/**
 * Created by Marcin on 2015-04-13.
 */
public class RecentsList extends FrameLayout implements GestureDetector.OnGestureListener {
	Scroller scroller;
	RecentsAdapter adapter;
	GestureDetector gestureDetector = new GestureDetector(this);
	int scroll = 0;
	OnItemClickListener onItemClickListener;
	Rect childTouchRect[];

	public interface OnItemClickListener {
		void onItemClick(View view, int position);
	}

	public RecentsList(Context context) {
		super(context);
		initRecentsList();
	}

	public RecentsList(Context context, AttributeSet attrs) {
		super(context, attrs);
		initRecentsList();
	}

	public RecentsList(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initRecentsList();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public RecentsList(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		initRecentsList();
	}

	private void initRecentsList() {
		scroller = new Scroller(getContext());
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public RecentsAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(RecentsAdapter adapter) {
		this.adapter = adapter;
		scroll = 0;
		invalidate();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if (adapter == null)
			return;

		initChildren();

		childTouchRect = new Rect[getChildCount()];
		for (int i = 0; i < getChildCount(); i++) {
			int width = getWidth() / 4 * 3;
			getChildAt(i).layout((getWidth() - width) / 2, 0,
					getWidth() / 2 + width / 2 - getPaddingLeft() - getPaddingRight(),
					width - getPaddingLeft() - getPaddingRight());
			childTouchRect[i] = new Rect();
		}
	}

	private void initChildren() {
		removeAllViews();
		for (int i = 0; i < adapter.getCount(); i++) {
			final View card = adapter.getView(i);
			addView(card, i, generateDefaultLayoutParams());
			final int finalI = i;
			if (Build.VERSION.SDK_INT >= 21)
				card.setElevation(UIUtils.convertDpToPixel(4 * i, getContext()));
			card.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					if (onItemClickListener != null)
						onItemClickListener.onItemClick(card, finalI);
				}
			});
		}
	}

	private void layoutChildren() {
		int width = getWidth() - getPaddingLeft() - getPaddingRight();
		int height = getHeight() - getPaddingTop() - getPaddingBottom();
		for (int i = 0; i < getChildCount(); i++) {
			float topSpace = height - width;
			int y = (int) (topSpace * Math.pow(2, (i * width - scroll) / (float) width));
			float scale = (float) (-Math.pow(2, -y / topSpace / 10.0f) + 19.0f / 10);
			childTouchRect[i].set(getPaddingLeft(), y + getPaddingTop(), (int) (scale * (getPaddingLeft() + getWidth() - getPaddingLeft() - getPaddingRight())), (int) (scale * (y + getPaddingTop() + getWidth() - getPaddingLeft() - getPaddingRight())));
			ViewHelper.setTranslationX(getChildAt(i), getPaddingLeft());
			ViewHelper.setTranslationY(getChildAt(i), y + getPaddingTop());
			ViewHelper.setScaleX(getChildAt(i), scale);
			ViewHelper.setScaleY(getChildAt(i), scale);
		}
	}

	private int getMaxScroll() {
		return (getChildCount() - 1) * (getWidth() - getPaddingLeft() - getPaddingRight());
	}

	@Override
	protected void dispatchDraw(@NonNull Canvas canvas) {
		layoutChildren();
		super.dispatchDraw(canvas);
		doScrolling();
	}

	@Override
	public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
		if (gestureDetector.onTouchEvent(event)) {
			for (int i = getChildCount() - 1; i >= 0; i--) {
				MotionEvent e = MotionEvent.obtain(event);
				event.setAction(MotionEvent.ACTION_CANCEL);
				e.offsetLocation(-childTouchRect[i].left, -childTouchRect[i].top);
				getChildAt(i).dispatchTouchEvent(e);
			}
			return true;
		}

		if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
			forceFinished();
		}

		for (int i = getChildCount() - 1; i >= 0; i--) {
			if (childTouchRect[i].contains((int) event.getX(), (int) event.getY())) {
				MotionEvent e = MotionEvent.obtain(event);
				e.offsetLocation(-childTouchRect[i].left, -childTouchRect[i].top);
				if (getChildAt(i).dispatchTouchEvent(e))
					break;
			}
		}

		return true;
	}

	@Override
	public boolean onDown(MotionEvent motionEvent) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent motionEvent) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent event) {

		return false;
	}

	@Override
	public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
		scroll = (int) Math.max(0, Math.min(scroll + v2, getMaxScroll()));
		postInvalidate();
		return true;
	}

	@Override
	public void onLongPress(MotionEvent motionEvent) {

	}

	void startScrolling(float initialVelocity) {
		scroller.fling(0, scroll, 0, (int) initialVelocity, 0,
				0, Integer.MIN_VALUE, Integer.MAX_VALUE);

		postInvalidate();
	}

	private void doScrolling() {
		if (scroller.isFinished())
			return;

		boolean more = scroller.computeScrollOffset();
		int y = scroller.getCurrY();

		scroll = Math.max(0, Math.min(y, getMaxScroll()));

		if (more)
			postInvalidate();
	}

	boolean isFlinging() {
		return !scroller.isFinished();
	}

	void forceFinished() {
		if (!scroller.isFinished()) {
			scroller.forceFinished(true);
		}
	}

	@Override
	public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
		startScrolling(-v2);
		return true;
	}

}
