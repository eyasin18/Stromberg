package de.repictures.stromberg.AsyncTasks;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
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

import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.MainActivity;
import de.repictures.stromberg.R;

public class LoginAsyncTask extends AsyncTask<String, Void, Integer> {

    private String TAG = "LoginAsyncTask";

    TextInputLayout passwordEditLayout, accountnumberEditLayout;
    Activity activity;

    public LoginAsyncTask(TextInputLayout accountnumberEditLayout, TextInputLayout passwordEditLayout, Activity activity) {
        this.passwordEditLayout = passwordEditLayout;
        this.accountnumberEditLayout = accountnumberEditLayout;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(String... keys) {
        String resp = "";
        try {
            Log.d(TAG, "doInBackground: " + keys[0] + keys[1]);
            String baseUrl = "/login?";
            URL url = new URL(LoginActivity.SERVERURL + baseUrl + "accountnumber=" + URLEncoder.encode(keys[0], "UTF-8") + "&password=" + URLEncoder.encode(keys[1], "UTF-8"));
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
        return Integer.parseInt(resp);
    }

    @Override
    protected void onPostExecute(Integer response) {
        switch (response){
            case 0:
                accountnumberEditLayout.setErrorEnabled(true);
                accountnumberEditLayout.setError(activity.getResources().getString(R.string.accountnumber_error));
                break;
            case 1:
                passwordEditLayout.setErrorEnabled(true);
                passwordEditLayout.setError(activity.getResources().getString(R.string.password_wrong));
                break;
            case 2:
                Intent i = new Intent(activity, MainActivity.class);
                activity.startActivity(i);
                break;
        }
    }
}

