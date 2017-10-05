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
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.PrivateKey;

import de.repictures.stromberg.Helper.Cryptor;
import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.MainActivity;
import de.repictures.stromberg.R;

public class GetFinancialStatusAsyncTask extends AsyncTask<String, Void, String[]>{

    private MainActivity mainActivity;
    private String TAG = "GetFinancialAsyncTask";
    private Internet internetHelper = new Internet();
    private Cryptor cryptor = new Cryptor();

    public GetFinancialStatusAsyncTask(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    @Override
    protected void onPreExecute() {
        if (!internetHelper.isNetworkAvailable(mainActivity)){
            cancel(true);
            onPostExecute("-1".split("ò"));
        }
    }

    @Override
    protected String[] doInBackground(String... parameters) {
        String baseUrl = LoginActivity.SERVERURL + "/postfinancialstatus?accountnumber=" + LoginActivity.ACCOUNTNUMBER + "&webstring=" + parameters[0];
        String doGetString = internetHelper.doGetString(baseUrl);
        Log.d(TAG, "doInBackground: " + baseUrl);
        String[] response = doGetString.split("ò");
        response[2] = getDecryptedPerson(response[2]);
        return response;
    }

    @Override
    protected void onPostExecute(String[] response) {
        switch (Integer.parseInt(response[0])){
            case -1:
                //Keine Internetverbindung
                mainActivity.setFinancialStatus("0000", "Max Mustermann", 0.0f);
            case 0:
                //Account mit dieser Accountnumber wurde nicht gefunden
                break;
            case 1:
                mainActivity.setFinancialStatus(response[1], response[2], Float.parseFloat(response[3]));
                break;
            case 2:
                //Webstring falsch
                //TODO: Return to login
                break;
        }
    }

    private String getDecryptedPerson(String encryptedStringHex) {
        byte[] encryptedPassword = cryptor.hashToByte(LoginActivity.PIN);
        byte[] encryptedName = cryptor.hexToBytes(encryptedStringHex);
        return cryptor.decryptSymetricToString(encryptedName, encryptedPassword);
    }
}