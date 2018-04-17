package com.lovebus.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lovebus.entity.LoginResult;
import com.lovebus.function.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import bus.android.com.lovebus.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Handler handler;
    private EditText username;
    private EditText password;
    private Button btn_login;
    private Button btn_register;
    private TextView txtResult;
    private String url = "http://lovebus.top/demo/login.php";
    private String url1 = "http://192.168.0.1/Test/json_array.php";
    LoginResult m_result;

    Map<String, String> map = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //处理登录成功消息
        handler  = new Handler(){
            @Override
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                switch (msg.what)
                {
                    case 123:
                        try
                        {
                            //获取用户登录的结果
                            LoginResult result = (LoginResult)msg.obj;
                            String userName = result.getNicename();
                            txtResult.setText(userName+" 成功登录!");

                            Toast.makeText(LoginActivity.this, "您成功登录",Toast.LENGTH_SHORT).show();

                            //跳转到登录成功的界面
                            Intent intent = new Intent(LoginActivity.this, Main_Activity.class);
                            startActivity(intent);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        break;

                    default:
                        break;
                }
            }
        };

        Bundle bundle = this.getIntent().getExtras();

        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);

        btn_login = (Button)findViewById(R.id.btn_login);
        btn_register = (Button)findViewById(R.id.btn_register);

        txtResult = (TextView)findViewById(R.id.txtResult);

        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        if (bundle != null)
        {
            username.setText(bundle.getString("empNo"));
            password.setText(bundle.getString("pass"));
        }
    }

    private LoginResult parseJSONWithGson(String jsonData)
    {
        Gson gson = new Gson();
        return gson.fromJson(jsonData, LoginResult.class);
    }
    public void onClick(View v)
    {
        int id = v.getId();
        switch (id)
        {
            case R.id.btn_login:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try
                        {
                            //POST信息中加入用户名和密码
                            map.put("uid", username.getText().toString().trim());
                            map.put("pwd", password.getText().toString().trim());
                            //HttpUtils.httpPostMethod(url, json, handler);
                            HttpUtils.post(url, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.e("DaiDai", "OnFaile:",e);
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String responseBody = response.body().string();
                                    m_result = parseJSONWithGson(responseBody);
                                    //发送登录成功的消息
                                    Message msg = handler.obtainMessage();
                                    msg.what = 123;
                                    msg.obj = m_result; //把登录结果也发送过去
                                    handler.sendMessage(msg);
                                }
                            }, map);
                        }
                        catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case R.id.btn_register:
                Intent intent = new Intent(LoginActivity.this, Main_Activity.class);
                startActivity(intent);
                break;
        }
    }
}

