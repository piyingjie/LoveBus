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
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.BusStep;
import com.amap.api.services.route.Doorway;
import com.amap.api.services.route.RouteBusLineItem;
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

                        //Bus route initializes here !!!
                        // BusRouteResult <- BusPath <- BusStep
                        BusRouteResult busRouteResult = new BusRouteResult();

                        for (int i = 0; i < item.size(); i++)
                        {

                            BusStationItem stationItem = item.get(i);
                            List<BusLineItem> busLines = stationItem.getBusLineItems();
                            for (BusLineItem busLineItem :busLines)
                            {
                                List<BusStationItem> busStations_of_a_line = busLineItem.getBusStations();
                                BusStep step = new BusStep();  //One step of a Bus path

                                //Using Cursor to denote the current node in the List
                                int cursor = busStations_of_a_line.indexOf(stationItem);
                                List<RouteBusLineItem> routeBusLineItems = new ArrayList<>();
                                List<LatLonPoint> latLonSet = new ArrayList<>();
                                List<BusStationItem> passedStation = new ArrayList<>();
                                //initialize and setup entrance and exit for Metro only
                                //TO DO List: Check the type of the Transportation
                                Doorway entrance = new Doorway();
                                Doorway exit = new Doorway();
                                entrance.setLatLonPoint(busStations_of_a_line.get(cursor).getLatLonPoint());
                                entrance.setName(busStations_of_a_line.get(cursor).getBusStationName());
                                step.setEntrance(entrance);
                                for (int j = cursor ; j < busStations_of_a_line.size(); j++)
                                {
                                    BusStationItem target = busStations_of_a_line.get(j);

                                    if (target.getBusStationName().matches(destination))
                                    {
                                            RouteBusLineItem lineItem = (RouteBusLineItem)busLineItem;
                                            lineItem.setArrivalBusStation(target);
                                            lineItem.setDepartureBusStation(stationItem);
//                                            lineItem.setCityCode(target.getCityCode());
                                            lineItem.setPassStationNum(j - cursor + 1);
                                            lineItem.setPolyline(latLonSet);
                                            lineItem.setPassStations(passedStation);
//                                            lineItem.setBasicPrice(busLineItem.getBasicPrice());
//                                            lineItem.setBusLineId(busLineItem.getBusLineId());
//                                            lineItem.setFirstBusTime(busLineItem.getFirstBusTime());
//                                            lineItem.setLastBusTime(busLineItem.getLastBusTime());
//                                            lineItem.setBusLineName(busLineItem.getBusLineName());


                                            //add up BusLine information
                                            routeBusLineItems.add(lineItem);

                                            //set up exit parameters(just for metro)
//                                            exit.setLatLonPoint(target.getLatLonPoint());
//                                            exit.setName(target.getBusStationName());
//                                            step.setBusLines(routeBusLineItems);
//                                            step.setExit(exit);

                                            continue;
                                    }
                                    latLonSet.add(target.getLatLonPoint());
                                    passedStation.add(target);
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
