package com.lovebus.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lovebus.function.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import bus.android.com.lovebus.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity{

    private EditText et_empNo;
    private EditText et_pass;
    private EditText et_passConfirm;
    private EditText et_niceName;
    private TextView et_txtContent;


    private Button btn_Submit;

    private int status;
    private JSONObject json = new JSONObject();
    private Handler handler;
    private String url = "http://lovebus.top/demo//register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_empNo = (EditText)findViewById(R.id.et_empNo);
        et_pass = (EditText)findViewById(R.id.et_pass);
        et_passConfirm = (EditText)findViewById(R.id.et_passConfirm);
        et_niceName = (EditText)findViewById(R.id.et_name);
        et_txtContent = (TextView) findViewById(R.id.txtContent);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                if (msg.what==123)
                {
                    //跳转到登录成功的界面
                    Intent intent = new Intent(RegisterActivity.this, Main_Activity.class);
                    startActivity(intent);
                }
                else if (msg.what == 234)
                {
                    Toast.makeText(RegisterActivity.this, "您注册失败，可能是网络问题",Toast.LENGTH_SHORT).show();
                    et_txtContent.setText(msg.toString());
                }

            }
        };
        btn_Submit = (Button)findViewById(R.id.btn_submit);
        btn_Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try
                        {
                            json.put("empNo", et_empNo.getText().toString());
                            json.put("pass", et_pass.getText().toString());
                            json.put("name", et_niceName.getText().toString());

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        String jsonStr = json.toString();
                        HttpUtils.postJson(url, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.e("TAG", "NetConnect error!");
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String responseStr = response.toString();
                                String responseBodyStr = response.body().string();
                                try
                                {
                                    //获取返回的json数据，为{"success":"success"}形式.
                                    //JSONArray jsonArray = new JSONArray(responseBodyStr);
                                    JSONObject jsonData = new JSONObject(responseBodyStr);
                                    String resultStr = jsonData.getString("success");

                                    if (resultStr.equals("success")) //注册成功，发送消息
                                    {
                                        Message msg = handler.obtainMessage();
                                        msg.what = 123;
                                        handler.sendMessage(msg);
                                    }
                                    else //注册失败
                                    {
                                        Message msg = handler.obtainMessage();
                                        msg.what = 234;
                                        msg.obj = resultStr;
                                        handler.sendMessage(msg);
                                    }
                                }
                                catch(JSONException e)
                                {
                                    e.printStackTrace();
                                }

                            }
                        }, jsonStr);
                    }
                }).start();
            }
        });
    }

}
