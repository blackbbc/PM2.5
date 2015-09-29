package me.sweetll.pm25demo.model;

import java.util.HashMap;
import java.util.Vector;

public class Area {
	private HashMap<String,Vector<Position>> areas;
	
	public Area() {
		
	}
	
	public Area(String name, Vector<Position> positions) {
		areas.put(name, positions);
	}
	
	public HashMap<String,Vector<Position>> getAreas() {
		return areas;
	}
}
