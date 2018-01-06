package de.repictures.stromberg.AsyncTasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;

import javax.mail.internet.MimeMultipart;

import de.repictures.stromberg.Helper.Cryptor;
import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.R;
import de.repictures.stromberg.TransfersActivity;

public class GetTransfersAsyncTask extends AsyncTask<String, Void, String[][]>{

    private TransfersActivity transfersActivity;
    private String TAG = "GetFinancialAsyncTask";
    private Cryptor cryptor = new Cryptor();
    private Internet internetHelper = new Internet();
    private boolean itemLeft = false;

    public GetTransfersAsyncTask(TransfersActivity transfersActivity){
        this.transfersActivity = transfersActivity;
    }

    @Override
    protected void onPreExecute() {
        if (!internetHelper.isNetworkAvailable(transfersActivity)){
            cancel(true);
            onPostExecute(null);
        }
    }

    @Override
    protected String[][] doInBackground(String... params) {
        String baseUrl = LoginActivity.SERVERURL + "/posttransfers?accountnumber=" + params[0] + "&start=" + params[1];

        MimeMultipart multi = internetHelper.doGetMultipart(baseUrl, "multipart/x-mixed-replace;boundary=End");
        String itemLeftStr = internetHelper.parseTextBodyPart(multi, 1);
        Log.d(TAG, "doInBackground: " + itemLeftStr);
        itemLeft = Boolean.parseBoolean(itemLeftStr);

        String rawResultStr = internetHelper.parseTextBodyPart(multi, 0);
        if(rawResultStr.endsWith("ĵ")){
            return null;
        } else if (rawResultStr.length() > 0){
            String[] response = rawResultStr.split("ň");
            String[][] transfersArray = new String[response.length][];
            for (int i = 0; i < response.length; i++) {
                transfersArray[i] = response[i].split("ò");
            }

            for (int x = 0; x < transfersArray.length; x++){
                for (int y = 0; y < transfersArray[x].length; y++){
                    if (y == 1){
                        transfersArray[x][y] = getDecryptedPerson(transfersArray[x][y]);
                    } else if (y == 4){
                        transfersArray[x][y] = getDecryptedPurpose(transfersArray[x][y], transfersArray[x][y+1]);
                    }
                }
            }
            return transfersArray;
        } else {
            return new String[0][0];
        }
    }

    @Override
    protected void onPostExecute(String[][] transfersArray) {
        if (transfersArray == null){
            transfersActivity.updateRecycler();
        } else {
            transfersActivity.updateRecycler(transfersArray, itemLeft);
        }
    }

    private String getDecryptedPerson(String encryptedStringHex) {
        try {
            Log.d(TAG, "Encrypted Purpose String: " + encryptedStringHex);
            SharedPreferences sharedPref = transfersActivity.getSharedPreferences(transfersActivity.getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
            String encryptedPrivateKeyHex = sharedPref.getString(transfersActivity.getResources().getString(R.string.sp_encrypted_private_key_hex_2), null);
            if (encryptedPrivateKeyHex == null) return "0";
            byte[] encryptedPrivateKey = cryptor.hexToBytes(encryptedPrivateKeyHex);
            byte[] passwordBytes = LoginActivity.PIN.getBytes("ISO-8859-1");
            byte[] passwordKey = new byte[32];
            for (int i = 0; i < passwordKey.length; i++){
                passwordKey[i] = passwordBytes[i % passwordBytes.length];
            }
            byte[] privateKeyByte = cryptor.decryptSymetricToByte(encryptedPrivateKey, passwordKey);
            PrivateKey privateKey = cryptor.byteToPrivateKey(privateKeyByte);
            byte[] encryptedString = cryptor.hexToBytes(encryptedStringHex);
            Log.d(TAG, "Encrypted Purpose Length: " + encryptedString.length);
            byte[] decryptedString = cryptor.decryptAsymetric(encryptedString, privateKey);
            Log.d(TAG, "Decrypted Purpose Length: " + decryptedString.length);
            return new String(decryptedString, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getDecryptedPurpose(String encryptedStringHex, String encryptedAesKeyHex){
        try {
            Log.d(TAG, "Encrypted Purpose String: " + encryptedStringHex);
            SharedPreferences sharedPref = transfersActivity.getSharedPreferences(transfersActivity.getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
            String encryptedPrivateKeyHex = sharedPref.getString(transfersActivity.getResources().getString(R.string.sp_encrypted_private_key_hex_2), null);
            if (encryptedPrivateKeyHex == null) return "0";
            byte[] encryptedPrivateKey = cryptor.hexToBytes(encryptedPrivateKeyHex);
            byte[] passwordBytes = LoginActivity.PIN.getBytes("ISO-8859-1");
            byte[] passwordKey = new byte[32];
            for (int i = 0; i < passwordKey.length; i++){
                passwordKey[i] = passwordBytes[i % passwordBytes.length];
            }
            byte[] privateKeyByte = cryptor.decryptSymetricToByte(encryptedPrivateKey, passwordKey);
            PrivateKey privateKey = cryptor.byteToPrivateKey(privateKeyByte);

            byte[] encryptedAesKey = cryptor.hexToBytes(encryptedAesKeyHex);
            byte[] aesKey = cryptor.decryptAsymetric(encryptedAesKey, privateKey);
            byte[] encryptedString = cryptor.hexToBytes(encryptedStringHex);
            byte[] decryptedString = cryptor.decryptSymetricToByte(encryptedString, aesKey);
            return new String(decryptedString, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
