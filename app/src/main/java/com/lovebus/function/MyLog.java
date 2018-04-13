package com.lovebus.function;

import android.util.Log;

public class MyLog {
    public static final boolean DEBUG=true;
    public static void d(String tag,String text){
        if(DEBUG){
            Log.d(tag,text);
        }
    }
    public static void v(String tag,String text){
        if(DEBUG){
            Log.v(tag,text);
        }
    }
    public static void i(String tag,String text){
        if(DEBUG){
            Log.i(tag,text);
        }
    }
    public static void w(String tag,String text){
        if(DEBUG){
            Log.w(tag,text);
        }
    }
    public static void e(String tag,String text){
        if(DEBUG){
            Log.e(tag,text);
        }
    }
    public static void wtf(String tag,String text){
        if(DEBUG){
            Log.wtf(tag,text);
        }
    }
}
