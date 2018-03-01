package de.repictures.stromberg.AsyncTasks;

import android.os.AsyncTask;

import de.repictures.stromberg.Features.TransferWageActivity;
import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;

public class TransferWageAsyncTask extends AsyncTask<String, Void, String> {

    private Internet internetHelper = new Internet();
    private TransferWageActivity transferWageActivity;

    public TransferWageAsyncTask(TransferWageActivity transferWageActivity){

        this.transferWageActivity = transferWageActivity;
    }

    @Override
    protected void onPreExecute() {
        if (!internetHelper.isNetworkAvailable(transferWageActivity)){
            cancel(true);
            onPostExecute("-1");
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String url = LoginActivity.SERVERURL + "doadmintransferwage?company=" + params[0] + "&employee=" + params[1] + "&amount=" + params[2] + "&hours=" + params[3];
        return internetHelper.doGetString(url);
    }

    @Override
    protected void onPostExecute(String s) {
        try{
            transferWageActivity.postResponse(Integer.valueOf(s));
        } catch (NumberFormatException e){
            e.printStackTrace();
            transferWageActivity.postResponse(-2);
        }
    }
}