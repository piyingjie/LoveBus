package com.lovebus.function;

import com.amap.api.services.route.BusStep;
import com.amap.api.services.route.RouteBusLineItem;

import java.util.List;

public  class CalculateUtil {
    public static float calculateDistance_of_steps(List<BusStep> steps)
    {
        float dist =0;
        for (BusStep step:steps)
        {
            List<RouteBusLineItem> temp = step.getBusLines();
            if (temp.size() > 1)
            {
                for (int i = 0; i < temp.size(); i++) {
                    dist += temp.get(i).getDistance();
                }
            }
            else if (temp.size() == 1){
                dist += temp.get(0).getDistance();
            }
        }
        return dist;
    }

    public  CalculateUtil(){

    }
}
