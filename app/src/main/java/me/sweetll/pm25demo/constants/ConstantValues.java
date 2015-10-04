package me.sweetll.pm25demo.constants;

public class ConstantValues {
	//breath according to state
	public static final double boy_breath = 6.6; // L/min
	public static final double girl_breath = 6.0; // L/min
	public static double static_breath = boy_breath;
	public static final double walk_breath = 2.1 * static_breath;
	public static final double bicycle_breath = 2.1 * static_breath;
	public static final double run_breath = 6 * static_breath;
	
	public static final String pollutionsURL = "http://ilab.tongji.edu.cn/pm25/web/restful/air-qualities";
	public static final String positionsURL = "http://ilab.tongji.edu.cn/pm25/web/restful/area-positions";

	public static String movementStatus = "static";
	public static String doorStatus = "outDoor";

	public static double DENSITY;
	public static String CITY;
	
}
