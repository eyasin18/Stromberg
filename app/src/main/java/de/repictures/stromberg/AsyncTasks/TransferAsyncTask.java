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
                    + "&senderaccountnumber=" + URLEncoder.encode(transferArray[0], "UTF-8") + "&webstring=" + transferArray[2];
            String getUrlRespStr = internetHelper.doGetString(getUrlStr);

            String[] getResponse = getUrlRespStr.split("ò");

            if (Integer.parseInt(getResponse[0]) != 1){
                return null;
                //TODO: Return to login
            }

            PublicKey senderPublicKey = cryptor.stringToPublicKey(getResponse[1]);
            PublicKey receiverPublicKey = cryptor.stringToPublicKey(getResponse[2]);
            byte[] senderAesKey = cryptor.generateRandomAesKey();
            byte[] receiverAesKey = cryptor.generateRandomAesKey();

            byte[] encryptedSenderPurpose = cryptor.encryptSymetricFromString(transferArray[4], senderAesKey);
            byte[] encryptedReceiverPurpose = cryptor.encryptSymetricFromString(transferArray[4], receiverAesKey);

            byte[] encryptedSenderAesKey = cryptor.encryptAsymetric(senderAesKey, senderPublicKey);
            byte[] encryptedReceiverAesKey = cryptor.encryptAsymetric(receiverAesKey, receiverPublicKey);

            String postUrlStr = LoginActivity.SERVERURL + "/transfer"
                    + "?senderpurpose=" + cryptor.bytesToHex(encryptedSenderPurpose)
                    + "&senderkey=" + cryptor.bytesToHex(encryptedSenderAesKey)
                    + "&receiverpurpose=" + cryptor.bytesToHex(encryptedReceiverPurpose)
                    + "&receiverkey=" + cryptor.bytesToHex(encryptedReceiverAesKey)
                    + "&amount=" + URLEncoder.encode(transferArray[3], "UTF-8")
                    + "&receiveraccountnumber=" + URLEncoder.encode(transferArray[1], "UTF-8")
                    + "&senderaccountnumber=" + URLEncoder.encode(transferArray[0], "UTF-8")
                    + "&code=" + transferArray[2];

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

