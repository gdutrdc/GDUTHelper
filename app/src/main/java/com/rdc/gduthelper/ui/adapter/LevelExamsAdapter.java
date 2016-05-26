package com.rdc.gduthelper.ui.adapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.rdc.gduthelper.BR;
import com.rdc.gduthelper.R;
import com.rdc.gduthelper.bean.LevelExam;
import com.rdc.gduthelper.ui.LevelExamActivity;
import com.rdc.gduthelper.utils.UIUtils;

import java.util.ArrayList;

/**
 * Created by seasonyuu on 16-5-27.
 */

public class LevelExamsAdapter extends RecyclerView.Adapter<LevelExamsAdapter.LevelExamHolder> {
	private Context mContext;

	private ArrayList<LevelExam> mLevelExams = new ArrayList<>();

	public LevelExamsAdapter(Context context) {
		mContext = context;
	}

	public ArrayList<LevelExam> getLevelExams() {
		return mLevelExams;
	}

	public void setLevelExams(ArrayList<LevelExam> levelExams) {
		this.mLevelExams = levelExams;
	}

	@Override
	public LevelExamHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new LevelExamHolder(LayoutInflater.from(mContext)
				.inflate(R.layout.item_level_exam, parent, false));
	}

	@Override
	public int getItemCount() {
		return mLevelExams.size();
	}

	@Override
	public void onBindViewHolder(LevelExamHolder holder, int position) {

		holder.binding.setVariable(BR.levelExam, mLevelExams.get(position));
		holder.binding.executePendingBindings();
	}

	class LevelExamHolder extends RecyclerView.ViewHolder implements View.OnClickListener, Animator.AnimatorListener {
		private ViewDataBinding binding;
		private View itemView;
		private int unexpandedHeight;
		private ImageView ivArrow;

		LevelExamHolder(View itemView) {
			super(itemView);
			this.itemView = itemView;
			binding = DataBindingUtil.bind(itemView);

			ivArrow = (ImageView) itemView.findViewById(R.id.level_exam_grade_expand);
			ivArrow.setOnClickListener(this);

			if (Build.VERSION.SDK_INT >= 21) {
				ivArrow.setImageResource(R.drawable.animate_expand);
			}
		}

		@Override
		public void onClick(View v) {
			if (unexpandedHeight == 0)
				unexpandedHeight = itemView.getHeight();

			boolean flag = itemView.getHeight() > unexpandedHeight;
			if (Build.VERSION.SDK_INT >= 21) {
				ImageView iv = (ImageView) v;
				ivArrow.setImageResource(flag ? R.drawable.animate_collapse : R.drawable.animate_expand);
				Drawable drawable = iv.getDrawable();
				if (drawable instanceof Animatable) {
					((Animatable) drawable).start();
				}
			} else {
				ivArrow.setImageResource(flag ? R.drawable.ic_keyboard_arrow_down_black_24dp
						: R.drawable.ic_keyboard_arrow_up_black_24dp);
			}

			PropertyValuesHolder holder = PropertyValuesHolder.ofInt("minimumHeight",
					itemView.getHeight(), itemView.getHeight()
							+ (flag ? -1 : 1) * (int) UIUtils.convertDpToPixel(56, mContext));
			ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(itemView, holder);
			animator.addListener(this);
			animator.setDuration(200);
			animator.setInterpolator(new AccelerateDecelerateInterpolator());
			animator.start();

		}

		@Override
		public void onAnimationStart(Animator animation) {
			if (itemView.getHeight() <= unexpandedHeight) {
				itemView.findViewById(R.id.level_exam_bottom_shadow).setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onAnimationEnd(Animator animation) {
			if (Math.abs(itemView.getHeight() - unexpandedHeight)
					<= UIUtils.convertDpToPixel(28, mContext)) {
				itemView.findViewById(R.id.level_exam_bottom_shadow).setVisibility(View.GONE);
			}
		}

		@Override
		public void onAnimationCancel(Animator animation) {

		}

		@Override
		public void onAnimationRepeat(Animator animation) {

		}
	}
}