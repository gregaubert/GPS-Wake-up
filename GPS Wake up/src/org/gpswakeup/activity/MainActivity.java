package org.gpswakeup.activity;


import java.util.List;

import org.gpswakeup.db.AlarmBD;
import org.gpswakeup.resources.Alarm;
import org.gpswakeup.resources.OverlayManager;
import org.gpswakeup.resources.SearchListener;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import com.actionbarsherlock.view.Menu;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class MainActivity extends SherlockMapActivity {

	private MapView mMapView;
	private MyLocationOverlay mMyLocation;
	private EditText mTxtSearch;
	private ImageButton mBtnSearch;
	private OverlayManager mOverlayManager;
	private AlarmBD mAlarmBD;
	private List<Alarm> mAlarmList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mMapView = (MapView) findViewById(R.id.mapview);
		mMapView.setBuiltInZoomControls(true);

		mTxtSearch = (EditText) findViewById(R.id.txtSearch);
		mBtnSearch = (ImageButton) findViewById(R.id.btnSearch);
		mBtnSearch.setOnClickListener(new SearchListener(this, mTxtSearch));
		
		mOverlayManager = new OverlayManager(this, mMapView);
		OverlayManager.setInstance(mOverlayManager);

		mAlarmBD = new AlarmBD(this);
		mAlarmBD.open();
		
		mAlarmList = mAlarmBD.getAllAlarm();
		
		for(Alarm alarm : mAlarmList)
			mOverlayManager.addAlarm(alarm);
		
		mOverlayManager.addAlarm(new Alarm(1000, new GeoPoint(19240000,-99120000), 3000));
		
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
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
