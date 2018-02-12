package de.repictures.stromberg.Adapters;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.repictures.stromberg.Fragments.ShowTransferDetailDialogFragment;
import de.repictures.stromberg.POJOs.Transfer;
import de.repictures.stromberg.R;
import de.repictures.stromberg.TransfersActivity;

public class TransferListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private static final String TAG = "TransferListAdapter";
    private Activity activity;
    private List<Transfer> transfers;
    public boolean itemsLeft;

    public TransferListAdapter(Activity activity, List<Transfer> transfers, boolean itemsLeft) {
        this.activity = activity;
        this.transfers = transfers;
        this.itemsLeft = itemsLeft;
    }

    @Override
    public int getItemViewType(int position) {
        return (position >= 0) && (position < transfers.size()) ? VIEW_TYPE_ITEM : VIEW_TYPE_LOADING;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.transferlist, parent, false);
            return new TransferListViewHolder(v);
        } else if (viewType == VIEW_TYPE_LOADING){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.transfer_load_more_list, parent, false);
            return new LoadingViewHolder(v);
        }
        return null;
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.load_more_progress_bar);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TransferListViewHolder) {
            TransferListViewHolder itemHolder = (TransferListViewHolder) holder;
            Log.d(TAG, "onBindViewHolder: executed");

            Calendar calendar = transfers.get(position).getTime();
            itemHolder.transferDay.setText(activity.getResources().getStringArray(R.array.weekdays_short)[calendar.get(Calendar.DAY_OF_WEEK) - 1]);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            String time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
            itemHolder.transferTime.setText(time);

            double amount = transfers.get(position).getAmount();
            String amountWholeStr;
            if (amount <= 0.0) {
                amountWholeStr = "-";
                itemHolder.transferAmountCents.setTextColor(activity.getResources().getColor(R.color.balance_minus));
                itemHolder.transferAmountEuros.setTextColor(activity.getResources().getColor(R.color.balance_minus));
            } else {
                amountWholeStr = "+";
                itemHolder.transferAmountCents.setTextColor(activity.getResources().getColor(R.color.balance_plus));
                itemHolder.transferAmountEuros.setTextColor(activity.getResources().getColor(R.color.balance_plus));
            }
            double amountFrac = amount % 1;
            long amountWhole = (long) Math.abs(amount - amountFrac);
            long amountFraction = Math.abs(Math.round((amountFrac) * 100));
            amountWholeStr += String.valueOf(amountWhole);
            String amountFractionStr = String.format(Locale.getDefault(), "%02d", amountFraction);
            itemHolder.transferAmountCents.setText(amountFractionStr);
            itemHolder.transferAmountEuros.setText(amountWholeStr);

            if(transfers.get(position).getOtherPersonName() != null) itemHolder.transferCompanyName.setText(transfers.get(position).getOtherPersonName());
            else itemHolder.transferCompanyName.setText(transfers.get(position).getOtherPersonAccountnumber());
            itemHolder.transferType.setText(transfers.get(position).getType());
            itemHolder.setClickListener((v, clickPosi, isLongClick) -> {
                Calendar calendar1 = transfers.get(clickPosi).getTime();
                int hour1 = calendar1.get(Calendar.HOUR_OF_DAY);
                int minute1 = calendar1.get(Calendar.MINUTE);
                String time1 = String.format(Locale.getDefault(), "%02d:%02d", hour1, minute1);
                String day = activity.getResources().getStringArray(R.array.weekdays_short)[calendar1.get(Calendar.DAY_OF_WEEK) - 1] + '.';

                ShowTransferDetailDialogFragment dialogFragment = new ShowTransferDetailDialogFragment();
                Bundle args = new Bundle();
                args.putString("day", day);
                args.putString("time", time1);
                args.putString("purpose", transfers.get(clickPosi).getPurpose());
                args.putString("isSenderStr", String.valueOf(transfers.get(clickPosi).isSender()));
                args.putString("person", transfers.get(clickPosi).getOtherPersonName());
                args.putString("type", transfers.get(clickPosi).getType());
                args.putString("accountnumber", transfers.get(clickPosi).getOtherPersonAccountnumber());
                dialogFragment.setArguments(args);
                FragmentManager fm = ((TransfersActivity) activity).getSupportFragmentManager();
                dialogFragment.show(fm, "ShowTransferDetailDialogFragment");
            });
        }
    }

    @Override
    public int getItemCount() {
        return itemsLeft ? transfers.size() + 1 : transfers.size();
    }
}
