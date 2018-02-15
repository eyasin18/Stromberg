package de.repictures.stromberg.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import de.repictures.stromberg.POJOs.Product;
import de.repictures.stromberg.R;
import de.repictures.stromberg.ScanProductActivity;

public class ChooseProductAdapter extends RecyclerView.Adapter<ChooseProductAdapter.ViewHolder>{

    private final ScanProductActivity scanProductActivity;
    private final List<Product> products;
    private final DecimalFormat df = new DecimalFormat("0.00");

    public ChooseProductAdapter(ScanProductActivity scanProductActivity, List<Product> products){
        this.scanProductActivity = scanProductActivity;
        this.products = products;
    }

    @Override
    public ChooseProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_chooser_list_item, parent, false);
        return new ChooseProductAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ChooseProductAdapter.ViewHolder holder, int position) {
        SharedPreferences sharedPref = scanProductActivity.getSharedPreferences(scanProductActivity.getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        float tax = sharedPref.getInt(scanProductActivity.getResources().getString(R.string.sp_vat), 100);
        double netPrice = 0;
        tax = tax/100;
        netPrice = products.get(position).getPrice() + products.get(position).getPrice()*tax;

        holder.companyText.setText(products.get(position).getCompanyname());
        holder.productNameText.setText(String.format(scanProductActivity.getResources().getString(R.string.product_name_format), products.get(position).getName()));
        holder.priceText.setText(String.format(scanProductActivity.getResources().getString(R.string.product_price_format), df.format(netPrice)));

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanProductActivity.addProduct(products.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView companyText, productNameText, priceText;
        RelativeLayout layout;

        public ViewHolder(View v) {
            super(v);
            companyText = (TextView) v.findViewById(R.id.product_chooser_company_name);
            productNameText = (TextView) v.findViewById(R.id.product_chooser_product_name);
            priceText = (TextView) v.findViewById(R.id.product_chooser_product_price);
            layout = (RelativeLayout) v.findViewById(R.id.product_chooser_layout);
        }
    }
}