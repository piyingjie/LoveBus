package com.lovebus.function;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.amap.api.services.busline.BusStationItem;

import java.util.List;

import bus.android.com.lovebus.R;

public class BusLineDetailDialog extends Dialog implements DialogInterface.OnClickListener, View.OnClickListener {
    private Button preButton, nextButton;
    private Button lineView;
    private ListView listView;
    public BusLineDetailDialog(@NonNull Context context) {
        super(context);
    }
    private List<BusStationItem> busStationItemList;
    private BusStationAdapter busStationAdapter;

    public BusLineDetailDialog(Context context, int theme) {
        super(context, theme);
    }

    public BusLineDetailDialog(Context context, List<BusStationItem> busStationItems) {
        this(context, android.R.style.Theme_NoTitleBar);
        this.busStationItemList = busStationItems;
        busStationAdapter = new BusStationAdapter(context, busStationItems);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.busline_detail_dialog);
        /*preButton = (Button) findViewById(R.id.preButton);
        nextButton = (Button) findViewById(R.id.nextButton);*/
        lineView = (Button)findViewById(R.id.lineView);
        listView = (ListView) findViewById(R.id.listview_detail);
        listView.setAdapter(busStationAdapter);
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                onListItemlistener.onListItemClick(BusLineDialog.this,busLineItems.get(arg2));
                //onListItemlistener.onListItemClick(BusLineSearch.BusLineDialog.this,
                //busLineItems.get(arg2));
                dismiss();

            }
        });*/
        lineView.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        /*preButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        if (currentpage <= 0) {
            preButton.setEnabled(false);
        }
        if (currentpage >= busLineResult.getPageCount() - 1) {
            nextButton.setEnabled(false);
        }*/

    }
    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    @Override
    public void onClick(View v) {

    }
}
