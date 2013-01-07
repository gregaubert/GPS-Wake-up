package org.gpswakeup.services;

import java.util.List;

import org.gpswakeup.activity.AlertActivity;
import org.gpswakeup.db.AlarmBD;
import org.gpswakeup.resources.Alarm;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseBooleanArray;

public class GPSWakeupService extends Service {
	
	private List<Alarm> mAlarmList;
	private SparseBooleanArray mAlreadyRingID;
	private AlarmBD mAlarmBD;
	private LocationManager mLocManager;
	private LocationListener mLocationListener;
	private Criteria mCriteria;
	private Object mLock = new Object();
	
	private final long UPDATE_MIN_TIME = 10*1000;//2 * 60 * 1000;
	private final float UPDATE_MIN_DISTANCE = 100;
	
	@Override
	public void onCreate() {
		
		Log.i("SERVICEGPS", "onCreate");
		
		mAlarmBD = new AlarmBD(this);
		
		mAlreadyRingID = new SparseBooleanArray();
		
		mLocManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		initLocationListener();
		
		HandlerThread thread = new HandlerThread("ServiceStartArguments", HandlerThread.MIN_PRIORITY);
	    thread.start();
		
		mLocManager.requestLocationUpdates(UPDATE_MIN_TIME, UPDATE_MIN_DISTANCE, mCriteria , mLocationListener, thread.getLooper());
		
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("SERVICEGPS", "startcommand");
		refreshAlarmsList();
		super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	private void sendNotification(Alarm alarm, float distance){
		Intent intent = new Intent(this, AlertActivity.class);
		intent.putExtra("distance", distance);
		intent.putExtra("alarm_name", alarm.getName());
		intent.putExtra("ringtone_name", alarm.getAlarmName());
		intent.putExtra("vibrator", alarm.isVibrator());
		intent.putExtra("volume", alarm.getVolume());
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	private void refreshAlarmsList(){
		mAlarmBD.open();
		synchronized(mLock){
			mAlarmList = mAlarmBD.getAllActiveAlarm();
		}
		mAlarmBD.close();
	}
	
	private void initLocationListener() {
		mCriteria = new Criteria();
		mCriteria.setAltitudeRequired(false);
		mCriteria.setHorizontalAccuracy(Criteria.ACCURACY_MEDIUM);
		mCriteria.setBearingRequired(false);
		mCriteria.setCostAllowed(false);
		mCriteria.setSpeedAccuracy(Criteria.ACCURACY_LOW);
		mCriteria.setPowerRequirement(Criteria.POWER_MEDIUM);
		
		mLocationListener = new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLocationChanged(Location location) {
				float[] distance = new float[1];
				Log.i("SERVICEGPS", "new location");
				synchronized(mLock){
					
//					if(mAlarmList.isEmpty())
//						stopSelf();
					
					for(Alarm alarm : mAlarmList){
						Location.distanceBetween(location.getLatitude(), location.getLongitude(), 
								alarm.getPoint().getLatitudeE6() / 1E6, alarm.getPoint().getLongitudeE6() / 1E6, 
								distance);
						if(distance[0] <= alarm.getRadius()	|| 
								distance[0] - location.getAccuracy() <= alarm.getRadius() / 2){
							if(!mAlreadyRingID.get(alarm.getId())){
								mAlreadyRingID.put(alarm.getId(), true);
								sendNotification(alarm, distance[0]);
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
	}
}
