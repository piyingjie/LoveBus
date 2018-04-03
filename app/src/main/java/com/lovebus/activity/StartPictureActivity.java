package com.lovebus.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

import bus.android.com.lovebus.R;

public class StartPictureActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private boolean first_start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_picture);
        /*通过sharedpreferences保存第一次的消息*/
        sharedPreferences = getSharedPreferences("setting", MODE_PRIVATE);
        if(sharedPreferences.getBoolean("first_start",true)==true)
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("first_start", false);
            editor.commit();
            first_start=true;
        }
        else {
            first_start=false;
        }
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if(first_start==false) {
                    startActivity(new Intent(StartPictureActivity.this, Main_Activity.class));
                    finish();//关闭启动界面
                }
                else {
                    startActivity(new Intent(StartPictureActivity.this, FirstLoginActivity.class));
                    finish();//关闭启动界面
                }
            }
        };
        timer.schedule(timerTask, 2000);
    }

}
