package com.lovebus.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lovebus.activity.LoginActivity;
import com.lovebus.activity.Main_Activity;
import com.lovebus.function.LoveBusUtil;

import bus.android.com.lovebus.R;

public class ChooseLocationWidget extends LinearLayout implements View.OnClickListener {

    public ChooseLocationWidget(Context context) {
        super(context);
        init();
    }

    public ChooseLocationWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChooseLocationWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private String keyword = "";
    private void init(){
        setOrientation(LinearLayout.VERTICAL);
        setBackgroundResource(R.color.common_bg);

        LayoutInflater.from(getContext()).inflate(R.layout.widget_choose_location,this);
        ChooseLocationItemWidget mStartItem = (ChooseLocationItemWidget) findViewById(R.id.choose_start_item_widget);
        ChooseLocationItemWidget mDestItem = (ChooseLocationItemWidget) findViewById(R.id.choose_dest_item_widget);

        mStartItem.setOnClickListener(this);
        mDestItem.setOnClickListener(this);

        mStartItem.setType(ChooseLocationItemWidget.START_TYPE);
        mDestItem.setType(ChooseLocationItemWidget.DEST_TYPE);
    }



    @Override
    public void onClick(View v) {
       switch (v.getId()){
            case R.id.choose_start_item_widget:
              //  Log.d("CHOOSE", "onClick:起始点 ");
                break;
            case R.id.choose_dest_item_widget:
             //   Log.d("CHOOSE", "onClick: 目的地");
                break;
        }
    }


 /*   public void setStartLocation(String location) {
        mStartItem.setLocation(location);
    }
    public void setDestLocation(String location) {
        mDestItem.setLocation(location);
    }*/

    public static class ChooseLocationItemWidget extends RelativeLayout{

        public ChooseLocationItemWidget(Context context) {
            super(context);
            init();
        }

        public ChooseLocationItemWidget(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public ChooseLocationItemWidget(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }
        public static final int START_TYPE = 0;
        public static final int DEST_TYPE = 1;

        private ImageView mTypeIV;
        private AutoCompleteTextView mInputET;
        private View mDivider;

        private void init(){
            LayoutInflater.from(getContext()).inflate(R.layout.widget_choose_item_location,this);

            mTypeIV =  findViewById(R.id.type_icon);
            mInputET= findViewById(R.id.keyword5);
            mDivider = findViewById(R.id.divider);

        }
    /*    public void setLocation(String location ){
            if(location == null){
                location = "";
            }
            mInputET.setText(location);
        }*/
        @SuppressLint("ResourceType")
        public void setType(int type) {
            if (START_TYPE == type) {
                mTypeIV.setImageResource(R.mipmap.source);
                mInputET.setHint("你从哪里出发");
                mInputET.setId(R.id.keyword5);
                mDivider.setVisibility(View.VISIBLE);
            } else {
                mTypeIV.setImageResource(R.mipmap.dest_icon);
                mInputET.setHint("你要去哪儿");
                mInputET.setId(R.id.keyword6);
                mDivider.setVisibility(View.GONE);

            }
        }
    }


}
