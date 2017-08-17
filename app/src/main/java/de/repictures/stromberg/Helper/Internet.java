package de.repictures.stromberg.Helper;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;

public class Internet {

    private final String TAG = "Internet";

    public String doGetString(String urlStr){
        try {
            String resp = "";
            URL url = new URL(urlStr);
            URLConnection urlConnection = url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader r = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            resp += total;
            in.close();
            Log.d(TAG, "doGetString: " + URLDecoder.decode(resp, "UTF-8"));
            return URLDecoder.decode(resp, "UTF-8");
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public String doPostString(String urlStr){
        try {
            URL postUrl = new URL(urlStr);

            HttpURLConnection httpURLConnection = (HttpURLConnection) postUrl.openConnection();
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");

            InputStream postInputStream = new BufferedInputStream(httpURLConnection.getInputStream());
            BufferedReader postBufferedReader = new BufferedReader(new InputStreamReader(postInputStream, "UTF-8"));
            StringBuilder postTotal = new StringBuilder();
            String postLine;
            while ((postLine = postBufferedReader.readLine()) != null) {
                postTotal.append(postLine);
            }
            Log.d(TAG, "doPostString: " + URLDecoder.decode(postTotal.toString(), "UTF-8"));
            return URLDecoder.decode(postTotal.toString(), "UTF-8");
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
}
