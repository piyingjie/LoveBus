package com.lovebus.function;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusStationItem;


import java.util.List;

import bus.android.com.lovebus.R;

public class BusLineAdapter extends BaseAdapter {
	private List<BusLineItem> busLineItems;
	private LayoutInflater layoutInflater;
	private List<BusStationItem> mBusStations;

	public BusLineAdapter(Context context, List<BusLineItem> busLineItems) {
		this.busLineItems = busLineItems;
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return busLineItems.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.busline_item, null);
			holder = new ViewHolder();
			holder.busName = (TextView) convertView.findViewById(R.id.busname);
			holder.busId = (TextView) convertView.findViewById(R.id.busid);
			holder.busTest=(TextView)convertView.findViewById(R.id.bustest);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		mBusStations=busLineItems.get(position).getBusStations();
		holder.busName.setText("公交名 :"
				+ busLineItems.get(position).getBusLineName());
		holder.busId.setText("公交ID:"
				+ busLineItems.get(position).getBusLineId());
		holder.busTest.setText("经过站点："+mBusStations.get(mBusStations.size()-1).getBusStationName());
		return convertView;

	}

	class ViewHolder {
		public TextView busName;
		public TextView busId;
		public TextView busTest;
	}

}
