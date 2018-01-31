package de.repictures.stromberg.AsyncTasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import de.repictures.stromberg.Features.EditEmployeeActivity;
import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.POJOs.Account;
import de.repictures.stromberg.R;

public class UpdateEmployeeAsyncTask extends AsyncTask<Account, Void, String>{

    private final String TAG = this.getClass().getName();
    private EditEmployeeActivity editEmployeeActivity;
    private Internet internetHelper = new Internet();

    @Override
    protected void onPreExecute() {
        if (!internetHelper.isNetworkAvailable(editEmployeeActivity)){
            cancel(true);
            onPostExecute("-1");
        }
    }

    public UpdateEmployeeAsyncTask(EditEmployeeActivity editEmployeeActivity) {

        this.editEmployeeActivity = editEmployeeActivity;
    }

    @Override
    protected String doInBackground(Account... accounts) {
        JSONObject object = new JSONObject();
        JSONArray startTimesArray = new JSONArray();
        JSONArray endTimesArray = new JSONArray();
        JSONArray featuresArray = new JSONArray();

        for (int i = 0; i < accounts[0].getStartTimesInt().size(); i++) {
            startTimesArray.put(accounts[0].getStartTimesInt().get(i));
            endTimesArray.put(accounts[0].getEndTimesInt().get(i));
        }

        for (int feature : accounts[0].getPermissions()) {
            featuresArray.put(feature);
        }

        try {
            object.put("accountnumber", accounts[0].getAccountnumber());
            object.put("wage", accounts[0].getWage());
            object.put("start_times", startTimesArray);
            object.put("end_times", endTimesArray);
            object.put("features", featuresArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonObjectStr = null;
        try {
            jsonObjectStr = URLEncoder.encode(object.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPref = editEmployeeActivity.getSharedPreferences(editEmployeeActivity.getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        String accountnumber = sharedPref.getString(editEmployeeActivity.getResources().getString(R.string.sp_accountnumber), "");

        String url = LoginActivity.SERVERURL + "getemployee?companynumber=" + LoginActivity.COMPANY_NUMBER
                + "&body=" + jsonObjectStr
                + "&editoraccoutnumber=" + accountnumber
                + "&authstring=" + LoginActivity.WEBSTRING;
        return internetHelper.doPostString(url);
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute: " + s);
        editEmployeeActivity.processServerResponse(Integer.valueOf(s));
    }
}
