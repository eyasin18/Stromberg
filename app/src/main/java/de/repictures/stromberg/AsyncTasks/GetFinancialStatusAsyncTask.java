package de.repictures.stromberg.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;

import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.MainActivity;

public class GetFinancialStatusAsyncTask extends AsyncTask<String, Void, String>{

    private MainActivity mainActivity;
    private String TAG = "GetFinancialAsyncTask";

    public GetFinancialStatusAsyncTask(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(String... parameters) {
        String baseUrl = LoginActivity.SERVERURL + "/postfinancialstatus?accountnumber=" + LoginActivity.ACCOUNTNUMBER + "&webstring=" + parameters[0];
        Log.d(TAG, "doInBackground: " + baseUrl);
        Internet internetHelper = new Internet();
        return internetHelper.doGetString(baseUrl);
    }

    @Override
    protected void onPostExecute(String s) {
        String[] response = s.split("ò");
        switch (Integer.parseInt(response[0])){
            case 0:
                //Account mit dieser Accountnumber wurde nicht gefunden
                break;
            case 1:
                mainActivity.setFinancialStatus(response[1], response[2], Float.parseFloat(response[3]));
                break;
            case 2:
                //Webstring falsch
                //TODO: Return to login
                break;
        }
    }
}