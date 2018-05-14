package com.lovebus.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import bus.android.com.lovebus.R;

public class poi_message_view extends RelativeLayout{
    public poi_message_view(Context context, AttributeSet attrs){
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.poi_click_message, this);
    }
}
