package me.sweetll.pm25demo.model;

public class StateInformation {
	private int id;
	private String userid;
	private String time_point;
	private String longtitude;
	private String latitude;
	private String outdoor;
	private String status;
	private String steps;
	private String avg_rate;
	private String ventilation_volume;
	private String pm25;
	private String source;
	
	
	public StateInformation() {
		super();
	}
	
	public StateInformation(String userid, String time_point,
			String longtitude, String latitude, String outdoor, String status,
			String steps, String avg_rate, String ventilation_volume,
			String pm25, String source) {
		super();
		this.userid = userid;
		this.time_point = time_point;
		this.longtitude = longtitude;
		this.latitude = latitude;
		this.outdoor = outdoor;
		this.status = status;
		this.steps = steps;
		this.avg_rate = avg_rate;
		this.ventilation_volume = ventilation_volume;
		this.pm25 = pm25;
		this.source = source;
	}
	
	public StateInformation(int id, String userid, String time_point,
			String longtitude, String latitude, String outdoor, String status,
			String steps, String avg_rate, String ventilation_volume,
			String pm25, String source) {
		super();
		this.id = id;
		this.userid = userid;
		this.time_point = time_point;
		this.longtitude = longtitude;
		this.latitude = latitude;
		this.outdoor = outdoor;
		this.status = status;
		this.steps = steps;
		this.avg_rate = avg_rate;
		this.ventilation_volume = ventilation_volume;
		this.pm25 = pm25;
		this.source = source;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getTime_point() {
		return time_point;
	}
	public void setTime_point(String time_point) {
		this.time_point = time_point;
	}
	public String getLongtitude() {
		return longtitude;
	}
	public void setLongtitude(String longtitude) {
		this.longtitude = longtitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getOutdoor() {
		return outdoor;
	}
	public void setOutdoor(String outdoor) {
		this.outdoor = outdoor;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSteps() {
		return steps;
	}
	public void setSteps(String steps) {
		this.steps = steps;
	}
	public String getAvg_rate() {
		return avg_rate;
	}
	public void setAvg_rate(String avg_rate) {
		this.avg_rate = avg_rate;
	}
	public String getVentilation_volume() {
		return ventilation_volume;
	}
	public void setVentilation_volume(String ventilation_volume) {
		this.ventilation_volume = ventilation_volume;
	}
	public String getPm25() {
		return pm25;
	}
	public void setPm25(String pm25) {
		this.pm25 = pm25;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}	
	
}
