package com.lovebus.function;

import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;

public class Geocoder {
    private static GeocodeSearch geocoderSearch;
    public static void getLatlon(final String poiName,String cityName) {
        GeocodeQuery query = new GeocodeQuery(poiName, cityName);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
        geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
    }
    public interface GeocodeSearchListener{
        void result(GeocodeResult result, int rCode);
    }
    public static void getLatlonResult(final GeocodeSearchListener listener){
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
                listener.result(geocodeResult,i);
            }
        });
    }



}
