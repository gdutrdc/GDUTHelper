package com.rdc.gduthelper.ui.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rdc.gduthelper.R;

/**
 * Created by seasonyuu on 16/1/4.
 */
public class AddScheduleDialog extends DialogFragment implements View.OnClickListener {
	private View contentView;
	private Context mContext;

	private OnButtonClickListener onButtonClickListener;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater,
	                         @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		contentView = LayoutInflater.from(mContext)
				.inflate(R.layout.dialog_add_schedule, container, false);
		return contentView;
	}

	@Override
	public void onClick(View v) {

	}

	public interface OnButtonClickListener {
		void onEnsure(DialogInterface dialog);

		void onCancel(DialogInterface dialog);
	}


	public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
		this.onButtonClickListener = onButtonClickListener;
	}

}
