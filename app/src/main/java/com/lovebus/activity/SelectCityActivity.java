package com.lovebus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.lljjcoder.style.citythreelist.ProvinceActivity;

import bus.android.com.lovebus.R;

public class SelectCityActivity extends AppCompatActivity {

    TextView location;
    TextView chooseCity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_picture);
        findView();
    }

    private void findView() {
        location = (TextView)findViewById(R.id.position_city);
        chooseCity = (TextView)findViewById(R.id.choose_city);
        chooseCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list();
            }
        });
    }

    private void list() {
        Intent intent = new Intent(SelectCityActivity.this, ProvinceActivity.class);
    }
}
