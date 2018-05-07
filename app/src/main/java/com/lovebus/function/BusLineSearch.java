package com.lovebus.function;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusLineQuery;
import com.amap.api.services.busline.BusLineResult;
import com.amap.api.services.busline.BusStationItem;
import com.amap.api.services.busline.BusStationQuery;
import com.amap.api.services.busline.BusStationResult;
import com.amap.api.services.busline.BusStationSearch;
import com.amap.api.services.core.AMapException;
import com.lovebus.activity.Main_Activity;

import java.util.List;

import bus.android.com.lovebus.R;

public class BusLineSearch  {
    private static int currentpage;
    public static BusLineQuery busLineQuery;
    private BusLineResult busLineResult;// 公交线路搜索返回的结果
    public static com.amap.api.services.busline.BusLineSearch busLineSearch;// 公交线路列表查询
    private static BusStationSearch busStationSearch;
    private static ProgressDialog progDialog = null;// 进度框
    private List<BusLineItem> lineItems = null;// 公交线路搜索返回的busline


    /*通过名字查询公交*/
    public static void searchLine_byName(Context context, String searchText, String cityName) {
        currentpage = 0;// 第一页默认从0开始
        String search = searchText.trim();
        busLineQuery = new BusLineQuery(search, BusLineQuery.SearchType.BY_LINE_NAME,
                cityName);// 第一个参数表示公交线路名，第二个参数表示公交线路查询，第三个参数表示所在城市名或者城市区号
        busLineQuery.setPageSize(10);// 设置每页返回多少条数据
        busLineQuery.setPageNumber(currentpage);// 设置查询第几页，第一页从0开始算起
        busLineSearch = new com.amap.api.services.busline.BusLineSearch(context, busLineQuery);
        busLineSearch.searchBusLineAsyn();// 异步查询公交线路名称
    }

    /*通过id查询公交*/
    public static void searchLine_byId(Context context, String lineId, String cityName) {
        currentpage = 0;// 第一页默认从0开始
        busLineQuery = new BusLineQuery(lineId, BusLineQuery.SearchType.BY_LINE_ID,
                cityName);// 第一个参数表示公交线路名，第二个参数表示公交线路查询，第三个参数表示所在城市名或者城市区号
        busLineQuery.setPageSize(10);// 设置每页返回多少条数据
        busLineQuery.setPageNumber(currentpage);// 设置查询第几页，第一页从0开始算起
        busLineSearch = new com.amap.api.services.busline.BusLineSearch(context, busLineQuery);
        busLineSearch.searchBusLineAsyn();// 异步查询公交线路名称
    }
    public static void searchStation(Context context,String searchText,String cityName){
        currentpage = 0;
        String search = searchText.trim();
        BusStationQuery query = new BusStationQuery(search,cityName);
        query.setPageSize(10);
        query.setPageNumber(currentpage);
        BusStationSearch busStationSearch = new BusStationSearch(context,query);
        busStationSearch.setQuery(query);
        busStationSearch.searchBusStationAsyn();
    }

    public static BusLineQuery getQuery() {
        return busLineQuery;
    }

    public interface BusLineListener {
        void result(BusLineResult result, int code);
    }
    public interface BusStationListener{
        void result(BusStationResult result,int code);
    }


    public static void getBusLine(final BusLineListener listener) {
        busLineSearch.setOnBusLineSearchListener(new com.amap.api.services.busline.BusLineSearch.OnBusLineSearchListener() {
            @Override
            public void onBusLineSearched(BusLineResult busLineResult, int i) {
                listener.result(busLineResult, i);
            }
        });
    }
    public static void getBusStation(final BusStationListener listener){
        busStationSearch.setOnBusStationSearchListener(new BusStationSearch.OnBusStationSearchListener(){

            @Override
            public void onBusStationSearched(BusStationResult busStationResult, int i) {
                listener.result(busStationResult,i);
            }
        });
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog(Context context) {
        if (progDialog == null)
            progDialog = new ProgressDialog(context);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索:\n");
        progDialog.show();
    }



    /**
     * 隐藏进度框
     */
    public static void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }
    /**
     * BusLineDialog ListView 选项点击回调
     */
    /*public interface OnListItemlistener {
        public void onListItemClick(BusLineDialog dialog, BusLineItem item);
    }*/

    /**
     * 所有公交线路显示页面
     */
    /*public static class BusLineDialog extends Dialog implements View.OnClickListener {
        public BusLineDialog(@NonNull Context context) {
            super(context);
        }




        private List<BusLineItem> busLineItems;
        private BusLineAdapter busLineAdapter;
        private Button preButton, nextButton;
        private ListView listView;
        protected OnListItemlistener onListItemlistener;

        public BusLineDialog(Context context, int theme) {
            super(context, theme);
        }

        public void onListItemClicklistener(
                OnListItemlistener onListItemlistener) {
            this.onListItemlistener = onListItemlistener;

        }

        public BusLineDialog(Context context, List<BusLineItem> busLineItems) {
            this(context, android.R.style.Theme_NoTitleBar);
            this.busLineItems = busLineItems;
            busLineAdapter = new BusLineAdapter(context, busLineItems);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.busline_dialog);
            preButton = (Button) findViewById(R.id.preButton);
            nextButton = (Button) findViewById(R.id.nextButton);
            listView = (ListView) findViewById(R.id.listview);
            listView.setAdapter(busLineAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    onListItemlistener.onListItemClick(BusLineDialog.this,
                            busLineItems.get(arg2));
                    dismiss();

                }
            });
            preButton.setOnClickListener(this);
            nextButton.setOnClickListener(this);
            if (currentpage <= 0) {
                preButton.setEnabled(false);
            }
            *//*if (currentpage >= busLineResult.getPageCount() - 1) {
                nextButton.setEnabled(false);
            }*//*

        }

        @Override
        public void onClick(View v) {
            this.dismiss();
            if (v.equals(preButton)) {
                currentpage--;
            } else if (v.equals(nextButton)) {
                currentpage++;
            }

            //showProgressDialog();
            busLineQuery.setPageNumber(currentpage);// 设置公交查询第几页
            //busLineSearch.setOnBusLineSearchListener();
            busLineSearch.searchBusLineAsyn();// 异步查询公交线路名称
        }
    }*/


}
