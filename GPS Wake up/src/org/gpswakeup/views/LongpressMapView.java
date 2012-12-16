package org.gpswakeup.views;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class LongpressMapView extends MapView {

	static final int LONGPRESS_THRESHOLD = 500;

	private GeoPoint mLastMapCenter;
	private Timer mLongpressTimer;
	private OnMapLongpressListener mOnMapLongpressListener;

	public LongpressMapView(Context context, String apiKey) {
		super(context, apiKey);
	}

	public LongpressMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LongpressMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setOnMapLongpressListener(OnMapLongpressListener listener) {
		mOnMapLongpressListener = listener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		handleLongpress(event);
		return super.onTouchEvent(event);
	}

	private void handleLongpress(final MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mLongpressTimer = new Timer();
			mLongpressTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					GeoPoint longpressLocation = getProjection().fromPixels(
							(int) event.getX(), (int) event.getY());
					if (mOnMapLongpressListener != null)
						mOnMapLongpressListener.onMapLongpress(
								LongpressMapView.this, longpressLocation);
				}
			}, LONGPRESS_THRESHOLD);

			mLastMapCenter = getMapCenter();
		}

		if (event.getAction() == MotionEvent.ACTION_MOVE) {

			if (!getMapCenter().equals(mLastMapCenter))
				mLongpressTimer.cancel();

			mLastMapCenter = getMapCenter();
		}

		if (event.getAction() == MotionEvent.ACTION_UP)
			mLongpressTimer.cancel();

		if (event.getPointerCount() > 1)
			mLongpressTimer.cancel();
	}
}