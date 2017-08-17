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

import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.TransfersActivity;

public class GetTransfersAsyncTask extends AsyncTask<String, Void, String>{

    private TransfersActivity transfersActivity;
    private String TAG = "GetFinancialAsyncTask";

    public GetTransfersAsyncTask(TransfersActivity transfersActivity){
        this.transfersActivity = transfersActivity;
    }

    @Override
    protected String doInBackground(String... accountKeys) {
        String baseUrl = LoginActivity.SERVERURL + "/posttransfers?accountnumber=" + accountKeys[0];
        return new Internet().doGetString(baseUrl);
        /*String resp = "";
        try {
            Log.d(TAG, "doInBackground: " + accountKeys[0]);
            String baseUrl = LoginActivity.SERVERURL + "/posttransfers?accountnumber=" + accountKeys[0];
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
        Log.d(TAG, "onPostExecute: " + s);
        if(s.endsWith("ĵ")){
            transfersActivity.updateRecycler();
        } else if (s.length() > 0){
            String[] response = s.split("ň");
            String[][] transfersArray = new String[response.length][];
            for (int i = 0; i < response.length; i++) {
                transfersArray[i] = response[i].split("ò");
            }
            transfersActivity.updateRecycler(transfersArray);
        } else {
            transfersActivity.updateRecycler();
        }
    }
}
