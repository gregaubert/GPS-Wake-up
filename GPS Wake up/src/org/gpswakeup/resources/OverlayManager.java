package org.gpswakeup.resources;


import java.util.List;

import org.gpswakeup.activity.R;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class OverlayManager {

	private static OverlayManager instance;
	private MapView mMapView;
	private Context mContext;
	private List<Overlay> mMapOverlays;
	
	private Drawable mDrawAlarm;
	private Drawable mDrawSearch;
	
	private RadiusItemizedOverlay mItemOverlay;
	
	public OverlayManager(Context context, MapView mapView) {
		mContext = context;
		mMapView = mapView;
		mMapOverlays = mMapView.getOverlays();
		mDrawAlarm = context.getResources().getDrawable(R.drawable.icon_blue);
		mDrawSearch = context.getResources().getDrawable(R.drawable.icon_red);
		mItemOverlay = new RadiusItemizedOverlay(mDrawAlarm, mContext);
		mMapOverlays.add(mItemOverlay);
	}
	
	public static void setInstance(OverlayManager instance){
		OverlayManager.instance = instance;
	}
	
	public static OverlayManager getInstance(){
		return instance;
	}

	public void addAlarm(Alarm alarm){
		mItemOverlay.addAlarm(alarm);
	}
	
	public void addSearch(GeoPoint p, String txt){
		mMapView.getController().animateTo(p);
		mMapView.getController().setZoom(12);
		OverlayItem overlay = new OverlayItem(p, "Recherche", txt);
		overlay.setMarker(mDrawSearch);
		mItemOverlay.addOverlay(overlay);
	}
	
	public void invalidate(){
		mMapView.invalidate();
	}
	
}
