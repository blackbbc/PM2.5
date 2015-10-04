package me.sweetll.pm25demo.util;

import android.os.Handler;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.sweetll.pm25demo.constants.ConstantValues;
import me.sweetll.pm25demo.model.Polution;


public class AreaPolutions {
	public static Vector<Polution> getPolution(String location) {
		if (location==null) {
			return null;
		}
		Vector<Polution> polutions = new Vector<Polution>();
		String result;
		try {
			String url = ConstantValues.pollutionsURL+"?area="+ URLEncoder.encode(location, "utf-8");
			HttpClient hc = new HttpClient(url);
			Thread thread = new Thread(hc);
			thread.start();
			result = hc.getResult();

//			RequestFuture<JSONArray> future = RequestFuture.newFuture();
//			JsonArrayRequest request = new JsonArrayRequest(url, null, future, future);
//			GlobalGlass.getQueue().add(request);
//
//			try {
//				JSONArray object = future.get(30, TimeUnit.SECONDS);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			} catch (ExecutionException e) {
//				e.printStackTrace();
//			} catch (TimeoutException e) {
//				e.printStackTrace();
//			}

			JSONArray array;
		
			array = new JSONArray(result);
			
		
			for(int i=0 ; i < array.length();i++)
			{
				JSONObject myjObject = array.getJSONObject(i);	
				int id = myjObject.getInt("id");
				int aqi = myjObject.getInt("aqi");
				String area = myjObject.getString("area");
				String position_name = myjObject.getString("position_name");	
				String station_code = myjObject.getString("station_code");
				int pm2_5 = myjObject.getInt("pm2_5");
				int pm2_5_24h = myjObject.getInt("pm2_5_24h");
				String primary_pollutant = myjObject.getString("primary_pollutant");
				String quality = myjObject.getString("quality");
				String time_point = myjObject.getString("time_point");
				  
				polutions.add(new Polution(id, aqi, area,position_name,station_code, pm2_5, 
							pm2_5_24h,primary_pollutant, quality, time_point));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return polutions;		
	}
	
	/*public static void main(String args[]) {
		AreaPolutions ap = new AreaPolutions();
		Vector<Polution> polutions = ap.getPolution("����");
		for (Polution polution:polutions) {
			System.out.println(polution.toString());
		}
	}*/
}
