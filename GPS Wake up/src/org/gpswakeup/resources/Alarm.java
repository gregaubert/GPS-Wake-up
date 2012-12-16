package org.gpswakeup.resources;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class Alarm extends OverlayItem {

	private int mId = -1;
	private String mName = "";
	private GeoPoint mLocation;
	private boolean mEnabled = true;
	private int mRadius = 5000;
	private boolean mVibrator = true;
	private String mAlarmName = "";
	private int mVolume = 20;

	public Alarm(int id, String name, GeoPoint location, boolean enabled,
			int radius, boolean vibrator, String alarmName, int volume) {
		super(location, "Alarme " + id, name);
		mId = id;
		mName = name;
		mLocation = location;
		mEnabled = enabled;
		mRadius = radius;
		mVibrator = vibrator;
		mAlarmName = alarmName;
		mVolume = volume;
	}

	public Alarm(int id, GeoPoint location, int radius, String name) {
		super(location, "Alarme " + id, "");
		mId = id;
		mLocation = location;
		mRadius = radius;
		mName = name;
	}

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		mId = id;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public GeoPoint getLocation() {
		return mLocation;
	}

	public void setLocation(GeoPoint location) {
		this.mLocation = location;
	}

	public boolean isEnabled() {
		return mEnabled;
	}

	public void setEnabled(boolean enabled) {
		this.mEnabled = enabled;
	}

	public int getRadius() {
		return mRadius;
	}

	public void setRadius(int radius) {
		this.mRadius = radius;
	}

	public boolean isVibrator() {
		return mVibrator;
	}

	public void setVibrator(boolean vibrator) {
		this.mVibrator = vibrator;
	}

	public String getAlarmName() {
		return mAlarmName;
	}

	public void setAlarmName(String alarmName) {
		this.mAlarmName = alarmName;
	}

	public int getVolume() {
		return mVolume;
	}

	public void setVolume(int volume) {
		this.mVolume = volume;
	}

}
