package com.lovebus.function;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.amap.api.services.busline.BusLineItem;

import java.util.List;

import bus.android.com.lovebus.R;

import static com.lovebus.activity.Main_Activity.busLineResult;
import static com.lovebus.function.BusLineSearch.busLineQuery;
import static com.lovebus.function.BusLineSearch.busLineSearch;

public class BusLineDialog extends Dialog implements View.OnClickListener{
    private List<BusLineItem> busLineItems;
    private BusLineAdapter busLineAdapter;
    private Button preButton, nextButton;
    private ListView listView;
    private static int currentpage;
    protected OnListItemlistener onListItemlistener;

    public BusLineDialog(Context context, int theme) {
        super(context, theme);
    }

    public void onListItemClicklistener(
            OnListItemlistener onListItemlistener) {
        this.onListItemlistener = (OnListItemlistener) onListItemlistener;

    }

    public BusLineDialog(Context context, List<BusLineItem> busLineItems) {
        this(context, android.R.style.Theme_NoTitleBar);
        this.busLineItems = busLineItems;
        busLineAdapter = new BusLineAdapter(context, busLineItems);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.busline_dialog);
        preButton = (Button) findViewById(R.id.preButton);
        nextButton = (Button) findViewById(R.id.nextButton);
        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(busLineAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                onListItemlistener.onListItemClick(BusLineDialog.this,busLineItems.get(arg2));
                //onListItemlistener.onListItemClick(BusLineSearch.BusLineDialog.this,
                        //busLineItems.get(arg2));
                dismiss();

            }
        });
        preButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        if (currentpage <= 0) {
            preButton.setEnabled(false);
        }
            if (currentpage >= busLineResult.getPageCount() - 1) {
                nextButton.setEnabled(false);
            }

    }

    @Override
    public void onClick(View v) {
        this.dismiss();
        if (v.equals(preButton)) {
            currentpage--;
        } else if (v.equals(nextButton)) {
            currentpage++;
        }

        //showProgressDialog();
        busLineQuery.setPageNumber(currentpage);// 设置公交查询第几页
        //busLineSearch.setOnBusLineSearchListener();
        busLineSearch.searchBusLineAsyn();// 异步查询公交线路名称
    }
    public interface OnListItemlistener {
        void onListItemClick(BusLineDialog dialog, BusLineItem item);
    }
}


