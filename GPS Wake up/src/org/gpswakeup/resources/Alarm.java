package org.gpswakeup.resources;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

/**
 * Class representing an alarm in the application
 * Last update : 16.01.2013
 * @author Gregoire Aubert
 */
public class Alarm extends OverlayItem {

	// MEMBERS
	private int mId = -1;
	private String mName = "";
	private transient GeoPoint mLocation;
	private boolean mEnabled = true;
	private int mRadius = 5000;
	private boolean mVibrator = true;
	private String mAlarmName = "";
	private int mVolume = 20;

	/**
	 * Constructor with all the values
	 * @param id is the id of the alarm in the database
	 * @param name is the name of the alarm
	 * @param location is the GeoPoint defining the address of the alarm
	 * @param enabled say if the alarm is enabled or not
	 * @param radius is the radius for the notification
	 * @param vibrator say if the notification must vibrate
	 * @param alarmName is the uri of the ringtone
	 * @param volume is the volume of the ringtone
	 */
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

	/**
	 * Constructor with the minimum of values
	 * @param id is the id of the alarm in the database
	 * @param location is the GeoPoint defining the address of the alarm
	 * @param radius is the radius for the notification
	 * @param name is the name of the alarm
	 */
	public Alarm(int id, GeoPoint location, int radius, String name) {
		super(location, "Alarme " + id, "");
		mId = id;
		mLocation = location;
		mRadius = radius;
		mName = name;
	}

	/**
	 * @return the id of the alarm
	 */
	public int getId() {
		return mId;
	}

	/**
	 * Set the id of the alarm
	 * @param id is the new id
	 */
	public void setId(int id) {
		mId = id;
	}

	/**
	 * @return the name of the alarm
	 */
	public String getName() {
		return mName;
	}

	/**
	 * Set the name of the alarm
	 * @param name is the new name of the alarm
	 */
	public void setName(String name) {
		mName = name;
	}

	/**
	 * @return the GeoPoint of the alarm
	 */
	public GeoPoint getLocation() {
		return mLocation;
	}

	/**
	 * Set the location of the alarm
	 * @param location is the new location of the alarm
	 */
	public void setLocation(GeoPoint location) {
		this.mLocation = location;
	}

	/**
	 * @return the status of the alarm
	 */
	public boolean isEnabled() {
		return mEnabled;
	}

	/**
	 * Set the status of the alarm
	 * @param enabled is the new status
	 */
	public void setEnabled(boolean enabled) {
		this.mEnabled = enabled;
	}

	/**
	 * @return the radius of the alarm in meters
	 */
	public int getRadius() {
		return mRadius;
	}

	/**
	 * Set the radius of the alarm
	 * @param radius is the new radius of the alarm in meters
	 */
	public void setRadius(int radius) {
		this.mRadius = radius;
	}

	/**
	 * @return the vibrator status of the alarm
	 */
	public boolean isVibrator() {
		return mVibrator;
	}

	/**
	 * Set the vibrator status of the alarm
	 * @param vibrator is the new vibrator status
	 */
	public void setVibrator(boolean vibrator) {
		this.mVibrator = vibrator;
	}

	/**
	 * @return the ringtone uri of the alarm, null if no ringtone
	 */
	public String getAlarmName() {
		return mAlarmName;
	}

	/**
	 * Set the new ringtone uri of the alarm
	 * @param alarmName is the new rington uri
	 */
	public void setAlarmName(String alarmName) {
		this.mAlarmName = alarmName;
	}

	/**
	 * @return the volume of the ringtone
	 */
	public int getVolume() {
		return mVolume;
	}

	/**
	 * Set the volume of the ringtone
	 * @param volume is the new volume of the ringtone
	 */
	public void setVolume(int volume) {
		this.mVolume = volume;
	}
}
