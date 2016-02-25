package com.rdc.gduthelper.ui.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rdc.gduthelper.R;


/**
 * Created by seasonyuu on 15/7/23.
 */
public class ChooseColorsDialog extends AlertDialog implements DialogInterface.OnClickListener {
	private View contentView;
	private RecyclerView recyclerView;
	private ThemeAdapter adapter;
	private Context context;

	private OnClickListener onClickListener = null;

	private String[] colorNames;
	private int[] colors;

	private boolean choosedColors[] = new boolean[20];

	public ChooseColorsDialog(Context context) {
		super(context);
		this.context = context;

		contentView = View.inflate(context, R.layout.dialog_choose_card_colors, null);
		recyclerView = (RecyclerView) contentView.findViewById(R.id.choose_colors);
		GridLayoutManager layoutManager = new GridLayoutManager(context,5);
		layoutManager.setAutoMeasureEnabled(false);
		recyclerView.setLayoutManager(layoutManager);
		adapter = new ThemeAdapter();
		recyclerView.setAdapter(adapter);

		colorNames = context.getResources().getStringArray(R.array.color_names);
		colors = context.getResources().getIntArray(R.array.colors);

		choosedColors = new boolean[colorNames.length];

		setView(contentView);

		setButton(BUTTON_POSITIVE, context.getString(R.string.ensure), this);
		setButton(BUTTON_NEGATIVE, context.getString(R.string.cancel), this);
	}

	public void setChoosedColors(boolean[] choosedColors) {
		this.choosedColors = choosedColors;
		adapter.notifyDataSetChanged();
	}

	public boolean[] getChoosedColors() {
		return choosedColors;
	}

	public void setOnClickListener(DialogInterface.OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (onClickListener != null)
			onClickListener.onClick(dialog, which);
	}

	private class ThemeAdapter extends RecyclerView.Adapter<ColorViewHolder> {

		@Override
		public ColorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			ColorViewHolder holder = new ColorViewHolder(LayoutInflater.from(context).inflate(R.layout.item_choose_colors, parent, false));
			return holder;
		}

		@Override
		public void onBindViewHolder(ColorViewHolder holder, int position) {
			holder.ivTheme.setBackgroundColor(colors[position]);
			holder.ivTheme.invalidate();
			holder.tvThemeName.setText(colorNames[position]);
			holder.position = position;

			if (choosedColors[position])
				holder.ivMark.setVisibility(View.VISIBLE);
			else
				holder.ivMark.setVisibility(View.GONE);
		}

		@Override
		public int getItemCount() {
			return colorNames.length;
		}
	}

	private class ColorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		CircleView ivTheme;
		TextView tvThemeName;
		ImageView ivMark;
		int position;

		public ColorViewHolder(View itemView) {
			super(itemView);
			ivTheme = (CircleView) itemView.findViewById(R.id.item_color);
			tvThemeName = (TextView) itemView.findViewById(R.id.item_color_name);
			ivMark = (ImageView) itemView.findViewById(R.id.item_color_mark);
			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			choosedColors[position] = !choosedColors[position];
			int count = 0;
			for (boolean flag : choosedColors)
				if (flag) {
					count++;
				}
			if (count == 0) {
				Toast.makeText(getContext(), R.string.schedule_card_colors_tips, Toast.LENGTH_SHORT)
						.show();
			} else
				adapter.notifyDataSetChanged();
		}
	}
}
