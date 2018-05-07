package com.lovebus.function;

import android.content.Context;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;

public class PoiSearch {
    private static int currentPage = 0;// 当前页面，从0开始计数
    private static com.amap.api.services.poisearch.PoiSearch.Query query;// Poi查询条件类
    private static com.amap.api.services.poisearch.PoiSearch poiSearch;// POI搜索
    /* poi搜索*/
    public static void doSearchQuery(Context context,String keyWord,String localCity) {
        query = new com.amap.api.services.poisearch.PoiSearch.Query(keyWord, "", localCity);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(1);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页
        query.setCityLimit(true);
        poiSearch = new com.amap.api.services.poisearch.PoiSearch(context, query);
        poiSearch.searchPOIAsyn();
    }
    public interface PoiSearchListener{
        void result(PoiResult result, int rCode);
        void item(PoiItem item, int rCode);
    }
    public static void getPoiSearch(final PoiSearchListener listener){
        poiSearch.setOnPoiSearchListener(new com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int i) {
                listener.result(poiResult, i);
            }

            @Override
            public void onPoiItemSearched(PoiItem poiItem, int i) {
                listener.item(poiItem, i);
            }
        });
    }
    public static com.amap.api.services.poisearch.PoiSearch.Query getQuery(){
        return query;
    }

    public static void BusStationNear(LatLonPoint lp, Context context, int boundary, String localCity) {
        query = new com.amap.api.services.poisearch.PoiSearch.Query("公交站", "1507", localCity);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(0);// 设置查第一页
        poiSearch = new com.amap.api.services.poisearch.PoiSearch(context, query);
        poiSearch.setBound(new com.amap.api.services.poisearch.PoiSearch.SearchBound(lp, boundary, true));//
        poiSearch.searchPOIAsyn();// 异步搜索
    }
}
