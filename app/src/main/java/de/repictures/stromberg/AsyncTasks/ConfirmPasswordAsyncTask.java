package de.repictures.stromberg.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import de.repictures.stromberg.Fragments.ConfirmationLoginDialogFragment;
import de.repictures.stromberg.Helper.Cryptor;
import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;

public class ConfirmPasswordAsyncTask extends AsyncTask<String, Void, String> {

    private final String TAG = "ConfirmPasswordAsync";
    private ConfirmationLoginDialogFragment confirmationLoginDialogFragment;
    private Internet internetHelper = new Internet();
    private Cryptor cryptor = new Cryptor();

    public ConfirmPasswordAsyncTask(ConfirmationLoginDialogFragment confirmationLoginDialogFragment){
        this.confirmationLoginDialogFragment = confirmationLoginDialogFragment;
    }

    @Override
    protected void onPreExecute() {
        if (!internetHelper.isNetworkAvailable(confirmationLoginDialogFragment.getContext())){
            cancel(true);
            onPostExecute("-1");
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String getUrlStr = LoginActivity.SERVERURL + "/confirmlogin?accountnumber=" + params[0] + "&sessionaccountnumber=" + LoginActivity.ACCOUNTNUMBER + "&webstring=" + LoginActivity.WEBSTRING;
        String serverTimeStamp = internetHelper.doGetString(getUrlStr);
        String decodedServerTimeStamp;
        if (internetHelper.getResponseCode() == 206){
            return "2";
        }
        try {
            decodedServerTimeStamp = URLDecoder.decode(serverTimeStamp, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "-2";
        }
        Log.d(TAG, "Server Timestamp: " + serverTimeStamp);
        String hashedPassword = cryptor.hashToString(params[1]);
        String hashedSaltetPassword = cryptor.hashToString(hashedPassword + serverTimeStamp);
        String postUrlStr = LoginActivity.SERVERURL + "/confirmlogin?accountnumber=" + params[0] + "&sessionaccountnumber=" + LoginActivity.ACCOUNTNUMBER + "&webstring=" + LoginActivity.WEBSTRING
                + "&password=" + hashedSaltetPassword + "&servertimestamp=" + decodedServerTimeStamp;
        return internetHelper.doPostString(postUrlStr);
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG, "onPostExecute: " + result);
        int responseCode = Integer.parseInt(result);
        if (confirmationLoginDialogFragment != null) confirmationLoginDialogFragment.progressResponse(responseCode);
    }
}
