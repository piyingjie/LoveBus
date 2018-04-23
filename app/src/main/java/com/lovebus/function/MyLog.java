package com.lovebus.function;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
public class MyLog {
    public static final boolean DEBUG=true;
    public static final boolean TOAST=true;
    public static void d(String tag,String text){
        if(DEBUG){
            if(!text.equals(null))
            {
                Log.d(tag,text);
            }
            else {
                Log.d(tag,"字符串为空");
            }
        }
    }
    public static void v(String tag,String text){
        if(DEBUG){
            if(!text.equals(null))
            {
                Log.v(tag,text);
            }
            else {
                Log.v(tag,"字符串为空");
            }
        }
    }
    public static void i(String tag,String text){
        if(DEBUG){
            if(!text.equals(null))
            {
                Log.i(tag,text);
            }
            else {
                Log.i(tag,"字符串为空");
            }
        }
    }
    public static void w(String tag,String text){
        if(DEBUG){
            if(!text.equals(null))
            {
                Log.w(tag,text);
            }
            else {
                Log.w(tag,"字符串为空");
            }
        }
    }
    public static void e(String tag,String text){
        if(DEBUG){
            if(!text.equals(null))
            {
                Log.e(tag,text);
            }
            else {
                Log.e(tag,"字符串为空");
            }
        }
    }
    public static void wtf(String tag,String text){
        if(DEBUG){
            if(!text.equals(null))
            {
                Log.wtf(tag,text);
            }
            else {
                Log.wtf(tag,"字符串为空");
            }
        }
    }
    public static void  Toast(Context context,String str){
        if(TOAST){
             Toast.makeText(context,str,Toast.LENGTH_SHORT).show();
        }
    }
    public static void  Toast_re(Context context,String str){
        Toast.makeText(context,str,Toast.LENGTH_SHORT).show();
    }
}
