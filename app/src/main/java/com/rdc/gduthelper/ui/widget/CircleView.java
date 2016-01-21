package com.rdc.gduthelper.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

import com.rdc.gduthelper.bean.MaterialColors;

/**
 * Created by seasonyuu on 16/1/22.
 */
public class CircleView extends View {
	private static final int DEFAULT_BACKGROUND_COLOR = MaterialColors.getColor(MaterialColors.BLUE);
	private int backgroundColor = DEFAULT_BACKGROUND_COLOR;

	public CircleView(Context context) {
		super(context);
	}

	public CircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}


	@Override
	protected void onDraw(Canvas canvas) {
		int size = getWidth();
		canvas.drawBitmap(createCircleImage(size), 0, 0, null);
	}

	/**
	 * 121      * 根据原图和边长绘制圆形图片
	 * 122      *
	 * 123      * @param source
	 * 124      *            color 这两个参数只能取一个
	 * 125      * @paramsize
	 * 126      * @return
	 * 127
	 */
	private Bitmap createCircleImage(int size) {

		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap target = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
		/**
		 * 产生一个同样大小的画布
		 */
		Canvas canvas = new Canvas(target);
		/**
		 * 首先绘制圆形
		 */
		canvas.drawCircle(size / 2, size / 2, size / 2, paint);

		/**
		 * 使用SRC_IN，参考上面的说明
		 */
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		/**
		 * 绘制图片
		 */
		paint.setColor(backgroundColor);
		canvas.drawCircle(size / 2, size / 2, size / 2, paint);
		// 让画出的图形是空心的
		paint.setStyle(Paint.Style.STROKE);
		// 设置画出的线的 粗细程度
		paint.setStrokeWidth(2);
		paint.setColor(Color.WHITE);
		canvas.drawCircle(size / 2, size / 2, size / 2, paint);
		return target;

	}

	@Override
	public void setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

}
