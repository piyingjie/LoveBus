package com.lovebus.function;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import bus.android.com.lovebus.R;

public class InformAdapter extends BaseAdapter{


    private List<informBean> newsList;
    private View view;
    private Context mContext;
    private ViewHolder viewHolder;
    private int resourceId;

    public InformAdapter(Context mContext,List<informBean> obj){
        this.mContext = mContext;
        this.newsList = obj;
    }

    @Override
    public int getCount() {
        return newsList.size();
    }

    @Override
    public Object getItem(int position) {return newsList.get(position);}


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.news_item, null);

            viewHolder = new ViewHolder();
            viewHolder.Title = (TextView) view.findViewById(R.id.item1);
            viewHolder.Detail = (TextView)view.findViewById(R.id.item2);
            viewHolder.Url = (TextView)view.findViewById(R.id.item3);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.Title.setText(newsList.get(position).getTitle());
        viewHolder.Detail.setText(newsList.get(position).getDetail());
        viewHolder.Url.setText(newsList.get(position).getDetailUrl());

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /*
                Intent intent = new Intent(mContext.getApplicationContext(),
                        LoginActivity.class);
                mContext.startActivity(intent);
                */

                //String url ="http://www.wuhanbus.com"+newsList.get(position).getDetailUrl();

                String url =newsList.get(position).getDetailUrl();


                Uri uri= Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                mContext.startActivity(intent);



                //WebView webView.loadUrl(url);
            }
        });
        return view;

    }

    class ViewHolder{
        TextView Title;
        TextView Url;
        TextView Detail;
    }



}





