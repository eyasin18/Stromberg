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
import java.security.PrivateKey;

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
    protected String[][] doInBackground(String... accountKeys) {
        String baseUrl = LoginActivity.SERVERURL + "/posttransfers?accountnumber=" + accountKeys[0];

        String rawResultStr = internetHelper.doGetString(baseUrl);
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
            return null;
        }
    }

    @Override
    protected void onPostExecute(String[][] transfersArray) {
        if (transfersArray == null){
            transfersActivity.updateRecycler();
        } else {
            transfersActivity.updateRecycler(transfersArray);
        }
    }

    private String getDecryptedPerson(String encryptedStringHex) {
        Log.d(TAG, "Encrypted Purpose String: " + encryptedStringHex);
        SharedPreferences sharedPref = transfersActivity.getSharedPreferences(transfersActivity.getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        String encryptedPrivateKeyHex = sharedPref.getString(transfersActivity.getResources().getString(R.string.sp_encrypted_private_key_hex), null);
        if (encryptedPrivateKeyHex == null) return "0";
        byte[] encryptedPrivateKey = cryptor.hexToBytes(encryptedPrivateKeyHex);
        byte[] hashedPassword = cryptor.hashToByte(LoginActivity.PIN);
        String privateKeyHex = cryptor.decryptSymetricToString(encryptedPrivateKey, hashedPassword);
        Log.d(TAG, "Private Key String: " + privateKeyHex);
        PrivateKey privateKey = cryptor.stringToPrivateKey(privateKeyHex);
        byte[] encryptedString = cryptor.hexToBytes(encryptedStringHex);
        Log.d(TAG, "Encrypted Purpose Length: " + encryptedString.length);
        byte[] decryptedString = cryptor.decryptAsymetric(encryptedString, privateKey);
        Log.d(TAG, "Decrypted Purpose Length: " + decryptedString.length);
        try {
            return new String(decryptedString, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getDecryptedPurpose(String encryptedStringHex, String encryptedAesKeyHex){
        Log.d(TAG, "Encrypted Purpose String: " + encryptedStringHex);
        SharedPreferences sharedPref = transfersActivity.getSharedPreferences(transfersActivity.getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        String encryptedPrivateKeyHex = sharedPref.getString(transfersActivity.getResources().getString(R.string.sp_encrypted_private_key_hex), null);
        if (encryptedPrivateKeyHex == null) return "0";
        byte[] encryptedPrivateKey = cryptor.hexToBytes(encryptedPrivateKeyHex);
        byte[] hashedPassword = cryptor.hashToByte(LoginActivity.PIN);
        String privateKeyHex = cryptor.decryptSymetricToString(encryptedPrivateKey, hashedPassword);
        PrivateKey privateKey = cryptor.stringToPrivateKey(privateKeyHex);

        byte[] encryptedAesKey = cryptor.hexToBytes(encryptedAesKeyHex);
        byte[] aesKey = cryptor.decryptAsymetric(encryptedAesKey, privateKey);
        byte[] encryptedString = cryptor.hexToBytes(encryptedStringHex);
        byte[] decryptedString = cryptor.decryptSymetricToByte(encryptedString, aesKey);
        try {
            return new String(decryptedString, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
