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
import de.repictures.stromberg.ScanProductActivity;

public class GetProductAsyncTask extends AsyncTask<String, Void, String>{

    private ScanProductActivity scanProductActivity;
    private String TAG = "GetProductAsyncTask";

    public GetProductAsyncTask(ScanProductActivity scanProductActivity){
        this.scanProductActivity = scanProductActivity;
    }

    @Override
    protected String doInBackground(String... codes) {
        String baseUrl = LoginActivity.SERVERURL + "/postproducts?code=" + codes[0];
        return new Internet().doGetString(baseUrl);
        /*String resp = "";
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
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resp;*/
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
            scanProductActivity.receiveResult(products);
        }
    }
}
