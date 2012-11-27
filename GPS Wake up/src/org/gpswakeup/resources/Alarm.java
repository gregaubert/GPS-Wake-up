package org.gpswakeup.resources;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class Alarm extends OverlayItem {
	
	private int id;
	private String name;
	private GeoPoint location;
	private boolean enabled;
	private int radius;
	private boolean vibrator;
	private String alarmName;
	private int volume;
	
	public Alarm(int id, String name, GeoPoint location, boolean enabled, int radius, boolean vibrator, String alarmName, int volume) {
		super(location, "Alarme " + id, name);
		this.id = id;
		this.name = name;
		this.location = location;
		this.enabled = enabled;
		this.radius = radius;
		this.vibrator = vibrator;
		this.alarmName = alarmName;
		this.volume = volume;
	}



	public Alarm(int id, GeoPoint location, int radius){
		super(location, "Alarme " + id, "");
		this.id = id;
		this.location = location;
		this.radius = radius;
	}

    

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public GeoPoint getLocation() {
		return location;
	}

	public void setLocation(GeoPoint location) {
		this.location = location;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public boolean isVibrator() {
		return vibrator;
	}

	public void setVibrator(boolean vibrator) {
		this.vibrator = vibrator;
	}

	public String getAlarmName() {
		return alarmName;
	}

	public void setAlarmName(String alarmName) {
		this.alarmName = alarmName;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

}
