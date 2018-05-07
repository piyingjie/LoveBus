package com.lovebus.activity;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;

import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusLineQuery;
import com.amap.api.services.busline.BusLineResult;

//import com.amap.api.services.busline.BusLineSearch;
import com.amap.api.services.busline.BusStationItem;
import com.amap.api.services.busline.BusStationQuery;
import com.amap.api.services.busline.BusStationResult;
//import com.amap.api.services.busline.BusStationSearch;
import com.amap.api.services.core.AMapException;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import com.lovebus.entity.Location;
import com.lovebus.entity.User;

import com.lovebus.function.BusLineDialog;
import com.lovebus.function.BusLineOverlay;

import com.lovebus.function.BusLineSearch;
import com.lovebus.function.BusStationSearch;
import com.lovebus.function.Locate;
import com.lovebus.function.LoveBusUtil;
import com.lovebus.function.MyLog;
import com.lovebus.function.Okhttp;
import com.lovebus.function.OnListItemlistener;
import com.lovebus.function.PoiOverlay;
import com.lovebus.function.SharedPreferences_tools;
import com.lovebus.function.ToastUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bus.android.com.lovebus.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;





public class Main_Activity extends AppCompatActivity implements View.OnClickListener,TextWatcher,AMap.OnMarkerClickListener,Inputtips.InputtipsListener,
        AMap.InfoWindowAdapter {
    MapView mMapView;
    private AMap aMap;
    private DrawerLayout drawerLayout;
    ImageView leftMenu;
    ImageView search;
    de.hdodenhof.circleimageview.CircleImageView user_head_image;
    TextView username;
    TextView userSetCity;
    Bitmap photo;
    private String image_response;
    User user=new User(false,null,null,null,null,null,null);
    AutoCompleteTextView searchText;// 输入搜索关键字
    private String keyWord = "";// 要输入的poi搜索关键字
    private String localCity;
    private EditText editCity;// 要输入的城市名字或者城市区号
    PoiResult poiResult; // poi返回的结果

    private PoiSearch.Query query;// Poi查询条件类
    PoiSearch poiSearch;// POI搜索

    private SharedPreferences sp;//获取当前城市

    SharedPreferences login_sp;
    SharedPreferences.Editor editor;
    private boolean first_login;//获取登录状态

    /*公交线路搜索*/
    private ProgressDialog progDialog = null;// 进度框
    int currentpage = 0;// 当前页面，从0开始计数
    public static BusLineResult busLineResult;// 公交线路搜索返回的结果
    private List<BusLineItem> lineItems = null;// 公交线路搜索返回的busline
    private BusLineQuery busLineQuery;// 公交线路查询的查询类
    private com.amap.api.services.busline.BusLineSearch busLineSearch;
    // 公交线路列表查询

    Location locationMsg=new Location(0,0,null,null,null,null,null,null,null);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        sp= getSharedPreferences("currentCity",MODE_PRIVATE);
        login_sp = getSharedPreferences("first_login",MODE_PRIVATE);
        if (login_sp.getBoolean("first_login", true)) {
            editor = login_sp.edit();
            editor.putBoolean("first_login",false);
            editor.apply();
            first_login=true;
        }else {
            first_login=false;
        }

        showMap(savedInstanceState);
        init();
    }

    /**
     * 设置marker的监听和信息窗口的监听
     */


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        Locate.destroyLocation();
     }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
        if(SharedPreferences_tools.load("User","info",Main_Activity.this)!=null){
            user=(User)SharedPreferences_tools.load("User","info",Main_Activity.this);
        }
        else {
            user.setAccount(null);
            user.setPassword(null);
            user.setIs_login(false);
            user.setNickname(null);
            user.setPhone(null);
            user.setCity(null);
            user.setHead_image(null);
        }
        updateUserInfo();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }


    /*初始化活动*/
    private void init(){
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        NavigationView navigationView= (NavigationView) findViewById(R.id.leftView_1);
        leftMenu =(ImageView) findViewById(R.id.leftMenu);
        search=(ImageView) findViewById(R.id.search);
        View user_header=navigationView.inflateHeaderView(R.layout.header_nav);
        user_head_image=(de.hdodenhof.circleimageview.CircleImageView)user_header.findViewById(R.id.userHeadImage);
        username=(TextView) user_header.findViewById(R.id.user_set_name);
        userSetCity=(TextView)user_header.findViewById(R.id.user_set_city);
        leftMenu.setOnClickListener(this);
        search.setOnClickListener(this);
        user_head_image.setOnClickListener(this);
        if(SharedPreferences_tools.load("User","info",Main_Activity.this)!=null){
            user=(User)SharedPreferences_tools.load("User","info",Main_Activity.this);
        }
        updateUserInfo();
        if (aMap==null) {
            aMap = mMapView.getMap();
        }
        setUpMap();
        searchText = (AutoCompleteTextView) findViewById(R.id.keyWord);
        searchText.addTextChangedListener(this);// 添加文本输入框监听事件
        locate_main();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    /*左侧菜单点击事件在下方添加*/
                    case R.id.item1:
                        Toast.makeText(Main_Activity.this,"item1",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.city_change:
                        startActivity(new Intent(Main_Activity.this,CitySelectActivity.class));
                        break;
                    case R.id.logout:
                        menu_switch();
                    case R.id.update_password:
                        menu_update_password();
                    default:
                }
                return false;
            }
        });
    }
    /* 设置页面监听*/
    private void setUpMap() {
        aMap.setOnMarkerClickListener(this);// 添加点击marker监听事件
        aMap.setInfoWindowAdapter((AMap.InfoWindowAdapter) this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.leftMenu:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.search:
                onclick_search();
                showProgressDialog();
                break;
            case R.id.userHeadImage:
                onclick_userHeadImage();
                break;
            default:
        }
    }


  private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索:\n");
        progDialog.show();
    }

    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /*点击事件的一些函数*/
    private void onclick_search(){
        keyWord = LoveBusUtil.checkEditText(searchText);
        if ("".equals(keyWord)) {
            Toast.makeText(Main_Activity.this,"请输入关键字",Toast.LENGTH_SHORT).show();
        } else {
            com.lovebus.function.PoiSearch.doSearchQuery(Main_Activity.this,keyWord,localCity);
            com.lovebus.function.PoiSearch.getPoiSearch(new com.lovebus.function.PoiSearch.PoiSearchListener() {
                @Override
                public void result(PoiResult result, int rCode) {
                    dissmissProgressDialog();
                    if (rCode == AMapException.CODE_AMAP_SUCCESS) {
                        if (result != null && result.getQuery() != null) {// 搜索poi的结果
                            if (result.getQuery().equals(com.lovebus.function.PoiSearch.getQuery())) {// 是否是同一条
                                poiResult = result;
                                // 取得搜索到的poiitems有多少页
                                List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                                List<SuggestionCity> suggestionCities = poiResult
                                        .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                                if (poiItems != null && poiItems.size() > 0) {
                                    aMap.clear();// 清理之前的图标
                                    PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
                                    poiOverlay.removeFromMap();
                                    poiOverlay.addToMap();
                                    poiOverlay.zoomToSpan();
                                }
                                else if (suggestionCities != null
                                        && suggestionCities.size() > 0) {
                                }
                            }
                        }
                    }
                }

                @Override
                public void item(PoiItem item, int rCode) {

                }
            });
            //searchLine();
            com.lovebus.function.BusLineSearch.searchLine_byName(Main_Activity.this,
                    keyWord,localCity);
            com.lovebus.function.BusLineSearch.getBusLine(new BusLineSearch.BusLineListener() {

                @Override
                public void result(BusLineResult result, final int rCode) {
                    dissmissProgressDialog();
                    if (rCode == AMapException.CODE_AMAP_SUCCESS){
                        if (result!=null&&result.getQuery()!=null
                                &&result.getQuery().equals(com.lovebus.function.BusLineSearch.getQuery())){
                            if (result.getQuery().getCategory()== BusLineQuery.SearchType.BY_LINE_NAME){
                                if (result.getPageCount()>0
                                    &&result.getBusLines()!=null
                                        &&result.getBusLines().size()>0){
                                    busLineResult = result;
                                    lineItems = result.getBusLines();
                                    if (lineItems !=null){
                                        //showResultList(lineItems);
                                        BusLineDialog busLineDialog = new BusLineDialog(
                                                Main_Activity.this,lineItems);
                                        busLineDialog.show();
                                        busLineDialog.onListItemClicklistener(new OnListItemlistener() {
                                            @Override
                                            public void onListItemClick(BusLineDialog dialog, BusLineItem item) {
                                                showProgressDialog();

                                                String lineId =item.getBusLineId();
                                                com.lovebus.function.BusLineSearch.searchLine_byId(Main_Activity.this,lineId,localCity);
                                                com.lovebus.function.BusLineSearch.getBusLine(new BusLineSearch.BusLineListener() {
                                                    @Override
                                                    public void result(BusLineResult result, int rCode) {
                                                        dissmissProgressDialog();
                                                        if (rCode == AMapException.CODE_AMAP_SUCCESS){
                                                            if (result!=null&&result.getQuery()!=null
                                                                    &&result.getQuery().equals(com.lovebus.function.BusLineSearch.getQuery())){
                                                                if(result.getQuery().getCategory()== BusLineQuery.SearchType.BY_LINE_ID){
                                                                    aMap.clear();// 清理地图上的marker
                                                                    busLineResult = result;
                                                                    lineItems = busLineResult.getBusLines();
                                                                    if (lineItems!=null&&lineItems.size()>0){
                                                                        BusLineOverlay busLineOverlay = new BusLineOverlay(Main_Activity.this,aMap,lineItems.get(0));
                                                                        busLineOverlay.removeFromMap();
                                                                        busLineOverlay.addToMap();
                                                                        busLineOverlay.zoomToSpan();
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                        busLineDialog.show();
                                    }
                                }
                            }else if(result.getQuery().getCategory()== BusLineQuery.SearchType.BY_LINE_ID){
                                aMap.clear();// 清理地图上的marker
                                busLineResult = result;
                                lineItems = busLineResult.getBusLines();
                                if (lineItems!=null&&lineItems.size()>0){
                                    BusLineOverlay busLineOverlay = new BusLineOverlay(Main_Activity.this,aMap,lineItems.get(0));
                                    busLineOverlay.removeFromMap();
                                    busLineOverlay.addToMap();
                                    busLineOverlay.zoomToSpan();
                                }
                            }
                        }else {
                            ToastUtil.show(Main_Activity.this,R.string.no_result);
                        }
                    }else {
                        ToastUtil.showerror(Main_Activity.this,rCode);
                    }
                }
            });
            com.lovebus.function.BusStationSearch.searchStation(Main_Activity.this,keyWord,localCity);
            com.lovebus.function.BusStationSearch.getBusStation(new BusStationSearch.BusStationListener() {
                @Override
                public void result(BusStationResult result, int rCode) {
                    dissmissProgressDialog();
                    if (rCode == AMapException.CODE_AMAP_SUCCESS) {
                        if (result != null && result.getPageCount() > 0
                                && result.getBusStations() != null
                                && result.getBusStations().size() > 0) {
                            ArrayList<BusStationItem> item = (ArrayList<BusStationItem>) result
                                    .getBusStations();
                            StringBuffer buf = new StringBuffer();
                            for (int i = 0; i < item.size(); i++) {
                                BusStationItem stationItem = item.get(i);


                                buf.append(" station: ").append(i).append(" name: ")
                                        .append(stationItem.getBusStationName());
                                Log.d("LG", "stationName:"
                                        + stationItem.getBusStationName() + "stationpos:"
                                        + stationItem.getLatLonPoint().toString());
                            }
                            String text = buf.toString();
                            Toast.makeText(Main_Activity.this, text,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            ToastUtil.show(Main_Activity.this, R.string.no_result);
                        }
                    } else  {
                        ToastUtil.showerror(Main_Activity.this, rCode);
                    }
                }
            });
        }
    }
    private void onclick_userHeadImage(){
        if(!user.isIs_login())
        {
            Intent intent=new Intent(Main_Activity.this,LoginActivity.class);
            startActivity(intent);
        }
        else {
            getAlbumPhoto(Main_Activity.this);
        }
    }
    private void menu_switch(){
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.account)//这里是显示提示框的图片信息，我这里使用的默认androidApp的图标
                .setTitle("提示")
                .setMessage("退出登陆？")
                .setNegativeButton("取消",null)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.setAccount(null);
                        user.setPassword(null);
                        user.setIs_login(false);
                        user.setNickname(null);
                        user.setPhone(null);
                        user.setCity(null);
                        user.setHead_image(null);
                        SharedPreferences_tools.clear(Main_Activity.this,"User");
                        SharedPreferences_tools.clear(Main_Activity.this,"UserHead");
                        updateUserInfo();
                    }
                }).show();
    }
    private void menu_update_password(){
        if(user.isIs_login())
        {
            Intent intent=new Intent(Main_Activity.this,UpdatePasswordActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(Main_Activity.this,"您还未登陆",Toast.LENGTH_SHORT).show();
        }
    }


    /*退出确认*/
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.account)//这里是显示提示框的图片信息，我这里使用的默认androidApp的图标
                .setTitle("退出爱公交")
                .setMessage("您真的要抛弃爱公交吗")
                .setNegativeButton("取消",null)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
    }

    //地图显示
    private void showMap(Bundle savedInstanceState){
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        //定义了一个地图view
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        UiSettings mUiSettings;//定义一个UiSettings对象
        mUiSettings = aMap.getUiSettings();//实例化UiSettings类对象
        /*指北针*/
        mUiSettings.setCompassEnabled(true);
        /*比例尺*/
        mUiSettings.setScaleControlsEnabled(true);
        /*缩放按钮位置*/
        mUiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
        mUiSettings.setTiltGesturesEnabled(false);
        /*回到当前位置按钮*/
        mUiSettings.setMyLocationButtonEnabled(true);
        //开始的缩放比例
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();
        //初始化定位蓝点样式类
        /*myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);*/
         myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        //连续定位、蓝点不会移动到地图中心点，地图依照设备方向旋转，并且蓝点会跟随设备移动
        myLocationStyle.interval(2000);
        //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);

        myLocationStyle.strokeWidth(0);//设置定位蓝点精度圈的边框宽度的方法。
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        //设置定位蓝点的Style
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);
        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

    }


    /*定位功能调用*/
    private void locate_main(){
        /*初始化定位*/
        Locate.init(Main_Activity.this);
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
                if (sp.getBoolean("first_start",true)){
                    localCity=locationMsg.getCity();
                }else {
                    localCity=sp.getString("cCity","");
                }
                MyLog.d("yang",localCity);
                MyLog.d("yang","sb");

            }
        });
    }

    private void updateUserInfo(){
        if(user.getCity()!=null&&(!user.getCity().equals("null")))
        {
            if(first_login){

                userSetCity.setText(user.getCity());
            }else {
                localCity=sp.getString("cCity","");
                userSetCity.setText(localCity);
            }
        }else {
            localCity=sp.getString("cCity","");
            MyLog.d("yang",localCity);
            userSetCity.setText(localCity);
        }
        if (user.getNickname()!=null&&(!user.getNickname().equals("null"))){
            username.setText(user.getNickname());
        }else {
            username.setText("爱公交游客用户");
        }
        if(user.getHead_image()!=null&&!user.getHead_image().equals("null")){
            Bitmap bitmap=SharedPreferences_tools.getImage(Main_Activity.this,"UserHead","image");
            if(bitmap==null){
                updateImage();
            }
            else {
                user_head_image.setImageBitmap(bitmap);
            }
        }
        else {
            user_head_image.setImageResource(R.drawable.account);
        }
    }

    /*从服务器上获取头像*/
    private void updateImage(){
        Okhttp.getOkHttpRequest("http://lovebus.top/lovebus/head/" + user.getAccount() + ".jpg", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Main_Activity.this, "请检查网络连接是否顺畅,从服务器获取头像失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                byte[] Picture_bt = response.body().bytes();
                final Bitmap getHeader = BitmapFactory.decodeByteArray(Picture_bt, 0, Picture_bt.length);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(getHeader==null){
                            Toast.makeText(Main_Activity.this, "请检查网络连接是否顺畅,从服务器获取头像失败", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            user_head_image.setImageBitmap(getHeader);
                            SharedPreferences_tools.saveImage(Main_Activity.this,getHeader,"UserHead","image");
                            getHeader.recycle();
                        }
                    }
                });
            }
        });
    }


    /*打开相册获取图片*/
    public void getAlbumPhoto(Activity activity){
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            MyLog.d("PHO","申请权限");
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        else{
            MyLog.d("PHO","打开相册");
            openAlbum(2);
        }
    }
    private void openAlbum(int CHOOSE_PHOTO){
        Intent intent= new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openAlbum(2);
                }
                else {
                    Toast.makeText(this,"你拒绝权限",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 2:
                if(resultCode==RESULT_OK){
                    if(Build.VERSION.SDK_INT>=19){
                        handleImageOnKitkat(data);
                    }
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @TargetApi(19)
    private void handleImageOnKitkat(Intent data){
        String imagePath=null;
        Uri uri=data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            String docId=DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];
                String selection= MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }
            else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
        }
        else if("content".equalsIgnoreCase(uri.getScheme())){
            imagePath=getImagePath(uri,null);
        }
        else if("file".equalsIgnoreCase(uri.getScheme())){
            imagePath=uri.getPath();
        }
        displayImage(imagePath);
    }
    private String getImagePath(Uri uri,String seleciton){
        String path=null;
        Cursor cursor=getContentResolver().query(uri,null,seleciton,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


    private void displayImage(String imagePath){
        if(imagePath!=null){
            photo= LoveBusUtil.compressImageFromFile(imagePath);
            MyLog.d("IMAGE",imagePath);
            LoveBusUtil.saveBitmap(photo);
            MediaType MEDIA_TYPE=MediaType.parse("image/*");
            File image=new File(Environment.getExternalStorageDirectory().getPath()+"/UserHeader.jpg");
            RequestBody req = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("account",user.getAccount())
                    .addFormDataPart("head",image.getName(), RequestBody.create(MEDIA_TYPE, image)).build();
            Okhttp.postOkHttpRequest("http://lovebus.top/lovebus/uploadhead.php", req, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Main_Activity.this, "请检查网络连接是否顺畅", Toast.LENGTH_SHORT).show();
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
                                user_head_image.setImageBitmap(photo);
                                Toast.makeText(Main_Activity.this, "头像修改成功", Toast.LENGTH_SHORT).show();
                                File image=new File(Environment.getExternalStorageDirectory().getPath()+"/UserHeader.jpg");
                                image.delete();
                                SharedPreferences_tools.saveImage(Main_Activity.this,photo,"UserHead","image");
                                user.setHead_image("http://lovebus.top/lovebus/head"+user.getAccount());
                                SharedPreferences_tools.save("User","info",Main_Activity.this,user);
                                photo.recycle();
                                photo=null;
                            }
                        });
                    } else if (image_response.equals("false")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Main_Activity.this, "头像修改失败", Toast.LENGTH_SHORT).show();
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
        else{
            Toast.makeText(this,"获取图片失败",Toast.LENGTH_SHORT).show();
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


    /**
     * 提供一个给默认信息窗口定制内容的方法
     */
    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    /**
     * 提供一个个性化定制信息窗口的方法
     */
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }






    /*poi搜索的回调*/
    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return false;
    }
    @Override
    public void afterTextChanged(Editable s) { }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String newText = s.toString().trim();
        if (!LoveBusUtil.IsEmptyOrNullString(newText)) {
            InputtipsQuery inputquery = new InputtipsQuery(newText, localCity);
            Inputtips inputTips = new Inputtips(Main_Activity.this, inputquery);
            inputTips.setInputtipsListener(this);
            inputTips.requestInputtipsAsyn();
        }
    }
    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {// 正确返回
            List<String> listString = new ArrayList<String>();
            for (int i = 0; i < tipList.size(); i++) {
                listString.add(tipList.get(i).getName());
            }
            ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(
                    getApplicationContext(),
                    R.layout.route_inputs, listString);
            searchText.setAdapter(aAdapter);
            aAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 公交线路搜索
     */
    /*public void searchLine() {
        currentpage = 0;// 第一页默认从0开始
        showProgressDialog();
        String search = keyWord.trim();
        if ("".equals(search)) {
            search = "641";
            keyWord=search;
        }
        busLineQuery = new BusLineQuery(search, BusLineQuery.SearchType.BY_LINE_NAME,
                localCity);// 第一个参数表示公交线路名，第二个参数表示公交线路查询，第三个参数表示所在城市名或者城市区号
        busLineQuery.setPageSize(10);// 设置每页返回多少条数据
        busLineQuery.setPageNumber(currentpage);// 设置查询第几页，第一页从0开始算起
        busLineSearch = new com.amap.api.services.busline.BusLineSearch(this, busLineQuery);// 设置条件
        busLineSearch.setOnBusLineSearchListener(this);// 设置查询结果的监听
        busLineSearch.searchBusLineAsyn();// 异步查询公交线路名称
        // 公交站点搜索事例

          BusStationQuery query = new BusStationQuery(search,localCity);
         query.setPageSize(10);
         query.setPageNumber(currentpage);
          BusStationSearch busStationSearch = new BusStationSearch(this,query);
         busStationSearch.setOnBusStationSearchListener(this);
         busStationSearch.searchBusStationAsyn();

    }

    *//**
     * 公交线路查询结果回调
     *//*
    @Override
    public void onBusLineSearched(BusLineResult result, int rCode) {
        dissmissProgressDialog();
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null
                    && result.getQuery().equals(busLineQuery)) {
                if (result.getQuery().getCategory() == BusLineQuery.SearchType.BY_LINE_NAME) {
                    if (result.getPageCount() > 0
                            && result.getBusLines() != null
                            && result.getBusLines().size() > 0) {
                        busLineResult = result;
                        lineItems = result.getBusLines();
                        if(lineItems != null) {
                            showResultList(lineItems);
                        }
                    }
                } else if (result.getQuery().getCategory() == BusLineQuery.SearchType.BY_LINE_ID) {
                    aMap.clear();// 清理地图上的marker
                    busLineResult = result;
                    lineItems = busLineResult.getBusLines();
                    if(lineItems != null && lineItems.size() > 0) {
                        BusLineOverlay busLineOverlay = new BusLineOverlay(this,
                                aMap, lineItems.get(0));
                        busLineOverlay.removeFromMap();
                        busLineOverlay.addToMap();
                        busLineOverlay.zoomToSpan();
                    }
                }
            } else {
                ToastUtil.show(Main_Activity.this, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(Main_Activity.this, rCode);
        }
    }
    *//**
     * 公交线路搜索返回的结果显示在dialog中
     *//*
    private void showResultList(List<BusLineItem> busLineItems) {
        BusLineDialog busLineDialog = new BusLineDialog(this, busLineItems);
        busLineDialog.onListItemClicklistener(new OnListItemlistener() {
            @Override
            public void onListItemClick(BusLineDialog dialog,
                                        final BusLineItem item) {
                showProgressDialog();

                String lineId = item.getBusLineId();// 得到当前点击item公交线路id
                busLineQuery = new BusLineQuery(lineId, BusLineQuery.SearchType.BY_LINE_ID,
                        localCity);// 第一个参数表示公交线路id，第二个参数表示公交线路id查询，第三个参数表示所在城市名或者城市区号
                com.amap.api.services.busline.BusLineSearch busLineSearch = new com.amap.api.services.busline.BusLineSearch(
                        Main_Activity.this, busLineQuery);
                busLineSearch.setOnBusLineSearchListener(Main_Activity.this);
                busLineSearch.searchBusLineAsyn();// 异步查询公交线路id
            }
        });
        busLineDialog.show();
    }

    @Override
    public void onBusStationSearched(BusStationResult result, int rCode) {
        dissmissProgressDialog();
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPageCount() > 0
                    && result.getBusStations() != null
                    && result.getBusStations().size() > 0) {
                ArrayList<BusStationItem> item = (ArrayList<BusStationItem>) result
                        .getBusStations();
                StringBuffer buf = new StringBuffer();
                for (int i = 0; i < item.size(); i++) {
                    BusStationItem stationItem = item.get(i);


                    buf.append(" station: ").append(i).append(" name: ")
                            .append(stationItem.getBusStationName());
                    Log.d("LG", "stationName:"
                            + stationItem.getBusStationName() + "stationpos:"
                            + stationItem.getLatLonPoint().toString());
                }
                String text = buf.toString();
                Toast.makeText(Main_Activity.this, text,
                        Toast.LENGTH_SHORT).show();
            } else {
                ToastUtil.show(Main_Activity.this, R.string.no_result);
            }
        } else  {
            ToastUtil.showerror(Main_Activity.this, rCode);
        }
    }*/
}
