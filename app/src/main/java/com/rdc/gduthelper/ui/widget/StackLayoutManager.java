package com.rdc.gduthelper.ui.widget;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by seasonyuu on 16/1/27.
 */
public class StackLayoutManager extends RecyclerView.LayoutManager {

	@Override
	public RecyclerView.LayoutParams generateDefaultLayoutParams() {
		return new RecyclerView.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	}

	@Override
	public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
		if (getItemCount() == 0) {
			detachAndScrapAttachedViews(recycler);
			return;
		}
		if (getChildCount() == 0 && state.isPreLayout()) {
			//Nothing to do during prelayout when empty
			return;
		}
		detachAndScrapAttachedViews(recycler);

		int offsetX = 48;
		int offsetY = getHeight() / 4;

		for (int i = 0; i < getItemCount(); i++) {
			View scrap = recycler.getViewForPosition(i);
			addView(scrap);
			if (Build.VERSION.SDK_INT >= 21)
				scrap.setElevation(i * 3.0f);
			measureChildWithMargins(scrap, 0, 0);  // 计算此碎片view包含边距的尺寸

			int width = getDecoratedMeasuredWidth(scrap);  // 获取此碎片view包含边距和装饰的宽度width
			int height = getDecoratedMeasuredHeight(scrap); // 获取此碎片view包含边距和装饰的高度height

			layoutDecorated(scrap,
					offsetX,
					offsetY,
					getWidth() - 48,
					offsetY + height); // Important！布局到RecyclerView容器中，所有的计算都是为了得出任意position的item的边界来布局

			offsetY += (height + 48) / 2;
		}
	}

	private void fill(RecyclerView.Recycler recycler, RecyclerView.State state) {

	}

	@Override
	public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
		if (getChildCount() <= 1 || dy == 0)
			return 0;
		return super.scrollVerticallyBy(dy, recycler, state);
	}

	@Override
	public boolean canScrollVertically() {
		return true;
	}
}
