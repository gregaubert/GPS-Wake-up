package org.gpswakeup.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite database helper class
 * Last update : 16.01.2013
 * @author Gregoire Aubert
 */
public class BDSQLite extends SQLiteOpenHelper {
	
	// Global settings
	private static final int DB_VERSION = 2;
	private static final String DB_NAME = "BD_GPSWakeUp";
	
	// Alarms table creation request
	private static final String CREATE_TABLE_ALARMS = 
			"CREATE TABLE " + AlarmBD.TABLE_ALARMS + " (" +
					AlarmBD.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					AlarmBD.COL_NAME + " TEXT," + AlarmBD.COL_LAT + " INTEGER NOT NULL, " +
					AlarmBD.COL_LONG + " INTEGER NOT NULL, " + AlarmBD.COL_ENABLED + " INTEGER, " + 
					AlarmBD.COL_RADIUS + " INTEGER NOT NULL, " + AlarmBD.COL_VIBRATOR + " INTEGER, " + 
					AlarmBD.COL_ALARM_NAME + " TEXT, " + AlarmBD.COL_VOLUME + " INTEGER);";
	
	/**
	 * Constructor of the database
	 * @param context is the context of the calling activity
	 */
	public BDSQLite(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_ALARMS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE " + AlarmBD.TABLE_ALARMS + ";");
		onCreate(db);
	}

}
