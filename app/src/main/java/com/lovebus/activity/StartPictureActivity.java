package com.lovebus.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import bus.android.com.lovebus.R;

public class StartPictureActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private SharedPreferences sharedPreferences;
    private boolean first_start;

    private ViewPager vp;
    private int []imageIdArray;//图片资源的数组
    private List<View> viewList;//图片资源的集合
    private ViewGroup vg;//放置圆点
    //实例化原点View
    private ImageView iv_point;
    private ImageView []ivPointArray;
    //最后一页的按钮
    private ImageButton ib_start;


    LinearLayout skip;
    //LinearLayout changeCity;


    private Timer timer;
    Button next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_start_picture);
        ib_start = (ImageButton) findViewById(R.id.guide_ib_start);
        ib_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(GuideActivity.this,MainActivity.class));
                jump();
            }
        });
        //加载ViewPager
        initViewPager();

        //加载底部圆点
        initPoint();
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
          //  timer.schedule(timerTask, 2000);
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
            //不是第一次登录
            startActivity(new Intent(StartPictureActivity.this, Main_Activity.class));
            finish();//关闭启动界面
        }
        else {
            //第一次登录
            startActivity(new Intent(StartPictureActivity.this, CitySelectActivity.class));
            finish();//关闭启动界面
        }
    }
    /**
     * 加载底部圆点
     */
    private void initPoint() {

        //这里实例化LinearLayout

        vg = (ViewGroup) findViewById(R.id.guide_ll_point);

        //根据ViewPager的item数量实例化数组

        ivPointArray = new ImageView[viewList.size()];

        //循环新建底部圆点ImageView，将生成的ImageView保存到数组中

        int size = viewList.size();
        for (int i = 0;i<size;i++){
            iv_point = new ImageView(this);
            iv_point.setLayoutParams(new ViewGroup.LayoutParams(20,20));
            iv_point.setPadding(30,0,30,0);//left,top,right,bottom
            ivPointArray[i] = iv_point;
            //第一个页面需要设置为选中状态，这里采用两张不同的图片

            if (i == 0){

                iv_point.setBackgroundResource(R.mipmap.page_indicator_focused);

            }else{
                iv_point.setBackgroundResource(R.mipmap.page_indicator_unfocused);
            }
            //将数组中的ImageView加入到ViewGroup
            vg.addView(ivPointArray[i]);

        }
    }
    /**
     * 加载图片ViewPager
     */
    private void initViewPager() {
        vp = (ViewPager) findViewById(R.id.guide_vp);
        //实例化图片资源
        imageIdArray = new int[]{R.mipmap.bus0,R.mipmap.busx,R.mipmap.bus2,R.mipmap.bus3};
        viewList = new ArrayList<>();
        //获取一个Layout参数，设置为全屏
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        //循环创建View并加入到集合中
        int len = imageIdArray.length;
        for (int i = 0;i<len;i++){
            //new ImageView并设置全屏和图片资源
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(params);
            imageView.setBackgroundResource(imageIdArray[i]);
            //将ImageView加入到集合中
            viewList.add(imageView);
        }
        //View集合初始化好后，设置Adapter
        vp.setAdapter(new GuidePageAdapter(viewList));
        //设置滑动监听
        vp.setOnPageChangeListener(this);
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    /**
     * 滑动后的监听
     * @param position
     */
    @Override
    public void onPageSelected(int position) {
        //循环设置当前页的标记图
        int length = imageIdArray.length;
        for (int i = 0;i<length;i++){
            ivPointArray[position].setBackgroundResource(R.mipmap.page_indicator_focused);
            if (position != i){
                ivPointArray[i].setBackgroundResource(R.mipmap.page_indicator_unfocused);
            }
        }
        //判断是否是最后一页，若是则显示按钮
        if (position == imageIdArray.length - 1){
            ib_start.setVisibility(View.VISIBLE);
        }else {
            ib_start.setVisibility(View.GONE);
        }
    }
    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
