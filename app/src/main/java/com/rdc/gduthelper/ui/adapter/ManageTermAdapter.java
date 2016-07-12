package com.rdc.gduthelper.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapterHelper;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.rdc.gduthelper.R;
import com.rdc.gduthelper.bean.TermLessons;
import com.rdc.gduthelper.bean.YearSchedule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seasonyuu on 16-7-8.
 */

public class ManageTermAdapter extends ExpandableRecyclerAdapter
		<ManageTermAdapter.TitleViewHolder, ManageTermAdapter.ContentViewHolder> {
	private Context context;
	private boolean inActionMode = false;

	private boolean[][] selectedPositions;
	private boolean[][] selectedPositionsAM;

	private List<YearSchedule> termData;

	public ManageTermAdapter(Context context, @NonNull List<YearSchedule> termData, View emptyView) {
		super(termData);
		for (YearSchedule yearSchedule : termData) {
			yearSchedule.initPosition(termData.indexOf(yearSchedule));
		}
		this.termData = termData;
		this.context = context;

		if (termData.size() == 0) {
			selectedPositions = null;
			selectedPositions = null;
		} else {
			selectedPositions = new boolean[termData.size()][10];
			selectedPositionsAM = new boolean[termData.size()][10];
		}

		if (termData.size() == 0) {
			emptyView.setVisibility(View.VISIBLE);
		} else {
			emptyView.setVisibility(View.GONE);
		}
	}

	@Override
	public TitleViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
		TitleViewHolder holder = new TitleViewHolder(LayoutInflater.from(context)
				.inflate(R.layout.item_manage_term_title, parentViewGroup, false));
		return holder;
	}

	@Override
	public ContentViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
		ContentViewHolder holder = new ContentViewHolder(LayoutInflater.from(context)
				.inflate(R.layout.item_manage_term_content, childViewGroup, false));
		return holder;
	}

	@Override
	public void onBindParentViewHolder(TitleViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
		YearSchedule schedule = (YearSchedule) parentListItem;
		parentViewHolder.title.setText(schedule.getTitle());
	}

	@Override
	public void onBindChildViewHolder(ContentViewHolder childViewHolder, int position, Object childListItem) {
		TermLessons termLessons = (TermLessons) childListItem;

		childViewHolder.childPosition = termLessons.getChildPosition();
		childViewHolder.groupPosition = termLessons.getParentPosition();

		if (inActionMode)
			if (selectedPositionsAM != null
					&& selectedPositionsAM[childViewHolder.groupPosition][childViewHolder.childPosition]) {
				childViewHolder.title.setBackgroundResource(R.color.purple_500);
			} else
				childViewHolder.title.setBackgroundResource(R.color.blue_grey_500);
		else {
			if (selectedPositions != null
					&& selectedPositions[childViewHolder.groupPosition][childViewHolder.childPosition]) {
				childViewHolder.title.setBackgroundResource(R.color.pink_a200);
			} else
				childViewHolder.title.setBackgroundResource(R.color.blue_grey_500);
		}


		childViewHolder.setTitle(termLessons.getTerm(),
				selectedPositions[childViewHolder.groupPosition][childViewHolder.childPosition]);
		childViewHolder.setLessonCount(termLessons.getLessons().size());

	}

	public interface OnChildClickListener {
		void onChildClick(View v, int groupPosition, int childPosition);
	}

	public interface OnChildLongClickListener {
		boolean onChildLongClick(View v, int groupPosition, int childPosition);
	}

	private OnChildClickListener onChildClickListener;
	private OnChildLongClickListener onChildLongClickListener;

	public OnChildLongClickListener getOnChildLongClickListener() {
		return onChildLongClickListener;
	}

	public void setOnChildLongClickListener(OnChildLongClickListener onChildLongClickListener) {
		this.onChildLongClickListener = onChildLongClickListener;
	}


	public boolean isInActionMode() {
		return inActionMode;
	}

	public void setInActionMode(boolean inActionMode) {
		this.inActionMode = inActionMode;
		if (selectedPositionsAM != null)
			for (boolean[] group : selectedPositionsAM) {
				for (int i = 0; i < group.length; i++)
					group[i] = false;
			}
		notifyDataSetChanged();
	}

	public String getTerm(int groupPosition, int childPosition) {
		return termData.get(groupPosition).getChildItemList().get(childPosition).getTerm();
	}

	public List<YearSchedule> getTermData() {
		return termData;
	}

	public void setTermData(List<YearSchedule> termData) {
		this.termData = termData;
		if (termData == null || termData.size() == 0) {
			selectedPositions = null;
			selectedPositions = null;
		} else {
			selectedPositions = new boolean[termData.size()][10];
			selectedPositionsAM = new boolean[termData.size()][10];
		}
	}

	public int getSelectedNum() {
		int result = 0;
		if (inActionMode) {
			for (boolean[] ba : selectedPositionsAM) {
				for (boolean b : ba)
					if (b) result++;
			}
		} else {
			result++;
		}
		return result;
	}

	public void setSelectedPosition(int groupPosition, int childPosition, boolean selected) {
		if (selectedPositionsAM == null || selectedPositions == null)
			return;
		if (inActionMode)
			selectedPositionsAM[groupPosition][childPosition] = selected;
		else {
			for (boolean[] group : selectedPositions) {
				for (int i = 0; i < group.length; i++)
					group[i] = false;
			}
			selectedPositions[groupPosition][childPosition] = selected;
		}
		notifyDataSetChanged();
	}

	public boolean getSelectedPosition(int groupPosition, int childPosition) {
		if (selectedPositionsAM == null || selectedPositions == null)
			return false;
		if (inActionMode)
			return selectedPositionsAM[groupPosition][childPosition];
		return selectedPositions[groupPosition][childPosition];
	}

	public OnChildClickListener getOnChildClickListener() {
		return onChildClickListener;
	}

	public void setOnChildClickListener(OnChildClickListener onChildClickListener) {
		this.onChildClickListener = onChildClickListener;
	}

	class TitleViewHolder extends ParentViewHolder {
		TextView title;

		public TitleViewHolder(View itemView) {
			super(itemView);
			title = (TextView) itemView.findViewById(R.id.tv_title_manage_term);
			expandView();
		}
	}

	class ContentViewHolder extends ChildViewHolder implements View.OnClickListener, View.OnLongClickListener {
		TextView title;
		TextView lessonCount;
		int groupPosition;
		int childPosition;

		public ContentViewHolder(View itemView) {
			super(itemView);
			title = (TextView) itemView.findViewById(R.id.tv_content_title_manage_term);
			lessonCount = (TextView) itemView.findViewById(R.id.tv_content_lesson_count_manage_term);

			itemView.findViewById(R.id.cv_container_manage_term).setOnClickListener(this);
			itemView.findViewById(R.id.cv_container_manage_term).setOnLongClickListener(this);
		}

		public void setTitle(String term, boolean current) {
			this.title.setText("第" + term.substring(term.length() - 1)
					+ "学期" + (current ? "(当前)" : ""));
		}

		public void setLessonCount(int lessonCount) {
			this.lessonCount.setText("本学期共" + lessonCount + "门课");
		}

		@Override
		public void onClick(View v) {
			if (onChildClickListener != null) {
				onChildClickListener.onChildClick(v, groupPosition, childPosition);
			}
		}

		@Override
		public boolean onLongClick(View v) {
			return onChildLongClickListener != null
					&& onChildLongClickListener.onChildLongClick(v, groupPosition, childPosition);
		}
	}

}
