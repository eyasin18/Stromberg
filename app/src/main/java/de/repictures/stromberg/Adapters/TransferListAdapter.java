package de.repictures.stromberg.Adapters;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.repictures.stromberg.Fragments.ShowTransferDetailDialogFragment;
import de.repictures.stromberg.POJOs.Transfer;
import de.repictures.stromberg.R;
import de.repictures.stromberg.TransfersActivity;

public class TransferListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private static final String TAG = "TransferListAdapter";
    private TransfersActivity activity;
    private List<Transfer> transfers;
    private List<Transfer> finishedFilteredTransfers;
    private LinearLayoutManager layoutManager;

    public TransferListAdapter(TransfersActivity activity, List<Transfer> transfers, LinearLayoutManager layoutManager) {
        this.activity = activity;
        this.transfers = transfers;
        this.finishedFilteredTransfers = transfers;
        this.layoutManager = layoutManager;
    }

    @Override
    public int getItemViewType(int position) {
        return (position >= 0) && (position < finishedFilteredTransfers.size()) ? VIEW_TYPE_ITEM : VIEW_TYPE_LOADING;
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

            Calendar calendar = finishedFilteredTransfers.get(position).getTime();
            itemHolder.transferDay.setText(activity.getResources().getStringArray(R.array.weekdays_short)[calendar.get(Calendar.DAY_OF_WEEK) - 1]);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            String time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
            itemHolder.transferTime.setText(time);

            double amount = finishedFilteredTransfers.get(position).getAmount();
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

            if(finishedFilteredTransfers.get(position).getOtherPersonName() != null) itemHolder.transferCompanyName.setText(finishedFilteredTransfers.get(position).getOtherPersonName());
            else itemHolder.transferCompanyName.setText(finishedFilteredTransfers.get(position).getOtherPersonAccountnumber());
            itemHolder.transferType.setText(finishedFilteredTransfers.get(position).getType());
            itemHolder.setClickListener((v, clickPosi, isLongClick) -> {
                Calendar calendar1 = finishedFilteredTransfers.get(clickPosi).getTime();
                int hour1 = calendar1.get(Calendar.HOUR_OF_DAY);
                int minute1 = calendar1.get(Calendar.MINUTE);
                String time1 = String.format(Locale.getDefault(), "%02d:%02d", hour1, minute1);
                String day = activity.getResources().getStringArray(R.array.weekdays_short)[calendar1.get(Calendar.DAY_OF_WEEK) - 1] + '.';

                ShowTransferDetailDialogFragment dialogFragment = new ShowTransferDetailDialogFragment();
                Bundle args = new Bundle();
                args.putString("day", day);
                args.putString("time", time1);
                args.putString("purpose", finishedFilteredTransfers.get(clickPosi).getPurpose());
                args.putString("isSenderStr", String.valueOf(finishedFilteredTransfers.get(clickPosi).isSender()));
                args.putString("person", finishedFilteredTransfers.get(clickPosi).getOtherPersonName());
                args.putString("type", finishedFilteredTransfers.get(clickPosi).getType());
                args.putString("accountnumber", finishedFilteredTransfers.get(clickPosi).getOtherPersonAccountnumber());
                dialogFragment.setArguments(args);
                FragmentManager fm = ((TransfersActivity) activity).getSupportFragmentManager();
                dialogFragment.show(fm, "ShowTransferDetailDialogFragment");
            });
        }
    }

    @Override
    public int getItemCount() {
        return activity.itemsLeft ? finishedFilteredTransfers.size() + 1 : finishedFilteredTransfers.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()){
                    finishedFilteredTransfers = transfers;
                } else {
                    List<Transfer> filteredTransfers = new ArrayList<>();
                    for (Transfer transfer : transfers) {
                        if (transfer.getOtherPersonName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredTransfers.add(transfer);
                        }
                    }
                    finishedFilteredTransfers = filteredTransfers;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = finishedFilteredTransfers;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                finishedFilteredTransfers = (ArrayList<Transfer>) filterResults.values;
                notifyDataSetChanged();
                int totalItemCount = getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (!activity.isLoading && totalItemCount <= (lastVisibleItem + activity.visibleThreshold)) {
                    activity.isLoading = true;
                    Log.d(TAG, "onScrolled: lastVisibleItem: " + lastVisibleItem);
                    activity.loadMoreItems();
                }
            }
        };
    }
}