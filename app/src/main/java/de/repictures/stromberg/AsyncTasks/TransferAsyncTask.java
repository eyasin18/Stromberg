package de.repictures.stromberg.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;

import de.repictures.stromberg.Features.TransferDialogActivity;
import de.repictures.stromberg.LoginActivity;

public class TransferAsyncTask extends AsyncTask<String, Void, String> {

    private TransferDialogActivity transferDialogActivity;
    private String TAG = "TransferAsyncTask";

    public TransferAsyncTask(TransferDialogActivity transferDialogActivity){
        this.transferDialogActivity = transferDialogActivity;
    }

    @Override
    protected String doInBackground(String... transferArray) {
        String resp = "";
        try {
            String baseUrl = LoginActivity.SERVERURL + "/transfer?receiveraccountnumber=" + URLEncoder.encode(transferArray[1], "UTF-8") + "&intendedpurpose=" + URLEncoder.encode(transferArray[3], "UTF-8")
                    + "&amount=" + URLEncoder.encode(transferArray[2], "UTF-8") + "&senderaccountnumber=" + URLEncoder.encode(transferArray[0], "UTF-8");
            URL url = new URL(baseUrl);
            URLConnection urlConnection = url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader r = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            Log.d(TAG, "doInBackground: " + total);
            resp += total;
            resp = URLDecoder.decode(resp, "UTF-8");
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resp;
    }

    @Override
    protected void onPostExecute(String s) {
        transferDialogActivity.postResult(Integer.parseInt(s));
    }
}

