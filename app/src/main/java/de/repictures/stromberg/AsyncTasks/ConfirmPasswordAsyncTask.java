package de.repictures.stromberg.AsyncTasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import de.repictures.stromberg.Fragments.ConfirmationLoginDialogFragment;
import de.repictures.stromberg.Helper.Cryptor;
import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.R;

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
        SharedPreferences sharedPref = confirmationLoginDialogFragment.getActivity().getSharedPreferences(confirmationLoginDialogFragment.getActivity().getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        String accountnumber = sharedPref.getString(confirmationLoginDialogFragment.getActivity().getResources().getString(R.string.sp_accountnumber), "");
        String webstring = sharedPref.getString(confirmationLoginDialogFragment.getActivity().getResources().getString(R.string.sp_webstring), "");

        String getUrlStr = LoginActivity.SERVERURL + "/confirmlogin?accountnumber=" + params[0] + "&sessionaccountnumber=" + accountnumber + "&webstring=" + webstring;
        String serverTimeStamp = internetHelper.doGetString(getUrlStr);
        String encodedServerTimeStamp;
        if (internetHelper.getResponseCode() == 206){
            return "2";
        }
        try {
            encodedServerTimeStamp = URLEncoder.encode(serverTimeStamp, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "-2";
        }
        Log.d(TAG, "Server Timestamp: " + serverTimeStamp);
        String hashedPassword = cryptor.hashToString(params[1]);
        String hashedSaltetPassword = cryptor.hashToString(hashedPassword + serverTimeStamp);
        String postUrlStr = LoginActivity.SERVERURL + "/confirmlogin?accountnumber=" + params[0] + "&sessionaccountnumber=" + accountnumber + "&webstring=" + webstring
                + "&password=" + hashedSaltetPassword + "&servertimestamp=" + encodedServerTimeStamp;
        return internetHelper.doPostString(postUrlStr);
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG, "onPostExecute: " + result);
        int responseCode = Integer.parseInt(result);
        if (confirmationLoginDialogFragment != null) confirmationLoginDialogFragment.progressResponse(responseCode);
    }
}
