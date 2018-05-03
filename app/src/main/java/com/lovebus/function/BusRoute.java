package com.lovebus.function;

import android.content.Context;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;

public class BusRoute {
    private static RouteSearch routeSearch;
    public static void searchRouteResult(Context context,LatLonPoint startPoint, LatLonPoint endPoint, String cityName){
        if (startPoint == null) {
            MyLog.d("BR","出发点为空");
        }
        if (endPoint == null) {
            MyLog.d("BR","结束点为空");
        }
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                startPoint, endPoint);
        RouteSearch.BusRouteQuery query = new RouteSearch.BusRouteQuery(fromAndTo, 0,
                cityName, 0);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
        routeSearch = new RouteSearch(context);
        routeSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
    }
    public interface RouteSearchListener{
        void result(BusRouteResult result, int errorCode);
    }
    public static void getBusRoute(final RouteSearchListener listener){
        routeSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
            @Override
            public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
                listener.result(busRouteResult, i);
            }

            @Override
            public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

            }

            @Override
            public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

            }

            @Override
            public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

            }
        });
    }
}
