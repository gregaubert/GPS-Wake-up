package org.gpswakeup.resources;

import java.util.ArrayList;

import org.gpswakeup.activity.EditAlarmActivity;

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
import android.util.Log;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class RadiusItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	private ArrayList<Alarm> mOverlaysAlarm = new ArrayList<Alarm>();
	private ArrayList<OverlayItem> mOverlaysSearch = new ArrayList<OverlayItem>();
	private Context mContext;
	
	public RadiusItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}
	
	public RadiusItemizedOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
		populate();
	}
	
	public void addOverlay(OverlayItem overlay) {
		overlay.setMarker(boundCenterBottom(overlay.getMarker(0)));
		mOverlaysSearch.add(overlay);
	    populate();
	}
	
	public void addAlarm(Alarm alarm) {
	    mOverlaysAlarm.add(alarm);
	    populate();
	}
	
	public boolean removeAlarm(Alarm alarm){
		return mOverlaysAlarm.remove(alarm);
	}
	
	public void clearAlarm(){
		mOverlaysAlarm.clear();
	}
	
	public void clearSearch(){
		mOverlaysSearch.clear();
	}
	
	@Override
	protected OverlayItem createItem(int i) {
		if(i < mOverlaysAlarm.size())
			return mOverlaysAlarm.get(i);
		
		return mOverlaysSearch.get(i - mOverlaysAlarm.size());
	}
	
	@Override
	public int size() {
		return mOverlaysAlarm.size() + mOverlaysSearch.size();
	}

	@Override
	protected boolean onTap(int index) {
		
		if(index >= 0 && index < mOverlaysAlarm.size()){
			final Alarm alarm = mOverlaysAlarm.get(index);
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle(alarm.getName());
			dialog.setMessage("Modifier les options de l'alarme ?");
			OnClickListener listener = new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(DialogInterface.BUTTON_POSITIVE == which){
						Log.i("GPSWAKEUP", "modifier alarm ");
						Intent intent = new Intent(mContext, EditAlarmActivity.class);
						intent.putExtra("id", alarm.getId());
						mContext.startActivity(intent);
					}
					else if(DialogInterface.BUTTON_NEGATIVE == which){
						Log.i("GPSWAKEUP", "annuler");
						dialog.cancel();
					}
				}
			};
			dialog.setPositiveButton("Modifier", listener);
			dialog.setNegativeButton("Annuler", listener);
			dialog.show();
			
			return true;
		}
		else if(index >= mOverlaysAlarm.size() && index < mOverlaysAlarm.size() + mOverlaysSearch.size()){
			final OverlayItem overlay = mOverlaysSearch.get(index-mOverlaysAlarm.size());
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle("Ajouter une alarme");
			dialog.setMessage("Recherche : " + overlay.getSnippet());
			OnClickListener listener = new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(DialogInterface.BUTTON_POSITIVE == which){
						Log.i("GPSWAKEUP", "ajouter alarm ");
						//Intent intent = new Intent(mContext, EditAlarmActivity.class);
						//intent.putExtra("id", overlay.getId());
						//mContext.startActivity(intent);
					}
					else if(DialogInterface.BUTTON_NEGATIVE == which){
						Log.i("GPSWAKEUP", "annuler");
						dialog.cancel();
					}
				}
			};
			dialog.setPositiveButton("Ajouter", listener);
			dialog.setNegativeButton("Annuler", listener);
			dialog.show();
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow){
		if(shadow){
			for(Alarm alarm : mOverlaysAlarm)
				if(alarm.getRadius() > 0){
					Point screenPts = new Point();
					mapView.getProjection().toPixels(alarm.getPoint(), screenPts);
					
					float circleRadius = metersToRadius(alarm.getRadius(), mapView, alarm.getPoint().getLatitudeE6());
			
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
	
	public static int metersToRadius(float meters, MapView map, int latitudeE6) {
		double latitude = (double)latitudeE6/1000000;
	    return (int) (map.getProjection().metersToEquatorPixels(meters) * (1/ Math.cos(Math.toRadians(latitude))));         
	}
	
}
