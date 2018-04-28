package com.lovebus.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lovebus.entity.User;
import com.lovebus.function.MyLog;
import com.lovebus.function.Okhttp;
import com.lovebus.function.SharedPreferences_tools;

import org.json.JSONObject;

import java.io.IOException;

import bus.android.com.lovebus.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpdatePasswordActivity extends AppCompatActivity {
    String old_password_s;
    String  register_password;
    String register_password_confirm;
    User user=new User(false,null,null,null,null,null,null);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        final EditText old_password=(EditText)findViewById(R.id.old_password);
        final EditText register_password_text=(EditText)findViewById(R.id.register_password);
        final EditText register_password_confirm_text=(EditText)findViewById(R.id.register_password_confirm);
        Button update=(Button)findViewById(R.id.update);
        if(SharedPreferences_tools.load("User","info",UpdatePasswordActivity.this)!=null){
            user=(User)SharedPreferences_tools.load("User","info",UpdatePasswordActivity.this);
        }
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                old_password_s=old_password.getText().toString();
                register_password=register_password_text.getText().toString();
                register_password_confirm=register_password_confirm_text.getText().toString();

                if(old_password_s.length()>16||old_password_s.length()<8)
                {
                    Toast.makeText(UpdatePasswordActivity.this,"原密码为8-16位",Toast.LENGTH_SHORT).show();
                }
                else if(register_password.length()>16||register_password.length()<8){
                    Toast.makeText(UpdatePasswordActivity.this,"密码为8-16位",Toast.LENGTH_SHORT).show();
                }
                else if(!register_password.equals(register_password_confirm))
                {
                    Toast.makeText(UpdatePasswordActivity.this,"密码与确认密码不相同",Toast.LENGTH_SHORT).show();
                }
                else if(old_password_s.equals(register_password))
                {
                    Toast.makeText(UpdatePasswordActivity.this,"新密码与原密码相同",Toast.LENGTH_SHORT).show();
                }
                else{
                    RequestBody requestBody = new FormBody.Builder().add("account",user.getAccount()).add("old_password", old_password_s).add("password",register_password).build();
                    Okhttp.postOkHttpRequest("http://lovebus.top/lovebus/pwdchange.php", requestBody, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(UpdatePasswordActivity.this, "请检查网络连接是否顺畅", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String data=response.body().string();
                            parseJSONWithJSONObject(data);
                            MyLog.d("UDP",data);
                        }
                    });
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
            JSONObject json_data = new JSONObject(response);
            String id=json_data.getString("status");
            if(id.equals("1")){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UpdatePasswordActivity.this,"密码修改成功",Toast.LENGTH_SHORT).show();
                    }
                });
                user.setAccount(null);
                user.setPassword(null);
                user.setIs_login(false);
                user.setNickname(null);
                user.setPhone(null);
                user.setCity(null);
                user.setHead_image(null);
                SharedPreferences_tools.clear(UpdatePasswordActivity.this,"User");
                SharedPreferences_tools.clear(UpdatePasswordActivity.this,"UserHead");
                finish();
            }
            if(id.equals("0")){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UpdatePasswordActivity.this,"请检查网络连接",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if(id.equals("-2")){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UpdatePasswordActivity.this,"原密码错误",Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
