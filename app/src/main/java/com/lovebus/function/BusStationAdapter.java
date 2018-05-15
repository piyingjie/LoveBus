package com.lovebus.function;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.services.busline.BusStationItem;

import java.util.List;

import bus.android.com.lovebus.R;

public class BusStationAdapter extends BaseAdapter{
    private List<BusStationItem> busStationItems;
    private LayoutInflater layoutInflater;

    public BusStationAdapter(Context context,List<BusStationItem> busStationItems){
        this.busStationItems = busStationItems;
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return busStationItems.size();
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
        if (convertView == null){
            convertView = layoutInflater.inflate(R.layout.busline_detail_layout,null);
            holder =new ViewHolder();
            holder.stationName = (TextView)convertView.findViewById(R.id.stationName);
            holder.stationId=(TextView)convertView.findViewById(R.id.station_id);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.stationName.setText("站点名："+busStationItems.get(position).getBusStationName());
        holder.stationId.setText("站点经纬度："+busStationItems.get(position).getLatLonPoint());
        return convertView;
    }
    class ViewHolder {
        public TextView stationName;
        public TextView stationId;
    }
}
