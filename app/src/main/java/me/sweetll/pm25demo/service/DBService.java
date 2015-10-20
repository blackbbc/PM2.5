package me.sweetll.pm25demo.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.sweetll.pm25demo.MainActivity;
import me.sweetll.pm25demo.R;
import me.sweetll.pm25demo.constants.ConstantValues;
import me.sweetll.pm25demo.model.State;
import me.sweetll.pm25demo.movement.SimpleStepDetector;
import me.sweetll.pm25demo.movement.StepListener;
import me.sweetll.pm25demo.util.DBHelper;
import me.sweetll.pm25demo.util.VolleyQueue;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class DBService extends Service implements SensorEventListener {
    public static final String ACTION = "me.sweetll.pm25demo.service.DBService";

	private DBHelper dbHelper;
    private SQLiteDatabase db;

    //DB
    private static Double PM25 = 0.0;
    private static Double VENTILATION_VOLUME = 0.0;
	private static final int DB_TIME_INTERVAL = 60*1000;//1分钟
    private Handler DBHandler = new Handler();
    private Runnable DBRunnable = new Runnable() {
        @Override
        public void run() {
            addPM25();
            DBHandler.postDelayed(DBRunnable, DB_TIME_INTERVAL);
        }
    };

    //Movement
    private SimpleStepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private int numSteps;
    private long time1;
    private static MotionStatus mMotionStatus = MotionStatus.STATIC;
    private enum MotionStatus{
        NULL, STATIC, WALK, RUN
    }

    //Density
    private Double latitude = null;
    private Double longitude = null;
    private Double mDensity = null;
    private final static int DENSITY_TIME_INTERVAL = 60*60*1000; //1个小时
    Handler DenHandler = new Handler();
    Runnable DenRunnable = new Runnable() {
        @Override
        public void run() {
            if (latitude != null && longitude != null) {
                sendDensityRequest();
            } else {
                DenHandler.postDelayed(DenRunnable, 5000);
            }
            DenHandler.postDelayed(DenRunnable, DENSITY_TIME_INTERVAL);
        }
    };

    //Location
    private LocationManager lm;
    private static final int LOC_TIME_INTERVAL = 60*1000;//1分钟

    //GPS
    GpsStatus mGpsStatus;
    public static Boolean mInDoor = null;
    private final static int GPS_TIME_INTERVAL = 60*1000;//1分钟


	@Override
	public void onCreate() {
		super.onCreate();
		initDB();
        initMovement();
        initDensity();
        initLocation();
        initGPS();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        DBHandler.removeCallbacks(DBRunnable);
        DenHandler.removeCallbacks(DBRunnable);
    }

	private void initDB() {
        dbHelper = new DBHelper(getApplicationContext());
        db = dbHelper.getReadableDatabase();

        HandlerThread thread = new HandlerThread("DBService");
        thread.start();

        DBHandler = new Handler(thread.getLooper());
        DBHandler.post(DBRunnable);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(year, month, day, 0, 0, 0);

        Long nowTime = calendar.getTime().getTime();
        calendar.set(year, month, day, 23, 59, 59);
        Long nextTime = calendar.getTime().getTime();

        List<State> states = cupboard().withDatabase(db).query(State.class).withSelection("time_point > ? AND time_point < ?", nowTime.toString(), nextTime.toString()).list();
        if (states.isEmpty()) {
            PM25 = 0.0;
            VENTILATION_VOLUME = 0.0;
        } else {
            State state = states.get(states.size() - 1);
            PM25 = Double.parseDouble(state.getPm25());
            VENTILATION_VOLUME = Double.parseDouble(state.getVentilation_volume());
        }


        Intent intent = new Intent(ACTION);
        intent.putExtra("pm2_5", PM25);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_google_plus)
				.setContentTitle("PM2.5")
				.setContentText("服务运行中")
				.setContentIntent(pendingIntent)
				.setOngoing(true);

		startForeground(12450, mBuilder.build());
	}

    private void addPM25() {
        if (mDensity == null || mInDoor == null || mMotionStatus == MotionStatus.NULL)
            return;

        Double breath = 0.0;
        Double density = mDensity;
        if (mInDoor) {
            density /= 3;
        }
        if (mMotionStatus == MotionStatus.STATIC) {
            breath = ConstantValues.static_breath;
        } else if (mMotionStatus == MotionStatus.WALK) {
            breath = ConstantValues.walk_breath;
        } else if (mMotionStatus == MotionStatus.RUN) {
            breath = ConstantValues.run_breath;
        }

        VENTILATION_VOLUME += breath;
        PM25 += density*breath;

//        Toast.makeText(getApplicationContext(), Double.toString(PM25), Toast.LENGTH_SHORT).show();

        State state = new State("0", Long.toString(System.currentTimeMillis()), longitude.toString(), latitude.toString(),
                mInDoor.toString(), mMotionStatus.name(), Integer.toString(numSteps), "", VENTILATION_VOLUME.toString(), PM25.toString(), "");
        insertState(state);

        Intent intent = new Intent(ACTION);
        intent.putExtra("pm2_5", PM25);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void initMovement() {
        numSteps = 0;
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_FASTEST);
        simpleStepDetector = new SimpleStepDetector();
        simpleStepDetector.registerListener(new StepListener() {
            @Override
            public void step(long timeNs) {
                numSteps++;
            }
        });
        time1 = System.currentTimeMillis();
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                event.timestamp, event.values[0], event.values[1], event.values[2]);
        }

        long time2 = System.currentTimeMillis();
        if(time2 - time1 > 5000){
            if (numSteps > 70)
                mMotionStatus = MotionStatus.RUN;
            else if(numSteps <= 70 && numSteps >= 30)
                mMotionStatus = MotionStatus.WALK;
            else
                mMotionStatus = MotionStatus.STATIC;
            numSteps = 0;
            time1 = time2;
        }
    }

    private void initDensity() {
        DenHandler.post(DenRunnable);
    }

    public void sendDensityRequest() {
        String url = String.format(ConstantValues.densityURL, latitude, longitude, System.currentTimeMillis() / 1000);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    mDensity = response.getDouble("PM25");
                } catch (JSONException e) {
                    Logger.e(e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e(error.getMessage());
                Toast.makeText(getApplicationContext(), "无法连接服务器", Toast.LENGTH_SHORT).show();
            }
        });

        VolleyQueue.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void initLocation() {
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
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOC_TIME_INTERVAL, 0, locationListener);
        } catch (SecurityException e) {
            Logger.e(e.getMessage());
        }
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
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

    private void initGPS() {
        try {
            lm.addGpsStatusListener(new GpsStatus.Listener() {
                @Override
                public void onGpsStatusChanged(int event) {
                    mGpsStatus = lm.getGpsStatus(null);
                    switch (event) {
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
            });
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_TIME_INTERVAL, 0, gpsLocationListener);
        } catch (SecurityException e) {
            Toast.makeText(this, "请打开GPS", Toast.LENGTH_SHORT).show();
            Logger.e(e.getMessage());
        }
    }

    private LocationListener gpsLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
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

    /*
    数据库操作
     */
    private void insertState(State state) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        cupboard().withDatabase(db).put(state);
    }

    /*
    暂时不支持数据库
     */

}
