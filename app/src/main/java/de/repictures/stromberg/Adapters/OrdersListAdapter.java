package de.repictures.stromberg.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.repictures.stromberg.Fragments.EditAccountnumberPurchaseDialogFragment;
import de.repictures.stromberg.Fragments.OrderDetailFragment;
import de.repictures.stromberg.OrderDetailActivity;
import de.repictures.stromberg.OrderListActivity;
import de.repictures.stromberg.POJOs.PurchaseOrder;
import de.repictures.stromberg.R;

public class OrdersListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OrdersListViewHolder.ClickListener, Filterable {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_ADD_ORDER = 1;

    private final boolean mTwoPane;
    private List<PurchaseOrder> purchaseOrders;
    private List<PurchaseOrder> finishedFilteredPurchaseOrders;
    private final OrderListActivity mParentActivity;

    public OrdersListAdapter(OrderListActivity orderListActivity,
                             boolean mTwoPane,
                             List<PurchaseOrder> purchaseOrders) {

        this.mParentActivity = orderListActivity;
        this.mTwoPane = mTwoPane;
        this.purchaseOrders = purchaseOrders;
        this.finishedFilteredPurchaseOrders = purchaseOrders;
    }

    @Override
    public int getItemViewType(int position) {
        return position > 0 ? VIEW_TYPE_ITEM : VIEW_TYPE_ADD_ORDER;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.order_list_content, parent, false);
            return new OrdersListViewHolder(view);
        } else if (viewType == VIEW_TYPE_ADD_ORDER){
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.order_list_content_add_order, parent, false);
            return new AddPurchaseOrderViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(holder.getAdapterPosition()) == VIEW_TYPE_ITEM) {
            OrdersListViewHolder itemHolder = (OrdersListViewHolder) holder;
            int arrayPosition = position - 1;
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSSS z", Locale.GERMANY);
            Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(sdf.parse(finishedFilteredPurchaseOrders.get(arrayPosition).getDateTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            itemHolder.day.setText(mParentActivity.getResources().getStringArray(R.array.weekdays_short)[calendar.get(Calendar.DAY_OF_WEEK) - 1]);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            String time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
            itemHolder.time.setText(time);

            double priceSum = 0.0;

            for (int i = 0; i < finishedFilteredPurchaseOrders.get(arrayPosition).getAmounts().length; i++) {
                double price = finishedFilteredPurchaseOrders.get(arrayPosition).getProducts()[i].getPrice();
                int amount = finishedFilteredPurchaseOrders.get(arrayPosition).getAmounts()[i];
                priceSum += (price * amount);
            }
            String amountWholeStr;
            if (priceSum < 0.0 && !finishedFilteredPurchaseOrders.get(arrayPosition).isCompleted()) {
                amountWholeStr = "-";
                itemHolder.cents.setTextColor(mParentActivity.getResources().getColor(R.color.balance_minus));
                itemHolder.euros.setTextColor(mParentActivity.getResources().getColor(R.color.balance_minus));
            } else if (!finishedFilteredPurchaseOrders.get(arrayPosition).isCompleted()){
                amountWholeStr = "+";
                itemHolder.cents.setTextColor(mParentActivity.getResources().getColor(R.color.balance_plus));
                itemHolder.euros.setTextColor(mParentActivity.getResources().getColor(R.color.balance_plus));
            } else if (priceSum < 0.0 && finishedFilteredPurchaseOrders.get(arrayPosition).isCompleted()){
                amountWholeStr = "-";
                itemHolder.cents.setTextColor(mParentActivity.getResources().getColor(R.color.grey));
                itemHolder.euros.setTextColor(mParentActivity.getResources().getColor(R.color.grey));
            } else {
                amountWholeStr = "+";
                itemHolder.cents.setTextColor(mParentActivity.getResources().getColor(R.color.grey));
                itemHolder.euros.setTextColor(mParentActivity.getResources().getColor(R.color.grey));
            }
            double priceSumFrac = priceSum % 1;
            long priceSumWhole = (long) Math.abs(priceSum - priceSumFrac);
            long priceSumFraction = Math.abs(Math.round((priceSumFrac) * 100));
            amountWholeStr += String.valueOf(priceSumWhole);
            String amountFractionStr = String.format(Locale.getDefault(), "%02d", priceSumFraction);
            itemHolder.cents.setText(amountFractionStr);
            itemHolder.euros.setText(amountWholeStr);

            itemHolder.customer.setText(finishedFilteredPurchaseOrders.get(arrayPosition).getBuyerAccountnumber());

            if (!finishedFilteredPurchaseOrders.get(arrayPosition).isCompleted()) {
                itemHolder.day.setTextColor(mParentActivity.getResources().getColor(R.color.transfer_date_color));
                itemHolder.time.setTextColor(mParentActivity.getResources().getColor(R.color.transfer_date_color));
                itemHolder.customer.setTextColor(mParentActivity.getResources().getColor(R.color.transfer_date_color));
            } else {
                itemHolder.day.setTextColor(mParentActivity.getResources().getColor(R.color.grey));
                itemHolder.time.setTextColor(mParentActivity.getResources().getColor(R.color.grey));
                itemHolder.customer.setTextColor(mParentActivity.getResources().getColor(R.color.grey));
            }

            itemHolder.setClickListener(this);
        } else if (getItemViewType(holder.getAdapterPosition()) == VIEW_TYPE_ADD_ORDER){
            AddPurchaseOrderViewHolder addHolder = (AddPurchaseOrderViewHolder) holder;
            addHolder.addItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("fuck off", "onClick: clicked");
                    EditAccountnumberPurchaseDialogFragment dialogFragment = new EditAccountnumberPurchaseDialogFragment();
                    dialogFragment.setOrderListActivity(mParentActivity);
                    dialogFragment.setCompanyPosition(mParentActivity.companyPosition);
                    dialogFragment.show(mParentActivity.getSupportFragmentManager(), "blub");
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return finishedFilteredPurchaseOrders.size() + 1;
    }

    @Override
    public void onClick(View view, int position, boolean isLongClick) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();

            OrderDetailFragment fragment = new OrderDetailFragment();
            fragment.purchaseOrder = finishedFilteredPurchaseOrders.get(position -1);
            fragment.setArguments(arguments);
            mParentActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.order_detail_container, fragment)
                    .commit();
        } else {
            Context context = view.getContext();
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("company_array_position", mParentActivity.companyPosition);
            intent.putExtra("purchaseOrder", finishedFilteredPurchaseOrders.get(position -1));
            context.startActivity(intent);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()){
                    finishedFilteredPurchaseOrders = purchaseOrders;
                } else {
                    List<PurchaseOrder> filteredPurchaseOrders = new ArrayList<>();
                    for (PurchaseOrder purchaseOrder : purchaseOrders){
                        if (purchaseOrder.getBuyerAccountnumber().contains(charString)){
                            filteredPurchaseOrders.add(purchaseOrder);
                        }
                    }
                    finishedFilteredPurchaseOrders = filteredPurchaseOrders;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = finishedFilteredPurchaseOrders;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                finishedFilteredPurchaseOrders = (ArrayList<PurchaseOrder>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    private class AddPurchaseOrderViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout addItemLayout;

        AddPurchaseOrderViewHolder(View view) {
            super(view);
            addItemLayout = (RelativeLayout) view.findViewById(R.id.order_list_content_add_order_layout);
            addItemLayout.setClickable(true);
        }
    }
}