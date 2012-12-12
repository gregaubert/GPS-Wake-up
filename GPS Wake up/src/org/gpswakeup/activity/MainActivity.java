package org.gpswakeup.activity;


import java.util.List;

import org.gpswakeup.db.AlarmBD;
import org.gpswakeup.resources.Alarm;
import org.gpswakeup.resources.MapSearchView;
import org.gpswakeup.resources.OverlayManager;

import android.content.Context;
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

	private MapView mMapView;
	private MyLocationOverlay mMyLocation;
	private OverlayManager mOverlayManager;
	private AlarmBD mAlarmBD;
	private List<Alarm> mAlarmList;
	private ConnectivityManager mConnectivityManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);	// transparence
		setContentView(R.layout.activity_main);
		//getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_bg_gray));	// transparence

		mMapView = (MapView) findViewById(R.id.mapview);
		mMapView.setBuiltInZoomControls(true);
		
		mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		
		mOverlayManager = new OverlayManager(this, mMapView);
		OverlayManager.setInstance(mOverlayManager);

		mAlarmBD = new AlarmBD(this);
		mAlarmBD.open();
		
		mAlarmList = mAlarmBD.getAllAlarm();
		
		for(Alarm alarm : mAlarmList)
			mOverlayManager.addAlarm(alarm);
		
		mMyLocation = new MyLocationOverlay(getApplicationContext(), mMapView);
		mMyLocation.enableMyLocation();

		mMyLocation.runOnFirstFix(new Runnable() {
			public void run() {
				mMapView.getController().animateTo(mMyLocation.getMyLocation());
			}
		});
		
		mMapView.getOverlays().add(mMyLocation);
		mMapView.invalidate();
		//mapView.setLongClickable(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		
		menu.add(getString(R.string.menu_list))
	        .setIcon(R.drawable.ic_list)
	        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		
		//Create the search view
		SearchView searchView = new MapSearchView(this, getSupportActionBar().getThemedContext());
		
        searchView.setQueryHint(getString(R.string.recherche_hint));
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryRefinementEnabled(true);
        
        menu.add(getString(R.string.menu_search))
	        .setIcon(R.drawable.ic_search)
	        .setActionView(searchView)
	        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
	
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
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
}
