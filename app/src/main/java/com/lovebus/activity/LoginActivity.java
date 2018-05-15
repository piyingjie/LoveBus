package com.lovebus.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lovebus.entity.User;
import com.lovebus.function.LoveBusUtil;
import com.lovebus.function.MyLog;
import com.lovebus.function.Okhttp;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.lovebus.function.SharedPreferences_tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import bus.android.com.lovebus.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String APP_ID="1106786595";//官方获取的APPID
    private Tencent mTencent;
    private BaseUiListener mIUiListener;
    private UserInfo mUserInfo;
    private String image_response;

     String nickname;
     String ret;

    private SharedPreferences login_sp;//获取登录状态
    private SharedPreferences.Editor editor;

    private ProgressDialog progDialog = null;// 进度框

    EditText account_edit = null, passwordEdit = null;//帐号和密码编辑框
    Button signInBtn = null;//登录按钮
    String account, password;//帐号和密码功能
    String status;
    String figureurl;
    Bitmap photo;
    User user=new User(false,null,null,null,null,null,null);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //传入参数APPID和全局Context上下文
        mTencent = Tencent.createInstance(APP_ID,LoginActivity.this.getApplicationContext());

        account_edit = (EditText)findViewById(R.id.account);
        passwordEdit = (EditText)findViewById(R.id.password);
        signInBtn = (Button)findViewById(R.id.signInBtn);

        login_sp = getSharedPreferences("first_login",MODE_PRIVATE);

        //登录按钮点击事件
        signInBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                account = account_edit.getText().toString();
                password = passwordEdit.getText().toString();
                if(account == null || account.length() <= 0){
                    Toast.makeText(LoginActivity.this,"请填写用户名",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password == null || password.length() <= 0){
                    Toast.makeText(LoginActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                logIn();
            }
        });
    }

    public void buttonLogin(View v){
        mIUiListener = new BaseUiListener();
        //all表示获取所有权限
        mTencent.login(LoginActivity.this,"all", mIUiListener);
    }

    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            Toast.makeText(LoginActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "response:" + response);
            JSONObject obj = (JSONObject) response;
            try {
                final String openID = obj.getString("openid");
                account = openID;
                String accessToken = obj.getString("access_token");
                String expires = obj.getString("expires_in");
                Log.e("yuan",expires);
                mTencent.setOpenId(openID);
                mTencent.setAccessToken(accessToken,expires);
                QQToken qqToken = mTencent.getQQToken();
                mUserInfo = new UserInfo(getApplicationContext(),qqToken);
                mUserInfo.getUserInfo(new IUiListener() {
                    @Override
                    public void onComplete(Object response) {
                        if(response == null){
                            return;
                        }else{
                            Log.e("meng","登录成功"+response.toString());
                            try {
                                JSONObject jo = (JSONObject)response;
                                ret = String.valueOf(jo.getInt("ret"));
                                Log.e("yuan","ret:"+ret);
                                nickname = jo.getString("nickname");
                                String city = jo.getString("city");
                                figureurl = jo.getString("figureurl_2");
                                qqlogIn();
                                //commit
                                //new NewAsynTask().execute(figureurl);



                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }

                    }

                    private void qqlogIn() {
                        Log.e("yuan","qqlogin");
                        Log.e("meng","ret:"+ret);
                        showProgressDialog();
                        RequestBody requestBody = new FormBody.Builder().add("account", openID).add("ret",ret).
                                add("nickname",nickname).build();
                        Okhttp.postOkHttpRequest("http://lovebus.top/lovebus/qqlogin.php", requestBody, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                /**这里写出错后的日志记录*/
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this, "请检查网络连接是否顺畅", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                /* 这个部分是子线程，和主线程通信很麻烦；另外里面不能直接进行UI操作，需要使用runOnUiThread（）*/
                                String data=response.body().string();
                                Log.e("meng","data:"+data);
                                MyLog.d("Login",data);
                                parseJSONWithJSONObject(data);
                                Log.e("meng","status:"+status);
                                if (status.equals("1")||status.equals("-2")){

                                    toast_qqlogin_1();
                                }
                                else {
                                    toast_login_2();
                                }
                            }
                        });

                    }

                    @Override
                    public void onError(UiError uiError) {
                        Log.e(TAG,"登录失败"+uiError.toString());
                    }

                    @Override
                    public void onCancel() {
                        Log.e(TAG,"登录取消");

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onError(UiError uiError) {
            Toast.makeText(LoginActivity.this, "授权失败", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onCancel() {
            Toast.makeText(LoginActivity.this, "授权取消", Toast.LENGTH_SHORT).show();

        }

    }

    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在登录:\n");
        progDialog.show();
    }

    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }
    private void toast_qqlogin_1() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("yuan","qq登录");
                Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                SharedPreferences_tools.save("User","info",LoginActivity.this,user);
                if(user.getHead_image()!=null){
                    editor = login_sp.edit();
                    editor.putBoolean("first_login",true);
                    editor.apply();
                    finish();
                }else{
                    new NewAsynTask().execute(figureurl);
                }


            }
        });
    }


    /**
     * 在调用Login的Activity或者Fragment中重写onActivityResult方法
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.REQUEST_LOGIN){
            Tencent.onActivityResultData(requestCode,resultCode,data,mIUiListener);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /* 登录函数 */
    public void logIn(){
        RequestBody requestBody = new FormBody.Builder().add("account", account).add("password",password).build();
        Okhttp.postOkHttpRequest("http://lovebus.top/lovebus/login.php", requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                /**这里写出错后的日志记录*/
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "请检查网络连接是否顺畅", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                /* 这个部分是子线程，和主线程通信很麻烦；另外里面不能直接进行UI操作，需要使用runOnUiThread（）*/
                String data=response.body().string();
                MyLog.d("Login",data);
                parseJSONWithJSONObject(data);
                if (status.equals("1")){
                    toast_login_1();
                }
                else {
                    toast_login_2();
                }
            }
        });
    }


    /* 密码正确 */
    private void toast_login_1(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                SharedPreferences_tools.save("User","info",LoginActivity.this,user);

                    editor = login_sp.edit();
                    editor.putBoolean("first_login",true);
                    editor.apply();
                    finish();


            }
        });
    }
    /* 密码错误 */
    private void toast_login_2(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this,"密码错误或用户名不存在",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* 忘记密码点击事件 */
    public void forgetPasswordText(View view){
        startActivity(new Intent(LoginActivity.this, PasswordFindActivity.class));
    }

    /* 注册点击事件 */
    public void registerText(View view){
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }
    /* json数据转换 */
    private void parseJSONWithJSONObject(String response) {
        /**这个部分是json的解析部分*/
        try {
            /**response可能需要接下来的一步改变编码*/
            if(response != null && response.startsWith("\ufeff"))
            {
                response =  response.substring(1);
            }
            JSONObject json_data = new JSONObject(response);
            status=json_data.getString("status");
            if(status.equals("1")||status.equals("-2")){
                user.setAccount(account);
                user.setPassword(password);
                user.setIs_login(true);
                user.setNickname(json_data.getString("nickname"));
                user.setPhone(json_data.getString("phonenumber"));
                user.setCity(json_data.getString("city"));
                user.setHead_image(json_data.getString("head").replaceAll("\\\\",""));
                MyLog.d("HEAD",user.getHead_image());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class NewAsynTask extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            return getBitmapByUrl(url);
        }

        @Override
        protected void onPostExecute(final Bitmap result) {
            super.onPostExecute(result);//result为获取的头像bimap
            /*
            * 头像的处理
            * */
            photo = result;
            LoveBusUtil.saveBitmap(photo);
            MediaType MEDIA_TYPE=MediaType.parse("image/*");
            File image=new File(Environment.getExternalStorageDirectory().getPath()+"/UserHeader.jpg");
            Log.e("meng","account:"+account);
            RequestBody req = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("account",account)
                    .addFormDataPart("head",image.getName(), RequestBody.create(MEDIA_TYPE, image)).build();
            Okhttp.postOkHttpRequest("http://lovebus.top/lovebus/uploadhead.php", req, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "请检查网络连接是否顺畅", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String data = response.body().string();
                    MyLog.d("IMAGE",data);
                    parseJSONWithJSONObject_head(data);
                    if (image_response.equals("true")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "头像上传成功", Toast.LENGTH_SHORT).show();
                                File image=new File(Environment.getExternalStorageDirectory().getPath()+"/UserHeader.jpg");
                                image.delete();
                                SharedPreferences_tools.saveImage(LoginActivity.this,photo,"UserHead","image");
                                user.setHead_image("http://lovebus.top/lovebus/head"+user.getAccount());
                                //commit
                                SharedPreferences_tools.save("User","info",LoginActivity.this,user);
                                dissmissProgressDialog();
                                photo.recycle();
                                photo=null;
                                finish();
                            }
                        });
                    } else if (image_response.equals("false")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "头像上传失败", Toast.LENGTH_SHORT).show();
                                File image=new File(Environment.getExternalStorageDirectory().getPath()+"/UserHeader.jpg");
                                image.delete();
                                photo.recycle();
                                photo=null;
                            }
                        });
                    }
                }
            });
        }
    }

    /*上传头像后数据解析*/
    private void parseJSONWithJSONObject_head(String response) {
        /**这个部分是json的解析部分*/
        try {
            /**response可能需要接下来的一步改变编码*/
            if(response != null && response.startsWith("\ufeff"))
            {
                response =  response.substring(1);
            }
            JSONObject json_data = new JSONObject(response);
            image_response=json_data.getString("status");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap getBitmapByUrl(String urlString) {
        Bitmap bitmap;
        InputStream is = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            is = new BufferedInputStream(connection.getInputStream());
            bitmap = BitmapFactory.decodeStream(is);
            connection.disconnect();
            return bitmap;
        }catch (MalformedURLException e){
            e.printStackTrace();;
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try{
                is.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return null;
    }
}
