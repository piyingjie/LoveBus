package com.lovebus.activity;


import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.lovebus.function.InformAdapter;
import com.lovebus.function.informBean;

import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import bus.android.com.lovebus.R;

public class informActivity extends AppCompatActivity {


    private ListView listView;
    private Handler handler;
    private List<informBean> mdata ;
    public int i = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inform);
        mdata = new ArrayList<>();
        listView = (ListView) findViewById(R.id.newslist);
        myThread();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1){
                    InformAdapter newAdapter = new InformAdapter(informActivity.this,mdata);
                    listView.setAdapter(newAdapter);
                }
            }
        };

    }

    public void myThread(){
        new Thread(){
            @Override
            public void run() {
                try{
                    //创建一个Document对象去链接目标网站获取
                    Document doc = Jsoup.connect("http://www.wuhanbus.com/html/class/xlxx/index.html").get();
                    //创建一个Elements对象获取内容
                    Elements els = doc.select("div.js-cont");

                    Log.d("TAG", els.toString());
                    //遍历Elements对象的内容
                    for (int j = 0 ;j<els.size();j++) {
                        String title = els.get(j).select("a").text();
                        String detailUrl = els.get(j).select("a").attr("href");
                        String detail = els.get(j).select("div").text();
                        Log.e("TAG","标题：" + els.get(j).select("a").text());
                        Log.e("TAG", "地址：" + els.get(j).select("a").attr("href"));
                        Log.e("TAG","详情:"+els.get(j).select("div").text());
                        detail = detail.replace(" ","");
                        //String regex = "^([^\\>]*)\\>.*$";
                        String regex = "(?<=\\>).*";
                        detail = detail.replaceAll(regex,"");
                        //detail = detail.substring(0,detail.length()*3/8);
                        //detail = detail.replace("阅读原文>>","");
                        title = title.replace("阅读原文>>","");
                        detailUrl = "http://www.wuhanbus.com"+ detailUrl;
                        informBean news = new informBean(detailUrl,title,detail);
                        mdata.add(news);
                    }
                    Message msg = new Message();//创建一个Message对象
                    msg.what = 1;
                    handler.sendMessage(msg);//通过Handler切换主线程
                }catch (Exception e){
                    Log.d("TAG",e.toString());
                }
            }
        }.start();
    }



}



