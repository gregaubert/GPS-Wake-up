package gps.wake.up;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class AlarmBD {
	
	// Alarms table definition
	static final String TABLE_ALARMS = "t_alarms";
	static final String COL_ID = "id";
	static final String COL_NAME = "name";
	static final String COL_LAT = "lat";
	static final String COL_LONG = "long";
	static final String COL_ENABLED = "enabled";
	static final String COL_RADIUS = "radius";
	static final String COL_VIBRATOR = "vibrator";
	static final String COL_ALARM_NAME = "alarm_name";
	static final String COL_VOLUME = "volume";
	static final int NUM_COL_ID = 0;
	static final int NUM_COL_NAME = 1;
	static final int NUM_COL_LAT = 2;
	static final int NUM_COL_LONG = 3;
	static final int NUM_COL_ENABLED = 4;
	static final int NUM_COL_RADIUS = 5;
	static final int NUM_COL_VIBRATOR = 6;
	static final int NUM_COL_ALARM_NAME = 7;
	static final int NUM_COL_VOLUME = 8;
	
	private SQLiteDatabase bd;
	
	private BDSQLite myBD;

	public AlarmBD(Context context) {
		myBD = new BDSQLite(context);
	}
	
	public void open(){
		bd = myBD.getWritableDatabase();
	}
	
	public void close(){
		bd.close();
	}
	
	public SQLiteDatabase getBD(){
		return bd;
	}
	
	public long insertAlarm(Alarm alarm){
		ContentValues values = new ContentValues();
		values.put(COL_ID, alarm.getId());
		values.put(COL_LAT, alarm.getLocation().getLatitudeE6());
		values.put(COL_LONG, alarm.getLocation().getLongitudeE6());
		values.put(COL_NAME, alarm.getName());
		values.put(COL_ENABLED, alarm.isEnabled()?1:0);
		values.put(COL_RADIUS, alarm.getRadius());
		values.put(COL_ALARM_NAME, alarm.getAlarmName());
		values.put(COL_VOLUME, alarm.getVolume());
		values.put(COL_VIBRATOR, alarm.isVibrator()?1:0);
		return bd.insert(TABLE_ALARMS, null, values);
	}
	
	public int updateAlarm(int id, Alarm alarm){
		ContentValues values = new ContentValues();
		values.put(COL_LAT, alarm.getLocation().getLatitudeE6());
		values.put(COL_LONG, alarm.getLocation().getLongitudeE6());
		values.put(COL_NAME, alarm.getName());
		values.put(COL_ENABLED, alarm.isEnabled()?1:0);
		values.put(COL_RADIUS, alarm.getRadius());
		values.put(COL_ALARM_NAME, alarm.getAlarmName());
		values.put(COL_VOLUME, alarm.getVolume());
		values.put(COL_VIBRATOR, alarm.isVibrator()?1:0);
		return bd.update(TABLE_ALARMS, values, COL_ID + " = " + id, null);
	}
	
	public int removeAlarmByID(int id){
		return bd.delete(TABLE_ALARMS, COL_ID + " = " + id, null);
	}
	
	public int disableAlarmByID(int id){
		ContentValues values = new ContentValues();
		values.put(COL_ENABLED, 0);
		return bd.update(TABLE_ALARMS, values, COL_ID + " = " + id, null);
	}
	
	public int enableAlarmByID(int id){
		ContentValues values = new ContentValues();
		values.put(COL_ENABLED, 1);
		return bd.update(TABLE_ALARMS, values, COL_ID + " = " + id, null);
	}
	
	public int setRadiusByID(int id, int radius){
		ContentValues values = new ContentValues();
		values.put(COL_RADIUS, radius);
		return bd.update(TABLE_ALARMS, values, COL_ID + " = " + id, null);
	}
}
