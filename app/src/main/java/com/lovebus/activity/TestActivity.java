package com.lovebus.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusLineResult;
import com.amap.api.services.busline.BusStationItem;
import com.amap.api.services.busline.BusStationResult;
import com.lovebus.function.BusLineSearch;
import com.lovebus.function.BusStationSearch;
import com.amap.api.services.core.AMapException;
import com.lovebus.function.MyLog;

import java.util.ArrayList;
import java.util.List;

import bus.android.com.lovebus.R;

public class TestActivity extends AppCompatActivity {
    String destination = "武珞路街道口";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        BusStationSearch.searchStation(TestActivity.this, "珞瑜东路佳园路", "武汉");
        BusStationSearch.getBusStation(new BusStationSearch.BusStationListener()
        {
            @Override
            public void result(BusStationResult result, int code)
            {
                if (code == AMapException.CODE_AMAP_SUCCESS)
                {
                    if (result != null && result.getPageCount() > 0
                            && result.getBusStations() != null
                            && result.getBusStations().size() > 0) {
                        ArrayList<BusStationItem> item = (ArrayList<BusStationItem>) result
                                .getBusStations();
                        StringBuffer buf = new StringBuffer();
                        for (int i = 0; i < item.size(); i++) {
                            BusStationItem stationItem = item.get(i);
                            List<BusLineItem> busLines = stationItem.getBusLineItems();
                            for (BusLineItem busLineItem :busLines)
                            {
                                List<BusStationItem> busStations_of_a_line = busLineItem.getBusStations();
                                for (BusStationItem target: busStations_of_a_line)
                                {
                                        if (target.getBusStationName().matches(destination))
                                        {

                                            break;
                                        }
                                }

                            }
                            buf.append(" station: ").append(i).append(" name: ").append("id: ")
                                    .append(stationItem.getBusStationId()).append(stationItem.getBusStationName());
                            MyLog.d("LineDesignTest", "stationName:"
                                    + stationItem.getBusStationName() + "Id :" + stationItem.getBusStationId() +" stationpos:"
                                    + stationItem.getLatLonPoint().toString());
                            String text = buf.toString();
                            Toast.makeText(TestActivity.this, text,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }
}
