package com.yogesh.tracker;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter {

	private Context mContext;
	private List<Boy> mValue;
	LayoutInflater mInflater;
	public ListAdapter(Context context, List<Boy> value) {
		mContext = context;
		mValue = value;
		mInflater = LayoutInflater.from(mContext);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mValue.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mValue.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null)
			convertView = mInflater.inflate(R.layout.list_item, null);
		TextView tv = (TextView) convertView.findViewById(R.id.list_item_tv);
		tv.setText(mValue.get(position).name);
		convertView.setTag(mValue.get(position));
		
		return convertView;
	}

}
