package de.repictures.stromberg.AsyncTasks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

import de.repictures.stromberg.Helper.Cryptor;
import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.POJOs.Transfer;
import de.repictures.stromberg.R;
import de.repictures.stromberg.TransfersActivity;

public class GetTransfersAsyncTask extends AsyncTask<String, Void, List<Transfer>>{

    private TransfersActivity transfersActivity;
    private String TAG = "GetFinancialAsyncTask";
    private Cryptor cryptor = new Cryptor();
    private Internet internetHelper = new Internet();
    private boolean itemsLeft = false;
    private PrivateKey privateKey;
    private SharedPreferences sharedPref;

    public GetTransfersAsyncTask(TransfersActivity transfersActivity){
        this.transfersActivity = transfersActivity;
    }

    @Override
    protected void onPreExecute() {
        if (!internetHelper.isNetworkAvailable(transfersActivity)){
            cancel(true);
            onPostExecute(null);
        }

        //Lese privaten Schlüssel
        sharedPref = transfersActivity.getSharedPreferences(transfersActivity.getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        String encryptedPrivateKeyHex = sharedPref.getString(transfersActivity.getResources().getString(R.string.sp_encrypted_private_key_hex_2), null);
        String pin = sharedPref.getString(transfersActivity.getResources().getString(R.string.sp_pin), null);
        if (pin != null && encryptedPrivateKeyHex != null) {
            byte[] encryptedPrivateKey = cryptor.hexToBytes(encryptedPrivateKeyHex);
            byte[] passwordBytes = new byte[0];
            try {
                passwordBytes = pin.getBytes("ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                onPostExecute(null);
            }
            byte[] passwordKey = new byte[32];
            for (int i = 0; i < passwordKey.length; i++) {
                passwordKey[i] = passwordBytes[i % passwordBytes.length];
            }
            byte[] privateKeyByte = cryptor.decryptSymmetricToByte(encryptedPrivateKey, passwordKey);
            privateKey = cryptor.byteToPrivateKey(privateKeyByte);
        }
    }

    @Override
    protected List<Transfer> doInBackground(String... params) {
        String webstring = sharedPref.getString(transfersActivity.getResources().getString(R.string.sp_webstring), "");
        String baseUrl = LoginActivity.SERVERURL + "/posttransfers?accountnumber=" + params[0] + "&start=" + params[1] + "&code=" + webstring;

        String response = internetHelper.doGetString(baseUrl);
        try {
            JSONObject responseObject = new JSONObject(response);
            Intent i;
            switch (responseObject.getInt("response_code")){
                case 0:
                    i = new Intent(transfersActivity, LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    transfersActivity.startActivity(i);
                    break;
                case 1:
                    itemsLeft = responseObject.getBoolean("items_left");
                    JSONArray transfersArray = responseObject.getJSONArray("transfers");
                    List<Transfer> transfers = new ArrayList<>();
                    for (int j = 0; j < transfersArray.length(); j++) {
                        Transfer transfer = new Transfer();
                        transfer.setTime(transfersArray.getJSONObject(j).getString("date_time"));
                        transfer.setOtherPersonName(getDecryptedPurpose(transfersArray.getJSONObject(j).getString("name"), transfersArray.getJSONObject(j).getString("aes")));
                        transfer.setOtherPersonAccountnumber(transfersArray.getJSONObject(j).getString("accountnumber"));
                        transfer.setType(transfersArray.getJSONObject(j).getString("type"));
                        transfer.setPurpose(getDecryptedPurpose(transfersArray.getJSONObject(j).getString("purpose"), transfersArray.getJSONObject(j).getString("aes")));
                        transfer.setSender(transfersArray.getJSONObject(j).getBoolean("is_sender"));
                        transfer.setAmount(transfersArray.getJSONObject(j).getDouble("amount"));
                        transfers.add(transfer);
                    }
                    return transfers;
                case 2: //Account existiert nicht
                    i = new Intent(transfersActivity, LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    transfersActivity.startActivity(i);
                    break;
                case 3:
                    return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Transfer> transfers) {
        if (transfersActivity != null && transfers == null){
            transfersActivity.updateRecycler();
        } else if (transfersActivity != null){
            transfersActivity.updateRecycler(transfers, itemsLeft);
        }
    }

    private String getDecryptedPurpose(String encryptedStringHex, String encryptedAesKeyHex){
        if (privateKey == null) return null;
        try {
            Log.d(TAG, "Encrypted Purpose String: " + encryptedStringHex);

            byte[] encryptedAesKey = cryptor.hexToBytes(encryptedAesKeyHex);
            byte[] aesKey = cryptor.decryptAsymmetric(encryptedAesKey, privateKey);
            byte[] encryptedString = cryptor.hexToBytes(encryptedStringHex);
            byte[] decryptedString = cryptor.decryptSymmetricToByte(encryptedString, aesKey);
            return new String(decryptedString, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
