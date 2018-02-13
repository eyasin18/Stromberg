package de.repictures.stromberg.POJOs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.repictures.stromberg.R;

public class Account implements Serializable {

    private List<Integer> startTimesInt = new ArrayList<>();
    private List<Integer> endTimesInt = new ArrayList<>();
    private List<Integer> permissions = new ArrayList<>();
    private double wage = 1.0;
    private String accountnumber = null;

    public List<Integer> getStartTimesInt() {
        return startTimesInt;
    }

    public void setStartTimesInt(List<Integer> startTimesInt) {
        this.startTimesInt = startTimesInt;
    }

    public List<Integer> getEndTimesInt() {
        return endTimesInt;
    }

    public void setEndTimesInt(List<Integer> endTimesInt) {
        this.endTimesInt = endTimesInt;
    }

    public double getWage() {
        return wage;
    }

    public void setWage(double wage) {
        this.wage = wage;
    }

    public String getAccountnumber() {
        return accountnumber;
    }

    public void setAccountnumber(String accountnumber) {
        this.accountnumber = accountnumber;
    }

    public List<Integer> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Integer> permissions) {
        this.permissions = permissions;
    }

    public static int getDaysFromMinutes(int minutes){
        return minutes/1440;
    }

    public static int getHoursFromMinutes(int minutes){
        int minutesOfDay = minutes%1440;
        return minutesOfDay/60;
    }

    public static int getMinutesOfHourFromMinutes(int minutes){
        int minutesOfDay = minutes%1440;
        return minutesOfDay%60;
    }

    public static int getMinutesFromValues(int days, int hours, int minutes){
        return days*1440 + hours*60 + minutes;
    }

    public static List<Integer> getSpecificFeaturesLongListFromString(String jsonStr, String companynumber){
        List<Integer> features = new ArrayList<>();
        try {
            JSONObject featuresObject = new JSONObject(jsonStr);
            JSONArray featuresArray = featuresObject.getJSONArray(companynumber);
            for (int i = 0; i < featuresArray.length(); i++){
                features.add(featuresArray.getInt(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return features;
    }
}