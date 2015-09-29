package me.sweetll.pm25demo.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.sweetll.pm25demo.constants.ConstantValues;
import me.sweetll.pm25demo.model.Position;

public class AreaPositions {
	
	public static HashMap<String,Vector<Position>> getAllPositions() {
		HashMap<String,Vector<Position>> areas = new HashMap<String,Vector<Position>>();

//		String result = HttpClient.getCrawlResult(ConstantValues.allPositionsURL);
        String url = ConstantValues.allPositionsURL;
        HttpClient hc = new HttpClient(url);
        Thread thread = new Thread(hc);
        thread.start();
        String result = hc.getResult();

		System.out.println("postions"+result);
		JSONObject object;
		try {
			object = new JSONObject(result);
			Iterator<String> keys = object.keys();
			while (keys.hasNext()) {
				String key = keys.next();
				JSONArray array = object.getJSONArray(key);
				Vector<Position> positions = new Vector<Position>();
				for(int i=0 ; i < array.length();i++)
				{
				    JSONObject myjObject = array.getJSONObject(i);	
				    String position_name = myjObject.getString("position_name");
				    double latitude = myjObject.getDouble("latitude");
				    double longtitude = myjObject.getDouble("longtitude");
				    String alias = myjObject.getString("alias");	
				    positions.add(new Position(position_name,latitude,longtitude,alias));
				}
				areas.put(key, positions);
			}
			return areas;		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return null;
	}
	
}
