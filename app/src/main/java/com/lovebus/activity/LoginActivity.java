package com.lovebus.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lovebus.function.MyLog;
import com.lovebus.function.Okhttp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import bus.android.com.lovebus.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    EditText account_edit = null, passwordEdit = null;//帐号和密码编辑框
    Button signInBtn = null;//登录按钮
    String account, password;//帐号和密码功能
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        account_edit = (EditText)findViewById(R.id.account);
        passwordEdit = (EditText)findViewById(R.id.password);
        signInBtn = (Button)findViewById(R.id.signInBtn);

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

    /* 登录函数 */
    public void logIn(){
        RequestBody requestBody = new FormBody.Builder().add("account", account).add("password",password).build();
        Okhttp.postOkHttpRequest("http://lovebus.top/demo/login.php", requestBody, new Callback() {
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
            }
        });
    }


    /* 密码正确 */
    private void toast_login_1(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
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
            JSONArray jArray = new JSONArray(response);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}