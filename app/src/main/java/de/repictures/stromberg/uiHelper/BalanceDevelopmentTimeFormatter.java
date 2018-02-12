package de.repictures.stromberg.uiHelper;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.Calendar;

import de.repictures.stromberg.POJOs.Account;

public class BalanceDevelopmentTimeFormatter implements IAxisValueFormatter {

    private String[] weekdays = {"Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"};
    private DecimalFormat decimalFormat = new DecimalFormat("00");

    @Override
    public String getFormattedValue(float valueFloat, AxisBase axis) {
        int time = (int) valueFloat;
        int days = Account.getDaysFromMinutes(time);
        int hours = Account.getHoursFromMinutes(time);
        int minutes = Account.getMinutesOfHourFromMinutes(time);
        return weekdays[days%7] + " " + decimalFormat.format(hours) + ":" + decimalFormat.format(minutes);
    }
}