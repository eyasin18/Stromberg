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
    protected String doInBackground(String... accountKeys) {
        String baseUrl = LoginActivity.SERVERURL + "/postfinancialstatus?accountkey=" + accountKeys[0];
        Internet internetHelper = new Internet();
        return internetHelper.doGetString(baseUrl);
        /*String resp = "";
        try {
            Log.d(TAG, "doInBackground: " + accountKeys[0]);
            String baseUrl = LoginActivity.SERVERURL + "/postfinancialstatus?accountkey=" + accountKeys[0];
            URL url = new URL(baseUrl);
            URLConnection urlConnection = url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader r = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            Log.d(TAG, "doInBackground: " + total);
            resp += total;
            resp = URLDecoder.decode(resp, "UTF-8");
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resp;*/
    }

    @Override
    protected void onPostExecute(String s) {
        String[] response = s.split("ò");
        mainActivity.setFinancialStatus(response[0], response[1], Float.parseFloat(response[2]));
    }
}
