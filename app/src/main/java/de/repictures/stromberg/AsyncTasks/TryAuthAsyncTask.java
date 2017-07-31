package de.repictures.stromberg.AsyncTasks;

import android.content.Context;
import android.content.SharedPreferences;
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
import java.util.Objects;

import de.repictures.stromberg.AuthScanActivity;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.R;

public class TryAuthAsyncTask extends AsyncTask<String, Void, Boolean> {


    private AuthScanActivity authScanActivity;
    private String authCode;
    private String TAG = "TryAuthAsyncTask";

    public TryAuthAsyncTask(AuthScanActivity authScanActivity) {
        this.authScanActivity = authScanActivity;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        String accountnumber = strings[0].substring(0, authScanActivity.getResources().getInteger(R.integer.accountnumberlength));
        authCode = strings[0].substring(authScanActivity.getResources().getInteger(R.integer.accountnumberlength));
        String[] authParts = {authCode.substring(0, authScanActivity.getResources().getInteger(R.integer.auth_key_length)/2), authCode.substring(authScanActivity.getResources().getInteger(R.integer.auth_key_length)/2)};
        String resp = "";
        try {
            Log.d(TAG, "doInBackground:\nAuthCode: " + authCode + "\nAccountnumber: " + accountnumber);
            String baseUrl = LoginActivity.SERVERURL + "/auth?accountnumber=" + URLEncoder.encode(accountnumber, "UTF-8") + "&authPart=" + URLEncoder.encode(authParts[0], "UTF-8");
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
            return authParts[1].equals(resp);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        SharedPreferences sharedPref = authScanActivity.getSharedPreferences(authScanActivity.getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(authScanActivity.getResources().getString(R.string.sp_authcode), authCode);
        editor.apply();
        authScanActivity.getAuthResult(result, authCode);
    }
}
