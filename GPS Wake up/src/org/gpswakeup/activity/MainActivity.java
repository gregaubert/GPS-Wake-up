package org.gpswakeup.activity;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.gpswakeup.db.AlarmBD;
import org.gpswakeup.resources.Alarm;
import org.gpswakeup.resources.MapSearchView;
import org.gpswakeup.resources.OverlayManager;
import org.gpswakeup.resources.Utility;
import org.gpswakeup.views.LongpressMapView;
import org.gpswakeup.views.OnMapLongpressListener;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.HandlerThread;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

/**
 * Main activity, showing the map and the alarms location.
 * Last update : 16.01.2013
 * @author Gregoire Aubert
 */
public class MainActivity extends SherlockMapActivity {

	// CONSTANTS
	private final int MENU_LIST = 0;
	private final int MENU_GPS = 1;
	private final int MENU_SEARCH = 2;
	private final int MENU_HELP = 3;
	private final long UPDATE_MIN_TIME = 5 * 1000;//2 * 60 * 1000;
	private final float UPDATE_MIN_DISTANCE = 100;
	private final int NOTIFICATION_ID = 8776445;
	
	// MEMBERS
	private SparseBooleanArray mAlreadyRingID;
	private AlarmBD mAlarmBD;
	private LongpressMapView mMapView;
	private MyLocationOverlay mMyLocation;
	private OverlayManager mOverlayManager;
	private ConnectivityManager mConnectivityManager;
	private LocationManager mLocManager;
	private LocationListener mLocationListener;
	private NotificationManager mNotificationManager;
	private AudioManager mAudioManager;
	private Criteria mCriteria;
	private HandlerThread mLocationListenerThread;
	private String mBestLocationProvider = "";
	private MediaPlayer mMediaPlayer;
	private Menu mMenu;
	
	// STATIC MEMBERS
	private static List<Alarm> mAlarmList = new CopyOnWriteArrayList<Alarm>();
	private static MainActivity mInstance = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Assign the longpressable mapview and activate the zoom
		mMapView = (LongpressMapView) findViewById(R.id.mapview);
		mMapView.setBuiltInZoomControls(true);
		mMapView.getController().setZoom(11);
		
		// Retrieve some manager needed in the application
		mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		mLocManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mAudioManager = (AudioManager) getApplication().getApplicationContext().getSystemService(AUDIO_SERVICE);
		
		// The instance of the overlay manager is set here because some elements
		// of the main activity are needed to initialize it
		mOverlayManager = new OverlayManager(this, mMapView);
		OverlayManager.setInstance(mOverlayManager);

		// This array is to avoid ringing when we move in an alarm zone after we entered it
		mAlreadyRingID = new SparseBooleanArray();
		
		mAlarmBD = new AlarmBD(this);
		mAlarmBD.open();
		mAlarmList = mAlarmBD.getAllAlarm();
		mAlarmBD.close();
		
		if(mAlarmList.size() > 0){
			mMapView.getController().animateTo(mAlarmList.get(0).getLocation());
		}
		
		// Add the alarm to the overlay manager so they are displayed on the map
		for(Alarm alarm : mAlarmList)
			mOverlayManager.addAlarm(alarm);
		
		// Show the user location on the map
		mMyLocation = new MyLocationOverlay(getApplicationContext(), mMapView);
		mMyLocation.enableMyLocation();

		mMyLocation.runOnFirstFix(new Runnable() {
			public void run() {
				mMapView.getController().animateTo(mMyLocation.getMyLocation());
			}
		});
		
		mMapView.getOverlays().add(mMyLocation);
		mMapView.invalidate();
		
		mInstance = this;
		
		setMapLongPress();		
		initLocationListener();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		mMenu = menu;
		
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		
		int icon_gps = R.drawable.ic_location_off,
		    txt_gps = R.string.menu_gps_disable;
		if(mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			icon_gps = R.drawable.ic_location_on;
			txt_gps = R.string.menu_gps_enable;
		}
		
