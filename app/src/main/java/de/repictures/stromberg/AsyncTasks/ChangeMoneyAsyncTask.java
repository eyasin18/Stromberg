package de.repictures.stromberg.AsyncTasks;

import android.os.AsyncTask;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import de.repictures.stromberg.Features.ChangeMoneyActivity;
import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;

public class ChangeMoneyAsyncTask extends AsyncTask<String, Void, String> {

    private Internet internet = new Internet();
    private ChangeMoneyActivity changeMoneyActivity;

    public ChangeMoneyAsyncTask(ChangeMoneyActivity changeMoneyActivity){
        this.changeMoneyActivity = changeMoneyActivity;
    }

    @Override
    protected void onPreExecute() {
        if (!internet.isNetworkAvailable(changeMoneyActivity)){
            cancel(true);
            onPostExecute("-1");
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String url = LoginActivity.SERVERURL + "changemoney?infos=" + URLEncoder.encode(strings[0], "UTF-8");
            return internet.doPostString(url);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        changeMoneyActivity.processServerResponse(Integer.valueOf(s));
    }
}
