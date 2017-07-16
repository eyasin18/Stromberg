package de.repictures.stromberg.Adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.repictures.stromberg.R;

public class TransferListAdapter extends RecyclerView.Adapter<TransferListViewHolder> {

    private static final String TAG = "TransferListAdapter";
    private Activity activity;
    private String[][] transfers;

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
        holder.transferCompanyName.setText(transfers[position][1]);
        holder.transferType.setText(transfers[position][3]);
        holder.setClickListener(new TransferListViewHolder.ClickListener(){

            @Override
            public void onClick(View v, int position, boolean isLongClick) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return transfers.length;
    }
}
