package me.sweetll.pm25demo.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import java.util.Iterator;

import com.orhanobut.logger.Logger;

/**
 * Created by sweet on 15-9-9.
 */
public class GPSService extends IntentService implements GpsStatus.Listener {
    LocationManager lm;
    GpsStatus mGpsStatus;
    public static Boolean mInDoor = null;

    private final static int TIME_INTERVAL = 1000;

    public GPSService() {
        super("GPS");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    private void init() {
        try {
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            lm.addGpsStatusListener(this);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME_INTERVAL, 0, locationListener);
        } catch (SecurityException e) {
            Logger.e(e.getMessage());
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

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

    @Override
    public void onGpsStatusChanged(int event) {
        mGpsStatus = lm.getGpsStatus(null);
        switch (event)
        {
            case GpsStatus.GPS_EVENT_FIRST_FIX:// 第一次定位
                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS: {
                // 得到所有收到的卫星的信息，包括 卫星的高度角、方位角、信噪比、和伪随机号（及卫星编号）
                Iterable<GpsSatellite> allGps = mGpsStatus.getSatellites();
                Iterator<GpsSatellite> items = allGps.iterator();
                int i = 0;
                int ii = 0;
                while (items.hasNext()) {
                    GpsSatellite tmp = items.next();
                    if (tmp.usedInFix())
                        ii++;
                    i++;
                }

                mInDoor = ii < 4;

                break;
            }
            case GpsStatus.GPS_EVENT_STARTED:
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                break;
        }
    }

}
