package com.lovebus.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.lovebus.function.BusRouteOverlay;
import com.lovebus.function.LoveBusUtil;
import com.lovebus.function.MyLog;

import bus.android.com.lovebus.R;

public class BusRouteDetailActivity extends Activity implements OnMapLoadedListener,
        OnMapClickListener, InfoWindowAdapter, OnInfoWindowClickListener, OnMarkerClickListener {
	private AMap aMap;
	private MapView mapView;
	private BusPath mBuspath;
	private BusRouteResult mBusRouteResult;
	TextView mTitle, mTitleBusRoute, mDesBusRoute;
	ListView mBusSegmentList;
	BusSegmentListAdapter mBusSegmentListAdapter;
	private LinearLayout mBusMap, mBuspathview;
	private BusRouteOverlay mBusrouteOverlay;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_detail);
		mapView = (MapView) findViewById(R.id.route_map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		getIntentData();
		init();
	}

	private void getIntentData() {
		Intent intent = getIntent();
		if (intent != null) {
			mBuspath = intent.getParcelableExtra("bus_path");
			mBusRouteResult = intent.getParcelableExtra("bus_result");
		}
	}

	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();	
		}
		registerListener();
		
		mTitle = (TextView) findViewById(R.id.title_center);
		mTitle.setText("公交路线详情");
		mTitleBusRoute = (TextView) findViewById(R.id.firstline);
		mDesBusRoute = (TextView) findViewById(R.id.secondline);
		String dur = LoveBusUtil.getFriendlyTime((int) mBuspath.getDuration());
		String dis = LoveBusUtil.getFriendlyLength((int) mBuspath.getDistance());
		mTitleBusRoute.setText(dur + "(" + dis + ")");
		int taxiCost = (int) mBusRouteResult.getTaxiCost();
		mDesBusRoute.setText("打车约"+taxiCost+"元");
		mDesBusRoute.setVisibility(View.VISIBLE);
		mBusMap = (LinearLayout)findViewById(R.id.title_map);
		mBusMap.setVisibility(View.VISIBLE);
		mBuspathview = (LinearLayout)findViewById(R.id.bus_path);
		configureListView();
	}

	private void registerListener() {
		aMap.setOnMapLoadedListener(this);
		aMap.setOnMapClickListener(this);
		aMap.setOnMarkerClickListener(this);
		aMap.setOnInfoWindowClickListener(this);
		aMap.setInfoWindowAdapter(this);
	}

	private void configureListView() {
		mBusSegmentList = (ListView) findViewById(R.id.bus_segment_list);
		mBusSegmentListAdapter = new BusSegmentListAdapter(
				this.getApplicationContext(), mBuspath.getSteps());
		mBusSegmentList.setAdapter(mBusSegmentListAdapter);
		
	}
	
	public void onBackClick(View view) {
		if(mBuspathview.getVisibility()==View.VISIBLE){
			finish();
		}
		else {
			mBuspathview.setVisibility(View.VISIBLE);
			mBusMap.setVisibility(View.VISIBLE);
			mapView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onBackPressed() {
		if(mBuspathview.getVisibility()==View.VISIBLE){
			finish();
		}
		else {
			mBuspathview.setVisibility(View.VISIBLE);
			mBusMap.setVisibility(View.VISIBLE);
			mapView.setVisibility(View.GONE);
		}
	}

	
	public void onMapClick(View view) {
		mBuspathview.setVisibility(View.GONE);
		mBusMap.setVisibility(View.GONE);
		mapView.setVisibility(View.VISIBLE);
		aMap.clear();// 清理地图上的所有覆盖物
		mBusrouteOverlay = new BusRouteOverlay(this, aMap,
				mBuspath, mBusRouteResult.getStartPos(),
				mBusRouteResult.getTargetPos());
		mBusrouteOverlay.removeFromMap();

	}
	public void onCollClick(View view){

    }

	@Override
	public void onMapLoaded() {
		if (mBusrouteOverlay != null) {
			mBusrouteOverlay.addToMap();
			mBusrouteOverlay.zoomToSpan();
		}
	}

	@Override
	public void onMapClick(LatLng arg0) {
	}

	@Override
	public View getInfoContents(Marker arg0) {
		return null;
	}

	@Override
	public View getInfoWindow(Marker arg0) {

		return null;
	}

	@Override
	public void onInfoWindowClick(Marker arg0) {
		
	}

	@Override
	public boolean onMarkerClick(Marker arg0) {

		return false;
	}
	

}
