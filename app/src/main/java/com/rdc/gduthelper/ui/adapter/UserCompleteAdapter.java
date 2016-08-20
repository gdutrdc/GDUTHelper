package com.rdc.gduthelper.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.rdc.gduthelper.R;
import com.rdc.gduthelper.bean.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seasonyuu on 16-8-20.
 */

public class UserCompleteAdapter extends BaseAdapter implements Filterable {
	private List<User> users;
	private List<User> displayUsers;
	private Context mContext;
	private Filter mFilter = new Filter() {
		@Override
		protected FilterResults performFiltering(CharSequence charSequence) {
			ArrayList<User> result = new ArrayList<>();
			for (User user : users) {
				if (user.getXh().contains(charSequence))
					result.add(user);
			}

			FilterResults filterResults = new FilterResults();
			filterResults.values = result;
			filterResults.count = result.size();
			return filterResults;
		}

		@Override
		protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
			if (filterResults != null && filterResults.count > 0) {
				// 有过滤结果，显示自动完成列表
				UserCompleteAdapter.this.displayUsers = ((List<User>) filterResults.values);
				notifyDataSetChanged();
			} else {
				// 无过滤结果，关闭列表
				notifyDataSetInvalidated();
			}
		}
	};

	public UserCompleteAdapter(Context context) {
		mContext = context;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
		this.displayUsers = users;
	}

	@Override
	public int getCount() {
		return displayUsers == null ? 0 : displayUsers.size();
	}

	@Override
	public Object getItem(int i) {
		return displayUsers.get(i).getXh();
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View convertView, ViewGroup viewGroup) {
		View view = LayoutInflater.from(mContext)
				.inflate( android.R.layout.simple_dropdown_item_1line, viewGroup, false);
		((TextView) view.findViewById(android.R.id.text1)).setText(displayUsers.get(i).getXh());
		return view;
	}

	@Override
	public Filter getFilter() {
		return mFilter;
	}
}
