package me.sweetll.pm25demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.sweetll.pm25demo.constants.LocationInformation;
import me.sweetll.pm25demo.service.LocationService;

public class MapActivity extends AppCompatActivity {
    @Bind(R.id.bmapView) MapView mMapView;

    private BaiduMap mBaiduMap = null;
    private DataReceiver dataReceiver;
    public LocationInformation Li;

    private double preLat = -1, preLon = -1;
    public ArrayList<LatLng> points;
    public boolean ViewSettingDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);

        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        Li = new LocationInformation();
        points = new ArrayList<>();
        startService(new Intent(this, LocationService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        dataReceiver = new DataReceiver();
        IntentFilter filter = new IntentFilter();//
        filter.addAction(".MainActivity");
        registerReceiver(dataReceiver, filter);
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    private class DataReceiver extends BroadcastReceiver {//¼Ì³Ð×ÔBroadcastReceiverµÄ×ÓÀà
        @Override
        public void onReceive(Context context, Intent intent) {//ÖØÐ´onReceive·½·¨
            double latitude = intent.getDoubleExtra("latitude", 0);
            double longtitude = intent.getDoubleExtra("longtitude", 0);
            String city = intent.getStringExtra("city");
//            cityView.setText("µ±Ç°³ÇÊÐ£º "+city);
//            locationView.setText("µ±Ç°GPS×ø±ê£º "+latitude + " " + longtitude + " " + points.size());

            Li.setCity(city);
            Li.setLatitude(latitude);
            Li.setLongtitude(longtitude);
            int minute = Calendar.getInstance().get(Calendar.MINUTE);

//            String value = MainActivity.this.getString(R.string.defaultPM25);
//            if (pm2_5View.getText().toString() == value) {
//                pm2_5View.setText("µ±Ç°PM2.5Å¨¶È£º "+Li.getPm2_5_density()+"ºÁ¿Ë/Ã¿Á¢·½Ã×");
//            }
//            if ((minute-10)%60==0) {
//                pm2_5View.setText("µ±Ç°PM2.5Å¨¶È£º "+Li.getPm2_5_density()+"ºÁ¿Ë/Ã¿Á¢·½Ã×");
//            }
            if ((preLat!=latitude&&preLon!=longtitude)&&(latitude!=0&&longtitude!=0)) {
                LatLng point = new LatLng(latitude,longtitude);
                points.add(point);
                preLat = latitude;
                preLon = longtitude;
                drawTrajectory();
                if (!ViewSettingDone) {
                    setViewAngle(point);
                    ViewSettingDone = true;
                }
            }
        }
    }

    public void drawTrajectory() {
        if (points.size()>=2) {
            OverlayOptions ooPolyline = new PolylineOptions().width(5).color(0xAAFF0000).points(points);
            mBaiduMap.addOverlay(ooPolyline);
        }
    }

    private void setViewAngle(LatLng cenpt) {
        MapStatus mMapStatus = new MapStatus.Builder().target(cenpt).zoom(15).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mBaiduMap.setMapStatus(mMapStatusUpdate);
    }

}
