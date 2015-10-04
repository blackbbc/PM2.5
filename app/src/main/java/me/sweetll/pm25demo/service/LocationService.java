package me.sweetll.pm25demo.service;

import java.util.Timer;
import java.util.TimerTask;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.orhanobut.logger.Logger;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.widget.TextView;

public class LocationService extends IntentService {
    LocationManager lm;
    public static final int TIME_INTERVAL = 1000;

    public LocationService() {
        super("location");
    }

    @Override
	public void onCreate() {
        super.onCreate();
        init();
    }

    @Override
    public void onHandleIntent(Intent intent) {

    }

    private void init(){
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);  //模糊模式
        criteria.setAltitudeRequired(false);             //不提供海拔信息
        criteria.setBearingRequired(false);              //不提供方向信息
        criteria.setCostAllowed(true);                   //允许运营商计费
        criteria.setPowerRequirement(Criteria.POWER_LOW);//低电池消耗
        criteria.setSpeedRequired(false);                //不提供位置信息

        String provider = lm.getBestProvider(criteria, true);

        try {
            lm.requestLocationUpdates(provider, TIME_INTERVAL, 0, locationListener);
        } catch (SecurityException e) {
            Logger.e(e.getMessage());
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            DensityService.longitude = location.getLongitude();
            DensityService.latitude = location.getLatitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    

}
