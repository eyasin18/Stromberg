package de.repictures.stromberg.AsyncTasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.R;
import de.repictures.stromberg.ScanProductActivity;

public class BuyItemsAsyncTask  extends AsyncTask<String, Void, String> {

    private static final String TAG = "BuyItemsAsyncTask";
    private ScanProductActivity scanProductActivity;
    private Internet internet = new Internet();

    public BuyItemsAsyncTask(ScanProductActivity scanProductActivity){

        this.scanProductActivity = scanProductActivity;
    }

    @Override
    protected void onPreExecute() {
        if (!internet.isNetworkAvailable(scanProductActivity)){
            cancel(true);
            onPostExecute("0");
        }
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            SharedPreferences sharedPref = scanProductActivity.getSharedPreferences(scanProductActivity.getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
            String accountnumber = sharedPref.getString(scanProductActivity.getResources().getString(R.string.sp_accountnumber), "");
            String webstring = sharedPref.getString(scanProductActivity.getResources().getString(R.string.sp_webstring), "");
            String getUrl = LoginActivity.SERVERURL + "/getshoppingrequest?code=" + webstring
                    + "&authaccountnumber=" + accountnumber
                    + "&accountnumber=" + accountnumber
                    + "&companynumber=" + params[1]
                    + "&shoppinglist=" + URLEncoder.encode(params[0], "UTF-8")
                    + "&madebyuser=true";
            return internet.doGetString(getUrl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "-4";
        }
    }

    @Override
    protected void onPostExecute(String responseStr) {
        String[] response = responseStr.split("ò");
        if (scanProductActivity != null) scanProductActivity.buyItemResult(Integer.parseInt(response[0]));
    }
}
