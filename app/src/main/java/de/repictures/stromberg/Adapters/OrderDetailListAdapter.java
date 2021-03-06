package de.repictures.stromberg.Adapters;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import de.repictures.stromberg.Fragments.EditOrderItemDialogFragment;
import de.repictures.stromberg.Fragments.OrderDetailFragment;
import de.repictures.stromberg.POJOs.Product;
import de.repictures.stromberg.R;

public class OrderDetailListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OrderDetailListViewHolder.ClickListener {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_FINISH = 2;
    private final int VIEW_TYPE_ADD_PRODUCT = 1;
    private final int VIEW_TYPE_TAX = 3;

    private static final String TAG = "TransferListAdapter";
    private Activity activity;
    private final OrderDetailFragment orderDetailFragment;
    private List<Product> products;

    public OrderDetailListAdapter(FragmentActivity activity, OrderDetailFragment orderDetailFragment, List<Product> products) {
        this.activity = activity;
        this.orderDetailFragment = orderDetailFragment;
        this.products = products;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == products.size()){
            if (orderDetailFragment.purchaseOrder.isCompleted()) return VIEW_TYPE_FINISH;
            else return VIEW_TYPE_TAX;
        }
        else if (position == (products.size() + 1)) return VIEW_TYPE_ADD_PRODUCT;
        else if (position == (products.size() + 2)) return VIEW_TYPE_FINISH;
        else return VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_detail_list, parent, false);
            return new OrderDetailListViewHolder(v);
        } else if (viewType == VIEW_TYPE_FINISH){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_detail_list_button, parent, false);
            return new FinishButtonViewHolder(v);
        } else if (viewType == VIEW_TYPE_ADD_PRODUCT){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_detail_list_add_product, parent, false);
            return new AddProductViewHolder(v);
        } else if (viewType == VIEW_TYPE_TAX){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_detail_list_tax, parent, false);
            return new TaxViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(holder.getAdapterPosition()) == VIEW_TYPE_ITEM) {
            OrderDetailListViewHolder itemHolder = (OrderDetailListViewHolder) holder;
            Log.d(TAG, "onBindViewHolder: executed");
            itemHolder.setClickListener(this);
            itemHolder.product.setText(products.get(position).getName());
            itemHolder.amount.setText(String.valueOf(orderDetailFragment.purchaseOrder.getAmounts()[position]));
            if (orderDetailFragment.purchaseOrder.getProducts()[position].isSelfBuy() && orderDetailFragment.purchaseOrder.isMadeByUser()
                    || orderDetailFragment.purchaseOrder.isCompleted()){
                itemHolder.listLayout.setEnabled(false);
                itemHolder.amount.setEnabled(false);
                itemHolder.product.setEnabled(false);
                itemHolder.productDelete.setVisibility(View.GONE);
            }
        } else if (getItemViewType(holder.getAdapterPosition()) == VIEW_TYPE_FINISH){
            FinishButtonViewHolder finishHolder = (FinishButtonViewHolder) holder;
            if (orderDetailFragment.purchaseOrder.isCompleted() || products.size() < 1) finishHolder.finishButon.setEnabled(false);
            finishHolder.finishButon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (orderDetailFragment.valuesChanged || !orderDetailFragment.purchaseOrder.isMadeByUser()) orderDetailFragment.showConfirmationDialog();
                    else orderDetailFragment.finishOrder();
                }
            });
        } else if (getItemViewType(holder.getAdapterPosition()) == VIEW_TYPE_ADD_PRODUCT){
            AddProductViewHolder addProductViewHolder = (AddProductViewHolder) holder;
            if (orderDetailFragment.purchaseOrder.isCompleted()) addProductViewHolder.addItemLayout.setEnabled(false);
            addProductViewHolder.addItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditOrderItemDialogFragment editOrderItemDialogFragment = new EditOrderItemDialogFragment();
                    editOrderItemDialogFragment.setOrderDetailFragment(orderDetailFragment);
                    Bundle args = new Bundle();
                    args.putInt("position", position);
                    args.putBoolean("newProduct", true);
                    editOrderItemDialogFragment.setArguments(args);
                    FragmentManager fm = orderDetailFragment.getFragmentManager();
                    editOrderItemDialogFragment.show(fm, "ShowEditOrderItemDialogFragment");
                }
            });
        } else if (getItemViewType(holder.getAdapterPosition()) == VIEW_TYPE_TAX){
            TaxViewHolder taxViewHolder = (TaxViewHolder) holder;
            DecimalFormat df = new DecimalFormat("0.00");
            SharedPreferences sharedPref = activity.getSharedPreferences(activity.getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
            float tax = sharedPref.getInt(activity.getResources().getString(R.string.sp_vat), 100);
            taxViewHolder.vatTextView.setText(String.format(activity.getResources().getString(R.string.vat_format), String.valueOf(tax)));
            float hasToPayNetTotal = 0.0f;
            float payedGrossTotal = 0.0f;
            float hasToPayGrossTotal = 0.0f;
            tax = tax/100;
            for (int i = 0; i < orderDetailFragment.purchaseOrder.getProducts().length; i++){
                double productPrice = orderDetailFragment.purchaseOrder.getProducts()[i].getPrice();
                int count = orderDetailFragment.purchaseOrder.getAmounts()[i];
                if (orderDetailFragment.purchaseOrder.getProducts()[i].isSelfBuy()) {
                    payedGrossTotal += (float) (count * productPrice);
                } else {
                    hasToPayGrossTotal += (float) (count * productPrice);
                    hasToPayNetTotal += (float) (count * (productPrice * tax + productPrice));
                }
            }
            taxViewHolder.customerHasPayedTextView.setText(String.format(activity.getResources().getString(R.string.customer_has_already_payed), df.format(payedGrossTotal)));
            taxViewHolder.customerHasToPayTextView.setText(String.format(activity.getResources().getString(R.string.rest_sum), df.format(hasToPayGrossTotal)));
            taxViewHolder.customerHasToPayVATTextView.setText(String.format(activity.getResources().getString(R.string.customer_has_to_pay_vat), df.format(hasToPayNetTotal)));
        }
    }

    @Override
    public int getItemCount() {
        if (orderDetailFragment.purchaseOrder.isCompleted()) return products.size() + 1;
        else return products.size() + 3;
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
                products.remove(position);
                notifyItemRemoved(position);
                break;
        }
    }

    private class FinishButtonViewHolder extends RecyclerView.ViewHolder {
        Button finishButon;

        FinishButtonViewHolder(View view) {
            super(view);
            finishButon = (Button) view.findViewById(R.id.order_detail_list_finish_button);
        }
    }

    private class AddProductViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout addItemLayout;

        AddProductViewHolder(View view) {
            super(view);
            addItemLayout = (RelativeLayout) view.findViewById(R.id.order_detail_list_add_product_layout);
            addItemLayout.setClickable(true);
        }
    }

    private class TaxViewHolder extends RecyclerView.ViewHolder {
        TextView customerHasToPayTextView, vatTextView, customerHasToPayVATTextView, customerHasPayedTextView;

        TaxViewHolder(View view) {
            super(view);
            customerHasToPayTextView = (TextView) view.findViewById(R.id.order_detail_list_customer_has_to_pay);
            vatTextView = (TextView) view.findViewById(R.id.order_detail_list_vat);
            customerHasToPayVATTextView = (TextView) view.findViewById(R.id.order_detail_list_customer_has_to_pay_vat);
            customerHasPayedTextView = (TextView) view.findViewById(R.id.order_detail_list_customer_has_payed);
        }
    }
}