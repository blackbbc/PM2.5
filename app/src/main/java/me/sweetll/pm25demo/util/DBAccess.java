package me.sweetll.pm25demo.util;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import me.sweetll.pm25demo.model.StateInformation;

public class DBAccess {
	private DBUtil dbUtil;
	private SQLiteDatabase db;
	
	private static String[] colNames = new String[]{
		DBConstants.DB_MetaData.STATE_ID_COL,
		DBConstants.DB_MetaData.STATE_USER_ID_COL,
		DBConstants.DB_MetaData.STATE_TIME_POINT_COL,
		DBConstants.DB_MetaData.STATE_LONGTITUDE_COL,
		DBConstants.DB_MetaData.STATE_LATITUDE_COL,
		DBConstants.DB_MetaData.STATE_OUTDOOR_COL,
		DBConstants.DB_MetaData.STATE_STATUS_COL,
		DBConstants.DB_MetaData.STATE_STEPS_COL,
		DBConstants.DB_MetaData.STATE_AVG_RATE_COL,
		DBConstants.DB_MetaData.STATE_VENTILATION_VOLUME_COL,
		DBConstants.DB_MetaData.STATE_PM25_COL,
		DBConstants.DB_MetaData.STATE_SOURCE_COL
	};
	
	public DBAccess(Context context) {
		Log.i("here", "DBAccess constructor");
		dbUtil = new DBUtil(context);
	}
	
	public void insertStateInfomation(StateInformation si) {
		db = dbUtil.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(DBConstants.DB_MetaData.STATE_ID_COL, si.getId());
		cv.put(DBConstants.DB_MetaData.STATE_USER_ID_COL, si.getUserid());
		cv.put(DBConstants.DB_MetaData.STATE_TIME_POINT_COL, si.getTime_point());
		cv.put(DBConstants.DB_MetaData.STATE_LONGTITUDE_COL, si.getLongtitude());
		cv.put(DBConstants.DB_MetaData.STATE_LATITUDE_COL, si.getLatitude());
		cv.put(DBConstants.DB_MetaData.STATE_OUTDOOR_COL, si.getOutdoor());
		cv.put(DBConstants.DB_MetaData.STATE_STATUS_COL, si.getStatus());
		cv.put(DBConstants.DB_MetaData.STATE_STEPS_COL, si.getSteps());
		cv.put(DBConstants.DB_MetaData.STATE_AVG_RATE_COL, si.getAvg_rate());
		cv.put(DBConstants.DB_MetaData.STATE_VENTILATION_VOLUME_COL, si.getVentilation_volume());
		cv.put(DBConstants.DB_MetaData.STATE_PM25_COL, si.getPm25());
		cv.put(DBConstants.DB_MetaData.STATE_SOURCE_COL, si.getSource());
		
		db.insert(DBConstants.TABLE_NAME, null, cv);
	}
	
	
	public void deleteStateInfomation(StateInformation si) {
		db = dbUtil.getWritableDatabase();
		
		db.delete(DBConstants.TABLE_NAME,
				DBConstants.DB_MetaData.STATE_ID_COL + "=" + si.getId(), null);
	}
	
	public void updateStateInfomation(StateInformation si) {
		db = dbUtil.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(DBConstants.DB_MetaData.STATE_ID_COL, si.getId());
		cv.put(DBConstants.DB_MetaData.STATE_USER_ID_COL, si.getUserid());
		cv.put(DBConstants.DB_MetaData.STATE_TIME_POINT_COL, si.getTime_point());
		cv.put(DBConstants.DB_MetaData.STATE_LONGTITUDE_COL, si.getLongtitude());
		cv.put(DBConstants.DB_MetaData.STATE_LATITUDE_COL, si.getLatitude());
		cv.put(DBConstants.DB_MetaData.STATE_OUTDOOR_COL, si.getOutdoor());
		cv.put(DBConstants.DB_MetaData.STATE_STATUS_COL, si.getStatus());
		cv.put(DBConstants.DB_MetaData.STATE_STEPS_COL, si.getSteps());
		cv.put(DBConstants.DB_MetaData.STATE_AVG_RATE_COL, si.getAvg_rate());
		cv.put(DBConstants.DB_MetaData.STATE_VENTILATION_VOLUME_COL, si.getVentilation_volume());
		cv.put(DBConstants.DB_MetaData.STATE_PM25_COL, si.getPm25());
		cv.put(DBConstants.DB_MetaData.STATE_SOURCE_COL, si.getSource());
		
		db.update(DBConstants.TABLE_NAME, cv,
				DBConstants.DB_MetaData.STATE_ID_COL + "=" + si.getId(), null);
	}
	
	/*
	 * 
	 */
	public Cursor selectStateInformationByUserid(String userid) {
		db = dbUtil.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM " + DBConstants.TABLE_NAME
				+ " WHERE " + DBConstants.DB_MetaData.STATE_USER_ID_COL + " = ? ", new String[]{userid});
		
		return c;
	}
	
	public StateInformation selectLastStateInformation() {
		Log.v("select","select first information");
		db = dbUtil.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM " + DBConstants.TABLE_NAME,null);
		if (c.moveToLast()) {
			StateInformation si = this.transferCursorToState(c);		
			return si;
		}
		return null;
	}
	
	//Cursor c = db.rawQuery("SELECT * FROM person WHERE age >= ?", new String[]{"33"}); 
	/**
	 * ����Լ����ص�����Note
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	public Cursor selectAllStateInformationCursor(String selection, String[] selectionArgs) {
		db = dbUtil.getReadableDatabase();
		Cursor c = db.query(DBConstants.TABLE_NAME, colNames,
				selection, selectionArgs, null, null, 
				DBConstants.DB_MetaData.DEFAULT_ORDER);
		
		return c;
	}
	
	/**
	 * ��ȡNote�б�
	 * @return
	 */
	public List<StateInformation> findAllStateInfomation() {
		Cursor c = selectAllStateInformationCursor(null, null);
		List<StateInformation> siList = new ArrayList<StateInformation>();
		
		while (c.moveToNext()) {
			StateInformation si = this.transferCursorToState(c);
			if (si!=null) {
				siList.add(si);
			}
		}
		
		DBUtil.closeCursor(c);
		
		return siList;
	}
	
	private StateInformation transferCursorToState(Cursor c) {
		StateInformation si = null;
		
		if (c!=null) {
			int ID = c.getInt(0);
			
			int num = 11;
			String values[] = new String[num];
			for (int i=1;i<=num;i++) {
				values[i-1] = c.getString(i);
			}
			si = new StateInformation();
			si.setId(ID);
			si.setUserid(values[0]);
			si.setTime_point(values[1]);
			si.setLongtitude(values[2]);
			si.setLatitude(values[3]);
			si.setOutdoor(values[4]);
			si.setStatus(values[5]);
			si.setSteps(values[6]);
			si.setAvg_rate(values[7]);
			si.setVentilation_volume(values[8]);
			si.setPm25(values[9]);
			si.setSource(values[10]);
			
			return si;
		}
		
		return null;
	}
}
