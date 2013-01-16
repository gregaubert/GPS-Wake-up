package org.gpswakeup.views;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

/**
 * Modified mapview class so that we can use a longpress on it.
 * Last update : 16.01.2013
 * @author Gregoire Aubert
 */
public class LongpressMapView extends MapView {

	// MEMBERS
	private GeoPoint mLastMapCenter;
	private Timer mLongpressTimer;
	private OnMapLongpressListener mOnMapLongpressListener;

	// CONSTANT
	static final int LONGPRESS_THRESHOLD = 500;
	
	/**
	 * Parent constructor
	 * @param context is the mapactivity context
	 * @param apiKey is the google map api key of the application
	 */
	public LongpressMapView(Context context, String apiKey) {
		super(context, apiKey);
	}

	/**
	 * Parent constructor
	 * @param context is the mapactivity context
	 * @param attrs is an attribute set; currently no attributes are used 
	 */
	public LongpressMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * Parent constructor
	 * @param context is the mapactivity context
	 * @param attrs is an attribute set; currently no attributes are used
	 * @param defStyle is the default style to apply to this view
	 */
	public LongpressMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * Set the longpress listener
	 * @param listener is the longpress listener
	 */
	public void setOnMapLongpressListener(OnMapLongpressListener listener) {
		mOnMapLongpressListener = listener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Handle the touch event to see if it is a longpress or not
		handleLongpress(event);
		return super.onTouchEvent(event);
	}

	/**
	 * Handle a touch event to see if it is a longpress
	 * @param event the motion event from the onTouchEvent
	 */
	private void handleLongpress(final MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			
			// If the action is a down we start the timer to see if it is long enough
			mLongpressTimer = new Timer();
			mLongpressTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					// At the end of the timer we call the longpress listener method
					// The timer will be canceled before the end if it not a longpress
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
			// If the event is a move we cancel the timer
			if (!getMapCenter().equals(mLastMapCenter))
				mLongpressTimer.cancel();

			mLastMapCenter = getMapCenter();
		}

		if (event.getAction() == MotionEvent.ACTION_UP)
			// If the action is a up we can cancel the timer
			mLongpressTimer.cancel();

		if (event.getPointerCount() > 1)
			// If this is a multi touch we cancel the timer too
			mLongpressTimer.cancel();
	}
}