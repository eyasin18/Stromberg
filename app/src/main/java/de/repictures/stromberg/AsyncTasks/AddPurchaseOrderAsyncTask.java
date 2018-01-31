package de.repictures.stromberg.AsyncTasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import de.repictures.stromberg.Fragments.EditAccountnumberDialogFragment;
import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.R;

public class AddPurchaseOrderAsyncTask extends AsyncTask<String, Void, String> {

    private Internet internet = new Internet();
    private EditAccountnumberDialogFragment editAccountnumberDialogFragment;

    public AddPurchaseOrderAsyncTask(EditAccountnumberDialogFragment editAccountnumberDialogFragment){
        this.editAccountnumberDialogFragment = editAccountnumberDialogFragment;
    }

    @Override
    protected void onPreExecute() {
        if (!internet.isNetworkAvailable(editAccountnumberDialogFragment.getContext())){
            cancel(true);
            onPostExecute("0");
        }
    }

    @Override
    protected String doInBackground(String... params) {
        SharedPreferences sharedPref = editAccountnumberDialogFragment.getActivity().getSharedPreferences(editAccountnumberDialogFragment.getActivity().getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        String accountnumber = sharedPref.getString(editAccountnumberDialogFragment.getActivity().getResources().getString(R.string.sp_accountnumber), "");
        String url = LoginActivity.SERVERURL + "/getshoppingrequest?code=" + LoginActivity.WEBSTRING
                + "&authaccountnumber=" + accountnumber
                + "&accountnumber=" + params[0]
                + "&companynumber=" + LoginActivity.COMPANY_NUMBER
                + "&shoppinglist=" + ""
                + "&madbyuser=true";
        return internet.doGetString(url);
    }

    @Override
    protected void onPostExecute(String responseStr) {
        if (responseStr != null && responseStr.length() > 0 && editAccountnumberDialogFragment != null){
            String[] response = responseStr.split("ò");
            editAccountnumberDialogFragment.processAddPurchaseOrderResponse(Integer.parseInt(response[0]), Integer.parseInt(response[1]));
        }
    }
}
