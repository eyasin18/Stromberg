package de.repictures.stromberg.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.repictures.stromberg.Helper.Cryptor;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.R;

public class TransferListAdapter extends RecyclerView.Adapter<TransferListViewHolder> {

    private static final String TAG = "TransferListAdapter";
    private Activity activity;
    private String[][] transfers;
    private Cryptor cryptor = new Cryptor();

    public TransferListAdapter(Activity activity, String[][] transfers) {
        this.activity = activity;
        this.transfers = transfers;
        String transferStr = "";
        for (String[] transfer : transfers) {
            for (String aTransfer : transfer) {
                transferStr += aTransfer;
                transferStr += "~";
            }
        }
        Log.d(TAG, "TransferListAdapter: " + transferStr);
    }

    @Override
    public TransferListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.transferlist, parent, false);
        return new TransferListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TransferListViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: executed");
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSSS z", Locale.GERMANY);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(transfers[position][0]));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.transferDay.setText(activity.getResources().getStringArray(R.array.weekdays_short)[calendar.get(Calendar.DAY_OF_WEEK)-1]);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        holder.transferTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
        double amount = Double.parseDouble(transfers[position][5]);
        if(amount <= 0){
            holder.tranferAmountCents.setTextColor(activity.getResources().getColor(R.color.balance_minus));
            holder.transferAmountEuros.setTextColor(activity.getResources().getColor(R.color.balance_minus));
        } else {
            holder.tranferAmountCents.setTextColor(activity.getResources().getColor(R.color.balance_plus));
            holder.transferAmountEuros.setTextColor(activity.getResources().getColor(R.color.balance_plus));
        }
        double amountFrac = amount%1;
        double amountWhole = amount - amountFrac;
        long amountFraction = Math.abs(Math.round((amountFrac)*100));
        String amountWholeStr = String.valueOf(amountWhole);
        String amountFractionStr = String.format(Locale.getDefault(), "%02d", amountFraction);
        holder.tranferAmountCents.setText(amountFractionStr);
        holder.transferAmountEuros.setText(amountWholeStr.substring(0, amountWholeStr.length() - 2));
        holder.transferCompanyName.setText(getDecryptedOwnerStr(transfers[position][1]));
        holder.transferType.setText(transfers[position][3]);
        holder.setClickListener(new TransferListViewHolder.ClickListener(){

            @Override
            public void onClick(View v, int position, boolean isLongClick) {

            }
        });
    }

    private String getDecryptedOwnerStr(String encryptedOwnerHex) {
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        String encryptedPrivateKeyHex = sharedPref.getString(activity.getResources().getString(R.string.sp_encrypted_private_key_hex), null);
        if (encryptedPrivateKeyHex == null) return "0";
        byte[] encryptedPrivateKey = cryptor.hexToBytes(encryptedPrivateKeyHex);
        byte[] hashedPassword = cryptor.hashToByte(LoginActivity.PIN);
        String privateKeyHex = cryptor.decryptSymetricToString(encryptedPrivateKey, hashedPassword);
        PrivateKey privateKey = cryptor.stringToPrivateKey(privateKeyHex);
        byte[] encryptedOwner = cryptor.hexToBytes(encryptedOwnerHex);
        byte[] decryptedOwner = cryptor.decryptAsymetric(encryptedOwner, privateKey);
        try {
            return new String(decryptedOwner, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return transfers.length;
    }
}
