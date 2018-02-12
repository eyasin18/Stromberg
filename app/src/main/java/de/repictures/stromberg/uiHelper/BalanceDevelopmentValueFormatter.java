package de.repictures.stromberg.uiHelper;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;

public class BalanceDevelopmentValueFormatter implements IAxisValueFormatter {

    private DecimalFormat decimalFormat = new DecimalFormat("0.00");

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return decimalFormat.format(value) + "S";
    }
}
