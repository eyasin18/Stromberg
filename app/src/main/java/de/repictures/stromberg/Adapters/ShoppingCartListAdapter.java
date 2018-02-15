package de.repictures.stromberg.Adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.repictures.stromberg.POJOs.Product;
import de.repictures.stromberg.R;
import de.repictures.stromberg.ScanProductActivity;

public class ShoppingCartListAdapter extends RecyclerView.Adapter<ShoppingCartListViewHolder> implements ShoppingCartListViewHolder.ClickListener {

    private static final String TAG = "ShoppingCartListAdapter";
    private ScanProductActivity productActivity;
    private ArrayList<Product> productsList = new ArrayList<>();

    public ShoppingCartListAdapter(ScanProductActivity productActivity, ArrayList<Product> productsList) {
        this.productActivity = productActivity;
        this.productsList = productsList;
    }

    @Override
    public ShoppingCartListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_cart_list, parent, false);
        return new ShoppingCartListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ShoppingCartListViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: executed");
        holder.setClickListener(this);

        if (!productsList.get(position).isSelfBuy()){
            holder.productTextBig.setVisibility(View.INVISIBLE);
            holder.productTextSmall.setVisibility(View.VISIBLE);
            holder.productTextSmall.setText(productsList.get(position).getName());
            holder.errorText.setVisibility(View.VISIBLE);
            for (int i = 0; i < holder.productLayout.getChildCount(); i++) {
                View child = holder.productLayout.getChildAt(i);
                child.setEnabled(false);
            }
            holder.productDeleteImage.setEnabled(true);
            holder.productLayout.setClickable(false);
        } else {
            holder.productTextBig.setText(productsList.get(position).getName());
        }

        productActivity.productAmounts.add(1);
        holder.productAmountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (holder.productAmountEditText.getText().toString().length() > 0){
                    productActivity.productAmounts.set(position, Integer.parseInt(holder.productAmountEditText.getText().toString()));
                    productActivity.updateSums();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public void onClick(View v, int position, boolean isLongClick) {
        switch (v.getId()){
            case R.id.shopping_list_product_delete:
                int productIndex = productActivity.scanResults.indexOf(productActivity.productsList.get(position).getCode());
                if (productIndex > -1) productActivity.scanResults.remove(productIndex);
                productActivity.productsList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, productActivity.productsList.size());
                if (productActivity.productsList.size() < 1){
                    productActivity.enableCheckoutButton(false);
                }
                productActivity.updateSums();
                break;
            default:
                break;
        }
    }
}