package com.lovebus.function;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Okhttp {
    private final static int CONNECT_TIMEOUT =60;
    private final static int READ_TIMEOUT=100;
    private final static int WRITE_TIMEOUT=60;
    /**接受消息，参数是服务器地址*/
    public  static void getOkHttpRequest(String address, okhttp3.Callback callback){

        OkHttpClient client= new OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(WRITE_TIMEOUT,TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(CONNECT_TIMEOUT,TimeUnit.SECONDS)//设置连接超时时间
                .build();
        Request request= new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
    /*发送消息，参数是消息和服务器地址,json数据*/
    public static void postJson(String address, okhttp3.Callback callback, String jsonStr)
    {
        OkHttpClient client= new OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(WRITE_TIMEOUT,TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(CONNECT_TIMEOUT,TimeUnit.SECONDS)//设置连接超时时间
                .build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, jsonStr);
        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }
    /**发送消息，参数是消息和服务器地址*/
    public static void postOkHttpRequest(String address, RequestBody requestBody, okhttp3.Callback callback)
    {
        OkHttpClient client= new OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(WRITE_TIMEOUT,TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(CONNECT_TIMEOUT,TimeUnit.SECONDS)//设置连接超时时间
                .build();
        Request request= new Request.Builder().url(address).post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }
}
