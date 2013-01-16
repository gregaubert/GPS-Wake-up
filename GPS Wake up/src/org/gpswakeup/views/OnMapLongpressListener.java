package org.gpswakeup.views;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

/**
 * This is the interface of the longpress listener for the longpressmapview.
 * Last update : 16.01.2013
 * @author Gregoire Aubert
 */
public interface OnMapLongpressListener {

	/**
	 * Called when a longpress on the map is detected
	 * @param view is the mapview where the longpress occurred
	 * @param longpressLocation is the location on the map of the longpress
	 */
	public void onMapLongpress(final MapView view, final GeoPoint longpressLocation);
}
