package com.seasonyuu.getgrade.bean;

import android.content.res.Resources;

import com.seasonyuu.getgrade.R;
import com.seasonyuu.getgrade.app.GGApplication;

/**
 * Created by seasonyuu on 15/9/8.
 */
public class MaterialColors {
	private static MaterialColors materialColors;
	private int[] colors;

	public static final int RED = 0;
	public static final int PINK = 1;
	public static final int PURPLE = 2;
	public static final int DEEP_PURPLE = 3;
	public static final int INDIGO = 4;
	public static final int BLUE = 5;
	public static final int LIGHT_BLUE = 6;
	public static final int CYAN = 7;
	public static final int TEAL = 8;
	public static final int GREEN = 9;
	public static final int LIGHT_GREEN = 10;
	public static final int LIME = 11;
	public static final int YELLOW = 12;
	public static final int AMBER = 13;
	public static final int ORANGE = 14;
	public static final int DEEP_ORANGE = 15;
	public static final int BROWN = 16;
	public static final int GREY = 17;
	public static final int BLUE_GREY = 18;
	public static final int BLACK = 19;
	public static final int WHITE = 20;


	private MaterialColors() {
		Resources resource = GGApplication.getInstance().getApplicationContext().getResources();
		colors = new int[21];
		colors[RED] = resource.getColor(R.color.red_500);
		colors[PINK] = resource.getColor(R.color.pink_500);
		colors[PURPLE] = resource.getColor(R.color.purple_500);
		colors[DEEP_PURPLE] = resource.getColor(R.color.deep_purple_500);
		colors[INDIGO] = resource.getColor(R.color.indigo_500);
		colors[BLUE] = resource.getColor(R.color.blue_500);
		colors[LIGHT_BLUE] = resource.getColor(R.color.light_blue_500);
		colors[CYAN] = resource.getColor(R.color.cyan_500);
		colors[TEAL] = resource.getColor(R.color.teal_500);
		colors[GREEN] = resource.getColor(R.color.green_500);
		colors[LIGHT_GREEN] = resource.getColor(R.color.light_green_500);
		colors[LIME] = resource.getColor(R.color.lime_500);
		colors[YELLOW] = resource.getColor(R.color.yellow_500);
		colors[AMBER] = resource.getColor(R.color.amber_500);
		colors[ORANGE] = resource.getColor(R.color.orange_500);
		colors[DEEP_ORANGE] = resource.getColor(R.color.deep_orange_500);
		colors[BROWN] = resource.getColor(R.color.brown_500);
		colors[GREY] = resource.getColor(R.color.grey_500);
		colors[BLUE_GREY] = resource.getColor(R.color.blue_grey_500);
		colors[BLACK] = resource.getColor(android.R.color.black);
		colors[WHITE] = resource.getColor(R.color.white);
	}

	public static int getColor(int index) {
		if (materialColors == null)
			materialColors = new MaterialColors();
		return materialColors.get(index);
	}

	private int get(int index) {
		return colors[index % colors.length];
	}

}
