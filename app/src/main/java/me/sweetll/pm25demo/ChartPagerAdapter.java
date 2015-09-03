package me.sweetll.pm25demo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by sweet on 15-9-3.
 */
public class ChartPagerAdapter extends FragmentPagerAdapter {
    public static final int CHART_BAR = 0;
    public static final int CHART_LINE = 1;

    private int pagerCount = 2;

    public ChartPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override public Fragment getItem(int i) {
        switch (i) {
            case CHART_BAR:
                return BarChartFragment.newInstance(i);
            case CHART_LINE:
                return LineChartFragment.newInstance(i);
            default:
                return null;
        }
    }

    @Override public int getCount() {
        return pagerCount;
    }
}
