package de.repictures.stromberg.POJOs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Transfer {

    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSSS z", Locale.GERMANY);
    private Calendar time;
    private String otherPersonName;
    private String otherPersonAccountnumber;
    private String type;
    private String purpose;
    private boolean isSender;
    private double amount;

    public Calendar getTime() {
        return time;
    }

    public String getTimeStr() {
        return sdf.format(time.getTime());
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    public void setTime(String timeStr){
        time = Calendar.getInstance();
        try {
            time.setTime(sdf.parse(timeStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getOtherPersonName() {
        return otherPersonName;
    }

    public void setOtherPersonName(String otherPersonName) {
        this.otherPersonName = otherPersonName;
    }

    public String getOtherPersonAccountnumber() {
        return otherPersonAccountnumber;
    }

    public void setOtherPersonAccountnumber(String otherPersonAccountnumber) {
        this.otherPersonAccountnumber = otherPersonAccountnumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public boolean isSender() {
        return isSender;
    }

    public void setSender(boolean sender) {
        isSender = sender;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
