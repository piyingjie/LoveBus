package com.lovebus.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.LinearLayout;


import com.lljjcoder.style.citylist.CityListSelectActivity;

import java.util.Timer;
import java.util.TimerTask;

import bus.android.com.lovebus.R;

public class StartPictureActivity extends AppCompatActivity implements View.OnClickListener {
    private SharedPreferences sharedPreferences;
    private boolean first_start;

    LinearLayout skip;
    //LinearLayout changeCity;


    private Timer timer;
    Button next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_picture);
        skip= this.<LinearLayout>findViewById(R.id.layout_skip);
        skip.setOnClickListener(this);
        /*通过sharedpreferences保存第一次的消息*/

        sharedPreferences = getSharedPreferences("setting", MODE_PRIVATE);

        if(sharedPreferences.getBoolean("first_start",true))
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("first_start", false);
            editor.apply();
            first_start=true;
        }
        else {
            first_start=false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    0);//自定义的code
        }
        else {
            timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    jump();
                }
            };
            timer.schedule(timerTask, 2000);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //可在此继续其他操作。
            timer = new Timer();
            TimerTask timerTask = new TimerTask() {
             @Override
              public void run() {
                   jump();
              }
            };
            timer.schedule(timerTask, 2000);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_skip:
                timer.cancel();
                jump();
                break;
            default:
        }
    }
    private void jump(){
        if(!first_start) {
            startActivity(new Intent(StartPictureActivity.this, Main_Activity.class));
            finish();//关闭启动界面
        }
        else {
            startActivity(new Intent(StartPictureActivity.this, CitySelectAcitivity.class));
            finish();//关闭启动界面
        }
    }
}
