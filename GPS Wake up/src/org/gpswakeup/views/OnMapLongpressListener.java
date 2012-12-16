package org.gpswakeup.views;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public interface OnMapLongpressListener {

	public void onMapLongpress(final MapView view, final GeoPoint longpressLocation);
}
