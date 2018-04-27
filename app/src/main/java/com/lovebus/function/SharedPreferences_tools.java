package com.lovebus.function;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import com.lovebus.entity.User;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferences_tools {
    public static void save(String str, String key, Context context, Object object){
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
    public static Object load(String str,String key, Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(str, MODE_PRIVATE);
        Object object=new Object();
        String objectString = sharedPreferences.getString(key, "");
        if(objectString.equals("")){
            return null;
        }
        byte[] base64Object = Base64.decode(objectString, Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(base64Object);
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            object=ois.readObject();
            /*MyLog.d("SHAT",user.getAccount());*/
            bais.close();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    public static void saveImage(Context context, Bitmap bitmap,String preferenceName,String key) {
        SharedPreferences sharedPreferences=context.getSharedPreferences(preferenceName,MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        if(bitmap==null){
            MyLog.d("HEAD","bitmap为空");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, baos);
        String imageBase64 = new String(Base64.encodeToString(baos.toByteArray(),Base64.DEFAULT));
        editor.putString(key,imageBase64 );
        editor.apply();
    }
    public static Bitmap getImage(Context context, String preferenceName, String key) {
        SharedPreferences sharedPreferences=context.getSharedPreferences(preferenceName,MODE_PRIVATE);
        String imageString=sharedPreferences.getString(key, "");
        if(imageString.equals("")){
            return null;
        }
        byte[] byteArray=Base64.decode(imageString, Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(byteArray);
        return BitmapFactory.decodeStream(byteArrayInputStream);
    }
    public static void clear(Context context,String preferenceName) {
        SharedPreferences preferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}
