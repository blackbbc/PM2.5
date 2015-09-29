package me.sweetll.pm25demo.service;

import java.util.Calendar;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import me.sweetll.pm25demo.model.StateInformation;
import me.sweetll.pm25demo.util.DBAccess;

public class DBService extends Service {

	public boolean flag = true;
	public int span = 5000;
	private DBAccess db;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {   
    	flag = true;  
    	db = new DBAccess(this);
		addStates();
		Log.v("add states","add states success");
    }
	
	@Override  
    public int onStartCommand(Intent intent, int flags, int startId) {
		IntentFilter filter = new IntentFilter();
		filter.addAction(".Locationservice");  
		Log.v("do job","we are doing jobs");
        doJob();//
        return super.onStartCommand(intent, flags, startId);  
    }  
	
	 public void doJob(){  
         new Thread(){  
        	 public void run(){  
        		 while(flag){  
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
