package me.sweetll.pm25demo.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.orhanobut.logger.Logger;

import java.util.Iterator;

/**
 * Created by sweet on 15-9-9.
 */
public class GPSService extends Service {
    LocationManager lm;
    Criteria ct;
    String provider;
    GpsStatus gpsstatus;

    private AutoThread athread = new AutoThread();
    private int timespace = 1000;

    public GPSService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initLocation();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initLocation()
    {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            ct = new Criteria();
            ct.setAccuracy(Criteria.ACCURACY_FINE);// 高精度定位慢
            ct.setAccuracy(Criteria.ACCURACY_COARSE);//非高精度定位更快
            ct.setAltitudeRequired(true);// 显示海拔
            ct.setBearingRequired(true);// 显示方向
            ct.setSpeedRequired(true);// 显示速度
            ct.setCostAllowed(true);// 不允许有花费
            ct.setPowerRequirement(Criteria.POWER_LOW);// 低功耗
            provider = lm.getBestProvider(ct, true);
            // 位置变化监听,默认1秒一次,0表示不考虑距离变化(距离10米以上)
            lm.requestLocationUpdates(provider, 1000, 0, locationListener);
            lm.addGpsStatusListener(statuslistener);
            Logger.d("GPS Initialize Done");
        }
    }

    private final LocationListener locationListener = new LocationListener()
    {

        public void onLocationChanged(Location arg0)
        {
            //isLocation = true;
            if (!athread.isAlive())
                athread.start();
        }

        public void onProviderDisabled(String arg0)
        {
            //showInfo(null, -1);
        }

        public void onProviderEnabled(String arg0)
        {
        }

        public void onStatusChanged(String arg0, int arg1, Bundle arg2)
        {
            //isLocation = true;
            if (!athread.isAlive())
                athread.start();
        }
    };

    private GpsStatus.Listener statuslistener = new GpsStatus.Listener()
    {

        public void onGpsStatusChanged(int event)
        {
            gpsstatus = lm.getGpsStatus(null);
            switch (event)
            {
                case GpsStatus.GPS_EVENT_FIRST_FIX:// 第一次定位
                    int c = gpsstatus.getTimeToFirstFix();
                    Log.i("AlimysoYang", String.valueOf(c));

                    break;
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                {
                    // 得到所有收到的卫星的信息，包括 卫星的高度角、方位角、信噪比、和伪随机号（及卫星编号）
                    Iterable<GpsSatellite> allgps = gpsstatus.getSatellites();
                    Iterator<GpsSatellite> items = allgps.iterator();
                    int i = 0;
                    int ii = 0;
                    while (items.hasNext())
                    {
                        GpsSatellite tmp = (GpsSatellite) items.next();
                        if (tmp.usedInFix())
                            ii++;
                        i++;
                    }
//                    tvvSaltnum.setText(String.format("可见卫星数:%d", i));
//                    tvcSaltnum.setText(String.format("已定位卫星数:%d", ii));
                    Logger.d("可见卫星数：" + i);
                    Logger.d("已定位卫星数：" + ii);

                    if(ii>4)
                    {
//                        Location.setText("室外");
                    }
                    else
//                        Location.setText("室内");
                    break;
                }
                case GpsStatus.GPS_EVENT_STARTED:
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    break;
            }
        }
    };

private class AutoThread extends Thread
    {
        private boolean running = true;
        private Handler h = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                //showInfo(getLastPosition(), 2);
            }
        };

        public AutoThread()
        {

        }

        @Override
        public void run()
        {
            while (running)
            {
                try
                {
                    h.sendEmptyMessage(0);
                    Thread.sleep(timespace);
                } catch (Exception e)
                {

                }
            }
        }

        //判断gps是否开启，并提醒其开启

    }


}
