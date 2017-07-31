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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;

import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.MainActivity;
import de.repictures.stromberg.R;
import de.repictures.stromberg.uiHelper.Cryptor;

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
        String postResp = "";
        String getResp = "";
        try {
            String getUrlStr = LoginActivity.SERVERURL + "/login?authPart=" + keys[2] + "&accountnumber=" + keys[0];
            URL getUrl = new URL(getUrlStr);
            URLConnection urlConnection = getUrl.openConnection();

            InputStream getInputStream = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader getBufferedReader = new BufferedReader(new InputStreamReader(getInputStream, "UTF-8"));
            StringBuilder getTotal = new StringBuilder();
            String getLine;
            while ((getLine = getBufferedReader.readLine()) != null) {
                getTotal.append(getLine);
            }
            Log.d(TAG, "doInBackground: " + getTotal);
            getResp += getTotal;
            getResp = URLDecoder.decode(getResp, "UTF-8");
            getInputStream.close();
            String getResults[] = getResp.split("~");
            if (!getResults[0].equals(keys[3])){
                return "3";
            }

            keys[1] = cryptor.hashToString(cryptor.hashToString(keys[1]) + getResults[1]);
            Log.d(TAG, "doInBackground:\nAccountnumber: " + keys[0] + "\nSaltetPin: " + keys[1]);
            String postUrlStr = LoginActivity.SERVERURL + "/login?accountnumber=" + URLEncoder.encode(keys[0], "UTF-8") + "&password=" + URLEncoder.encode(keys[1], "UTF-8");
            URL postUrl = new URL(postUrlStr);

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
            Log.d(TAG, "doInBackground: " + postTotal);
            postResp += postTotal;
            postResp = URLDecoder.decode(postResp, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            postResp = "-1";
        }
        return postResp + "ò" + keys[0];
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
                    editor.remove(activity.getResources().getString(R.string.sp_accountslist));
                    editor.remove(activity.getResources().getString(R.string.sp_featureslist));
                    editor.apply();
                }
                break;
            case 1:
                passwordEditLayout.setErrorEnabled(true);
                passwordEditLayout.setError(activity.getResources().getString(R.string.password_wrong));
                if(sharedPref.getString(activity.getResources().getString(R.string.sp_accountnumber), "") != ""){
                    editor.remove(activity.getResources().getString(R.string.sp_accountnumber));
                    editor.remove(activity.getResources().getString(R.string.sp_accountkey));
                    editor.remove(activity.getResources().getString(R.string.sp_accountslist));
                    editor.remove(activity.getResources().getString(R.string.sp_featureslist));
                    editor.apply();
                }
                break;
            case 2:
                editor.putString(activity.getResources().getString(R.string.sp_accountnumber), response[4]);
                editor.putString(activity.getResources().getString(R.string.sp_accountkey), response[1]);
                editor.putString(activity.getResources().getString(R.string.sp_accountslist), response[2]);
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
                    editor.remove(activity.getResources().getString(R.string.sp_accountslist));
                    editor.remove(activity.getResources().getString(R.string.sp_featureslist));
                    editor.apply();
                }
                break;
            case -1:
                accountnumberEditLayout.setErrorEnabled(true);
                accountnumberEditLayout.setError(activity.getResources().getString(R.string.internet_problems));
                break;
        }
    }

    private String getAccountInformation(String[] keys) {
        String resp = "";
        try {
            Log.d(TAG, "doInBackground: " + keys[0] + keys[1]);
            String baseUrl = LoginActivity.SERVERURL + "/login?firstRequest=false&accountnumber=" + URLEncoder.encode(keys[0], "UTF-8") + "&password=" + URLEncoder.encode(keys[1], "UTF-8");
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
        return resp + "ò" + keys[0];
    }
}

