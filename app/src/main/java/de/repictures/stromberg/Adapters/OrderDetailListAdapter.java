package de.repictures.stromberg.Adapters;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.repictures.stromberg.CompanyActivity;
import de.repictures.stromberg.Fragments.EditOrderItemDialogFragment;
import de.repictures.stromberg.Fragments.OrderDetailFragment;
import de.repictures.stromberg.R;
import de.repictures.stromberg.TransfersActivity;

public class OrderDetailListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OrderDetailListViewHolder.ClickListener {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_FINISH = 1;

    private static final String TAG = "TransferListAdapter";
    private Activity activity;
    private final OrderDetailFragment orderDetailFragment;

    public OrderDetailListAdapter(FragmentActivity activity, OrderDetailFragment orderDetailFragment) {

        this.activity = activity;
        this.orderDetailFragment = orderDetailFragment;
    }

    private class FinishButtonViewHolder extends RecyclerView.ViewHolder {
        Button finishButon;

        FinishButtonViewHolder(View view) {
            super(view);
            finishButon = (Button) view.findViewById(R.id.order_detail_list_finish_button);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position >= 0) && (position < orderDetailFragment.productCodesList.size()) ? VIEW_TYPE_ITEM : VIEW_TYPE_FINISH;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_detail_list, parent, false);
            return new OrderDetailListViewHolder(v);
        } else if (viewType == VIEW_TYPE_FINISH){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_detail_list, parent, false);
            return new FinishButtonViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof OrderDetailListViewHolder) {
            OrderDetailListViewHolder itemHolder = (OrderDetailListViewHolder) holder;
            Log.d(TAG, "onBindViewHolder: executed");
            itemHolder.setClickListener(this);
            itemHolder.product.setText(orderDetailFragment.productNamesList.get(position));
            itemHolder.amount.setText(String.valueOf(orderDetailFragment.amountsList.get(position)));
        } else if (holder instanceof FinishButtonViewHolder){
            FinishButtonViewHolder finishHolder = (FinishButtonViewHolder) holder;
            finishHolder.finishButon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    orderDetailFragment.showConfirmationDialog();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return orderDetailFragment.productCodesList.size() + 1;
    }

    @Override
    public void onClick(View v, int position, boolean isLongClick) {
        switch (v.getId()){
            case R.id.order_detail_list_product_layout:
                EditOrderItemDialogFragment editOrderItemDialogFragment = new EditOrderItemDialogFragment();
                editOrderItemDialogFragment.setOrderDetailFragment(orderDetailFragment);
                Bundle args = new Bundle();
                args.putInt("position", position);
                editOrderItemDialogFragment.setArguments(args);
                FragmentManager fm = orderDetailFragment.getFragmentManager();
                editOrderItemDialogFragment.show(fm, "ShowEditOrderItemDialogFragment");
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
