package gps.wake.up;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class SearchOverlay extends Overlay {
	
	private Context context;
	private GeoPoint p;
	
	public SearchOverlay(Context context, GeoPoint p) {
		this.context = context;
		this.p = p;
	}
	
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
			long when) {
		super.draw(canvas, mapView, shadow);

		// ---translate the GeoPoint to screen pixels---
		Point screenPts = new Point();
		mapView.getProjection().toPixels(p, screenPts);

		// ---add the marker---
		Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_red);
		canvas.drawBitmap(bmp, screenPts.x - bmp.getWidth() / 2, screenPts.y - bmp.getHeight(), null);
		return true;
	}
}