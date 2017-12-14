package de.repictures.stromberg.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GeneralUtils {

    public static List<int[]> parseNestedJsonIntArray(JSONObject jsonObject, String name){
        List<int[]> intList = new ArrayList<>();
        try {
            JSONArray intArray = jsonObject.getJSONArray(name);
            for (int i = 0; i < intArray.length(); i++){
                JSONArray oAmountsArray = intArray.getJSONArray(i);
                int[] oAmounts = new int[oAmountsArray.length()];
                for (int e = 0; e < oAmountsArray.length(); e++){
                    oAmounts[e] = oAmountsArray.getInt(e);
                }
                intList.add(oAmounts);
            }
            return intList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<double[]> parseNestedJsonDoubleArray(JSONObject jsonObject, String name){
        List<double[]> doubleList = new ArrayList<>();
        try {
            JSONArray doubleArray = jsonObject.getJSONArray(name);
            for (int i = 0; i < doubleArray.length(); i++){
                JSONArray oAmountsArray = doubleArray.getJSONArray(i);
                double[] oAmounts = new double[oAmountsArray.length()];
                for (int e = 0; e < oAmountsArray.length(); e++){
                    oAmounts[e] = oAmountsArray.getDouble(e);
                }
                doubleList.add(oAmounts);
            }
            return doubleList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String[]> parseNestedJsonStringArray(JSONObject jsonObject, String name){
        List<String[]> stringList = new ArrayList<>();
        try {
            JSONArray stringArray = jsonObject.getJSONArray(name);
            for (int i = 0; i < stringArray.length(); i++){
                JSONArray oAmountsArray = stringArray.getJSONArray(i);
                String[] oAmounts = new String[oAmountsArray.length()];
                for (int e = 0; e < oAmountsArray.length(); e++){
                    oAmounts[e] = oAmountsArray.getString(e);
                }
                stringList.add(oAmounts);
            }
            return stringList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<boolean[]> parseNestedJsonBooleanArray(JSONObject jsonObject, String name){
        List<boolean[]> stringList = new ArrayList<>();
        try {
            JSONArray stringArray = jsonObject.getJSONArray(name);
            for (int i = 0; i < stringArray.length(); i++){
                JSONArray oAmountsArray = stringArray.getJSONArray(i);
                boolean[] oAmounts = new boolean[oAmountsArray.length()];
                for (int e = 0; e < oAmountsArray.length(); e++){
                    oAmounts[e] = oAmountsArray.getBoolean(e);
                }
                stringList.add(oAmounts);
            }
            return stringList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> parseJsonStringArray(JSONObject jsonObject, String name){
        ArrayList<String> list = new ArrayList<>();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(name);
            int len = jsonArray.length();
            for (int i=0;i<len;i++){
                list.add(jsonArray.getString(i));
            }
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Double> parseJsonDoubleArray(JSONObject jsonObject, String name){
        ArrayList<Double> list = new ArrayList<>();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(name);
            int len = jsonArray.length();
            for (int i=0;i<len;i++){
                list.add(jsonArray.getDouble(i));
            }
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Integer> parseJsonIntArray(JSONObject jsonObject, String name){
        ArrayList<Integer> list = new ArrayList<>();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(name);
            int len = jsonArray.length();
            for (int i=0;i<len;i++){
                list.add(jsonArray.getInt(i));
            }
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
