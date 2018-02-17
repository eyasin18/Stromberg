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
    private Internet internetHelper = new Internet();

    public TransferAsyncTask(TransferDialogActivity transferDialogActivity){
        this.transferDialogActivity = transferDialogActivity;
    }

    @Override
    protected void onPreExecute() {
        if (!internetHelper.isNetworkAvailable(transferDialogActivity)){
            cancel(true);
            onPostExecute("-1");
        }
    }

    @Override
    protected String doInBackground(String... transferArray) {
        //0 = senderAccountnumber; 1 = receiverAccountnumber; 2 = webstring; 3 = amount; 4 = purpose; 5 = senderName; 6 = receiverName
        try {
            String getUrlStr = LoginActivity.SERVERURL + "/transfer?receiveraccountnumber=" + URLEncoder.encode(transferArray[1], "UTF-8")
                    + "&senderaccountnumber=" + URLEncoder.encode(transferArray[0], "UTF-8") + "&webstring=" + transferArray[2];
            String getUrlRespStr = internetHelper.doGetString(getUrlStr);

            String[] getResponse = getUrlRespStr.split("ò");

            if (Integer.parseInt(getResponse[0]) != 7){
                onPostExecute(getResponse[0]);
                return null;
            }

            PublicKey senderPublicKey = cryptor.stringToPublicKey(getResponse[1]);
            PublicKey receiverPublicKey = cryptor.stringToPublicKey(getResponse[2]);
            byte[] senderAesKey = cryptor.generateRandomAesKey();
            byte[] receiverAesKey = cryptor.generateRandomAesKey();

            byte[] encryptedSenderPurpose = cryptor.encryptSymmetricFromString(transferArray[4], senderAesKey);
            byte[] encryptedReceiverPurpose = cryptor.encryptSymmetricFromString(transferArray[4], receiverAesKey);
            byte[] encryptedSenderName = cryptor.encryptSymmetricFromString(transferArray[5], receiverAesKey);
            byte[] encryptedReceiverName = cryptor.encryptSymmetricFromString(transferArray[6], senderAesKey);

            byte[] encryptedSenderAesKey = cryptor.encryptAsymmetric(senderAesKey, senderPublicKey);
            byte[] encryptedReceiverAesKey = cryptor.encryptAsymmetric(receiverAesKey, receiverPublicKey);

            String postUrlStr = LoginActivity.SERVERURL + "/transfer"
                    + "?senderpurpose=" + cryptor.bytesToHex(encryptedSenderPurpose)
                    + "&senderkey=" + cryptor.bytesToHex(encryptedSenderAesKey)
                    + "&receiverpurpose=" + cryptor.bytesToHex(encryptedReceiverPurpose)
                    + "&receiverkey=" + cryptor.bytesToHex(encryptedReceiverAesKey)
                    + "&amount=" + URLEncoder.encode(transferArray[3], "UTF-8")
                    + "&receiveraccountnumber=" + URLEncoder.encode(transferArray[1], "UTF-8")
                    + "&senderaccountnumber=" + URLEncoder.encode(transferArray[0], "UTF-8")
                    + "&sendername=" + cryptor.bytesToHex(encryptedSenderName)
                    + "&receivername=" + cryptor.bytesToHex(encryptedReceiverName)
                    + "&code=" + transferArray[2];

            return internetHelper.doPostString(postUrlStr);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        if (transferDialogActivity != null) transferDialogActivity.postResult(Integer.parseInt(s));
    }
}