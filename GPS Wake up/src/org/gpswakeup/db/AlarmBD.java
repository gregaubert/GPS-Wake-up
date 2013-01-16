package org.gpswakeup.db;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.gpswakeup.resources.Alarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.maps.GeoPoint;

/**
 * Represent the alarms in the database
 * Last update : 16.01.2013
 * @author Gregoire Aubert
 */
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
	
	private SQLiteDatabase db;
	
	private BDSQLite myDB;

	/**
	 * Constructor of the table
	 * @param context is the activity context calling the constructor
	 */
	public AlarmBD(Context context) {
		myDB = new BDSQLite(context);
	}
	
	/**
	 * Open the database
	 */
	public void open(){
		db = myDB.getWritableDatabase();
	}
	
	/**
	 * Close the database
	 */
	public void close(){
		db.close();
	}
	
	/**
	 * @return the SQLiteDatabase object linked to the table
	 */
	public SQLiteDatabase getDB(){
		return db;
	}
	
	/**
	 * Insert an alarm in the database
	 * @param alarm is the alarm to be inserted
	 * @return the id of the alarm inserted
	 */
	public long insertAlarm(Alarm alarm){
		ContentValues values = new ContentValues();
		values.put(COL_NAME, alarm.getName());
		values.put(COL_LAT, alarm.getLocation().getLatitudeE6());
		values.put(COL_LONG, alarm.getLocation().getLongitudeE6());
		values.put(COL_ENABLED, alarm.isEnabled()?1:0);
		values.put(COL_RADIUS, alarm.getRadius());
		values.put(COL_VIBRATOR, alarm.isVibrator()?1:0);
		values.put(COL_ALARM_NAME, alarm.getAlarmName());
		values.put(COL_VOLUME, alarm.getVolume());
		return db.insert(TABLE_ALARMS, null, values);
	}
	
	/**
	 * Update an existing alarm in the database
	 * @param alarm is the modified alarm
	 * @return the number of row affected
	 */
	public int updateAlarm(Alarm alarm){
		ContentValues values = new ContentValues();
		values.put(COL_NAME, alarm.getName());
		values.put(COL_LAT, alarm.getLocation().getLatitudeE6());
		values.put(COL_LONG, alarm.getLocation().getLongitudeE6());
		values.put(COL_ENABLED, alarm.isEnabled()?1:0);
		values.put(COL_RADIUS, alarm.getRadius());
		values.put(COL_VIBRATOR, alarm.isVibrator()?1:0);
		values.put(COL_ALARM_NAME, alarm.getAlarmName());
		values.put(COL_VOLUME, alarm.getVolume());
		return db.update(TABLE_ALARMS, values, COL_ID + " = " + alarm.getId(), null);
	}
	
	/**
	 * Remove an alarm from the database using her id
	 * @param id is the id of the alarm to be removed
	 * @return the number of deleted rows
	 */
	public int removeAlarmByID(int id){
		return db.delete(TABLE_ALARMS, COL_ID + " = " + id, null);
	}
	
	/**
	 * Change the enabled state of an alarm
	 * @param id is the id of the alarm to be enabled/disabled
	 * @param enable is the new status (enabled or disabled)
	 * @return the number of rows affected
	 */
	public int enableAlarmByID(int id, boolean enable){
		ContentValues values = new ContentValues();
		values.put(COL_ENABLED, enable?1:0);
		return db.update(TABLE_ALARMS, values, COL_ID + " = " + id, null);
	}
	
	/**
	 * Change the radius of the alarm alone
	 * @param id is the id of the alarm to be modified
	 * @param radius is the new radius
	 * @return the number of rows affected
	 */
	public int setRadiusByID(int id, int radius){
		ContentValues values = new ContentValues();
		values.put(COL_RADIUS, radius);
		return db.update(TABLE_ALARMS, values, COL_ID + " = " + id, null);
	}
	
	/**
	 * Return an alarm from the database from her id
	 * @param id is the id of the alarm to retrieve
	 * @return an alarm corresponding to the given id
	 */
	public Alarm getAlarm(int id) {
		
		Cursor cursor = db.query(TABLE_ALARMS, new String[] { COL_ID, COL_NAME, COL_LAT, COL_LONG, COL_ENABLED, 
				COL_RADIUS, COL_VIBRATOR, COL_ALARM_NAME, COL_VOLUME}, COL_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		
		if (cursor != null)
			cursor.moveToFirst();
		
		Alarm alarm = new Alarm(cursor.getInt(NUM_COL_ID),cursor.getString(NUM_COL_NAME),
								new GeoPoint(cursor.getInt(NUM_COL_LAT), cursor.getInt(NUM_COL_LONG)),
								cursor.getInt(NUM_COL_ENABLED)==1, cursor.getInt(NUM_COL_RADIUS), 
								cursor.getInt(NUM_COL_VIBRATOR)==1, cursor.getString(NUM_COL_ALARM_NAME),
								cursor.getInt(NUM_COL_VOLUME));
		return alarm;
	}
	
	/**
	 * @return a thread-safe list of all alarms in the database
	 */
	public List<Alarm> getAllAlarm() {
	    List<Alarm> alarmList = new CopyOnWriteArrayList<Alarm>();

	    // Select All Query
	    String selectQuery = "SELECT  * FROM " + TABLE_ALARMS;
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // Looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	            Alarm alarm = new Alarm(cursor.getInt(NUM_COL_ID),cursor.getString(NUM_COL_NAME),
							new GeoPoint(cursor.getInt(NUM_COL_LAT), cursor.getInt(NUM_COL_LONG)),
							cursor.getInt(NUM_COL_ENABLED)==1, cursor.getInt(NUM_COL_RADIUS), 
							cursor.getInt(NUM_COL_VIBRATOR)==1, cursor.getString(NUM_COL_ALARM_NAME),
							cursor.getInt(NUM_COL_VOLUME));

	            // Adding alarm to list
	            alarmList.add(alarm);
	        } while (cursor.moveToNext());
	    }
	 
	    // Return the alarm list
	    return alarmList;
	}
	
	/**
	 * @return a thread-safe list of all the enabled alarms in the database
	 */
	public List<Alarm> getAllActiveAlarm() {
	    List<Alarm> alarmList = new CopyOnWriteArrayList<Alarm>();

	    // Select All Query
	    String selectQuery = "SELECT * FROM " + TABLE_ALARMS + " WHERE " + COL_ENABLED + " = 1;";
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // Looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	            Alarm alarm = new Alarm(cursor.getInt(NUM_COL_ID),cursor.getString(NUM_COL_NAME),
							new GeoPoint(cursor.getInt(NUM_COL_LAT), cursor.getInt(NUM_COL_LONG)),
							cursor.getInt(NUM_COL_ENABLED)==1, cursor.getInt(NUM_COL_RADIUS), 
							cursor.getInt(NUM_COL_VIBRATOR)==1, cursor.getString(NUM_COL_ALARM_NAME),
							cursor.getInt(NUM_COL_VOLUME));

	            // Adding alarm to list
	            alarmList.add(alarm);
	        } while (cursor.moveToNext());
	    }
	 
	    // Return the alarm list
	    return alarmList;
	}
}
