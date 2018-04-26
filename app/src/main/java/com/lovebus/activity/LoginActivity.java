package com.lovebus.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lovebus.entity.User;
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

import java.io.IOException;

import bus.android.com.lovebus.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String APP_ID="1106786595";//官方获取的APPID
    private Tencent mTencent;
    private BaseUiListener mIUiListener;
    private UserInfo mUserInfo;


    EditText account_edit = null, passwordEdit = null;//帐号和密码编辑框
    Button signInBtn = null;//登录按钮
    String account, password;//帐号和密码功能
    String status;
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
        /**通过这句代码，SDK实现了QQ的登录，这个方法有三个参数，第一个参数是context上下文，第二个参数SCOPO 是一个String类型的字符串，表示一些权限
         官方文档中的说明：应用需要获得哪些API的权限，由“，”分隔。例如：SCOPE = “get_user_info,add_t”；所有权限用“all”
         第三个参数，是一个事件监听器，IUiListener接口的实例，这里用的是该接口的实现类 */
        mIUiListener = new BaseUiListener();
        //all表示获取所有权限
        mTencent.login(LoginActivity.this,"all", mIUiListener);
    }
    /**
     * 自定义监听器实现IUiListener接口后，需要实现的3个方法
     * onComplete完成 onError错误 onCancel取消
     */
    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            Toast.makeText(LoginActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "response:" + response);
            JSONObject obj = (JSONObject) response;
            try {
                String openID = obj.getString("openid");
                String accessToken = obj.getString("access_token");
                String expires = obj.getString("expires_in");
                mTencent.setOpenId(openID);
                mTencent.setAccessToken(accessToken,expires);
                QQToken qqToken = mTencent.getQQToken();
                mUserInfo = new UserInfo(getApplicationContext(),qqToken);
                mUserInfo.getUserInfo(new IUiListener() {
                    @Override
                    public void onComplete(Object response) {
                        Log.e(TAG,"登录成功"+response.toString());
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
            if(status.equals("1")){
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
}
