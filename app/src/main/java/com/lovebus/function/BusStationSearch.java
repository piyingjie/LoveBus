package com.lovebus.function;

import android.content.Context;

import com.amap.api.services.busline.BusStationQuery;
import com.amap.api.services.busline.BusStationResult;

public class BusStationSearch {
    private static int currentpage;
    public static BusStationQuery busStationQuery;
    public static com.amap.api.services.busline.BusStationSearch busStationSearch;

    public static void searchStation(Context context, String searchText, String cityName){
        currentpage = 0;
        String search = searchText.trim();
        busStationQuery = new BusStationQuery(search,cityName);
        busStationQuery.setPageSize(10);
        busStationQuery.setPageNumber(currentpage);
        busStationSearch = new com.amap.api.services.busline.BusStationSearch(context,busStationQuery);


    }
    public interface BusStationListener{
        void result(BusStationResult result, int code);
    }
    public static void getBusStation(final BusStationListener listener){
        busStationSearch.setOnBusStationSearchListener(new com.amap.api.services.busline.BusStationSearch.OnBusStationSearchListener(){

            @Override
            public void onBusStationSearched(BusStationResult busStationResult, int i) {
                listener.result(busStationResult,i);
            }
        });
        busStationSearch.searchBusStationAsyn();
    }
}
