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

/**
 * This class is in charge of managing all the markers on the map.
 * This is not a singleton but the last instance of the class is saved
 * in a static field.
 * Last update : 16.01.2013
 * @author Gregoire Aubert
 */
public class OverlayManager {

	// MEMBERS
	private MapView mMapView;
	private Context mContext;
	
	private List<Overlay> mMapOverlays;
	private Drawable mDrawAlarmActiv;
	private Drawable mDrawAlarmInactiv;
	private Drawable mDrawSearch;
	
	private RadiusItemizedOverlay mItemOverlay;
	
	// STATIC MEMBERS
	private static OverlayManager instance;
	
	/**
	 * Constructor of the manager
	 * @param context is the activity context
	 * @param mapView is the mapview where the markers are put
	 */
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
	
	/**
	 * Remove an alarm marker on the map
	 * @param alarm is the alarm corresponding to the marker to remove
	 * @return true if the marker was removed
	 */
	public boolean removeAlarm(Alarm alarm){
		return mItemOverlay.removeAlarm(alarm);
	}
	
	/**
	 * Refresh an alarm marker on the map by removing it and adding int
	 * again in the RadiusItemizedOverlay.
	 * @param alarm is the alarm corresponding to the marker to be refreshed
	 */
	public void refreshAlarm(Alarm alarm){
		if(mItemOverlay.removeAlarm(alarm))
			mItemOverlay.addAlarm(alarm);
	}

	/**
	 * Add a new alarm marker on the map
	 * @param alarm is the alarm corresponding to the marker to be added
	 */
	public void addAlarm(Alarm alarm){
		mItemOverlay.addAlarm(alarm);
	}
	
	/**
	 * Remove all the search marker on the map
	 */
	public void clearSearch(){
		mItemOverlay.clearSearch();
	}
	
	/**
	 * Add a new search marker on the map
	 * @param location is the address where to put the marker
	 * @param text is the title of the marker
	 */
	public void addSearch(GeoPoint location, String text){
		mItemOverlay.addOverlay(new OverlayItem(location, "Recherche", text));
	}
	
	/**
	 * Move to map to center on a point
	 * @param location is the location where the map should center
	 */
	public void moveMapTo(GeoPoint location){
		mMapView.getController().animateTo(location);
		mMapView.getController().setZoom(11);
	}
	
	/**
	 * Tell the mapview to refresh her view, call this method when the markers
	 * have changed
	 */
	public void invalidate(){
		mMapView.invalidate();
	}

	/**
	 * Save the current status of the markers on the map in a bundle
	 * @return the bundle with all the information about the markers
	 */
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
	
	/**
	 * Restore the markers on the map from the bundle
	 * @param bundle is the bundle containing the markers informations
	 */
	public void restoreInstanceState(Bundle bundle){
		int[][] latlng = {bundle.getIntArray("latitude"), bundle.getIntArray("longitude")};
		String[][] titlesnippet = {bundle.getStringArray("title"), bundle.getStringArray("snippet")};
		for(int i = 0; i < latlng[0].length; i++)
			mItemOverlay.addOverlay(new OverlayItem(new GeoPoint(latlng[0][i], latlng[1][i]), titlesnippet[0][i], titlesnippet[1][i]));
		invalidate();
	}
	
	/**
	 * To set the current instance of the manager
	 * @param instance is the current instance of the manager
	 */
	public static void setInstance(OverlayManager instance){
		OverlayManager.instance = instance;
	}
	
	/**
	 * @return the instance of the current OverlayManager
	 */
	public static OverlayManager getInstance(){
		return instance;
	}
}
