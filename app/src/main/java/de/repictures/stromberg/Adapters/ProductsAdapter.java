package de.repictures.stromberg.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import de.repictures.stromberg.Features.ProductsActivity;
import de.repictures.stromberg.POJOs.Product;
import de.repictures.stromberg.R;

public class ProductsAdapter  extends RecyclerView.Adapter<ProductsAdapter.ViewHolder>{

    private final List<Product> products;
    private final DecimalFormat df = new DecimalFormat("0.00");
    private ProductsActivity productsActivity;

    public ProductsAdapter(ProductsActivity productsActivity, List<Product> products){
        this.productsActivity = productsActivity;
        this.products = products;
    }

    @Override
    public ProductsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_list, parent, false);
        return new ProductsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ProductsAdapter.ViewHolder holder, int position) {
        holder.productNameText.setText(products.get(position).getName());
        holder.productCodeText.setText(products.get(position).getCode());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productsActivity.editProduct(products.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView productNameText, productCodeText;
        RelativeLayout layout;

        public ViewHolder(View v) {
            super(v);
            productNameText = (TextView) v.findViewById(R.id.products_list_product_name);
            productCodeText = (TextView) v.findViewById(R.id.products_list_product_code);
            layout = (RelativeLayout) v.findViewById(R.id.products_list_layout);
        }
    }
}