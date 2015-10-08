package me.sweetll.pm25demo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by sweet on 15-9-3.
 */
public class ChartPagerAdapter extends FragmentPagerAdapter {
    private int pagerCount = 4;

    public ChartPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return BarChartBreathFragment.newInstance(i);
            case 1:
                return LineChartBreathFragment.newInstance(i);
            case 2:
                return BarChartPMFragment.newInstance(i);
            case 3:
                return LineChartPMFragment.newInstance(i);
            default:
                return null;
        }
    }

    @Override public int getCount() {
        return pagerCount;
    }
}
