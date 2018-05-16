package com.lovebus.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusLineQuery;
import com.amap.api.services.busline.BusLineResult;
import com.amap.api.services.busline.BusStationItem;
import com.amap.api.services.busline.BusStationResult;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.BusStep;
import com.amap.api.services.route.Doorway;
import com.amap.api.services.route.RouteBusLineItem;
import com.lovebus.function.BusLineOverlay;
import com.lovebus.function.BusLineSearch;
import com.lovebus.function.BusStationSearch;
import com.amap.api.services.core.AMapException;
import com.lovebus.function.MyLog;

import java.util.ArrayList;
import java.util.List;

import bus.android.com.lovebus.R;
import com.lovebus.function.CalculateUtil;

public class TestActivity extends AppCompatActivity {
    String destination = "武珞路街道口";
    String cityName = "武汉";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        BusStationSearch.searchStation(TestActivity.this, "珞瑜东路佳园路", cityName);
        BusStationSearch.getBusStation(new BusStationSearch.BusStationListener()
        {
            @Override
            public void result(BusStationResult result, int code)
            {
                if (code == AMapException.CODE_AMAP_SUCCESS)
                {
                    if (result != null && result.getPageCount() > 0
                            && result.getBusStations() != null
                            && result.getBusStations().size() > 0)
                    {
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
                            List<BusPath> pathPlans = new ArrayList<>();

                            for ( final BusLineItem busLineItem :busLines)
                            {
                                BusPath pathPlan = new BusPath();
                                BusLineSearch.searchLine_byId(TestActivity.this,busLineItem.getBusLineId(),cityName);
                                BusLineSearch.getBusLine(new BusLineSearch.BusLineListener() {
                                    @Override
                                    public void result(BusLineResult result, int code) {
                                        if (code == AMapException.CODE_AMAP_SUCCESS){
                                            if (result!=null&&result.getQuery()!=null
                                                    &&result.getQuery().equals(com.lovebus.function.BusLineSearch.getQuery())){
                                                if(result.getQuery().getCategory()== BusLineQuery.SearchType.BY_LINE_ID){
                                                    BusLineResult busLineResult = result;
                                                    List<BusLineItem> lineItems= busLineResult.getBusLines();
                                                    BusLineItem temp =  lineItems.get(0);
                                                    busLineItem.setBusStations(temp.getBusStations());
                                                    busLineItem.setBasicPrice(temp.getBasicPrice());
                                                    busLineItem.setBounds(temp.getBounds());
                                                    busLineItem.setBusCompany(temp.getBusCompany());
                                                    busLineItem.setBusLineId(temp.getBusLineId());

                                                    }
                                                }
                                            }
                                }
                                });
                                float tempCost = 0;
                                List<BusStationItem> busStations_of_a_line = busLineItem.getBusStations();
                                BusStep step = new BusStep();  //One step of a Bus path

                                //Using Cursor to denote the current node in the List
                                Integer cursor = busStations_of_a_line.indexOf(stationItem);
                                MyLog.d("CURSOR",cursor.toString() );
                                List<RouteBusLineItem> routeBusLineItems = new ArrayList<>();
                                List<LatLonPoint> latLonSet = new ArrayList<>();
                                List<BusStationItem> passedStation = new ArrayList<>();

                                //initialize and setup entrance and exit for Metro only
                                String  transType = busLineItem.getBusLineType();
                                Doorway exit = new Doorway();
                                if ( transType.matches("地铁") || transType.matches("轻轨"))
                                {
                                    MyLog.d("ENTRY","Entered 地铁判别");
                                    Doorway entrance = new Doorway();
                                    entrance.setLatLonPoint(busStations_of_a_line.get(cursor).getLatLonPoint());
                                    entrance.setName(busStations_of_a_line.get(cursor).getBusStationName());
                                    step.setEntrance(entrance);
                                }

                                //start matching destination
                                for (int j = cursor ; j < busStations_of_a_line.size(); j++)
                                {
                                    BusStationItem target = busStations_of_a_line.get(j);

                                    if (target.getBusStationName().matches(destination))
                                    {
                                            MyLog.d("ENTRY","Entered matches destination");
                                            RouteBusLineItem lineItem = (RouteBusLineItem)busLineItem;
                                            lineItem.setArrivalBusStation(target);
                                            lineItem.setDepartureBusStation(stationItem);
//                                            lineItem.setCityCode(target.getCityCode());
                                            lineItem.setPassStationNum(j - cursor + 1);
                                            lineItem.setPolyline(latLonSet);
                                            lineItem.setPassStations(passedStation);
                                            tempCost = lineItem.getBasicPrice();
//                                            lineItem.setBasicPrice(busLineItem.getBasicPrice());
//                                            lineItem.setBusLineId(busLineItem.getBusLineId());
//                                            lineItem.setFirstBusTime(busLineItem.getFirstBusTime());
//                                            lineItem.setLastBusTime(busLineItem.getLastBusTime());
//                                            lineItem.setBusLineName(busLineItem.getBusLineName());

                                            routeBusLineItems.add(lineItem);
                                            step.setBusLines(routeBusLineItems);
                                            //add up BusLine information
                                        if ( transType.matches("地铁") || transType.matches("轻轨"))
                                        {
                                            //set up exit parameters(just for metro)
                                            exit.setLatLonPoint(target.getLatLonPoint());
                                            exit.setName(target.getBusStationName());
                                            step.setExit(exit);
                                        }
                                            continue;
                                    }
                                    latLonSet.add(target.getLatLonPoint());
                                    passedStation.add(target);
                                }
                                MyLog.d("EXIT","exited matching inside a line");

                                //setup parameters for pathPlan
                                List<BusStep> temp_of_Steps = pathPlan.getSteps();
                                temp_of_Steps.add(step);
                                pathPlan.setSteps(temp_of_Steps);
                                pathPlan.setCost(tempCost);
                                Float dist = CalculateUtil.calculateDistance_of_steps(temp_of_Steps);
                                pathPlan.setBusDistance(dist);

                                //setup pathPlan for pathPlans
                                pathPlans.add(pathPlan);
                                MyLog.d("LINE","LineName: " + busLineItem.getBusLineName()
                                        + "Step1 : " + step.getBusLines().get(0).getArrivalBusStation().getBusStationName()  );

                            }
                            busRouteResult.setPaths(pathPlans);

                            MyLog.d("Target Plan","planA : " + pathPlans.get(0).getSteps().get(0)
                                    .getBusLines().get(0).getArrivalBusStation().getBusStationName() + "\n ByLineNo : "
                                    + pathPlans.get(0).getSteps().get(0).getBusLines().get(0).getBusLineName() );

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
