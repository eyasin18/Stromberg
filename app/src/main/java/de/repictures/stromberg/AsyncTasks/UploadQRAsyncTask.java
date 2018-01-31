package de.repictures.stromberg.AsyncTasks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.entity.mime.content.StringBody;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import de.repictures.stromberg.Helper.Cryptor;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.R;

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
        try {
            HttpClient client = HttpClientBuilder.create().build();

            String baseUrl = LoginActivity.SERVERURL + "/saveqr";
            HttpPost httpPost = new HttpPost(baseUrl);
            StringBody accountnumberBody = new StringBody(accountnumber, ContentType.TEXT_PLAIN);
            SharedPreferences sharedPref = activity.getSharedPreferences(activity.getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
            String accountnumber = sharedPref.getString(activity.getResources().getString(R.string.sp_accountnumber), "");
            StringBody userAccountnumberBody = new StringBody(accountnumber, ContentType.TEXT_PLAIN);

            byte[] hashedPassword = cryptor.hashToByte(LoginActivity.PIN);

            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            transferArray[0].compress(Bitmap.CompressFormat.PNG, 100, bao);
            byte[] rawImage = bao.toByteArray();
            byte[] imageData = cryptor.encryptSymmetricFromByte(rawImage, hashedPassword);
            FileBody fileBody = new FileBody(getImage(imageData, "bild"), ContentType.APPLICATION_OCTET_STREAM);

            StringBody bytelengthBody = new StringBody(String.valueOf(imageData.length), ContentType.TEXT_PLAIN);

            byte[] encrypedAuthCode = cryptor.encryptSymmetricFromString(authcode, hashedPassword);
            String encryptedAuthCodeHey = cryptor.bytesToHex(encrypedAuthCode);
            StringBody authCodeBody = new StringBody(encryptedAuthCodeHey, ContentType.TEXT_PLAIN);

            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("userAccountnumber", userAccountnumberBody)
                    .addPart("accountnumber", accountnumberBody)
                    .addPart("bytelength", bytelengthBody)
                    .addPart("authcode", authCodeBody)
                    .addPart("bin", fileBody)
                    .build();
            httpPost.setEntity(reqEntity);
            HttpResponse response = client.execute(httpPost);

            BufferedReader responseStreamReader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            String line;
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = responseStreamReader.readLine()) != null){
                stringBuilder.append(line).append("\n");
            }
            Log.d(TAG, "doInBackground: " + stringBuilder.toString());
            return null;
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String[] results) {
        Log.d(TAG, "onPostExecute: finished");
        //Log.d(TAG, "onPostExecute: " + results[0] + " " + results[1]);
    }

    private File getImage(byte[] imageData, String name) {
        try {
            File filesDir = activity.getApplicationContext().getCacheDir();
            File imageFile = new File(filesDir, name + ".jpg");
            imageFile.createNewFile();

            FileOutputStream fos = new FileOutputStream(imageFile);
            fos.write(imageData);
            fos.flush();
            fos.close();
            return imageFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

