package com.lovebus.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import bus.android.com.lovebus.R;

public class top_title extends RelativeLayout{
    public top_title(Context context, AttributeSet attrs){
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.top_title, this);
    }
}
