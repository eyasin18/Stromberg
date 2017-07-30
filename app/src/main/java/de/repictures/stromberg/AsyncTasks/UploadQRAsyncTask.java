package de.repictures.stromberg.AsyncTasks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.ByteArrayBody;
import cz.msebera.android.httpclient.extras.Base64;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.uiHelper.Cryptor;

public class UploadQRAsyncTask extends AsyncTask<Bitmap, Void, String[]> {

    private Activity activity;
    private String accountnumber;
    private String authcode;
    private String TAG = "UploadQRAsyncTask";
    private Cryptor cryptor = new Cryptor();

    public UploadQRAsyncTask(Activity activity, String accountnumber, String authcode){
        this.activity = activity;
        this.accountnumber = accountnumber;
        this.authcode = authcode;
    }

    @Override
    protected String[] doInBackground(Bitmap... transferArray) {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        transferArray[0].compress(Bitmap.CompressFormat.PNG, 100, bao);
        byte[] rawImage = bao.toByteArray();
        byte[] imageData = cryptor.encryptSymetricFromByte(rawImage, cryptor.hashToByte(LoginActivity.PIN));
        authcode = cryptor.bytesToHex(cryptor.encryptSymetricFromString(authcode, cryptor.hashToByte(LoginActivity.PIN)));

        String getURLURLStr = LoginActivity.SERVERURL + "/saveqr?accountnumber=" + accountnumber + "&bytelength=" + imageData.length + "&authcode=" + authcode;
        String[] results = new String[2];
        try {
            URL getURLURL = new URL(getURLURLStr);
            URLConnection urlConnection = getURLURL.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            try {
                BufferedReader r = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line);
                }
                results[0] = total.toString();
            } finally {
                in.close();
            }

            HttpURLConnection httpURLConnection;
            String postImageURLStr = LoginActivity.SERVERURL + "/saveqr";
            URL postImageURL = new URL(postImageURLStr);
            Log.d(TAG, "doInBackground: " + postImageURL.toString());
            httpURLConnection = (HttpURLConnection) postImageURL.openConnection();
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");

            DataOutputStream request = new DataOutputStream(httpURLConnection.getOutputStream());
            request.write(imageData);

            request.flush();
            request.close();

            InputStream responseStream = new BufferedInputStream(httpURLConnection.getInputStream());
            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));

            String line;
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = responseStreamReader.readLine()) != null){
                stringBuilder.append(line).append("\n");
            }
            results[1] = stringBuilder.toString();
            responseStream.close();
            return results;
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String[] results) {
        Log.d(TAG, "onPostExecute: " + results[0] + " " + results[1]);
    }
}

