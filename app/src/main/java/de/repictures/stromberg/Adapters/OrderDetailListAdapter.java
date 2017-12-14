package de.repictures.stromberg.Adapters;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.repictures.stromberg.CompanyActivity;
import de.repictures.stromberg.Fragments.EditOrderItemDialogFragment;
import de.repictures.stromberg.Fragments.OrderDetailFragment;
import de.repictures.stromberg.R;
import de.repictures.stromberg.TransfersActivity;

public class OrderDetailListAdapter extends RecyclerView.Adapter<OrderDetailListViewHolder> implements OrderDetailListViewHolder.ClickListener {

    private static final String TAG = "TransferListAdapter";
    private Activity activity;
    private final OrderDetailFragment orderDetailFragment;

    public OrderDetailListAdapter(FragmentActivity activity, OrderDetailFragment orderDetailFragment) {

        this.activity = activity;
        this.orderDetailFragment = orderDetailFragment;
    }

    @Override
    public OrderDetailListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_detail_list, parent, false);
        return new OrderDetailListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OrderDetailListViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: executed");
        holder.setClickListener(this);
        holder.product.setText(orderDetailFragment.productCodesList.get(position));
        holder.amount.setText(String.valueOf(orderDetailFragment.amountsList.get(position)));
    }

    @Override
    public int getItemCount() {
        return orderDetailFragment.productCodesList.size();
    }

    @Override
    public void onClick(View v, int position, boolean isLongClick) {
        switch (v.getId()){
            case R.id.order_detail_list_product_layout:
                EditOrderItemDialogFragment editOrderItemDialogFragment = new EditOrderItemDialogFragment();
                Bundle args = new Bundle();
                args.putInt("position", position);
                editOrderItemDialogFragment.setArguments(args);
                FragmentManager fm = orderDetailFragment.getFragmentManager();
                editOrderItemDialogFragment.show(fm, "ShowTransferDetailDialogFragment");
                break;
            case R.id.order_detail_list_product_delete:
                orderDetailFragment.productCodesList.remove(position);
                orderDetailFragment.amountsList.remove(position);
                orderDetailFragment.isSelfBuysList.remove(position);
                orderDetailFragment.pricesList.remove(position);
                notifyItemRemoved(position);
                break;
        }
    }
}
