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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashSet;

import de.repictures.stromberg.BuildConfig;
import de.repictures.stromberg.Helper.Cryptor;
import de.repictures.stromberg.Helper.GeneralUtils;
import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.MainActivity;
import de.repictures.stromberg.R;

public class LoginAsyncTask extends AsyncTask<String, Void, String> {

    private String TAG = "LoginAsyncTask";

    private TextInputLayout passwordEditLayout, accountnumberEditLayout;
    private final Button loginButton;
    private final ProgressBar loginProgressBar;
    private Activity activity;
    private Cryptor cryptor = new Cryptor();
    private Internet internetHelper = new Internet();

    public LoginAsyncTask(TextInputLayout accountnumberEditLayout, TextInputLayout passwordEditLayout, Button loginButton, ProgressBar loginProgressBar, Activity activity) {
        this.passwordEditLayout = passwordEditLayout;
        this.accountnumberEditLayout = accountnumberEditLayout;
        this.loginButton = loginButton;
        this.loginProgressBar = loginProgressBar;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        if (!internetHelper.isNetworkAvailable(activity)){
            cancel(true);
            onPostExecute("{\"response_code\": -1}");
        }
    }

    @Override
    protected String doInBackground(String... keys) {
        try {
            String getUrlStr = LoginActivity.SERVERURL + "/login?accountnumber=" + keys[0];
            String responseStr = internetHelper.doGetString(getUrlStr);
            String[] doGetResponse = responseStr.split("ò");

            if (responseStr.length() < 2){
                return "{\"response_code\": 6}";
            }

            if ((doGetResponse.length > 2 && doGetResponse[2].length() > 0)) {
                if (keys[3] != null && !doGetResponse[2].equals(keys[3]))
                    return new JSONObject().put("response_code", 3).toString();
            }

            doGetResponse[1] = URLDecoder.decode(doGetResponse[1], "UTF-8");
            String hashedPassword = cryptor.hashToString(keys[1]);
            String hashedSaltedPassword = cryptor.hashToString(hashedPassword + doGetResponse[1]);
            Log.d(TAG, "Server Timestamp: " + doGetResponse[1]);

            keys[4] = URLEncoder.encode(keys[4], "UTF-8");
            doGetResponse[1] = URLEncoder.encode(doGetResponse[1], "UTF-8");

            String postUrlStr = LoginActivity.SERVERURL + "/login?accountnumber=" + keys[0] + "&authPart=" + keys[2]
                    + "&token=" + keys[4] + "&password=" + hashedSaltedPassword + "&servertimestamp=" + doGetResponse[1] + "&appversion=" + BuildConfig.VERSION_CODE;
            return internetHelper.doPostString(postUrlStr);
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String responseStr) {
        try {
            Log.d(TAG, "onPostExecute: " + responseStr);
            loginButton.setText(activity.getResources().getString(R.string.login));
            loginProgressBar.setVisibility(View.INVISIBLE);
            JSONObject object = new JSONObject(responseStr);
            SharedPreferences sharedPref = activity.getSharedPreferences(activity.getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            switch (object.getInt("response_code")) {
                case 0: //Kontonummer existiert nicht
                    accountnumberEditLayout.setErrorEnabled(true);
                    accountnumberEditLayout.setError(activity.getResources().getString(R.string.accountnumber_error));
                    if (sharedPref.getString(activity.getResources().getString(R.string.sp_accountnumber), "") != "") {
                        editor.remove(activity.getResources().getString(R.string.sp_accountnumber));
                        editor.remove(activity.getResources().getString(R.string.sp_accountkey));
                        editor.remove(activity.getResources().getString(R.string.sp_webstring));
                        editor.remove(activity.getResources().getString(R.string.sp_featureslist));
                        editor.remove(activity.getResources().getString(R.string.sp_companynumbers));
                        editor.remove(activity.getResources().getString(R.string.sp_companysectors));
                        editor.remove(activity.getResources().getString(R.string.sp_companynames));
                        editor.remove(activity.getResources().getString(R.string.sp_is_prepaid));
                        editor.apply();
                    }
                    ((LoginActivity) activity).loginButtonClicked = false;
                    break;
                case 1: //Passwort falsch
                    if (sharedPref.getString(activity.getResources().getString(R.string.sp_accountnumber), "") != "") {
                        editor.remove(activity.getResources().getString(R.string.sp_accountnumber));
                        editor.remove(activity.getResources().getString(R.string.sp_accountkey));
                        editor.remove(activity.getResources().getString(R.string.sp_webstring));
                        editor.remove(activity.getResources().getString(R.string.sp_featureslist));
                        editor.remove(activity.getResources().getString(R.string.sp_companynumbers));
                        editor.remove(activity.getResources().getString(R.string.sp_companysectors));
                        editor.remove(activity.getResources().getString(R.string.sp_companynames));
                        editor.remove(activity.getResources().getString(R.string.sp_is_prepaid));
                        editor.apply();
                    }
                    long loginAttempts = object.getLong("failed_attempts");
                    ((LoginActivity) activity).loginButtonClicked = false;
                    ((LoginActivity) activity).startCountdown(loginAttempts);
                    break;
                case 2: //Alles gut
                    editor.putString(activity.getResources().getString(R.string.sp_accountnumber), object.getString("accountnumber"));
                    editor.putString(activity.getResources().getString(R.string.sp_accountkey), object.getString("account_key"));
                    editor.putString(activity.getResources().getString(R.string.sp_webstring), object.getString("random_web_string"));
                    editor.putString(activity.getResources().getString(R.string.sp_featureslist), object.getString("features"));
                    editor.putStringSet(activity.getResources().getString(R.string.sp_companynumbers), new HashSet<>(GeneralUtils.parseJsonStringArray(object, "company_accountnumbers")));
                    editor.putStringSet(activity.getResources().getString(R.string.sp_companysectors), new HashSet<>(GeneralUtils.parseJsonStringArray(object, "company_sectors")));
                    editor.putStringSet(activity.getResources().getString(R.string.sp_companynames), new HashSet<>(GeneralUtils.parseJsonStringArray(object, "company_names")));
                    editor.putBoolean(activity.getResources().getString(R.string.sp_is_prepaid), object.getBoolean("is_prepaid"));
                    editor.apply();

                    Intent i = new Intent(activity, MainActivity.class);
                    activity.startActivity(i);
                    activity.finish();
                    break;
                case 3: //Nicht authentifitiert
                    accountnumberEditLayout.setErrorEnabled(true);
                    accountnumberEditLayout.setError(activity.getResources().getString(R.string.wrong_auth));
                    if (sharedPref.getString(activity.getResources().getString(R.string.sp_accountnumber), "") != "") {
                        editor.remove(activity.getResources().getString(R.string.sp_accountnumber));
                        editor.remove(activity.getResources().getString(R.string.sp_accountkey));
                        editor.remove(activity.getResources().getString(R.string.sp_webstring));
                        editor.remove(activity.getResources().getString(R.string.sp_featureslist));
                        editor.remove(activity.getResources().getString(R.string.sp_companynumbers));
                        editor.remove(activity.getResources().getString(R.string.sp_companysectors));
                        editor.remove(activity.getResources().getString(R.string.sp_companynames));
                        editor.remove(activity.getResources().getString(R.string.sp_is_prepaid));
                        editor.apply();
                    }
                    ((LoginActivity) activity).loginButtonClicked = false;
                    break;
                case 4: //Du hasch noch ne CooldownTime
                    if (sharedPref.getString(activity.getResources().getString(R.string.sp_accountnumber), "") != "") {
                        editor.remove(activity.getResources().getString(R.string.sp_accountnumber));
                        editor.remove(activity.getResources().getString(R.string.sp_accountkey));
                        editor.remove(activity.getResources().getString(R.string.sp_webstring));
                        editor.remove(activity.getResources().getString(R.string.sp_featureslist));
                        editor.remove(activity.getResources().getString(R.string.sp_companynumbers));
                        editor.remove(activity.getResources().getString(R.string.sp_companysectors));
                        editor.remove(activity.getResources().getString(R.string.sp_companynames));
                        editor.remove(activity.getResources().getString(R.string.sp_is_prepaid));
                        editor.apply();
                    }
                    String dateTimeStr = object.getString("cooldown_time");
                    ((LoginActivity) activity).loginButtonClicked = false;
                    ((LoginActivity) activity).startCountdown(dateTimeStr);
                    break;
                case 5: //Zur Zentralbank und Konto entsperren lassen
                    ((LoginActivity) activity).showAccountLockedMessage();
                    break;
                case 6: //Konto existiert nicht
                    accountnumberEditLayout.setErrorEnabled(true);
                    accountnumberEditLayout.setError(activity.getResources().getString(R.string.account_does_not_exist));
                    ((LoginActivity) activity).loginButtonClicked = false;
                    break;
                case -1:
                    accountnumberEditLayout.setErrorEnabled(true);
                    accountnumberEditLayout.setError(activity.getResources().getString(R.string.internet_problems));
                    ((LoginActivity) activity).loginButtonClicked = false;
                    break;
                case -2:
                    accountnumberEditLayout.setErrorEnabled(true);
                    accountnumberEditLayout.setError(activity.getResources().getString(R.string.server_down));
                    ((LoginActivity) activity).loginButtonClicked = false;
                    break;
                case -3:
                    accountnumberEditLayout.setErrorEnabled(true);
                    accountnumberEditLayout.setError(activity.getResources().getString(R.string.old_app_version));
                    ((LoginActivity) activity).loginButtonClicked = false;
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}