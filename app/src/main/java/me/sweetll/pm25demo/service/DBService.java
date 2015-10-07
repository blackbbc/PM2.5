package me.sweetll.pm25demo.service;

import java.util.Calendar;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import me.sweetll.pm25demo.MainActivity;
import me.sweetll.pm25demo.R;
import me.sweetll.pm25demo.constants.ConstantValues;
import me.sweetll.pm25demo.model.StateInformation;
import me.sweetll.pm25demo.movement.SimpleStepDetector;
import me.sweetll.pm25demo.movement.StepListener;
import me.sweetll.pm25demo.util.DBAccess;

public class DBService extends Service implements SensorEventListener {
    public static final String ACTION = "me.sweetll.pm25demo.service.DBService";

	public int span = 5000;
	private DBAccess db;

    public static Double PM25 = 0.0;

	public static final int TIME_INTERVAL = 5000;

    private HandlerThread thread;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            addPM25();
            handler.postDelayed(runnable, TIME_INTERVAL);
        }
    };

    //Movement
    private SimpleStepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private int numSteps;
    private long time1;
    public static MotionStatus motionStatus = MotionStatus.STATIC;
    public enum MotionStatus{
        NULL, STATIC, WALK, RUN
    }


	@Override
	public void onCreate() {
		super.onCreate();
		init();
        initMovement();
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
    }

	protected void init() {
        HandlerThread thread = new HandlerThread("DBService");
        thread.start();

        handler = new Handler(thread.getLooper());
        handler.post(runnable);

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
                motionStatus = MotionStatus.RUN;
            else if(numSteps <= 70 && numSteps >= 30)
                motionStatus = MotionStatus.WALK;
            else
                motionStatus = MotionStatus.STATIC;
            numSteps = 0;
            time1 = time2;
        }
    }

    public void addPM25() {
        Double mDensity = DensityService.mDensity;
        Boolean mInDoor = GPSService.mInDoor;
        MotionStatus mMotionStatus = motionStatus;

        if (mDensity == null || mInDoor == null || mMotionStatus == MotionStatus.NULL)
            return;

        Double breath = 0.0;
        if (mInDoor) {
            mDensity /= 3;
        }
        if (mMotionStatus == MotionStatus.STATIC) {
            breath = ConstantValues.static_breath;
        } else if (mMotionStatus == MotionStatus.WALK) {
            breath = ConstantValues.walk_breath;
        } else if (mMotionStatus == MotionStatus.RUN) {
            breath = ConstantValues.run_breath;
        }

        PM25 += mDensity*breath;

        Intent intent = new Intent(ACTION);
        intent.putExtra("pm2_5", PM25);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /*
    暂时不支持数据库
     */
	 public void doJob(){
         new Thread(){  
        	 public void run(){  
        			 try{
        				 Thread.sleep(span);  
        			 	}	  
                      	catch(Exception e){  
                      		e.printStackTrace();  
                      	}  
                       	Intent intent = new Intent();
                        intent.setAction(".MainActivity");  
                        Log.v("here","query information");
                        StateInformation si = getNewestStateInformation();
                        if (si!=null) {
                            Log.v("id",si.getId()+"");
	                        intent.putExtra("density", si.getPm25());
	                        intent.putExtra("outdoor", si.getOutdoor());
	                        intent.putExtra("status", si.getStatus());
	                        sendBroadcast(intent);
                        } else {
                        	Log.v("state","state is null");
                        }
                        addStates();
        		 }                                  
         }.start();
	 } 
	 
	 private StateInformation getNewestStateInformation() {
		 int size = db.findAllStateInfomation().size();
		 Log.v("total size",size+"");
		 StateInformation si = db.selectLastStateInformation();		 
		 return si;
	 }
	 
	 private void addStates() {
		String values[] = new String[11];
		Calendar instance = Calendar.getInstance();
		int id = instance.get(Calendar.MINUTE)*60+instance.get(Calendar.SECOND);
		for (int i=0;i<11;i++) {
			values[i] = id+"test";
		}
		Log.v("id",id+"");
		StateInformation si = new StateInformation(id,values[0],values[1],values[2],values[3],values[4],
				values[5],values[6],values[7],values[8],values[9],values[10]);
		db.insertStateInfomation(si);
	}
	 
}
