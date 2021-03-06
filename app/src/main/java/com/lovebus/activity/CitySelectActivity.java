package com.lovebus.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.lljjcoder.style.citylist.bean.CityInfoBean;
import com.lljjcoder.style.citylist.sortlistview.CharacterParser;
import com.lljjcoder.style.citylist.sortlistview.PinyinComparator;
import com.lljjcoder.style.citylist.sortlistview.SideBar;
import com.lljjcoder.style.citylist.sortlistview.SortAdapter;
import com.lljjcoder.style.citylist.sortlistview.SortModel;
import com.lljjcoder.style.citylist.utils.CityListLoader;
import com.lljjcoder.style.citylist.widget.CleanableEditView;
import com.lljjcoder.utils.PinYinUtils;
import com.lovebus.entity.Location;
import com.lovebus.entity.User;
import com.lovebus.function.Locate;
import com.lovebus.function.MyLog;
import com.lovebus.function.Okhttp;
import com.lovebus.function.SharedPreferences_tools;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bus.android.com.lovebus.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CitySelectActivity extends AppCompatActivity{




        CleanableEditView mCityTextSearch;

        TextView mCurrentCityTag;

        TextView mCurrentCity;

        TextView mLocalCityTag;

        TextView mLocalCity;

        ListView sortListView;

        TextView mDialog;

        SideBar mSidrbar;

        ImageView imgBack;

        public SortAdapter adapter;

        private String cityLocation;

        private SharedPreferences sharedPreferences;

        private SharedPreferences.Editor editor;

    private SharedPreferences login_sp;//获取登录状态

        private boolean first_start;

        private boolean first_login;

        private String cityName;

        /**
         * 汉字转换成拼音的类
         */
        private CharacterParser characterParser;

        private List<SortModel> sourceDateList;

        /**
         * 根据拼音来排列ListView里面的数据类
         */
        private PinyinComparator pinyinComparator;

        private List<CityInfoBean> cityListInfo = new ArrayList<>();

        private CityInfoBean cityInfoBean = new CityInfoBean();

        String account;//帐号功能

        String status;


        //startActivityForResult flag
        public static final int CITY_SELECT_RESULT_FRAG = 0x0000032;



        public PinYinUtils mPinYinUtils = new PinYinUtils();

        Location locationMsg=new Location(0,0,null,null,null,null,null,null,null);

    User user=new User(false,null,null,null,null,null,null);

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.city_list_select);

            sharedPreferences = getSharedPreferences("currentCity", MODE_PRIVATE);

            login_sp = getSharedPreferences("first_login",MODE_PRIVATE);


            if(sharedPreferences.getBoolean("first_start",true)) {
                editor = sharedPreferences.edit();
                editor.putBoolean("first_start", false);
                editor.apply();
                first_start=true;
            }
            else {
                first_start=false;
            }
            if (login_sp.getBoolean("first_login", true)) {
                editor = login_sp.edit();
                editor.putBoolean("first_login",false);
                editor.apply();
                first_login=true;
            }else {
                first_login=false;
            }
        /*if(SharedPreferences_tools.load("User","info",CitySelectActivity.this)!=null){
                editor=login_sp.edit();
                editor.putBoolean("first_login",true);
                editor.apply();
                first_login=true;

        }else {
                first_login=false;
        }*/
            //init();


            CityListLoader.getInstance().loadCityData(this);

            initCityLocation();

            initView();

            initList();





            setCityData(CityListLoader.getInstance().getCityListData());

        }

    /*private void init() {
        if (first_start){
            CityListLoader.getInstance().loadCityData(this);
            initCityLocation();
            initView();
            initList();
            setCityData(CityListLoader.getInstance().getCityListData());
        }else{
            CityListLoader.getInstance().loadCityData(this);
            initCityLocation();
            initView();
            initList();
            setCityData(CityListLoader.getInstance().getCityListData());
        }
    }
*/
    private void initCityLocation() {
        /*初始化定位*/
        Locate.init(CitySelectActivity.this);

        /*定位调用*/
        Locate.getCurrentLocation(new Locate.MyLocationListener() {
            @Override
            public void result(AMapLocation location) {
                locationMsg.setLatitude(location.getLatitude());
                locationMsg.setLongitude(location.getLongitude());
                locationMsg.setAddress(location.getAddress());
                locationMsg.setCountry(location.getCountry());
                locationMsg.setProvince(location.getProvince());
                locationMsg.setCity(location.getCity());
                locationMsg.setDistrict(location.getDistrict());
                locationMsg.setStreet(location.getStreet());
                locationMsg.setPoiName(location.getPoiName());
                MyLog.d("Test",locationMsg.getAddress());
                cityLocation=locationMsg.getCity();
                mCurrentCity = (TextView) findViewById(R.id.currentCity);
                mLocalCity = (TextView) findViewById(R.id.localCity);
                MyLog.d("Test",cityLocation);

                if(first_start){
                    //这里把cityLocation保存sharedPreference后防止后边初始化取值为空
                    editor = sharedPreferences.edit();
                    editor.putString("cCity",cityLocation);
                    editor.apply();

                    mCurrentCity.setText(sharedPreferences.getString("cCity",""));
                }
                mLocalCity.setText(cityLocation);
                editor = sharedPreferences.edit();
                editor.putString("lCity",cityLocation);
                editor.apply();
            }
        });

    }


    private void initView() {
            mCityTextSearch = (CleanableEditView) findViewById(R.id.cityInputText);
            mCurrentCityTag = (TextView) findViewById(R.id.currentCityTag);
            mCurrentCity = (TextView) findViewById(R.id.currentCity);
            mLocalCityTag = (TextView) findViewById(R.id.localCityTag);
            mLocalCity = (TextView) findViewById(R.id.localCity);
            sortListView = (ListView) findViewById(R.id.country_lvcountry);
            mDialog = (TextView) findViewById(R.id.dialog);
            mSidrbar = (SideBar) findViewById(R.id.sidrbar);
            imgBack = (ImageView) findViewById(R.id.imgBack);
            imgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(CitySelectActivity.this, Main_Activity.class));
                    finish();
                }
            });
            if (!first_start){
                if (first_login){
                    //if(SharedPreferences_tools.load("User","info",CitySelectActivity.this)!=null){
                        checkAccount();

                    //}
                }
                else {
                    mCurrentCity.setText(sharedPreferences.getString("cCity",""));
                }
                //checkAccount();
                //mCurrentCity.setText(sharedPreferences.getString("cCity",""));
            }
            //MyLog.d("Test",cityLocation);
            //mCityTextSearch.setText(cityLocation);

        }

    private void toast_changeCity_2() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CitySelectActivity.this,"密码错误或用户名不存在",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toast_changeCity_1() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CitySelectActivity.this,"切换成功",Toast.LENGTH_SHORT).show();
                SharedPreferences_tools.save("User","info",CitySelectActivity.this,user);
                //MyLog.d("Login",user.getCity());
                mCurrentCity.setText(user.getCity());
                //finish();
            }
        });
    }

    private void checkAccount(){
        //if(SharedPreferences_tools.load("User","info",CitySelectActivity.this)!=null){
            user=(User)SharedPreferences_tools.load("User","info",CitySelectActivity.this);
            account=user.getAccount();
            MyLog.d("min",sharedPreferences.getString("cCity",""));

            RequestBody requestBody = new FormBody.Builder().add("account", account)
                    .add("city",sharedPreferences.getString("cCity","")).build();
            Okhttp.postOkHttpRequest("http://lovebus.top/lovebus/addcity.php", requestBody, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    /**这里写出错后的日志记录*/
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CitySelectActivity.this, "请检查网络连接是否顺畅", Toast.LENGTH_SHORT).show();
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
                        toast_changeCity_1();
                        //mCurrentCity.setText(user.getCity());
                    }
                    else {
                        toast_changeCity_2();
                    }
                }
            });
            //mCurrentCity.setText(user.getCity());
       /* }
        else {
            Toast.makeText(CitySelectActivity.this,"欢迎您",Toast.LENGTH_SHORT).show();
            //mCurrentCity.setText(sharedPreferences.getString("cCity",""));
        }*/
        //mCurrentCity.setText(sharedPreferences.getString("cCity",""));
    }

    private void setCityData(List<CityInfoBean> cityList) {
            cityListInfo = cityList;
            if (cityListInfo == null) {
                return;
            }
            int count = cityList.size();
            String[] list = new String[count];
            for (int i = 0; i < count; i++)
                list[i] = cityList.get(i).getName();

            sourceDateList.addAll(filledData(cityList));
            // 根据a-z进行排序源数据
            Collections.sort(sourceDateList, pinyinComparator);
            adapter.notifyDataSetChanged();
        }

        /**
         * 为ListView填充数据
         *
         * @return
         */
        private List<SortModel> filledData(List<CityInfoBean> cityList) {
            List<SortModel> mSortList = new ArrayList<SortModel>();

            for (int i = 0; i < cityList.size(); i++) {

                CityInfoBean result = cityList.get(i);

                if (result != null) {

                    SortModel sortModel = new SortModel();

                    String cityName = result.getName();
                    //汉字转换成拼音
                    if (!TextUtils.isEmpty(cityName) && cityName.length() > 0) {

                        String pinyin = "";
                        /*if (cityName.equals("重庆市")) {
                            pinyin = "chong";
                        }
                        else if (cityName.equals("长沙市")) {
                            pinyin = "chang";
                        }
                        else if (cityName.equals("长春市")) {
                            pinyin = "chang";
                        }
                        else {*/
                            pinyin = mPinYinUtils.getStringPinYin(cityName.substring(0, 1));
                        //}

                        if (!TextUtils.isEmpty(pinyin)) {

                            sortModel.setName(cityName);

                            String sortString = pinyin.substring(0, 1).toUpperCase();

                            // 正则表达式，判断首字母是否是英文字母
                            if (sortString.matches("[A-Z]")) {
                                sortModel.setSortLetters(sortString.toUpperCase());
                            }
                            else {
                                sortModel.setSortLetters("#");
                            }
                            mSortList.add(sortModel);
                        }
                        else {
                            Log.d("citypicker_log", "null,cityName:-> " + cityName + "       pinyin:-> " + pinyin);
                        }

                    }

                }
            }
            return mSortList;
        }

        private void initList() {
            sourceDateList = new ArrayList<SortModel>();
            adapter = new SortAdapter(CitySelectActivity.this, sourceDateList);
            sortListView.setAdapter(adapter);

            //实例化汉字转拼音类
            characterParser = CharacterParser.getInstance();
            pinyinComparator = new PinyinComparator();
            mSidrbar.setTextView(mDialog);
            //设置右侧触摸监听
            mSidrbar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

                @Override
                public void onTouchingLetterChanged(String s) {
                    //该字母首次出现的位置
                    int position = adapter.getPositionForSection(s.charAt(0));
                    if (position != -1) {
                        sortListView.setSelection(position);
                    }
                }
            });

            sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    cityName = ((SortModel) adapter.getItem(position)).getName();
                    if(cityName.equals(null)){
                        MyLog.d("CITY","-1");
                    }
                    else {
                        MyLog.d("CITY",cityName+"-1");
                        editor = sharedPreferences.edit();
                        //sharedPreferences.getString("currentCity",cityName);
                        editor.putString("cCity",cityName);
                        editor.commit();
                        if(SharedPreferences_tools.load("User","info",CitySelectActivity.this)!=null){
                            checkAccount();
                        }
                        else {
                            mCurrentCity.setText(sharedPreferences.getString("cCity",cityName));
                        }
                        MyLog.d("CITY","test"+"-"+sharedPreferences.getString("cCity",cityName));
                        //mCurrentCity.setText(sharedPreferences.getString("cCity",cityName));
                    }
                    //                    }
                    //                    //mCurrentCity.setText(cityName);
                    startActivity(new Intent(CitySelectActivity.this, Main_Activity.class));
                    /*cityInfoBean = CityInfoBean.findCity(cityListInfo, cityName);
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("cityinfo", cityInfoBean);
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);*/
                    finish();
                }
            });

            //根据输入框输入值的改变来过滤搜索
            mCityTextSearch.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                    filterData(s.toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

        }

        /**
         * 根据输入框中的值来过滤数据并更新ListView
         *
         * @param filterStr
         */
        private void filterData(String filterStr) {
            List<SortModel> filterDateList = new ArrayList<SortModel>();

            if (TextUtils.isEmpty(filterStr)) {
                filterDateList = sourceDateList;
            }
            else {
                filterDateList.clear();
                for (SortModel sortModel : sourceDateList) {
                    String name = sortModel.getName();
                    if (name.contains(filterStr) || characterParser.getSelling(name).startsWith(filterStr)) {
                        filterDateList.add(sortModel);
                    }
                }
            }

            // 根据a-z进行排序
            Collections.sort(filterDateList, pinyinComparator);
            adapter.updateListView(filterDateList);
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
            status=json_data.getString("status");
            MyLog.d("Login",status);
            if(status.equals("1")){
                //user.setAccount(account);
               /* user.setPassword(password);
                user.setIs_login(true);
                user.setNickname(json_data.getString("nickname"));
                user.setPhone(json_data.getString("phonenumber"));*/
                user.setCity(json_data.getString("city"));
                //user.setHead_image(json_data.getString("head").replaceAll("\\\\",""));
                //MyLog.d("HEAD",user.getHead_image());
                MyLog.d("Login",user.getCity());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    }





