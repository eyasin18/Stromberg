package de.repictures.stromberg.AsyncTasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.PrivateKey;
import java.security.PublicKey;

import de.repictures.stromberg.Features.TransferDialogActivity;
import de.repictures.stromberg.Helper.Cryptor;
import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.R;

public class TransferAsyncTask extends AsyncTask<String, Void, String> {

    private TransferDialogActivity transferDialogActivity;
    private String TAG = "TransferAsyncTask";
    private Cryptor cryptor = new Cryptor();

    public TransferAsyncTask(TransferDialogActivity transferDialogActivity){
        this.transferDialogActivity = transferDialogActivity;
    }

    @Override
    protected String doInBackground(String... transferArray) {
        try {
            Internet internetHelper = new Internet();
            String getUrlStr = LoginActivity.SERVERURL + "/transfer?receiveraccountnumber=" + URLEncoder.encode(transferArray[1], "UTF-8")
                    + "&senderaccountnumber=" + URLEncoder.encode(transferArray[0], "UTF-8");
            String getUrlRespStr = internetHelper.doGetString(getUrlStr);

            String[] publicKeys = getUrlRespStr.split("ò");
            PublicKey senderPublicKey = cryptor.stringToPublicKey(publicKeys[0]);
            PublicKey receiverPublicKey = cryptor.stringToPublicKey(publicKeys[1]);
            byte[] encryptedSenderPurpose = cryptor.encryptAsymetric(transferArray[3], senderPublicKey);
            byte[] encryptedReceiverPurpose = cryptor.encryptAsymetric(transferArray[3], receiverPublicKey);
            String postUrlStr = LoginActivity.SERVERURL + "/transfer?senderpurpose=" + cryptor.bytesToHex(encryptedSenderPurpose) + "&receiverpurpose=" + cryptor.bytesToHex(encryptedReceiverPurpose)
                    + "&amount=" + URLEncoder.encode(transferArray[2], "UTF-8") + "&receiveraccountnumber=" + URLEncoder.encode(transferArray[1], "UTF-8")
                    + "&senderaccountnumber=" + URLEncoder.encode(transferArray[0], "UTF-8");

            return internetHelper.doPostString(postUrlStr);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        transferDialogActivity.postResult(Integer.parseInt(s));
    }
}

