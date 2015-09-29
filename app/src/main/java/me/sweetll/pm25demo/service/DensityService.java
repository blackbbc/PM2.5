package me.sweetll.pm25demo.service;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Intent;
import android.os.ResultReceiver;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import me.sweetll.pm25demo.constants.ConstantValues;

/**
 * Created by sweet on 15-9-23.
 */
public class DensityService extends IntentService {

    public DensityService() {
        super("density");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String city = intent.getStringExtra("city");
        ResultReceiver rec = intent.getParcelableExtra("receiver");
    }

    public void sendPolutionRequest(String city) {
        try {
            String url = ConstantValues.polutionsURL + "?area="+ URLEncoder.encode(city, "utf-8");

//            JsonArrayRequest jsObjRequest = new JsonArrayRequest(
//                    (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
//
//                        @Override
//                        public void onResponse(JSONArray response) {
////                            mTxtDisplay.setText("Response: " + response.toString());
//                        }
//                    }, new Response.ErrorListener() {
//
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            // TODO Auto-generated method stub
//
//                        }
//                    });


        } catch (UnsupportedEncodingException e) {
            Logger.e(e.getMessage());
        }
    }
}
