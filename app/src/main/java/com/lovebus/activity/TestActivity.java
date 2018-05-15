package com.lovebus.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amap.api.location.AMapLocation;
import com.lovebus.function.Locate;
import com.lovebus.function.MyLog;

import bus.android.com.lovebus.R;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Locate.init(TestActivity.this);
        Locate.getCurrentLocation(new Locate.MyLocationListener() {
            @Override
            public void result(AMapLocation location) {
                MyLog.d("FUCKWHR",location.getAddress());
            }
        });
    }
}
