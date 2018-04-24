package com.lovebus.function;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferences_tools {
    public static void save(String str,String key, Context context,Object object){
        SharedPreferences sharedPreferences;
        sharedPreferences=context.getSharedPreferences(str, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        ByteArrayOutputStream save = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(save);
            oos.writeObject(object);
            String base64Object = Base64.encodeToString(save.toByteArray(), Base64.DEFAULT);
            editor.putString(key,base64Object);
            editor.apply();
            oos.close();
            save.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void load(String str,String key, Context context,Object object){
        SharedPreferences sharedPreferences = context.getSharedPreferences(str, MODE_PRIVATE);
        String objectString = sharedPreferences.getString(key, "");
        byte[] base64Object = Base64.decode(objectString, Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(base64Object);
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            object= ois.readObject();
            bais.close();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
