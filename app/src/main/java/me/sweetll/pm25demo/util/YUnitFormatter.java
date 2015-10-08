package me.sweetll.pm25demo.util;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by sweet on 15-10-8.
 */
public class YUnitFormatter implements YAxisValueFormatter {
    private String unit;

    public YUnitFormatter(String unit) {
        this.unit = unit;
    }

    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        return Integer.toString((int)value) + unit;
    }

}
