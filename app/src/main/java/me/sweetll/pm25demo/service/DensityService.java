package me.sweetll.pm25demo.service;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import me.sweetll.pm25demo.constants.ConstantValues;
import me.sweetll.pm25demo.util.VolleyQueue;

/**
 * Created by sweet on 15-9-23.
 */
public class DensityService extends IntentService {
    private String city;
    public static Double latitude = null;
    public static Double longitude = null;
    public static Double mDensity = null;

    private final static int TIME_INTERVAL = 5000;

    String mPosition = null;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (latitude != null && longitude != null) {
                sendPositionRequest();
                if (mPosition != null)
                    sendPollutionRequest();
            }
            handler.postDelayed(runnable, TIME_INTERVAL);
        }
    };

    public DensityService() {
        super("density");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    protected void init() {
        handler.post(runnable);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        this.city = intent.getStringExtra("city");
    }

    public void sendPollutionRequest() {
        try {
            String url = ConstantValues.pollutionsURL + "?area=" + URLEncoder.encode(city, "utf-8");

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET, url, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject pollution = response.getJSONObject(i);
                            if (pollution.getString("position_name").equals(mPosition)) {
                                mDensity = pollution.getDouble("pm2_5");
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        Logger.e(e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Logger.e(error.getMessage());
                }
            });

            VolleyQueue.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);

        } catch (UnsupportedEncodingException e) {
            Logger.e(e.getMessage());
        }
    }

    public void sendPositionRequest() {
        try {
            String url = ConstantValues.positionsURL + "?area=" + URLEncoder.encode(city, "utf8");

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        double minDistance = 10000000;
                        JSONArray positions = response.getJSONArray("上海");
                        for (int i = 0; i < positions.length(); i++) {
                            JSONObject position = positions.getJSONObject(i);
                            Double tmpDistance = Math.pow(longitude - position.getDouble("longtitude"), 2) + Math.pow(latitude - position.getDouble("latitude"), 2);
                            if (tmpDistance < minDistance) {
                                minDistance = tmpDistance;
                                mPosition = position.getString("position_name");
                            }
                        }
                    } catch (JSONException e) {
                        Logger.e(e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Logger.e(error.getMessage());
                }
            });

            VolleyQueue.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
        } catch (UnsupportedEncodingException e) {
            Logger.e(e.getMessage());
        }
    }

}
