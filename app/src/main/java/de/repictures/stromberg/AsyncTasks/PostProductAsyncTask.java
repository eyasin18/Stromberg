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

import de.repictures.stromberg.Features.AddProductActivity;
import de.repictures.stromberg.LoginActivity;

public class PostProductAsyncTask extends AsyncTask<String, Void, String>{

    private AddProductActivity addProductActivity;
    private String TAG = "PostProductAsyncTask";

    public PostProductAsyncTask(AddProductActivity addProductActivity){
        this.addProductActivity = addProductActivity;
    }

    @Override
    protected String doInBackground(String... productInfo) {
        String resp = "";
        try {
            Log.d(TAG, "doInBackground: " + productInfo[0]);
            String baseUrl = LoginActivity.SERVERURL + "/getproduct?code=" + productInfo[0] + "&name=" + productInfo[1] + "&price=" + productInfo[2] + "&accountnumber=" + productInfo[3];
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

    }
}
