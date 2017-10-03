package de.repictures.stromberg.AsyncTasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;

import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.MainActivity;
import de.repictures.stromberg.R;
import de.repictures.stromberg.Helper.Cryptor;

public class LoginAsyncTask extends AsyncTask<String, Void, String> {

    private String TAG = "LoginAsyncTask";

    private TextInputLayout passwordEditLayout, accountnumberEditLayout;
    private final Button loginButton;
    private final ProgressBar loginProgressBar;
    private Activity activity;
    private Cryptor cryptor = new Cryptor();

    public LoginAsyncTask(TextInputLayout accountnumberEditLayout, TextInputLayout passwordEditLayout, Button loginButton, ProgressBar loginProgressBar, Activity activity) {
        this.passwordEditLayout = passwordEditLayout;
        this.accountnumberEditLayout = accountnumberEditLayout;
        this.loginButton = loginButton;
        this.loginProgressBar = loginProgressBar;
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... keys) {
        Internet internetHelper = new Internet();
        String getUrlStr = LoginActivity.SERVERURL + "/login?accountnumber=" + keys[0];
        String[] doGetResponse;
        try {
            doGetResponse = internetHelper.doGetString(getUrlStr).split("ò");
        } catch (NullPointerException e){
            e.printStackTrace();
            return "-2";
        }
        if (!doGetResponse[0].equals(keys[3])) return "3";
        try {
            doGetResponse[1] = URLDecoder.decode(doGetResponse[1], "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String hashedSaltetPassword = cryptor.hashToString(cryptor.hashToString(keys[1]) + doGetResponse[1]);
        Log.d(TAG, "Server Timestamp: " + doGetResponse[1]);
        try {
            keys[4] = URLEncoder.encode(keys[4], "UTF-8");
            doGetResponse[1] = URLEncoder.encode(doGetResponse[1], "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String postUrlStr = LoginActivity.SERVERURL + "/login?accountnumber=" + keys[0] + "&authPart=" + keys[2]
                + "&token=" + keys[4] + "&password=" + hashedSaltetPassword + "&servertimestamp=" + doGetResponse[1];
        return internetHelper.doPostString(postUrlStr) + "ò" + keys[0];
    }

    @Override
    protected void onPostExecute(String responseStr) {
        loginButton.setText(activity.getResources().getString(R.string.login));
        loginProgressBar.setVisibility(View.INVISIBLE);
        String[] response = responseStr.split("ò");
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        switch (Integer.parseInt(response[0])){
            case 0:
                accountnumberEditLayout.setErrorEnabled(true);
                accountnumberEditLayout.setError(activity.getResources().getString(R.string.accountnumber_error));
                if(sharedPref.getString(activity.getResources().getString(R.string.sp_accountnumber), "") != ""){
                    editor.remove(activity.getResources().getString(R.string.sp_accountnumber));
                    editor.remove(activity.getResources().getString(R.string.sp_accountkey));
                    editor.remove(activity.getResources().getString(R.string.sp_webstring));
                    editor.remove(activity.getResources().getString(R.string.sp_featureslist));
                    editor.apply();
                }
                ((LoginActivity)activity).loginButtonClicked = false;
                break;
            case 1:
                passwordEditLayout.setErrorEnabled(true);
                passwordEditLayout.setError(activity.getResources().getString(R.string.password_wrong));
                if(sharedPref.getString(activity.getResources().getString(R.string.sp_accountnumber), "") != ""){
                    editor.remove(activity.getResources().getString(R.string.sp_accountnumber));
                    editor.remove(activity.getResources().getString(R.string.sp_accountkey));
                    editor.remove(activity.getResources().getString(R.string.sp_webstring));
                    editor.remove(activity.getResources().getString(R.string.sp_featureslist));
                    editor.apply();
                }
                ((LoginActivity)activity).loginButtonClicked = false;
                break;
            case 2:
                editor.putString(activity.getResources().getString(R.string.sp_accountnumber), response[4]);
                editor.putString(activity.getResources().getString(R.string.sp_accountkey), response[1]);
                editor.putString(activity.getResources().getString(R.string.sp_webstring), response[2]);
                editor.putString(activity.getResources().getString(R.string.sp_featureslist), response[3]);
                editor.apply();
                Log.d(TAG, "onPostExecute: " + response[2]);
                Intent i = new Intent(activity, MainActivity.class);
                activity.startActivity(i);
                activity.finish();
                break;
            case 3:
                accountnumberEditLayout.setErrorEnabled(true);
                accountnumberEditLayout.setError(activity.getResources().getString(R.string.wrong_auth));
                if(sharedPref.getString(activity.getResources().getString(R.string.sp_accountnumber), "") != ""){
                    editor.remove(activity.getResources().getString(R.string.sp_accountnumber));
                    editor.remove(activity.getResources().getString(R.string.sp_accountkey));
                    editor.remove(activity.getResources().getString(R.string.sp_webstring));
                    editor.remove(activity.getResources().getString(R.string.sp_featureslist));
                    editor.apply();
                }
                ((LoginActivity)activity).loginButtonClicked = false;
                break;
            case -1:
                accountnumberEditLayout.setErrorEnabled(true);
                accountnumberEditLayout.setError(activity.getResources().getString(R.string.internet_problems));
                ((LoginActivity)activity).loginButtonClicked = false;
                break;
            case -2:
                accountnumberEditLayout.setErrorEnabled(true);
                accountnumberEditLayout.setError(activity.getResources().getString(R.string.server_down));
                ((LoginActivity)activity).loginButtonClicked = false;
                break;
        }
    }
}

