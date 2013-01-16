package org.gpswakeup.resources;

import java.util.ArrayList;

import org.gpswakeup.activity.EditAlarmActivity;
import org.gpswakeup.activity.MainActivity;
import org.gpswakeup.activity.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

/**
 * This is an implementation of the ItemizedOverlay class of android to show a radius around the marker displayed 
 * Last update : 16.01.2013
 * @author Gregoire Aubert
 */
public class RadiusItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	// MEMBERS
	private ArrayList<Alarm> mOverlaysAlarm = new ArrayList<Alarm>();				// The alarms marker list
	private ArrayList<OverlayItem> mOverlaysSearch = new ArrayList<OverlayItem>();	// The search marker list
	
	private Context mContext;
	private Drawable mInactivMarker;
	private Drawable mSearchMarker;
	
	/**
	 * Constructor of the class
	 * @param defaultMarker is the default marker (used for active alarm)
	 * @param inactivMarker is the inactive alarm marker
	 * @param searchMarker is the search marker
	 */
	public RadiusItemizedOverlay(Drawable defaultMarker, Drawable inactivMarker, Drawable searchMarker) {
		super(boundCenterBottom(defaultMarker));
		mInactivMarker = inactivMarker;
		mSearchMarker = searchMarker;
		populate();
	}
	
	/**
	 * Constructor of the class
	 * @param defaultMarker is the default marker (used for active alarm)
	 * @param inactivMarker is the inactive alarm marker
	 * @param searchMarker is the search marker
	 * @param context is the activity context
	 */
	public RadiusItemizedOverlay(Drawable defaultMarker, Drawable inactivMarker, Drawable searchMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mInactivMarker = inactivMarker;
		mSearchMarker = searchMarker;
		mContext = context;
		populate();
	}
	
	@Override
	protected OverlayItem createItem(int i) {
		
		// Verify if the item to be returned is an alarm marker or a search marker
		if(i < mOverlaysAlarm.size())
			return mOverlaysAlarm.get(i);
		
		return mOverlaysSearch.get(i - mOverlaysAlarm.size());
	}
	
	@Override
	public int size() {
		// Return the size of the two list
		return mOverlaysAlarm.size() + mOverlaysSearch.size();
	}

	@Override
	protected boolean onTap(int index) {
		// If this index is the index of an alarm marker
		if(index >= 0 && index < mOverlaysAlarm.size()){
			
			// Define the alert dialog for the alarm marker
			final Alarm alarm = mOverlaysAlarm.get(index);
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle(alarm.getName());
			dialog.setMessage(R.string.dialog_modify);
			OnClickListener listener = new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(DialogInterface.BUTTON_POSITIVE == which){
						
						// Launch the edit intent for this alarm
						Intent intent = new Intent(mContext, EditAlarmActivity.class);
						intent.setAction(Utility.ACTION_EDIT);
						intent.putExtra("index", MainActivity.getAlarmIndex(alarm));
						mContext.startActivity(intent);
					}
					else if(DialogInterface.BUTTON_NEGATIVE == which){
						dialog.cancel();
					}
				}
			};
			dialog.setPositiveButton(R.string.dialog_btn_modify, listener);
			dialog.setNegativeButton(R.string.dialog_btn_cancel, listener);
			dialog.show();
			
			return true;
		}
		// Else this index is the index of a search marker
		else if(index >= mOverlaysAlarm.size() && index < mOverlaysAlarm.size() + mOverlaysSearch.size()){
			
			// Create the alert dialog for the search marker
			final OverlayItem overlay = mOverlaysSearch.get(index-mOverlaysAlarm.size());
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle(R.string.dialog_add);
			dialog.setMessage("Recherche : " + overlay.getSnippet());
			OnClickListener listener = new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(DialogInterface.BUTTON_POSITIVE == which){
						
						// Launch the add new intent for this search marker's GeoPoint
						Intent intent = new Intent(mContext, EditAlarmActivity.class);
						intent.setAction(Utility.ACTION_NEW);
						intent.putExtra("snippet", overlay.getSnippet());
						intent.putExtra("lat", overlay.getPoint().getLatitudeE6());
						intent.putExtra("long", overlay.getPoint().getLongitudeE6());
						mContext.startActivity(intent);
					}
					else if(DialogInterface.BUTTON_NEGATIVE == which){
						dialog.cancel();
					}
				}
			};
			dialog.setPositiveButton(R.string.dialog_btn_add, listener);
			dialog.setNegativeButton(R.string.dialog_btn_cancel, listener);
			dialog.show();
			return true;
		}
		return false;
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow){
		// Instead of the shadow we draw a radius
		if(shadow){
			for(Alarm alarm : mOverlaysAlarm)
				if(alarm.getRadius() > 0){
					
					// Convert the GeoPoint to a point on the screen
					Point screenPts = new Point();
					mapView.getProjection().toPixels(alarm.getPoint(), screenPts);
					
					// Convert the radius in meters to a radius in pixels
					float circleRadius = metersToRadius(alarm.getRadius(), mapView, alarm.getPoint().getLatitudeE6());
			
					// Draw the radius
					Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
					circlePaint.setColor(Color.BLUE);
					circlePaint.setAlpha(25);
					circlePaint.setStyle(Style.FILL);
					canvas.drawCircle(screenPts.x, screenPts.y, circleRadius, circlePaint);
					circlePaint.setAlpha(50);
					circlePaint.setStyle(Style.STROKE);
					circlePaint.setStrokeWidth(2);
					canvas.drawCircle(screenPts.x, screenPts.y, circleRadius, circlePaint);
				}
		}
		else
			super.draw(canvas, mapView, shadow);
	}
	
	/**
	 * Add a search marker on the map
	 * @param overlay is the overlay of a research
	 */
	public void addOverlay(OverlayItem overlay) {
		overlay.setMarker(boundCenterBottom(mSearchMarker));
		mOverlaysSearch.add(overlay);
		setLastFocusedIndex(-1);
		populate();
	}
	
	/**
	 * Add an alarm marker on the map
	 * @param alarm is the alarm to be displayed on the map
	 */
	public void addAlarm(Alarm alarm) {
		
		// Switch the marker is the alarm is disabled
		if(!alarm.isEnabled())
			alarm.setMarker(boundCenterBottom(mInactivMarker));
		else
			alarm.setMarker(null);
			
	    mOverlaysAlarm.add(alarm);
	    setLastFocusedIndex(-1);
		populate();
	}
	
	/**
	 * Remove an alarm from the map
	 * @param alarm is the alarm to be removed from the map
	 * @return true is the alarm was removed
	 */
	public boolean removeAlarm(Alarm alarm){
		if(mOverlaysAlarm.remove(alarm)){
			setLastFocusedIndex(-1);
			populate();
			return true;
		}
		return false;
	}
	
	/**
	 * Remove all the alarm marker on the map
	 */
	public void clearAlarm(){
		mOverlaysAlarm.clear();
		setLastFocusedIndex(-1);
		populate();
	}
	
	/**
	 * Remove all the search marker on the map
	 */
	public void clearSearch(){
		mOverlaysSearch.clear();
		setLastFocusedIndex(-1);
		populate();
	}
	
	/**
	 * @return all the search marker on the map
	 */
	public ArrayList<OverlayItem> getOverlaysSearch() {
		return mOverlaysSearch;
	}
	
	/**
	 * Translate the radius in meters to a radius in pixel that can be showed on the map
	 * according to the form of the planet
	 * @param meters is the radius in meters to be translated
	 * @param map is the mapview
	 * @param latitudeE6 is the latitude of the center of the radius
	 * @return the radius corresponding in pixel
	 */
	public static int metersToRadius(float meters, MapView map, int latitudeE6) {
		double latitude = (double)latitudeE6/1000000;
	    return (int) (map.getProjection().metersToEquatorPixels(meters) * (1/ Math.cos(Math.toRadians(latitude))));         
	}
	
}
