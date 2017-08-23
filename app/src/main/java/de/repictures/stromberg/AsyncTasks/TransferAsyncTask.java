package de.repictures.stromberg.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.PublicKey;

import de.repictures.stromberg.Features.TransferDialogActivity;
import de.repictures.stromberg.Helper.Cryptor;
import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;

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
            byte[] senderAesKey = cryptor.generateRandomAesKey();
            byte[] receiverAesKey = cryptor.generateRandomAesKey();

            byte[] encryptedSenderPurpose = cryptor.encryptSymetricFromString(transferArray[3], senderAesKey);
            byte[] encryptedReceiverPurpose = cryptor.encryptSymetricFromString(transferArray[3], receiverAesKey);

            byte[] encryptedSenderAesKey = cryptor.encryptAsymetric(senderAesKey, senderPublicKey);
            byte[] encryptedReceiverAesKey = cryptor.encryptAsymetric(receiverAesKey, receiverPublicKey);

            String postUrlStr = LoginActivity.SERVERURL + "/transfer"
                    + "?senderpurpose=" + cryptor.bytesToHex(encryptedSenderPurpose)
                    + "&senderkey=" + cryptor.bytesToHex(encryptedSenderAesKey)
                    + "&receiverpurpose=" + cryptor.bytesToHex(encryptedReceiverPurpose)
                    + "&receiverkey=" + cryptor.bytesToHex(encryptedReceiverAesKey)
                    + "&amount=" + URLEncoder.encode(transferArray[2], "UTF-8")
                    + "&receiveraccountnumber=" + URLEncoder.encode(transferArray[1], "UTF-8")
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

