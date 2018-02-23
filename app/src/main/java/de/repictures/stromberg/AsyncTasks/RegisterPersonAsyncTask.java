package de.repictures.stromberg.AsyncTasks;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import de.repictures.stromberg.Features.ScanPassportActivity;
import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;

public class RegisterPersonAsyncTask extends AsyncTask<String, Void, String> {

    private Internet internetHelper = new Internet();
    private ScanPassportActivity scanPassportActivity;

    public RegisterPersonAsyncTask(ScanPassportActivity scanPassportActivity){
        this.scanPassportActivity = scanPassportActivity;
    }

    @Override
    protected void onPreExecute() {
        if (!internetHelper.isNetworkAvailable(scanPassportActivity)){
            cancel(true);
            onPostExecute("{\"response_code\": -1}");
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String url = LoginActivity.SERVERURL + "register?user_accountnumber=" + params[0] + "&webstring=" + params[1]+ "&accountnumber=" + params[2];
        return internetHelper.doPostString(url);
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            JSONObject object = new JSONObject(s);
            if (object.getInt("response_code") == 1){
                if (object.has("presence_time") && object.has("minutes_to_go"))
                    scanPassportActivity.processResponse(object.getBoolean("entered"), object.getInt("presence_time"), object.getInt("minutes_to_go"));
                else
                    scanPassportActivity.processResponse(object.getBoolean("entered"), 0, 0);
            } else {
                scanPassportActivity.processResponse(object.getInt("response_code"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}