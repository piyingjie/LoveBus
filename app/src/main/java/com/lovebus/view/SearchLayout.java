package com.lovebus.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import bus.android.com.lovebus.R;


public class SearchLayout extends LinearLayout{
    public SearchLayout(Context context, AttributeSet attrs){
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.search_layout, this);
    }
}
