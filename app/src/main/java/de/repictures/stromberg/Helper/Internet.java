package de.repictures.stromberg.Helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

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
            Log.d(TAG, "doPostString: " + urlStr);
            URL postUrl = new URL(urlStr);

            HttpURLConnection httpURLConnection = (HttpURLConnection) postUrl.openConnection();
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");

            Log.d(TAG, "doPostString: " + httpURLConnection.getResponseCode());
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

    public MimeMultipart doGetMultipart(String urlStr, String contentType){
        try {
            URL url = new URL(urlStr);
            URLConnection urlConnection = url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            ByteArrayDataSource dataSource = new ByteArrayDataSource(in, contentType);
            return new MimeMultipart(dataSource);
        } catch (IOException | MessagingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String parseTextBodyPart(MimeMultipart multipart, int position){
        try {
            int count = multipart.getCount();
            Log.d(TAG, "parseTextBodyPart: Multipart content count: " + count);
                BodyPart bodyPart = multipart.getBodyPart(position);
                if (bodyPart.isMimeType("text/plain")){
                    Log.d(TAG, "parseTextBodyPart: bodypart no. " + position + ": " + String.valueOf(bodyPart.getContent()));
                    return URLDecoder.decode(String.valueOf(bodyPart.getContent()), "UTF-8").trim();
                } else {
                    Log.d(TAG, "parseTextBodyPart: BodyPart is not text/plain");
                    return null;
                }

        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}
