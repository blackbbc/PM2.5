package me.sweetll.pm25demo.service;

import java.util.Calendar;

import android.app.IntentService;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import me.sweetll.pm25demo.MainActivity;
import me.sweetll.pm25demo.constants.ConstantValues;
import me.sweetll.pm25demo.model.StateInformation;
import me.sweetll.pm25demo.util.DBAccess;

public class DBService extends IntentService {
    public static final String ACTION = "me.sweetll.pm25demo.service.DBService";

	public int span = 5000;
	private DBAccess db;

    public static Double PM25 = 0.0;

	public static final int TIME_INTERVAL = 5000;

    public Handler handler = new Handler();
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            addPM25();
            handler.postDelayed(runnable, TIME_INTERVAL);
        }
    };

	public DBService() {
		super("database");
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

	}

    public void addPM25() {
        Double mDensity = DensityService.mDensity;
        Boolean mInDoor = GPSService.mInDoor;
        MainActivity.MotionStatus mMotionStatus = MainActivity.motionStatus;

        if (mDensity == null || mInDoor == null || mMotionStatus == MainActivity.MotionStatus.NULL)
            return;

        Double breath = 0.0;
        if (mInDoor) {
            mDensity /= 3;
        }
        if (mMotionStatus == MainActivity.MotionStatus.STATIC) {
            breath = ConstantValues.static_breath;
        } else if (mMotionStatus == MainActivity.MotionStatus.WALK) {
            breath = ConstantValues.bicycle_breath;
        } else if (mMotionStatus == MainActivity.MotionStatus.RUN) {
            breath = ConstantValues.run_breath;
        }

        PM25 += mDensity*breath;

        Intent intent = new Intent(ACTION);
        intent.putExtra("pm2_5", PM25);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

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
