package me.sweetll.pm25demo;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.DecoDrawEffect;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;
import me.sweetll.pm25demo.service.GPSService;

public class MainActivity extends AppCompatActivity implements GooeyMenu.GooeyMenuInterface {
    @Bind(R.id.drawer_layout) DrawerLayout mDrawer;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.dynamicArcView) DecoView arcView;
    @Bind(R.id.pm_num) TextView pm_num_view;
    @Bind(R.id.healthy_status) TextView healthy_view;
    @Bind(R.id.gooey_menu) GooeyMenu gooeyMenu;

    private ActionBarDrawerToggle actionBarDrawerToggle;

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

        initArcView();
        initGPSService();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    private void initArcView() {
        // Create background track
        SeriesItem seriesItem = new SeriesItem.Builder(Color.argb(255, 218, 218, 218))
                .setRange(0, 100, 0)
                .setInitialVisibility(true)
                .setLineWidth(24f)
                .build();

        //Create data series track
        SeriesItem seriesItem1 = new SeriesItem.Builder(Color.argb(255, 64, 196, 0))
                .setRange(0, 100, 0)
                .setInitialVisibility(false)
                .setLineWidth(24f)
                .build();

        seriesItem1.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float v, float v1) {
                pm_num_view.setText("" + (int)v1 + "微克");
                healthy_view.setText("约等于" + (int)(v1 / 10) + "支烟");
            }

            @Override
            public void onSeriesItemDisplayProgress(float v) {

            }
        });

        int seriesIndex = arcView.addSeries(seriesItem);
        int series1Index = arcView.addSeries(seriesItem1);

        arcView.addEvent(new DecoEvent.Builder(100)
                .setIndex(seriesIndex)
                .setDuration(3000)
                .setDelay(100)
                .build());

        arcView.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                .setIndex(series1Index)
                .setDuration(2000)
                .setDelay(1250)
                .build());

        arcView.addEvent(new DecoEvent.Builder(25)
                .setIndex(series1Index)
                .setDelay(3250)
                .build());
    }

    private void initGPSService() {
        Intent GPSIntent= new Intent(this, GPSService.class);
        startService(GPSIntent);
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
            String mPath = Environment.getExternalStorageDirectory().toString() + "/PM2.5/" + now + ".jpg";
            View view = getWindow().getDecorView().getRootView();
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

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
//        Toast.makeText(this, "" + menuNumber, Toast.LENGTH_SHORT).show();
        switch (menuNumber) {
            case 1:
                Intent shareIntent = new Intent();
                shareIntent.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI"));
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.putExtra("Kdescription", "This is a demo");
                Uri uri = Uri.fromFile(getScreenShot());
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(shareIntent);
                break;
            case 2:
                break;
            case 3:
                break;
        }
    }

}
