package de.repictures.stromberg.AsyncTasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.repictures.stromberg.Fragments.EditAccountnumberPurchaseDialogFragment;
import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.R;

public class AddPurchaseOrderAsyncTask extends AsyncTask<String, Void, String> {

    private Internet internet = new Internet();
    private EditAccountnumberPurchaseDialogFragment editAccountnumberPurchaseDialogFragment;
    private int companyPosition;

    public AddPurchaseOrderAsyncTask(EditAccountnumberPurchaseDialogFragment editAccountnumberPurchaseDialogFragment, int companyPosition){
        this.editAccountnumberPurchaseDialogFragment = editAccountnumberPurchaseDialogFragment;
        this.companyPosition = companyPosition;
    }

    @Override
    protected void onPreExecute() {
        if (!internet.isNetworkAvailable(editAccountnumberPurchaseDialogFragment.getContext())){
            cancel(true);
            onPostExecute("0");
        }
    }

    @Override
    protected String doInBackground(String... params) {
        SharedPreferences sharedPref = editAccountnumberPurchaseDialogFragment.getActivity().getSharedPreferences(editAccountnumberPurchaseDialogFragment.getActivity().getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        String accountnumber = sharedPref.getString(editAccountnumberPurchaseDialogFragment.getActivity().getResources().getString(R.string.sp_accountnumber), "");
        String webstring = sharedPref.getString(editAccountnumberPurchaseDialogFragment.getActivity().getResources().getString(R.string.sp_webstring), "");
        List<String> companyNumbers = new ArrayList<>(sharedPref.getStringSet(editAccountnumberPurchaseDialogFragment.getActivity().getResources().getString(R.string.sp_companynumbers), new HashSet<>()));
        String url = LoginActivity.SERVERURL + "/getshoppingrequest?code=" + webstring
                + "&authaccountnumber=" + accountnumber
                + "&accountnumber=" + params[0]
                + "&companynumber=" + companyNumbers.get(companyPosition)
                + "&shoppinglist=" + ""
                + "&madbyuser=true";
        return internet.doGetString(url);
    }

    @Override
    protected void onPostExecute(String responseStr) {
        if (responseStr != null && responseStr.length() > 0 && editAccountnumberPurchaseDialogFragment != null){
            String[] response = responseStr.split("ò");
            if (response.length > 1) editAccountnumberPurchaseDialogFragment.processAddPurchaseOrderResponse(Integer.parseInt(response[0]), Integer.parseInt(response[1]));
            else editAccountnumberPurchaseDialogFragment.processAddPurchaseOrderResponse(Integer.parseInt(response[0]), 0);
        }
    }
}
