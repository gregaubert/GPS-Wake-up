package org.gpswakeup.activity;


import java.util.ArrayList;
import java.util.List;

import org.gpswakeup.db.AlarmBD;
import org.gpswakeup.resources.Alarm;
import org.gpswakeup.resources.MapSearchView;
import org.gpswakeup.resources.OverlayManager;
import org.gpswakeup.views.LongpressMapView;
import org.gpswakeup.views.OnMapLongpressListener;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class MainActivity extends SherlockMapActivity {

	private final int MENU_LIST = 1;
	private final int MENU_SEARCH = 2;
	private static List<Alarm> mAlarmList = new ArrayList<Alarm>();
	private AlarmBD mAlarmBD;
	private LongpressMapView mMapView;
	private MyLocationOverlay mMyLocation;
	private OverlayManager mOverlayManager;
	private ConnectivityManager mConnectivityManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);	// transparence
		setContentView(R.layout.activity_main);
		//getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_bg_gray));	// transparence
		
		mMapView = (LongpressMapView) findViewById(R.id.mapview);
		mMapView.setBuiltInZoomControls(true);
		
		mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		
		mOverlayManager = new OverlayManager(this, mMapView);
		OverlayManager.setInstance(mOverlayManager);

		mAlarmBD = new AlarmBD(this);
		mAlarmBD.open();
		
		mAlarmList = mAlarmBD.getAllAlarm();
		
		mMapView.getController().setZoom(11);
		if(mAlarmList.size() > 0){
			mMapView.getController().animateTo(mAlarmList.get(0).getLocation());
		}
		
		for(Alarm alarm : mAlarmList)
			mOverlayManager.addAlarm(alarm);
		
		mAlarmBD.close();
		
		mMyLocation = new MyLocationOverlay(getApplicationContext(), mMapView);
		mMyLocation.enableMyLocation();

		mMyLocation.runOnFirstFix(new Runnable() {
			public void run() {
				mMapView.getController().animateTo(mMyLocation.getMyLocation());
			}
		});
		
		mMapView.getOverlays().add(mMyLocation);
		mMapView.invalidate();
		setMapLongPress();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		
		menu.add(Menu.NONE, MENU_LIST, Menu.NONE, getString(R.string.menu_list))
	        .setIcon(R.drawable.ic_list)
	        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		
		//Create the search view
		SearchView searchView = new MapSearchView(this, getSupportActionBar().getThemedContext());
		
        searchView.setQueryHint(getString(R.string.recherche_hint));
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryRefinementEnabled(true);
        
        menu.add(Menu.NONE, MENU_SEARCH, Menu.NONE, getString(R.string.menu_search))
	        .setIcon(R.drawable.ic_search)
	        .setActionView(searchView)
	        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
	
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()){
		case MENU_LIST:
			Intent intent = new Intent(this, WakeupListActivity.class);
			startActivity(intent);
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	private void setMapLongPress() {
		mMapView.setOnMapLongpressListener(new OnMapLongpressListener() {
			@Override
			public void onMapLongpress(final MapView view, final GeoPoint longpressLocation) {
				runOnUiThread(new Runnable() 
	            {
	                public void run() 
	                {
	                	mOverlayManager.addSearch(longpressLocation, "Nouveau point");
	                }
	            });	
			}
		});
	}
	
	public boolean isOnline() {
	    NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnected()) {
	        return true;
	    }
	    return false;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putAll(mOverlayManager.saveInstanceState());
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mOverlayManager.restoreInstanceState(savedInstanceState);
	}

	public static Alarm getAlarm(int index) {
		return mAlarmList.get(index);
	}
	
	public static int getAlarmIndex(Alarm alarm){
		return mAlarmList.indexOf(alarm);
	}
	
	public static void addAlarm(Alarm alarm) {
		mAlarmList.add(alarm);
		OverlayManager.getInstance().addAlarm(alarm);
	}
}
