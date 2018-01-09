package de.repictures.stromberg.AsyncTasks;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import de.repictures.stromberg.Helper.Cryptor;
import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.MainActivity;

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
        if (response.length > 2) response[2] = getDecryptedPerson(response[2]);
        return response;
    }

    @Override
    protected void onPostExecute(String[] response) {
        if (mainActivity != null)
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
                    Intent i = new Intent(mainActivity, LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mainActivity.startActivity(i);
                    break;
            }
    }

    private String getDecryptedPerson(String encryptedStringHex) {
        byte[] encryptedPassword = cryptor.hashToByte(LoginActivity.PIN);
        byte[] encryptedName = cryptor.hexToBytes(encryptedStringHex);
        return cryptor.decryptSymmetricToString(encryptedName, encryptedPassword);
    }
}