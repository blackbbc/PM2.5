package me.sweetll.pm25demo.service;

import java.util.Timer;
import java.util.TimerTask;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.model.LatLng;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Vibrator;
import android.widget.TextView;

public class LocationService extends Service {
	
	private LocationClient mLocationClient;
    public MyLocationListener mMyLocationListener;
    public TextView tv;
    public Vibrator mVibrator;
    public int span = 1000;
    private double latitude,longtitude;
    private boolean flag;
	public String city;

    @Override
	public void onCreate() {   
    	flag = true;
        mLocationClient = new LocationClient(this.getApplicationContext());
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);       
        mLocationClient.registerLocationListener(new MyLocationListener());     
        this.initLocation();
    }    
    
    @Override
	public void onDestroy() {  
    	mLocationClient.stop();
    	super.onDestroy();
    }  
           
    @Override
    public void onStart(Intent intent,int startId) {
    	mLocationClient.start();  
    	super.onStart(intent, startId);
    }

	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	
	@Override  
    public int onStartCommand(Intent intent, int flags, int startId) {//��дonStartCommand����  
		IntentFilter filter = new IntentFilter();//����IntentFilter����  
		filter.addAction(".Locationervice");  
        doJob();//���÷��������߳�  
        return super.onStartCommand(intent, flags, startId);  
    }  
	
	 public void doJob(){  
         new Thread(){  
        	 public void run(){  
        		 while(flag){  
        			 try{//˯��һ��ʱ��  
        				 Thread.sleep(span);  
        			 	}	  
                      	catch(Exception e){  
                      		e.printStackTrace();  
                      	}  
                       	Intent intent = new Intent();//����Intent����  
                        intent.setAction(".MainActivity");  
                        intent.putExtra("latitude", latitude);  
                        intent.putExtra("longtitude", longtitude); 
                        intent.putExtra("city", city);
                        sendBroadcast(intent);//���͹㲥  
        		 }                                  
        	 }                   
         }.start();  
	 } 
	 
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
        	latitude = location.getLatitude();
        	longtitude = location.getLongitude();
//        	city = location.getCity();
//        	city = city.substring(0,city.length()-1);
        }
    }
    
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);//��ѡ��Ĭ�ϸ߾��ȣ����ö�λģʽ���߾��ȣ��͹��ģ����豸
        option.setCoorType("bd09ll");//��ѡ��Ĭ��gcj02�����÷��صĶ�λ�������ϵ��
        option.setScanSpan(span);//��ѡ��Ĭ��0��������λһ�Σ����÷���λ����ļ����Ҫ���ڵ���1000ms������Ч��
        option.setIsNeedAddress(true);//��ѡ�������Ƿ���Ҫ��ַ��Ϣ��Ĭ�ϲ���Ҫ
        option.setOpenGps(true);//��ѡ��Ĭ��false,�����Ƿ�ʹ��gps
        option.setLocationNotify(true);//��ѡ��Ĭ��false�������Ƿ�gps��Чʱ����1S1��Ƶ�����GPS���
        option.setIgnoreKillProcess(true);//��ѡ��Ĭ��true����λSDK�ڲ���һ��SERVICE�����ŵ��˶������̣������Ƿ���stop��ʱ��ɱ��������̣�Ĭ�ϲ�ɱ��
        option.setEnableSimulateGps(false);//��ѡ��Ĭ��false�������Ƿ���Ҫ����gps��������Ĭ����Ҫ
        option.setIsNeedLocationDescribe(true);//��ѡ��Ĭ��false�������Ƿ���Ҫλ�����廯�����������BDLocation.getLocationDescribe��õ�����������ڡ��ڱ����찲�Ÿ�����
        option.setIsNeedLocationPoiList(true);//��ѡ��Ĭ��false�������Ƿ���ҪPOI�����������BDLocation.getPoiList��õ�
        mLocationClient.setLocOption(option);
    }
    

}
