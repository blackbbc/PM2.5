/**
 * @author QYM
 */
package me.sweetll.pm25demo.util;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import me.sweetll.pm25demo.values.ConstantValue;

public class DBUtil extends SQLiteOpenHelper {
	
	public DBUtil(Context context) {
		this(context, ConstantValue.DB_NAME, null, ConstantValue.DB_VERSION);
	}
	
	public DBUtil(Context context,int version) {
		this(context, ConstantValue.DB_NAME, null, version);
	}
	
	public DBUtil(Context context, String name,int version) {
		this(context, name, null, version);
	}
	
	public DBUtil(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i("here", "onCreateSQL");
		db.execSQL("create table " + ConstantValue.TABLE_NAME + "(" +
				ConstantValue.DB_MetaData.STATE_ID_COL + " integer primary key autoincrement, " +
				ConstantValue.DB_MetaData.STATE_USER_ID_COL + " varchar, " + 
				ConstantValue.DB_MetaData.STATE_TIME_POINT_COL + " varchar, " + 
				ConstantValue.DB_MetaData.STATE_LONGTITUDE_COL + " varchar, " + 
				ConstantValue.DB_MetaData.STATE_LATITUDE_COL + " varchar, " + 
				ConstantValue.DB_MetaData.STATE_OUTDOOR_COL + " varchar, " + 
				ConstantValue.DB_MetaData.STATE_STATUS_COL + " varchar, " + 
				ConstantValue.DB_MetaData.STATE_STEPS_COL + " varchar, " + 
				ConstantValue.DB_MetaData.STATE_AVG_RATE_COL + " varchar, " + 
				ConstantValue.DB_MetaData.STATE_VENTILATION_VOLUME_COL + " varchar, " + 
				ConstantValue.DB_MetaData.STATE_PM25_COL + " varchar, " + 
				ConstantValue.DB_MetaData.STATE_SOURCE_COL + " varchar); " );
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP table if exists " + ConstantValue.TABLE_NAME);
		onCreate(db);
	}
		
	public static void closeDB(SQLiteDatabase db) {
		if(db != null) {
			db.close();
		}
	}
	
	public static void closeCursor(Cursor c) {
		if (c != null) {
			c.close();
		}
	}
	
}
