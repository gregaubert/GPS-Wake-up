package gps.wake.up;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class RadiusItemizedOverlay extends ItemizedOverlay<Alarm> {

	private ArrayList<Alarm> mOverlays = new ArrayList<Alarm>();
	private Context mContext;
	
	public RadiusItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}
	
	public RadiusItemizedOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
	}
	
	public void addOverlay(Alarm overlay) {
	    mOverlays.add(overlay);
	    populate();
	}

	@Override
	protected Alarm createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}
	
	@Override
	protected boolean onTap(int index) {
		OverlayItem item = mOverlays.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		dialog.show();
		return true;
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow){
		if (shadow)
			for(Alarm alarm : mOverlays)
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
		else
			super.draw(canvas, mapView, shadow);
	}
	
	public static int metersToRadius(float meters, MapView map, int latitudeE6) {
		double latitude = (double)latitudeE6/1000000;
	    return (int) (map.getProjection().metersToEquatorPixels(meters) * (1/ Math.cos(Math.toRadians(latitude))));         
	}
	
}
