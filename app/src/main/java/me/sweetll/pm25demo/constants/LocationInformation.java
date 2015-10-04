package me.sweetll.pm25demo.constants;

import java.util.HashMap;
import java.util.Vector;

import android.util.Log;

import me.sweetll.pm25demo.model.Polution;
import me.sweetll.pm25demo.model.Position;
import me.sweetll.pm25demo.util.AreaPolutions;
import me.sweetll.pm25demo.util.AreaPositions;


public class LocationInformation {

	private String city;
	private double latitude;
	private double longtitude;
	private double pm2_5_density;
	
	public LocationInformation() {
		city = "";
		pm2_5_density = 40; 
	}
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public double getLongtitude() {
		return longtitude;
	}
	public void setLongtitude(double longtitude) {
		this.longtitude = longtitude;
	}
	
	public double getDefaultPM25Density() {
		return pm2_5_density;
	}
	
	public double getPm2_5_density() {	
		Vector<Polution> polutions = AreaPolutions.getPolution(city);
		Log.e("polutions", polutions.size()+"");
		HashMap<String,Vector<Position>> areas = AreaPositions.getAllPositions();
		Vector<Position> positions = areas.get(city);
		Log.e("positions", positions.size()+"");

		String nearestDetectionLocation = this.getNearestDetectionLocation(positions);

		for (Polution polution:polutions) {
			if (polution.getPosition_name() == nearestDetectionLocation) {
				this.setPm2_5_density(polution.getPM2_5());
				break;
			}
		}
		return pm2_5_density;
	}
	public void setPm2_5_density(double pm2_5_density) {
		this.pm2_5_density = pm2_5_density;
	}	
	
	private String getNearestDetectionLocation(Vector<Position> positions) {
		double min = 1;
		String name = "";
		for (Position position:positions) {
			double distance = this.getDistance(position.getLatitude(), position.getLongtitude(), latitude, longtitude);
			if (distance<min) {
				min = distance;
				name = position.getName();
			}
		}
		if (name=="") {
			name = positions.get(0).getName();
		}
		return name;
	}
	
	private double getDistance(double lat1,double lng1,double lat2,double lng2) {
		return Math.sqrt((lat1-lng1)*(lat1-lng1) + (lat2-lng2)*(lat2-lng2));
	}
	
}
