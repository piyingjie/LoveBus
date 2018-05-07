package com.lovebus.function;

import com.amap.api.services.busline.BusLineItem;

/**
 * BusLineDialog ListView 选项点击回调
 */
public interface OnListItemlistener {
    public void onListItemClick(BusLineDialog dialog, BusLineItem item);
}