		mMenu.add(Menu.NONE, MENU_LIST, MENU_LIST, R.string.menu_list)
	        .setIcon(R.drawable.ic_list)
	        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		
        mMenu.add(Menu.NONE, MENU_GPS, MENU_GPS, txt_gps)
	        .setIcon(icon_gps)
	        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	
		SearchView searchView = new MapSearchView(this, getSupportActionBar().getThemedContext());
        searchView.setQueryHint(getString(R.string.recherche_hint));
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryRefinementEnabled(true);
        
        mMenu.add(Menu.NONE, MENU_SEARCH, MENU_SEARCH, R.string.menu_search)
	        .setIcon(R.drawable.ic_search)
	        .setActionView(searchView)
	        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        
        mMenu.add(Menu.NONE, MENU_HELP, MENU_HELP, R.string.menu_help)
        	.setIcon(android.R.drawable.ic_menu_help)
        	.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()){
		case MENU_LIST:
			Intent intent = new Intent(this, WakeupListActivity.class);
			startActivity(intent);
			break;
		case MENU_GPS:
			Intent gpsSettingsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);   
			startActivity(gpsSettingsIntent);
			break;
		case MENU_HELP:
			showHelpDialog();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// We do not show driving information on the map
		return false;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		// Save the alarm already notified
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		for(int i = 0; i < mAlreadyRingID.size(); i++){
			if(mAlreadyRingID.valueAt(i))
				indexList.add(mAlreadyRingID.keyAt(i));
		}
		outState.putIntegerArrayList("already_ring", indexList);
		
		// Save the markers
		outState.putAll(mOverlayManager.saveInstanceState());
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		if(mAlreadyRingID == null)
			mAlreadyRingID = new SparseBooleanArray();
		
		// Restore the already notified alarms
		for(Integer index : savedInstanceState.getIntegerArrayList("already_ring"))
			mAlreadyRingID.put(index, true);
		
		// Restore the markers
		mOverlayManager.restoreInstanceState(savedInstanceState);
	}
	
	/**
	 * Set the longpress listener to the mapview
	 */
	private void setMapLongPress() {
		mMapView.setOnMapLongpressListener(new OnMapLongpressListener() {
			@Override
			public void onMapLongpress(final MapView view, final GeoPoint longpressLocation) {
				runOnUiThread(new Runnable() 
	            {
	                public void run() 
	                {
	                	// On longpress create a new search point
	                	mOverlayManager.addSearch(longpressLocation, "Nouveau point");
	                }
	            });	
			}
		});
	}
	
	/**
	 * Check the network access of the client, used for the search function of the map
	 * @return true if the network is available, false in the other case
	 */
	public boolean isOnline() {
	    NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnected()) {
	        return true;
	    }
	    return false;
	}
	
	/**
	 * Show the help dialog of the application
	 */
	private void showHelpDialog() {
		View alertView = getLayoutInflater().inflate(R.layout.help_dialog, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.title_help)
			.setCancelable(true)
			.setIcon(android.R.drawable.ic_menu_help)
			.setView(alertView)
			.setNeutralButton(R.string.dialog_btn_close, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
		
		builder.create().show();
	}
	
	/**
	 * Activate the alarm and show a notification in the android system
	 * @param alarm is the address that triggered the alarm
	 * @param distance is the distance in meters form the address
	 */
	private void sendNotification(Alarm alarm, int distance){
		
		// It is possible to edit the alarm by clicking on the notification
		Intent notificationIntent = new Intent(this, EditAlarmActivity.class);
		notificationIntent.setAction(Utility.ACTION_EDIT);
		notificationIntent.putExtra("index", getAlarmIndex(alarm));
		
		PendingIntent contentIntent = PendingIntent.getActivity(this,
		        NOTIFICATION_ID, notificationIntent,
		        PendingIntent.FLAG_CANCEL_CURRENT);
		
		// Create the notification
		NotificationCompat.Builder notifBuilder =
		        new NotificationCompat.Builder(this)
				.setContentIntent(contentIntent)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle(alarm.getName())
		        .setContentText("Distance de la destination : " + distance + " mètres environ.");
		
		if(alarm.isVibrator()){
			long[] pattern = {500,500};
			notifBuilder.setVibrate(pattern);
		}
		
		// Use the mediaplayer for the sound, so that we can make it loop like a true alarmclock
		if(alarm.getAlarmName() != null && !alarm.getAlarmName().isEmpty() && alarm.getVolume() > 0){
			int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
			mMediaPlayer = new MediaPlayer();
			mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, Math.max(alarm.getVolume() * maxVolume / 100, 1), 0);
			try {
				mMediaPlayer.setDataSource(this, Uri.parse(alarm.getAlarmName()));
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
			} catch (Exception ex) {
				Log.e("Error playing alarm: " + alarm.getAlarmName(), ex.toString());
				ex.printStackTrace();
			}
		}

		// Autocancel allow us to cancel the notification when the user click on it
		notifBuilder.setAutoCancel(true);
		Notification notif = notifBuilder.build();
		
		mNotificationManager.notify(NOTIFICATION_ID, notif);
		
		// We show an alert dialog too, so the user can disable the alarm
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(alarm.getName());
		builder.setMessage("Distance de la destination : " + distance + " mètres environ.");
		builder.setIcon(R.drawable.ic_launcher);
		builder.setNegativeButton(R.string.dialog_btn_stop, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				AlertDialog alert = builder.create();
				alert.setCanceledOnTouchOutside(true);
				
				alert.setOnCancelListener(new OnCancelListener() {
				
					@Override
					public void onCancel(DialogInterface dialog) {
			            // Stop the ringing alarm when the user cancel the alert dialog
						if (mMediaPlayer != null) {
			                mMediaPlayer.stop();
			                mMediaPlayer.release();
			                mMediaPlayer = null;
			            }
					}
				});
				
				try{
					alert.show();
				}
				catch(Exception ex){
					Log.e("Error showing alert.", ex.toString());
					if (mMediaPlayer != null) {
		                mMediaPlayer.stop();
		                mMediaPlayer.release();
		                mMediaPlayer = null;
		            }
				}
			}
		});
	}
	
	/**
	 * Initialize the criteria and the location listener
	 */
	private void initLocationListener() {
		mCriteria = new Criteria();
		mCriteria.setAltitudeRequired(false);
		mCriteria.setBearingRequired(false);
		mCriteria.setCostAllowed(false);
		mCriteria.setSpeedRequired(false);
		mCriteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
		mCriteria.setPowerRequirement(Criteria.POWER_LOW);
		
		mLocationListener = new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {}
			
			@Override
			public void onProviderEnabled(String provider) {
				// If the provider is enabled we need to check if there is a better provider available
				Log.i("SERVICEGPS", "onProviderEnabled : " + provider);
				createLocationListener();
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				// If the provider is disabled we need to check if there is an other provider available
				Log.i("SERVICEGPS", "onProviderDisabled : " + provider);
				createLocationListener();
			}
			
			@Override
			public void onLocationChanged(Location location) {
				float[] distance = new float[1];
				Log.i("SERVICEGPS", "new location : " + location.toString());
				for(Alarm alarm : mAlarmList){
					if(alarm.isEnabled()){
						Location.distanceBetween(location.getLatitude(), location.getLongitude(), 
								alarm.getPoint().getLatitudeE6() / 1E6, alarm.getPoint().getLongitudeE6() / 1E6, 
								distance);
						
						// Send the notification if the user enter an alarm radius
						if(distance[0] <= alarm.getRadius()	|| distance[0] - location.getAccuracy() <= alarm.getRadius() / 2){
							if(!mAlreadyRingID.get(alarm.getId())){
								mAlreadyRingID.put(alarm.getId(), true);
								sendNotification(alarm, (int)distance[0]);
								return;
							}
						}
						else{
							mAlreadyRingID.delete(alarm.getId());
						}
					}
				}
			}
		};
		
		// Monitor the GPS status, so we can switch the location provider when he is activated
		mLocManager.addGpsStatusListener(new Listener() {
			
			@Override
			public void onGpsStatusChanged(int event) {
				if(event == GpsStatus.GPS_EVENT_STARTED || event == GpsStatus.GPS_EVENT_STOPPED){
					Log.i("SERVICEGPS", "onGpsStatusChanged : " + event);
					
					// Update the gps menu item status
					if(mMenu != null && mMenu.getItem(MENU_GPS) != null)
					{
						if(mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
							mMenu.getItem(MENU_GPS).setIcon(R.drawable.ic_location_on);
							mMenu.getItem(MENU_GPS).setTitle(R.string.menu_gps_enable);
						}
						else{
							mMenu.getItem(MENU_GPS).setIcon(R.drawable.ic_location_off);
							mMenu.getItem(MENU_GPS).setTitle(R.string.menu_gps_disable);
						}
					}
					createLocationListener();
				}
			}
		});
		
		createLocationListener();
	}
	
	/**
	 * Create the location listener by taking the best location provider available.
	 * This method is used to update the provider when a new one is available too.
	 */
	private void createLocationListener(){
		
		// If a better provider is available, disable the old thread and create a new one with the new provider
		if(mLocationListenerThread != null)
			if(mBestLocationProvider != null &&	!mBestLocationProvider.equals(mLocManager.getBestProvider(mCriteria, true)))
				mLocationListenerThread.quit();
			else
				return;
		
		// Create a thread to listen to the provider
		mLocationListenerThread = new HandlerThread("GPSWakeupThread", HandlerThread.NORM_PRIORITY);
	    mLocationListenerThread.start();
	    
	    try{
	    	mBestLocationProvider = mLocManager.getBestProvider(mCriteria, true);
	    	mLocManager.requestLocationUpdates(mBestLocationProvider, UPDATE_MIN_TIME, 
	    			UPDATE_MIN_DISTANCE, mLocationListener, mLocationListenerThread.getLooper());
	    }
	    catch(IllegalArgumentException ex){
	    	Toast.makeText(this, R.string.toast_no_provider, Toast.LENGTH_SHORT).show();
	    	mLocationListenerThread.quit();
	    }
	}
	
	/**
	 * @return the actual alarms list. This list is thread-safe.
	 */
	public static List<Alarm> getAlarms(){
		return mAlarmList;
	}

	/**
	 * @param index is the index of the alarm returned
	 * @return the alarm of the given index or null if doesn't exist
	 */
	public static Alarm getAlarm(int index) {
		return mAlarmList.get(index);
	}
	
	/**
	 * @param alarm is the alarm from which you want the index
	 * @return the index of the alarm in the alarms list
	 */
	public static int getAlarmIndex(Alarm alarm){
		return mAlarmList.indexOf(alarm);
	}
	
	/**
	 * Add an alarm to the alarms list and to the map
	 * @param alarm is the alarm to be added
	 */
	public static void addAlarm(Alarm alarm) {
		mAlarmList.add(alarm);
		OverlayManager.getInstance().addAlarm(alarm);
	}
	
	/**
	 * Delete an alarm from the list, the database and the map
	 * @param alarm is the alarm to be deleted from the saved alarms
	 * @return true if the alarm was removed and false if it wasn't
	 */
	public static boolean deleteAlarm(Alarm alarm){
		if(mInstance != null && mAlarmList.remove(alarm)){
			mInstance.mAlarmBD.open();
			mInstance.mAlarmBD.removeAlarmByID(alarm.getId());
			mInstance.mAlarmBD.close();
			OverlayManager.getInstance().removeAlarm(alarm);
			return true;
		}
		return false;
	}
	
	/**
	 * Collapse the searchview from the action bar
	 */
	public static void collapseSearchView(){
		if(mInstance != null && mInstance.mMenu != null && mInstance.mMenu.getItem(mInstance.MENU_SEARCH) != null){
			mInstance.mMenu.getItem(mInstance.MENU_SEARCH).collapseActionView();
		}
	}
}
