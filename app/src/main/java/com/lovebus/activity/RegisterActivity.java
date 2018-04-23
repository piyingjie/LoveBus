package com.lovebus.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class RegisterActivity extends AppCompatActivity {

    String account;
    String phone;
    String nickname;
    String register_password;
    String register_password_confirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final EditText account_text=(EditText)findViewById(R.id.register_account);
        final EditText phone_text=(EditText)findViewById(R.id.register_phone);
        final EditText nickname_text=(EditText)findViewById(R.id.register_nickname);
        final EditText register_password_text=(EditText)findViewById(R.id.register_password);
        final EditText register_password_confirm_text=(EditText)findViewById(R.id.register_password_confirm);
        Button register=(Button)findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account=account_text.getText().toString();
                phone=phone_text.getText().toString();
                nickname=nickname_text.getText().toString();
                register_password=register_password_text.getText().toString();
                register_password_confirm=register_password_confirm_text.getText().toString();
                if(account.length()>=8||account.length()<1){
                    Toast.makeText(RegisterActivity.this,"账号名为1-7位",Toast.LENGTH_SHORT).show();
                }
                else if(phone.length()!=11)
                {
                    Toast.makeText(RegisterActivity.this,"请输入11位手机号",Toast.LENGTH_SHORT).show();
                }
                else if(nickname.length()<1||nickname.length()>8)
                {
                    Toast.makeText(RegisterActivity.this,"昵称为0-8位",Toast.LENGTH_SHORT).show();
                }
                else if(register_password.length()>16||register_password.length()<8){
                    Toast.makeText(RegisterActivity.this,"密码为8-16位",Toast.LENGTH_SHORT).show();
                }
                else if(!register_password.equals(register_password_confirm))
                {
                    Toast.makeText(RegisterActivity.this,"密码与确认密码相同",Toast.LENGTH_SHORT).show();
                }
                else{
                    register_http();
                }
            }
        });
    }
    private void register_http(){
        RequestBody requestBody=new FormBody.Builder().add("account",account).add("phone",phone).add("nickname",nickname).add("password",register_password).build();
        Okhttp.postOkHttpRequest("http://lovebus.top/lovebus/register.php", requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RegisterActivity.this, "请检查网络连接是否顺畅", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data=response.body().string();
                if(data.length()==0)
                {
                    MyLog.d("RES","response是空");
                }
                else{
                    MyLog.d("RES",data);
                }
            }
        });
    }
    private void parseJSONWithJSONObject(String response) {
        /**这个部分是json的解析部分*/
        try {
            /**response可能需要接下来的一步改变编码*/
            if(response != null && response.startsWith("\ufeff"))
            {
                response =  response.substring(1);
            }
            String id;
            JSONArray jArray = new JSONArray(response);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);
                id=json_data.getString("id");
                MyLog.d("REG",id);
                toast(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void toast(final String response){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(response.equals("0")){
                    Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                    finish();
                }
                else if(response.equals("-1")){
                    Toast.makeText(RegisterActivity.this,"手机号已被使用",Toast.LENGTH_SHORT).show();
                }
                else if(response.equals("-2")){
                    Toast.makeText(RegisterActivity.this,"账号已被使用",Toast.LENGTH_SHORT).show();
                }
                else if(response.equals("-3")){
                    Toast.makeText(RegisterActivity.this,"网络连接失败",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }/**点击注册按钮之后的提醒*/
    /* 登录点击事件 */
    public void signInText(View view){
        finish();
    }
}
