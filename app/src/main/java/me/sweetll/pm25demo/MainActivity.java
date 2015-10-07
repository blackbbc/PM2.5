package me.sweetll.pm25demo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;

import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.DecoDrawEffect;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.orhanobut.logger.Logger;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.Tencent;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;
import me.sweetll.pm25demo.service.DBService;

public class MainActivity extends AppCompatActivity implements GooeyMenu.GooeyMenuInterface {
    @Bind(R.id.drawer_layout) DrawerLayout mDrawer;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.dynamicArcView) DecoView arcView;
    @Bind(R.id.pm_num) TextView pm_num_view;
    @Bind(R.id.healthy_status) TextView healthy_view;
    @Bind(R.id.gooey_menu) GooeyMenu gooeyMenu;

    private ActionBarDrawerToggle actionBarDrawerToggle;
    int seriesIndex;
    int series1Index;

    Tencent mTencent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_menu));
        ab.setDisplayHomeAsUpEnabled(true);

        actionBarDrawerToggle = setupDrawerToggle();
        mDrawer.setDrawerListener(actionBarDrawerToggle);

        gooeyMenu.setOnMenuListener(this);

        ViewPager chartViewpager = (ViewPager) findViewById(R.id.viewpager_chart);
        CircleIndicator chartIndicator = (CircleIndicator) findViewById(R.id.indicator_chart);

        ChartPagerAdapter chartPagerAdapter = new ChartPagerAdapter(getSupportFragmentManager());
        chartViewpager.setAdapter(chartPagerAdapter);
        chartIndicator.setViewPager(chartViewpager);

        mTencent = Tencent.createInstance("1104861076", getApplicationContext());

        initArcView();
        initDBService();       //数据库服务
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(DBService.ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(pm25Receiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(pm25Receiver);
    }

    private BroadcastReceiver pm25Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Double mPm2_5 = intent.getDoubleExtra("pm2_5", 0);
            arcView.addEvent(new DecoEvent.Builder(mPm2_5.intValue())
                    .setIndex(series1Index)
                    .build());
            pm_num_view.setText(String.valueOf(mPm2_5.intValue()));
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    private void initArcView() {
        // Create background track
        SeriesItem seriesItem = new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 1000, 0)
                .setInitialVisibility(true)
                .setLineWidth(24f)
                .build();

        //Create data series track
        SeriesItem seriesItem1 = new SeriesItem.Builder(Color.argb(255, 64, 196, 0))
                .setRange(0, 1000, 0)
                .setInitialVisibility(false)
                .setLineWidth(24f)
                .build();

        seriesItem1.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float v, float v1) {
                pm_num_view.setText("" + (int)v1 + "微克");
                healthy_view.setText("约等于" + (int)(v1 / 12) + "支烟");
            }

            @Override
            public void onSeriesItemDisplayProgress(float v) {

            }
        });

        seriesIndex = arcView.addSeries(seriesItem);
        series1Index = arcView.addSeries(seriesItem1);

        arcView.addEvent(new DecoEvent.Builder(1000)
                .setIndex(seriesIndex)
                .setDuration(3000)
                .setDelay(100)
                .build());

        arcView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                .setIndex(series1Index)
                .setDuration(2000)
                .setDelay(1250)
                .build());

    }

    protected void initDBService() {
        Intent DBIntent = new Intent(this, DBService.class);
        DBIntent.putExtra("city", "上海");
        startService(DBIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.action_map:
                Intent intent = new Intent(this, MapActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                return true;
            case R.id.action_quit:
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
                mBuilder.setTitle("退出");
                mBuilder.setMessage("真的要退出吗？");
                mBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this, DBService.class);
                        stopService(intent);
                        finish();
                    }
                });
                mBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                mBuilder.create().show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void menuOpen() {

    }

    @Override
    public void menuClose() {

    }

    public File getScreenShot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            String mDirectoryPath = Environment.getExternalStorageDirectory().toString() + "/PM2.5/";
            String mFilePath = mDirectoryPath + now + ".jpg";
            View view = getWindow().getDecorView().getRootView();
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);

            File imageDirectory = new File(mDirectoryPath);
            File imageFile = new File(mFilePath);
            if (!imageDirectory.exists()) {
                imageDirectory.mkdirs();
            }

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            return imageFile;
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public void menuItemClicked(int menuNumber) {
        Intent shareIntent = new Intent();
        switch (menuNumber) {
            case 1:
                shareIntent.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI"));
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.putExtra("Kdescription", "今天我的PM2.5吸入量");
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(getScreenShot()));
                startActivity(shareIntent);
                break;
            case 2:
                final Bundle params = new Bundle();
                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
                params.putString(QQShare.SHARE_TO_QQ_TITLE, "PM2.5");
                params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  "今天我的PM2.5吸入量");
                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  "http://www.qq.com/news/1.html");
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, getScreenShot().getAbsolutePath());
                params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "PM2.5Demo");
                mTencent.shareToQQ(MainActivity.this, params, null);
                break;
            case 3:
                break;
        }
    }

}
