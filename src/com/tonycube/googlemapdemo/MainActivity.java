package com.tonycube.googlemapdemo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Google Map and GPS Demo
 * @author Tony
 * @date 2013/6/7
 * @version 1.1
 *
 */
public class MainActivity extends Activity {
	private final String TAG = "=== Map Demo ==>";
	
	/**台北101*/
	final LatLng TAIPEI101 = new LatLng(25.033611, 121.565000);
	/**台北火車站*/
	final LatLng TAIPEI_TRAIN_STATION = new LatLng(25.047924, 121.517081);
	/**國立台灣博物館*/
	final LatLng NATIONAL_TAIWAN_MUSEUM = new LatLng(25.042902, 121.515030);
	/**墾丁*/
	final LatLng KENTING = new LatLng(21.946567, 120.798713);
	/**日月潭*/
	final LatLng ZINTUN = new LatLng(23.851676, 120.902008);
	
	/** Map */
	private GoogleMap mMap;
	private TextView txtOutput;
	private Marker markerMe;

	/** 記錄軌跡 */
	private ArrayList<LatLng> traceOfMe;

	/** GPS */
	private LocationManager locationMgr;
	private String provider;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}
	
	
	@Override
	protected void onStart() {
		super.onStart();
		
		initView();
		initMap();
		if (initLocationProvider()) {
			whereAmI();
		}else{
			txtOutput.setText("請開啟定位！");
		}
	}


	@Override
	protected void onStop() {
		locationMgr.removeUpdates(locationListener);
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		initMap();
		drawPolyline();
	}
	
	private void initView(){
		txtOutput = (TextView) findViewById(R.id.txtOutput);
	}

	
	/************************************************
	 * 
	 * 						Map部份
	 * 
	 ***********************************************/
	/**
	 * Map初始化
	 * 建立3個標記
	 */
	private void initMap(){
		if (mMap == null) {
			mMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
			
			if (mMap != null) {
				//設定地圖類型
				mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				
				//Marker1
				MarkerOptions markerOpt = new MarkerOptions();
				markerOpt.position(TAIPEI101);
				markerOpt.title("台北101");
				markerOpt.snippet("於1999年動工，2004年12月31日完工啟用，樓高509.2公尺。");
				markerOpt.draggable(false);
				markerOpt.visible(true);
				markerOpt.anchor(0.5f, 0.5f);//設為圖片中心
				markerOpt.icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_mylocation));
				
				mMap.addMarker(markerOpt);
				
				//Marker2
				MarkerOptions markerOpt2 = new MarkerOptions();
				markerOpt2.position(TAIPEI_TRAIN_STATION);
				markerOpt2.title("台北火車站");
				
				mMap.addMarker(markerOpt2);
				
				//Marker3
				MarkerOptions markerOpt3 = new MarkerOptions();
				markerOpt3.position(NATIONAL_TAIWAN_MUSEUM);
				markerOpt3.title("國立台灣博物館");
				markerOpt3.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
				
				mMap.addMarker(markerOpt3);
			}
		}
		
	}
	
	/**
	 * 畫線
	 */
	private void drawPolyline(){
		PolylineOptions polylineOpt = new PolylineOptions();
		polylineOpt.add(new LatLng(25.033611, 121.565000));
		polylineOpt.add(new LatLng(25.032728, 121.565137));
		polylineOpt.add(new LatLng(25.033739, 121.527886));
		polylineOpt.add(new LatLng(25.038716, 121.517758));
		polylineOpt.add(new LatLng(25.045656, 121.519636));
		polylineOpt.add(new LatLng(25.046200, 121.517533));
		
		polylineOpt.color(Color.BLUE);
		
		Polyline polyline = mMap.addPolyline(polylineOpt);
		polyline.setWidth(10);
	}
	
	/**
	 * 按鈕:移動攝影機到墾丁
	 * @param v
	 */
	public void moveOnClick(View v){
		//move camera
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(KENTING, 15));
	}
	
	/**
	 * 按鈕:放大地圖
	 * @param v
	 */
	public void zoomInOnClick(View v){
		//zoom in
		mMap.animateCamera(CameraUpdateFactory.zoomIn());
	}
	
	/**
	 * 按鈕:縮小地圖
	 * @param v
	 */
	public void zoomToOnClick(View v){
		//zoom to level 10, animating with a duration of 3 seconds
		mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 3000, null);
	}

	/**
	 * 按鈕:攝影機移動到日月潭
	 * @param v
	 */
	public void animToOnClick(View v){
		CameraPosition cameraPosition = new CameraPosition.Builder()
	    .target(ZINTUN)      		// Sets the center of the map to ZINTUN
	    .zoom(13)                   // Sets the zoom
	    .bearing(90)                // Sets the orientation of the camera to east
	    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
	    .build();                   // Creates a CameraPosition from the builder
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}
	
	/************************************************
	 * 
	 * 						GPS部份
	 * 
	 ***********************************************/
	/**
	 * GPS初始化，取得可用的位置提供器
	 * @return
	 */
	private boolean initLocationProvider() {
		locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		//1.選擇最佳提供器
//		Criteria criteria = new Criteria();
//		criteria.setAccuracy(Criteria.ACCURACY_FINE);
//		criteria.setAltitudeRequired(false);
//		criteria.setBearingRequired(false);
//		criteria.setCostAllowed(true);
//		criteria.setPowerRequirement(Criteria.POWER_LOW);
//		
//		provider = locationMgr.getBestProvider(criteria, true);
//		
//		if (provider != null) {
//			return true;
//		}
		
		
		
		//2.選擇使用GPS提供器
		if (locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			provider = LocationManager.GPS_PROVIDER;
			return true;
		}
		
		
		
		//3.選擇使用網路提供器
//		if (locationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//			provider = LocationManager.NETWORK_PROVIDER;
//			return true;
//		}
		
		return false;
	}
	
	/**
	 * 執行"我"在哪裡
	 * 1.建立位置改變偵聽器
	 * 2.預先顯示上次的已知位置
	 */
	private void whereAmI(){
//		String provider = LocationManager.GPS_PROVIDER;
		
		//取得上次已知的位置
		Location location = locationMgr.getLastKnownLocation(provider);
		updateWithNewLocation(location);
		
		//GPS Listener
		locationMgr.addGpsStatusListener(gpsListener);
		
		
		//Location Listener
		long minTime = 5000;//ms
		float minDist = 5.0f;//meter
		locationMgr.requestLocationUpdates(provider, minTime, minDist, locationListener);
	}
	
	/**
	 * 顯示"我"在哪裡
	 * @param lat
	 * @param lng
	 */
	private void showMarkerMe(double lat, double lng){
		if (markerMe != null) {
			markerMe.remove();
		}
		
		MarkerOptions markerOpt = new MarkerOptions();
		markerOpt.position(new LatLng(lat, lng));
		markerOpt.title("我在這裡");
		markerMe = mMap.addMarker(markerOpt);
		
		Toast.makeText(this, "lat:" + lat + ",lng:" + lng, Toast.LENGTH_SHORT).show();
	}
	
	private void cameraFocusOnMe(double lat, double lng){
		CameraPosition camPosition = new CameraPosition.Builder()
										.target(new LatLng(lat, lng))
										.zoom(16)
										.build();
		
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition));
	}
	
	private void trackToMe(double lat, double lng){
		if (traceOfMe == null) {
			traceOfMe = new ArrayList<LatLng>();
		}
		traceOfMe.add(new LatLng(lat, lng));
		
		PolylineOptions polylineOpt = new PolylineOptions();
		for (LatLng latlng : traceOfMe) {
			polylineOpt.add(latlng);
		}
		
		polylineOpt.color(Color.RED);
		
		Polyline line = mMap.addPolyline(polylineOpt);
		line.setWidth(10);
	}
	
	/**
	 * 更新並顯示新位置
	 * @param location
	 */
	private void updateWithNewLocation(Location location) {
		String where = "";
		if (location != null) {
			//經度
			double lng = location.getLongitude();
			//緯度
			double lat = location.getLatitude();
			//速度
			float speed = location.getSpeed();
			//時間
			long time = location.getTime();
			String timeString = getTimeString(time);
			
			where = "經度: " + lng + 
					"\n緯度: " + lat + 
					"\n速度: " + speed + 
					"\n時間: " + timeString +
					"\nProvider: " + provider;
			
			//標記"我"
			showMarkerMe(lat, lng);
			cameraFocusOnMe(lat, lng);
			trackToMe(lat, lng);
			
			//移動攝影機跟著"我"
//			CameraPosition cameraPosition = new CameraPosition.Builder()
//		    .target(new LatLng(lat, lng))      		// Sets the center of the map to ZINTUN
//		    .zoom(13)                   // Sets the zoom
//		    .bearing(90)                // Sets the orientation of the camera to east
//		    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
//		    .build();                   // Creates a CameraPosition from the builder
//			mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			
//			CameraPosition camPosition = new CameraPosition.Builder()
//											.target(new LatLng(lat, lng))
//											.zoom(16)
//											.build();
//
//			mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition));
			
		}else{
			where = "No location found.";
		}
		
		//位置改變顯示
		txtOutput.setText(where);
	}
	
	
	GpsStatus.Listener gpsListener = new GpsStatus.Listener() {
		
		@Override
		public void onGpsStatusChanged(int event) {
			switch (event) {
	        case GpsStatus.GPS_EVENT_STARTED:
	        	Log.d(TAG, "GPS_EVENT_STARTED");
	        	Toast.makeText(MainActivity.this, "GPS_EVENT_STARTED", Toast.LENGTH_SHORT).show();
	            break;

	        case GpsStatus.GPS_EVENT_STOPPED:
	        	Log.d(TAG, "GPS_EVENT_STOPPED");
	        	Toast.makeText(MainActivity.this, "GPS_EVENT_STOPPED", Toast.LENGTH_SHORT).show();
	            break;

	        case GpsStatus.GPS_EVENT_FIRST_FIX:
	        	Log.d(TAG, "GPS_EVENT_FIRST_FIX");
	        	Toast.makeText(MainActivity.this, "GPS_EVENT_FIRST_FIX", Toast.LENGTH_SHORT).show();
	            break;

	        case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
	        	Log.d(TAG, "GPS_EVENT_SATELLITE_STATUS");
	            break;
			}
		}
	};
	
	
	LocationListener locationListener = new LocationListener(){

		@Override
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
			updateWithNewLocation(null);
		}

		@Override
		public void onProviderEnabled(String provider) {
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			switch (status) {
		    case LocationProvider.OUT_OF_SERVICE:
		        Log.v(TAG, "Status Changed: Out of Service");
		        Toast.makeText(MainActivity.this, "Status Changed: Out of Service",
		                Toast.LENGTH_SHORT).show();
		        break;
		    case LocationProvider.TEMPORARILY_UNAVAILABLE:
		        Log.v(TAG, "Status Changed: Temporarily Unavailable");
		        Toast.makeText(MainActivity.this, "Status Changed: Temporarily Unavailable",
		                Toast.LENGTH_SHORT).show();
		        break;
		    case LocationProvider.AVAILABLE:
		        Log.v(TAG, "Status Changed: Available");
		        Toast.makeText(MainActivity.this, "Status Changed: Available",
		                Toast.LENGTH_SHORT).show();
		        break;
		    }
		}
		
	};
	
	private String getTimeString(long timeInMilliseconds){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(timeInMilliseconds);
	}
	
	
//	private boolean checkGooglePlayServices(){
//		int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
//		switch (result) {
//			case ConnectionResult.SUCCESS:
//				Log.d(TAG, "SUCCESS");
//				return true;
//	
//			case ConnectionResult.SERVICE_INVALID:
//				Log.d(TAG, "SERVICE_INVALID");
//				GooglePlayServicesUtil.getErrorDialog(ConnectionResult.SERVICE_INVALID, this, 0).show();
//				break;
//				
//			case ConnectionResult.SERVICE_MISSING:
//				Log.d(TAG, "SERVICE_MISSING");
//				GooglePlayServicesUtil.getErrorDialog(ConnectionResult.SERVICE_MISSING, this, 0).show();
//				break;
//				
//			case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
//				Log.d(TAG, "SERVICE_VERSION_UPDATE_REQUIRED");
//				GooglePlayServicesUtil.getErrorDialog(ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED, this, 0).show();
//				break;
//				
//			case ConnectionResult.SERVICE_DISABLED:
//				Log.d(TAG, "SERVICE_DISABLED");
//				GooglePlayServicesUtil.getErrorDialog(ConnectionResult.SERVICE_DISABLED, this, 0).show();
//				break;
//		}
//		
//		return false;
//	}
}
