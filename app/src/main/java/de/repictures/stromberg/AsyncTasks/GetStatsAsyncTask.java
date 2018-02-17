package de.repictures.stromberg.AsyncTasks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.repictures.stromberg.Features.StatsActivity;
import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.R;

public class GetStatsAsyncTask extends AsyncTask<Integer, Void, JSONObject>{

    Internet internetHelper = new Internet();
    private StatsActivity activity;

    public GetStatsAsyncTask(StatsActivity activity){
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        try {
            if (!internetHelper.isNetworkAvailable(activity)) {
                cancel(true);
                JSONObject object = new JSONObject();
                object.put("response", -1);
                onPostExecute(object);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JSONObject doInBackground(Integer... params) {
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        String accountnumber = sharedPref.getString(activity.getResources().getString(R.string.sp_accountnumber), "");
        String webstring = sharedPref.getString(activity.getResources().getString(R.string.sp_webstring), "");
        List<String> companyNumbers = new ArrayList<>(sharedPref.getStringSet(activity.getResources().getString(R.string.sp_companynumbers), new HashSet<>()));
        String urlStr = LoginActivity.SERVERURL + "poststats?authaccountnumber=" + accountnumber + "&authstring=" + webstring + "&companynumber=" + companyNumbers.get(params[0]);
        String jsonString = internetHelper.doGetString(urlStr);
        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject resp) {
        try {
            switch (resp.getInt("response")){
                case -1:
                    //Koi inderned
                    break;
                case 0:
                    //Daten wurden nicht ausreichend übertragen
                    break;
                case 1:
                    //Konto exisitert nicht
                    break;
                case 2:
                    //Mensch ist nicht bei diesem Unternehmen angemeldet
                    break;
                case 3:
                    //Webstring nicht aktuell
                    Intent intent = new Intent(activity, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("webstring_start", true);
                    activity.startActivity(intent);
                    break;
                case 4:
                    //Alles okay
                    JSONArray balanceValuesJson = resp.getJSONArray("balance_development");
                    JSONArray balanceTimesJson = resp.getJSONArray("balance_development_dates");
                    double[] balanceValues = new double[balanceValuesJson.length()];
                    int[] balanceTimes = new int[balanceTimesJson.length()];
                    for (int i = 0; i < balanceValuesJson.length(); i++) {
                        balanceValues[i] = balanceValuesJson.getDouble(i);
                        balanceTimes[i] = balanceTimesJson.getInt(i);
                    }
                    if (resp.has("stromer_value") && resp.has("euro_value")){
                        activity.setStromerEuroCard(resp.getDouble("stromer_value"), resp.getDouble("euro_value"));
                    }
                    activity.setBalanceDevelopment(balanceTimes, balanceValues);
                    activity.updateData();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}