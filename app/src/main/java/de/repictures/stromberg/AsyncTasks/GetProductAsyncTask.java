package de.repictures.stromberg.AsyncTasks;

import android.app.Activity;
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

import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.MainActivity;
import de.repictures.stromberg.ScanActivity;

public class GetProductAsyncTask extends AsyncTask<String, Void, String>{

    private ScanActivity scanActivity;
    private String TAG = "GetProductAsyncTask";

    public GetProductAsyncTask(ScanActivity scanActivity){
        this.scanActivity = scanActivity;
    }

    @Override
    protected String doInBackground(String... codes) {
        String resp = "";
        try {
            Log.d(TAG, "doInBackground: " + codes[0]);
            String baseUrl = LoginActivity.SERVERURL + "/postproducts?code=" + codes[0];
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resp;
    }

    @Override
    protected void onPostExecute(String s) {
        if (s.length() == 1 && s.charAt(0) == '0'){
            //Produkt gibts nicht
        } else {
            String[] responsesRaw = s.split("ň");
            String[][] products = new String[responsesRaw.length][4];
            for (int i = 0; i < products.length; i++) {
                products[i] = responsesRaw[i].split("ò");
            }
            scanActivity.receiveResult(products);
        }
    }
}
