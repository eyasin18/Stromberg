package de.repictures.stromberg.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.repictures.stromberg.POJOs.Product;

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

    public static List<Integer> parseJsonIntArray(JSONArray jsonArray){
        ArrayList<Integer> list = new ArrayList<>();
        try {
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

    public static List<Integer> parseJsonIntArray(String jsonArrayStr){
        ArrayList<Integer> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonArrayStr);
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

    public static List<Boolean> parseJsonBooleanArray(JSONObject jsonObject, String name){
        ArrayList<Boolean> list = new ArrayList<>();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(name);
            int len = jsonArray.length();
            for (int i=0;i<len;i++){
                list.add(jsonArray.getBoolean(i));
            }
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String arrayToString(ArrayList list){
        StringBuilder stringBuilder = new StringBuilder();
        for (Object object : list){
            stringBuilder.append(String.valueOf(object)).append("ò");
        }
        return stringBuilder.toString();
    }

    public static Product[] appendProduct(Product[] productArray, Product newProduct) {
        ArrayList<Product> products = new ArrayList<>(Arrays.asList(productArray));
        products.add(newProduct);
        return products.toArray(new Product[0]);
    }

    public static int[] appendInt(int[] a, int e) {
        a  = Arrays.copyOf(a, a.length + 1);
        a[a.length - 1] = e;
        return a;
    }
}