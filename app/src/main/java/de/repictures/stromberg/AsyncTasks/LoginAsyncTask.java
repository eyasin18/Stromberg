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
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;

import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.MainActivity;
import de.repictures.stromberg.R;

public class LoginAsyncTask extends AsyncTask<String, Void, String> {

    private String TAG = "LoginAsyncTask";

    TextInputLayout passwordEditLayout, accountnumberEditLayout;
    private final Button loginButton;
    private final ProgressBar loginProgressBar;
    Activity activity;

    public LoginAsyncTask(TextInputLayout accountnumberEditLayout, TextInputLayout passwordEditLayout, Button loginButton, ProgressBar loginProgressBar, Activity activity) {
        this.passwordEditLayout = passwordEditLayout;
        this.accountnumberEditLayout = accountnumberEditLayout;
        this.loginButton = loginButton;
        this.loginProgressBar = loginProgressBar;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... keys) {
        String resp = "";
        try {
            Log.d(TAG, "doInBackground: " + keys[0] + keys[1]);
            String baseUrl = LoginActivity.SERVERURL + "/login?accountnumber=" + URLEncoder.encode(keys[0], "UTF-8") + "&password=" + URLEncoder.encode(keys[1], "UTF-8");
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
        return resp + "~" + keys[0];
    }

    @Override
    protected void onPostExecute(String responseStr) {
        loginButton.setText(activity.getResources().getString(R.string.login));
        loginProgressBar.setVisibility(View.INVISIBLE);
        String[] response = responseStr.split("~");
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        switch (Integer.parseInt(response[0])){
            case 0:
                accountnumberEditLayout.setErrorEnabled(true);
                accountnumberEditLayout.setError(activity.getResources().getString(R.string.accountnumber_error));
                if(sharedPref.getString(activity.getResources().getString(R.string.sp_accountnumber), "") != ""){
                    editor.remove(activity.getResources().getString(R.string.sp_accountnumber));
                    editor.apply();
                }
                break;
            case 1:
                passwordEditLayout.setErrorEnabled(true);
                passwordEditLayout.setError(activity.getResources().getString(R.string.password_wrong));
                if(sharedPref.getString(activity.getResources().getString(R.string.sp_accountnumber), "") != ""){
                    editor.remove(activity.getResources().getString(R.string.sp_accountnumber));
                    editor.apply();
                }
                break;
            case 2:
                editor.putString(activity.getResources().getString(R.string.sp_accountnumber), response[2]);
                editor.apply();
                Intent i = new Intent(activity, MainActivity.class);
                i.putExtra("account_key", response[1]);
                activity.startActivity(i);
                activity.finish();
                break;
        }
    }
}

