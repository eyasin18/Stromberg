package de.repictures.stromberg.AsyncTasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.URLEncoder;

import de.repictures.stromberg.AuthScanActivity;
import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.R;

public class TryAuthAsyncTask extends AsyncTask<String, Void, Integer> {


    private AuthScanActivity authScanActivity;
    private String authCode;
    private String encryptedPrivateKeyHex = "";
    private String TAG = "TryAuthAsyncTask";
    private Internet internetHelper = new Internet();

    public TryAuthAsyncTask(AuthScanActivity authScanActivity) {
        this.authScanActivity = authScanActivity;
    }

    @Override
    protected void onPreExecute() {
        if (!internetHelper.isNetworkAvailable(authScanActivity)){
            cancel(true);
            onPostExecute(-1);
        }
    }

    @Override
    protected Integer doInBackground(String... strings) {
        String accountnumber = strings[0].substring(0, authScanActivity.getResources().getInteger(R.integer.accountnumberlength));
        authCode = strings[0].substring(authScanActivity.getResources().getInteger(R.integer.accountnumberlength));
        String[] authParts = {authCode.substring(0, authScanActivity.getResources().getInteger(R.integer.auth_key_length)/2), authCode.substring(authScanActivity.getResources().getInteger(R.integer.auth_key_length)/2)};
        try {
            Log.d(TAG, "doInBackground:\nAuthCode: " + authCode + "\nAccountnumber: " + accountnumber);
            String baseUrl = LoginActivity.SERVERURL + "/auth?accountnumber=" + URLEncoder.encode(accountnumber, "UTF-8");
            String responseStr = internetHelper.doGetString(baseUrl);
            if (responseStr == null){
                return -1;
            }
            String[] response = responseStr.split("ò");
            if (response[0] == "0"){
                return 0;
            }

            if (authParts[1].equals(response[1])){
                String postUrlStr = LoginActivity.SERVERURL + "/auth?accountnumber=" + URLEncoder.encode(accountnumber, "UTF-8") + "&authPart=" + URLEncoder.encode(authParts[0], "UTF-8");
                String postResponse = internetHelper.doPostString(postUrlStr);
                if (postResponse.length() > 1){
                    encryptedPrivateKeyHex = postResponse;
                    return 1;
                } else {
                    return 0;
                }
            } else {
                return 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    protected void onPostExecute(Integer result) {
        if (result == 1) {
            SharedPreferences sharedPref = authScanActivity.getSharedPreferences(authScanActivity.getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(authScanActivity.getResources().getString(R.string.sp_authcode), authCode);
            editor.putString(authScanActivity.getResources().getString(R.string.sp_encrypted_private_key_hex_2), encryptedPrivateKeyHex);
            editor.apply();
        }
        authScanActivity.getAuthResult(result, authCode);
    }
}