package org.gpswakeup.resources;


import java.util.ArrayList;
import java.util.List;

import org.gpswakeup.activity.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class OverlayManager {

	private static OverlayManager instance;
	private MapView mMapView;
	private Context mContext;
	private List<Overlay> mMapOverlays;

	private Drawable mDrawAlarmActiv;
	private Drawable mDrawAlarmInactiv;
	private Drawable mDrawSearch;
	
	private RadiusItemizedOverlay mItemOverlay;
	
	public OverlayManager(Context context, MapView mapView) {
		mContext = context;
		mMapView = mapView;
		mMapOverlays = mMapView.getOverlays();
		mDrawAlarmActiv = context.getResources().getDrawable(R.drawable.icon_green);
		mDrawAlarmInactiv = context.getResources().getDrawable(R.drawable.icon_red);
		mDrawSearch = context.getResources().getDrawable(R.drawable.icon_blue);
		mItemOverlay = new RadiusItemizedOverlay(mDrawAlarmActiv, mDrawAlarmInactiv, mDrawSearch, mContext);
		mMapOverlays.add(mItemOverlay);
	}
	
	public static void setInstance(OverlayManager instance){
		OverlayManager.instance = instance;
	}
	
	public static OverlayManager getInstance(){
		return instance;
	}
	
	public boolean removeAlarm(Alarm alarm){
		return mItemOverlay.removeAlarm(alarm);
	}
	
	public void refreshAlarm(Alarm alarm){
		if(mItemOverlay.removeAlarm(alarm))
			mItemOverlay.addAlarm(alarm);
	}

	public void addAlarm(Alarm alarm){
		mItemOverlay.addAlarm(alarm);
	}
	
	public void clearSearch(){
		mItemOverlay.clearSearch();
	}
	
	public void addSearch(GeoPoint p, String txt){
		mItemOverlay.addOverlay(new OverlayItem(p, "Recherche", txt));
	}
	
	public void moveMapTo(GeoPoint p){
		mMapView.getController().animateTo(p);
		mMapView.getController().setZoom(11);
	}
	
	public void invalidate(){
		mMapView.invalidate();
	}

	public Bundle saveInstanceState(){
		Bundle bundle = new Bundle();
		ArrayList<OverlayItem> searchOverlays = mItemOverlay.getOverlaysSearch();
		int[][] latlng = new int[2][searchOverlays.size()];
		String[][] titlesnippet = new String[2][searchOverlays.size()];
		for(int i = 0; i < searchOverlays.size(); i++){
			latlng[0][i] = searchOverlays.get(i).getPoint().getLatitudeE6();
			latlng[1][i] = searchOverlays.get(i).getPoint().getLongitudeE6();
			titlesnippet[0][i] = searchOverlays.get(i).getTitle();
			titlesnippet[1][i] = searchOverlays.get(i).getSnippet();
		}
		bundle.putIntArray("latitude", latlng[0]);
		bundle.putIntArray("longitude", latlng[1]);
		bundle.putStringArray("title", titlesnippet[0]);
		bundle.putStringArray("snippet", titlesnippet[1]);
		return bundle;
	}
	
	public void restoreInstanceState(Bundle bundle){
		int[][] latlng = {bundle.getIntArray("latitude"), bundle.getIntArray("longitude")};
		String[][] titlesnippet = {bundle.getStringArray("title"), bundle.getStringArray("snippet")};
		for(int i = 0; i < latlng[0].length; i++)
			mItemOverlay.addOverlay(new OverlayItem(new GeoPoint(latlng[0][i], latlng[1][i]), titlesnippet[0][i], titlesnippet[1][i]));
		invalidate();
	}
}
