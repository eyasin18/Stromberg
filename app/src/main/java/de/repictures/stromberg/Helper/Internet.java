package de.repictures.stromberg.Helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Map;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

public class Internet {

    public static int GET = 1;
    public static int POST = 2;

    private final String TAG = "Internet";
    private int responseCode;

    public String doGetString(String urlStr){
        try {
            String resp = "";
            URL url = new URL(urlStr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader r = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            resp += total;
            responseCode = urlConnection.getResponseCode();
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
            responseCode = httpURLConnection.getResponseCode();
            postInputStream.close();
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

    public MimeMultipart doPostMultipart(String urlStr, String contentType){
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
            ByteArrayDataSource dataSource = new ByteArrayDataSource(postInputStream, contentType);
            return new MimeMultipart(dataSource);
        } catch (IOException | MessagingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Deprecated
    public String doMultipartRequest(int methodCode, String url, Map<String, String> content){
        try {
            HttpClient client = HttpClientBuilder.create().build();

            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            for (Map.Entry<String, String> contentEntry : content.entrySet()){
                entityBuilder.addTextBody(contentEntry.getKey(), contentEntry.getValue());
            }
            HttpEntity entity = entityBuilder.build();

            HttpResponse response;
            switch (methodCode){
                case 1:
                    HttpGetWithEntity httpGet = new HttpGetWithEntity(url);
                    httpGet.setEntity(entity);
                    response = client.execute(httpGet);
                    break;
                case 2:
                    HttpPost httpPost = new HttpPost(url);
                    httpPost.setEntity(entity);
                    response = client.execute(httpPost);
                    break;
                default:
                    Log.d(TAG, "doMultipartRequest: there is no such network method");
                    return null;
            }

            BufferedReader responseStreamReader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            String line;
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            Log.d(TAG, "doInBackground: " + stringBuilder.toString());
            return stringBuilder.toString();
        } catch (IOException e){
            Log.e(TAG, "doMultipartRequest: ", e);
            return null;
        }
    }

    public String doMultipartRequest(int methodCode, String url, HttpEntity entity){
        try {
            HttpClient client = HttpClientBuilder.create().build();

            HttpResponse response;
            switch (methodCode){
                case 1:
                    HttpGetWithEntity httpGet = new HttpGetWithEntity(url);
                    httpGet.setEntity(entity);
                    response = client.execute(httpGet);
                    break;
                case 2:
                    HttpPost httpPost = new HttpPost(url);
                    httpPost.setEntity(entity);
                    response = client.execute(httpPost);
                    break;
                default:
                    Log.d(TAG, "doMultipartRequest: there is no such network method");
                    return null;
            }

            BufferedReader responseStreamReader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            String line;
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            Log.d(TAG, "doInBackground: " + stringBuilder.toString());
            return stringBuilder.toString();
        } catch (IOException e){
            Log.e(TAG, "doMultipartRequest: ", e);
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

    public JSONObject parseJsonBodyPart(MimeMultipart multipart, int position){
        try {
            int count = multipart.getCount();
            Log.d(TAG, "parseTextBodyPart: Multipart content count: " + count);
            BodyPart bodyPart = multipart.getBodyPart(position);
            if (bodyPart.isMimeType("text/plain")){
                String jsonString = String.valueOf(bodyPart.getContent());
                Log.d(TAG, "parseTextBodyPart: bodypart no. " + position + ": " + jsonString);
                return new JSONObject(jsonString);
            } else if (bodyPart.isMimeType("application/json")){
                String jsonString = String.valueOf(bodyPart.getContent());
                Log.d(TAG, "parseTextBodyPart: bodypart no. " + position + ": " + jsonString);
                return new JSONObject(jsonString);
            }
            else {
                Log.d(TAG, "parseTextBodyPart: BodyPart is not application/json");
                return null;
            }
        } catch (MessagingException | IOException | JSONException e){
            Log.e(TAG, "parseJsonBodyPart: ", e);
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

    public int getResponseCode(){
        return responseCode;
    }

    private class HttpGetWithEntity extends HttpPost {
        public final static String METHOD_NAME = "GET";

        public HttpGetWithEntity(String url) {
            super(url);
        }

        @Override
        public String getMethod() {
            return METHOD_NAME;
        }
    }
}
