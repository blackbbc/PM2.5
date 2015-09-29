package me.sweetll.pm25demo.values;

import java.util.Date;

public class ConstantValue {
	public static final String DB_NAME = "Information.db";
	public static final String TABLE_NAME = "state";
	public static final int DB_VERSION = 1;
	
	public static class DB_MetaData{
		public static final String STATE_ID_COL = "_id";
		public static final String STATE_USER_ID_COL = "userid"; 
		public static final String STATE_TIME_POINT_COL = "time_point"; 
		public static final String STATE_LONGTITUDE_COL= "longtitude";
		public static final String STATE_LATITUDE_COL = "latitude";
		public static final String STATE_OUTDOOR_COL = "outdoor";
		public static final String STATE_STATUS_COL= "status";
		public static final String STATE_STEPS_COL = "steps";
		public static final String STATE_AVG_RATE_COL = "avg_rate";
		public static final String STATE_VENTILATION_VOLUME_COL = "ventilation_volume";
		public static final String STATE_PM25_COL = "pm25";
		public static final String STATE_SOURCE_COL = "source";	
		public static final String DEFAULT_ORDER = "_id desc";
	}
	
	public static class Units {
		public static final String AQI_UNIT = "";
		public static final String PM2_5_UNIT = "΢��/������";
		public static final String PM10_UNIT = "΢��/������";
		public static final String O3_UNIT = "΢��/������";
		public static final String CO_UNIT = "����/������";
		public static final String SO2_UNIT = "΢��/������";
		public static final String NO2_UNIT = "΢��/������";
	}
	
}
