package me.sweetll.pm25demo;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.sweetll.pm25demo.model.State;
import me.sweetll.pm25demo.util.DBHelper;
import me.sweetll.pm25demo.util.YUnitFormatter;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by sweet on 15-9-3.
 */
public class BarChartBreathFragment extends Fragment {
    private static final String ARG_CHART = "chart_type";

    @Bind(R.id.chart_bar) BarChart mChart;

    private int mChartType;
    private Typeface mTf;

    DBHelper dbHelper;
    SQLiteDatabase db;

    protected String[] mMonths = new String[] {
            "2点", "4点", "6点", "8点", "10点", "12点", "14点", "16点", "18点", "20点", "22点", "24点"
    };

    public static BarChartBreathFragment newInstance(int chartType) {
        BarChartBreathFragment fragment = new BarChartBreathFragment();
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
        View view = inflater.inflate(R.layout.fragment_bar_chart, container, false);
        ButterKnife.bind(this, view);
        initChart();
        return view;
    }

    private void initChart() {
        mChart.setVisibility(View.VISIBLE);
        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);

        mChart.setDescription("");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        // draw shadows for each bar that show the maximum value
        // mChart.setDrawBarShadow(true);

        // mChart.setDrawXLabels(false);

        mChart.setDrawGridBackground(false);
        // mChart.setDrawYLabels(false);

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
        leftAxis.setValueFormatter(new YUnitFormatter("L"));

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setTypeface(mTf);
        rightAxis.setEnabled(false);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        setData(12, 50);

        // dont forget to refresh the drawing
        mChart.invalidate();
    }

    private void setData(int count, float range) {
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add(mMonths[i % 12]);
        }

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

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
            List<State> states = cupboard().withDatabase(db).query(State.class).withSelection("time_point > ? AND time_point < ?", nowTime.toString(), nextTime.toString()).list();
            State firstState = states.get(0);
            State lastState = states.get(states.size() - 1);
            Float val = Float.parseFloat(lastState.getVentilation_volume()) - Float.parseFloat(firstState.getVentilation_volume());

//            float mult = (range + 1);
//            float val = (float) (Math.random() * mult);
            yVals1.add(new BarEntry(val, i));
        }

        BarDataSet set1 = new BarDataSet(yVals1, "单位时间吸入的空气量");
        set1.setBarSpacePercent(35f);

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        data.setValueTypeface(mTf);

        mChart.setData(data);
    }


}
