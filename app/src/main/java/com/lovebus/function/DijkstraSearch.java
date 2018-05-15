package com.lovebus.function;

import android.content.Context;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

public class DijkstraSearch {
    private PoiResult poiResult;
    private PoiSearch.Query query;
    public Context mContext;
    private LatLonPoint ln;
    public GeocodeSearch geocodeSearch;
    public DijkstraSearch(Context mContext,LatLonPoint ln){
        this.mContext = mContext;
        this.ln = ln;
    }

    public void print(){
        System.out.println("DijkstraSearchTest");
    }
    public void mapReferring(){
        geocodeSearch = new GeocodeSearch(mContext);
        RegeocodeQuery query1 = new RegeocodeQuery(ln,200,GeocodeSearch.AMAP);
        geocodeSearch.getFromLocationAsyn(query1);
    }
}
