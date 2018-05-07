package com.lovebus.function;

import android.content.Context;

import com.amap.api.services.busline.BusLineQuery;
import com.amap.api.services.busline.BusLineResult;

public class BusLineSearch {
    private static int currentpage;
    private static BusLineQuery busLineQuery;
    private static com.amap.api.services.busline.BusLineSearch busLineSearch;// 公交线路列表查询

    /*通过名字查询公交*/
    public static void searchLine_byName(Context context,String searchText, String cityName) {
        currentpage = 0;// 第一页默认从0开始
        busLineQuery = new BusLineQuery(searchText, BusLineQuery.SearchType.BY_LINE_NAME,
                cityName);// 第一个参数表示公交线路名，第二个参数表示公交线路查询，第三个参数表示所在城市名或者城市区号
        busLineQuery.setPageSize(10);// 设置每页返回多少条数据
        busLineQuery.setPageNumber(currentpage);// 设置查询第几页，第一页从0开始算起
        busLineSearch = new com.amap.api.services.busline.BusLineSearch(context,busLineQuery);
    }

    /*通过id查询公交*/
    public static void searchLine_byId(Context context,String lineId, String cityName) {
        currentpage = 0;// 第一页默认从0开始
        busLineQuery = new BusLineQuery(lineId, BusLineQuery.SearchType.BY_LINE_ID,
                cityName);// 第一个参数表示公交线路名，第二个参数表示公交线路查询，第三个参数表示所在城市名或者城市区号
        busLineQuery.setPageSize(10);// 设置每页返回多少条数据
        busLineQuery.setPageNumber(currentpage);// 设置查询第几页，第一页从0开始算起
        busLineSearch = new com.amap.api.services.busline.BusLineSearch(context,busLineQuery);
        busLineSearch.searchBusLineAsyn();// 异步查询公交线路名称
    }
    public interface BusLineListener{
        void result(BusLineResult result,int code );
    }
    public static void getBusLine(final BusLineListener listener){
        busLineSearch.setOnBusLineSearchListener(new com.amap.api.services.busline.BusLineSearch.OnBusLineSearchListener() {
            @Override
            public void onBusLineSearched(BusLineResult busLineResult, int i) {
                listener.result(busLineResult,i);
            }
        });
        busLineSearch.searchBusLineAsyn();// 异步查询公交线路名称
    }
}
