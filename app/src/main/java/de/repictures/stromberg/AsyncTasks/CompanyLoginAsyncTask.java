package de.repictures.stromberg.AsyncTasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import de.repictures.stromberg.Fragments.CompanyLoginDialogFragment;
import de.repictures.stromberg.Helper.Cryptor;
import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.R;

public class CompanyLoginAsyncTask extends AsyncTask<String, Void, Integer>{

    private static final String TAG = "CompanyLoginActivity";

    private Internet internetHelper = new Internet();
    private CompanyLoginDialogFragment companyLoginDialogFragment;

    public CompanyLoginAsyncTask(CompanyLoginDialogFragment companyLoginDialogFragment) {
        this.companyLoginDialogFragment = companyLoginDialogFragment;
    }

    @Override
    protected void onPreExecute() {
        if (!internetHelper.isNetworkAvailable(companyLoginDialogFragment.getActivity())) {
            cancel(true);
            onPostExecute(-1);
        }
    }

    @Override
    protected Integer doInBackground(String... parameters) {
        Cryptor cryptor = new Cryptor();

        SharedPreferences sharedPref = companyLoginDialogFragment.getActivity().getSharedPreferences(companyLoginDialogFragment.getActivity().getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        String accountnumber = sharedPref.getString(companyLoginDialogFragment.getActivity().getResources().getString(R.string.sp_accountnumber), "");
        String encryptedPassword = cryptor.hashToString(parameters[0]);
        String baseUrl = LoginActivity.SERVERURL + "/companylogin?companynumber=" + LoginActivity.COMPANY_NUMBER
                + "&accountnumber=" + accountnumber
                + "&password=" + encryptedPassword
                + "&webstring=" + LoginActivity.WEBSTRING;
        String doGetString = internetHelper.doGetString(baseUrl);
        Log.d(TAG, "doInBackground: " + baseUrl);
        return Integer.parseInt(doGetString);
    }

    @Override
    protected void onPostExecute(Integer response) {
        if (companyLoginDialogFragment != null) companyLoginDialogFragment.progressResponse(response);
    }
}
