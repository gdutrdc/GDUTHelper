package com.rdc.gduthelper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.rdc.gduthelper.R;

/**
 * Created by seasonyuu on 16-5-15.
 */
public class HelpActivity extends BaseActivity implements AdapterView.OnItemClickListener {
	private RecyclerView mRvHelpList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);

		mRvHelpList = (RecyclerView) findViewById(R.id.help_list);
		if (mRvHelpList != null) {
//			mRvHelpList.setLayoutManager(new LinearLayoutManager(this));
			HelpAdapter adapter = new HelpAdapter();
			mRvHelpList.setAdapter(adapter);
			adapter.setOnItemClickListener(this);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// parent 为空，需要调用ViewGroup则直接调用view.getParent()
		switch (position) {
			case 0:
				break;
			case 1:
				Intent licenses = new Intent(this,WebViewActivity.class);
				licenses.putExtra("title",getResources().getString(R.string.licenses));
				licenses.putExtra("url","file:///android_asset/licenses.html");
				startActivity(licenses);
				break;
			case 2:
				break;
			case 3:
				break;
		}
	}

	private class HelpAdapter extends RecyclerView.Adapter<HelpAdapter.HelpViewHolder> {
		private AdapterView.OnItemClickListener onItemClickListener;
		private String[] helps = getResources().getStringArray(R.array.help_list);

		@Override
		public HelpViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			HelpViewHolder holder = new HelpViewHolder(
					LayoutInflater.from(HelpActivity.this)
							.inflate(R.layout.item_help, parent, false));
			return holder;
		}

		@Override
		public void onBindViewHolder(HelpViewHolder holder, int position) {
			holder.setText(helps[position]);
			holder.setPosition(position);
		}

		@Override
		public int getItemCount() {
			return helps.length;
		}

		public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
			this.onItemClickListener = onItemClickListener;
		}

		class HelpViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
			private int position = 0;
			private View itemView;

			public HelpViewHolder(View itemView) {
				super(itemView);
				this.itemView = itemView;
				this.itemView.setOnClickListener(this);
			}

			public void setPosition(int position) {
				this.position = position;
			}

			public void setText(String text) {
				((TextView) itemView.findViewById(R.id.text1)).setText(text);
			}

			@Override
			public void onClick(View v) {
				onItemClickListener.onItemClick(null, v, position, getItemId());
			}
		}
	}


}
