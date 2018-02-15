package de.repictures.stromberg.AsyncTasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.repictures.stromberg.Features.AddPrepaidAccountActivity;
import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.R;

public class AddPrepaidAccountAsyncTask extends AsyncTask<String, Void, JSONObject>{

    private Internet internet = new Internet();
    private AddPrepaidAccountActivity addPrepaidAccountActivity;

    public AddPrepaidAccountAsyncTask(AddPrepaidAccountActivity addPrepaidAccountActivity){

        this.addPrepaidAccountActivity = addPrepaidAccountActivity;
    }

    @Override
    protected void onPreExecute() {
        try {
            if (!internet.isNetworkAvailable(addPrepaidAccountActivity)) {
                cancel(true);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("response_code", -1);
                onPostExecute(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        SharedPreferences sharedPref = addPrepaidAccountActivity.getSharedPreferences(addPrepaidAccountActivity.getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        List<String> companyNumbers = new ArrayList<>(sharedPref.getStringSet(addPrepaidAccountActivity.getResources().getString(R.string.sp_companynumbers), new HashSet<>()));
        String accountnumber = sharedPref.getString(addPrepaidAccountActivity.getResources().getString(R.string.sp_accountnumber), "");
        String webstring = sharedPref.getString(addPrepaidAccountActivity.getResources().getString(R.string.sp_webstring), "");
        String url = LoginActivity.SERVERURL + "addprepaid?accountnumber=" + accountnumber + "&companynumber=" + companyNumbers.get(addPrepaidAccountActivity.companyPosition) + "&webstring=" + webstring;
        try {
            return new JSONObject(internet.doGetString(url));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject object) {
        addPrepaidAccountActivity.updateFields(object);
    }
}