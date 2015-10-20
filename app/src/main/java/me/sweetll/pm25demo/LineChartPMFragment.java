package me.sweetll.pm25demo;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.sweetll.pm25demo.model.State;
import me.sweetll.pm25demo.util.DBHelper;
import me.sweetll.pm25demo.util.YUnitFormatter;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by sweet on 15-9-3.
 */
public class LineChartPMFragment extends Fragment {
    private static final String ARG_CHART = "chart_type";

    @Bind(R.id.chart_line) LineChart mChart;

    private int mChartType;
    private Typeface mTf;

    DBHelper dbHelper;
    SQLiteDatabase db;

    protected String[] mMonths = new String[] {
            "2点", "4点", "6点", "8点", "10点", "12点", "14点", "16点", "18点", "20点", "22点", "24点"
    };

    Handler chartHandler;
    Runnable chartRunnable = new Runnable() {
        @Override
        public void run() {
            initChart();
            chartHandler.postDelayed(chartRunnable, 60*1000);
        }
    };


    public static LineChartPMFragment newInstance(int chartType) {
        LineChartPMFragment fragment = new LineChartPMFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CHART, chartType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mChartType = getArguments().getInt(ARG_CHART, 1);
        dbHelper = new DBHelper(getContext());
        db = dbHelper.getReadableDatabase();
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_line_chart, container, false);
        ButterKnife.bind(this, view);

        HandlerThread thread = new HandlerThread("line_pm");
        thread.start();
        chartHandler = new Handler(thread.getLooper());
        chartHandler.post(chartRunnable);

        return view;
    }

    private void initChart() {
        // no description text
        mChart.setDescription("");

        // enable value highlighting
        mChart.setHighlightEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);

        mTf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTf);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(2);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(mTf);
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setValueFormatter(new YUnitFormatter("ug"));

        mChart.getAxisRight().setEnabled(false);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        // add data
        setData(12, 150);

        // dont forget to refresh the drawing
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mChart.invalidate();
            }
        });
    }


    private void setData(int count, float range) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add(mMonths[i % 12]);
        }

        ArrayList<Entry> vals1 = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            calendar.set(year, month, day, 2*i, 0, 0);

            Long nowTime = calendar.getTime().getTime();
            calendar.set(year, month, day, 2*(i+1), 0, 0);
            Long nextTime = calendar.getTime().getTime();

            //数据库查询
            Float val;
            State state = cupboard().withDatabase(db).query(State.class).withSelection("time_point > ? AND time_point < ?", nowTime.toString(), nextTime.toString()).get();
            if (state == null) {
                val = 0.0f;
            } else {
                val = Float.parseFloat(state.getPm25());
            }

            vals1.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(vals1, "累计吸入的PM2.5量");
        set1.setDrawCubic(true);
        set1.setCubicIntensity(0.2f);
        //set1.setDrawFilled(true);
        set1.setDrawCircles(false);
        set1.setLineWidth(2f);
        set1.setCircleSize(5f);
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setColor(Color.rgb(104, 241, 175));
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setDrawHorizontalHighlightIndicator(false);

        // create a data object with the datasets
        LineData data = new LineData(xVals, set1);
        data.setValueTypeface(mTf);
        data.setValueTextSize(9f);
        data.setDrawValues(false);

        // set data
        mChart.setData(data);
    }

}
