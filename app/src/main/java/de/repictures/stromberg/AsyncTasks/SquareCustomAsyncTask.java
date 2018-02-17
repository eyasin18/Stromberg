package de.repictures.stromberg.AsyncTasks;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import de.repictures.stromberg.Features.CustomsActivity;
import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;

public class SquareCustomAsyncTask extends AsyncTask<String, Void, String> {

    private Internet internetHelper = new Internet();
    private CustomsActivity customsActivity;

    public SquareCustomAsyncTask(CustomsActivity customsActivity){
        this.customsActivity = customsActivity;
    }

    @Override
    protected void onPreExecute() {
        if (!internetHelper.isNetworkAvailable(customsActivity)){
            cancel(true);
            onPostExecute("{\"response_code\": -1}");
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String url = LoginActivity.SERVERURL + "squarecustoms?accountnumber=" + params[0]
                + "&webstring=" + params[1]
                + "&companynumber=" + params[2]
                + "&price=" + params[3]
                + "&meat=" + params[4]
                + "&package=" + params[5]
                + "&biomeat=" + params[6];
        return internetHelper.doPostString(url);
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            JSONObject object = new JSONObject(s);
            double amount = 0;
            if (object.has("amount")) amount = object.getDouble("amount");
            customsActivity.processResponse(object.getInt("response_code"), amount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}