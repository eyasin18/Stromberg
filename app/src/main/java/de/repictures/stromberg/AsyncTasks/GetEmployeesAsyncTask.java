package de.repictures.stromberg.AsyncTasks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.repictures.stromberg.Features.EmployeesActivity;
import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.MainActivity;
import de.repictures.stromberg.POJOs.Account;
import de.repictures.stromberg.R;

public class GetEmployeesAsyncTask extends AsyncTask<Integer, Void, JSONObject>{

    private Internet internetHelper = new Internet();
    private EmployeesActivity employeesActivity;

    public GetEmployeesAsyncTask(EmployeesActivity employeesActivity){

        this.employeesActivity = employeesActivity;
    }

    @Override
    protected void onPreExecute() {
        if (!internetHelper.isNetworkAvailable(employeesActivity)){
            cancel(true);
            JSONObject object = new JSONObject();
            try {
                object.put("responseCode", -1);
                onPostExecute(object);
            } catch (JSONException e) {
                e.printStackTrace();
                onPostExecute(null);
            }
        }
    }

    @Override
    protected JSONObject doInBackground(Integer... params) {
        SharedPreferences sharedPref = employeesActivity.getSharedPreferences(employeesActivity.getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        String accountnumber = sharedPref.getString(employeesActivity.getResources().getString(R.string.sp_accountnumber), "");
        String webstring = sharedPref.getString(employeesActivity.getResources().getString(R.string.sp_webstring), "");
        List<String> companyNumbers = new ArrayList<>(sharedPref.getStringSet(employeesActivity.getResources().getString(R.string.sp_companynumbers), new HashSet<>()));

        String urlStr = LoginActivity.SERVERURL + "postemployees?code=" + webstring + "&accountnumber=" + accountnumber + "&companynumber=" + companyNumbers.get(params[0]);
        String response = internetHelper.doGetString(urlStr);
        try {
            return new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject object) {
        try {
            int responseCode = object.getInt("responseCode");
            switch (responseCode){
                case -1:
                    //koi inderned
                    Snackbar.make(employeesActivity.coordinatorLayout, R.string.internet_problems, BaseTransientBottomBar.LENGTH_INDEFINITE)
                            .show();
                    break;
                case 1:
                    JSONArray accountnumberArray = object.getJSONArray("accountnumbers");
                    JSONArray wagesArray = object.getJSONArray("wages");
                    JSONArray startTimesArray = object.getJSONArray("start_times");
                    JSONArray endTimesArray = object.getJSONArray("end_times");
                    JSONArray featuresArray = object.getJSONArray("features");
                    List<Account> accountList = new ArrayList<>();

                    for (int i = 0; i < accountnumberArray.length(); i++) {
                        Account account = new Account();
                        account.setAccountnumber(accountnumberArray.getString(i));
                        account.setWage(wagesArray.getDouble(i));
                        List<Integer> startTimes = new ArrayList<>();
                        List<Integer> endTimes = new ArrayList<>();
                        List<Integer> features = new ArrayList<>();
                        if (startTimesArray.length() > i && endTimesArray.length() > i && startTimesArray.getJSONArray(i).length() > 0 && endTimesArray.getJSONArray(i).length() > 0)
                            for (int j = 0; j < startTimesArray.getJSONArray(i).length(); j++) {
                                if (startTimesArray.getJSONArray(i).getString(j).length() > 0) startTimes.add(startTimesArray.getJSONArray(i).getInt(j));
                                if (endTimesArray.getJSONArray(i).getString(j).length() > 0) endTimes.add(endTimesArray.getJSONArray(i).getInt(j));
                            }
                        if (featuresArray.length() > 0)
                            for (int j = 0; j < featuresArray.getJSONArray(i).length(); j++) {
                                features.add(featuresArray.getJSONArray(i).getInt(j));
                            }
                        account.setStartTimesInt(startTimes);
                        account.setEndTimesInt(endTimes);
                        account.setPermissions(features);
                        accountList.add(account);
                    }

                    employeesActivity.setAdapter(accountList);
                    break;
                case 2:
                    //Du hasch ned die berechtigung dazu
                    Intent i = new Intent(employeesActivity, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    employeesActivity.startActivity(i);
                    break;
                case 3:
                    //Authstring abgelaufen
                    i = new Intent(employeesActivity, LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    employeesActivity.startActivity(i);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
