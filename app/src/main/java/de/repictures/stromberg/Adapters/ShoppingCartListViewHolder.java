package de.repictures.stromberg.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.repictures.stromberg.R;

public class ShoppingCartListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    private ShoppingCartListViewHolder.ClickListener clickListener;
    RelativeLayout productLayout;
    TextView productTextBig, productTextSmall, errorText;
    EditText productAmountEditText;
    ImageView productDeleteImage;

    public ShoppingCartListViewHolder(View itemView) {
        super(itemView);
        productTextBig = (TextView) itemView.findViewById(R.id.shopping_list_product_text_big);
        productLayout = (RelativeLayout) itemView.findViewById(R.id.shopping_list_product_layout);
        productLayout.setClickable(true);
        productLayout.setOnClickListener(this);
        productTextSmall = (TextView) itemView.findViewById(R.id.shopping_list_product_text_small);
        errorText = (TextView) itemView.findViewById(R.id.shopping_list_product_text_error);
        productAmountEditText = (EditText) itemView.findViewById(R.id.shopping_list_product_editText);
        productDeleteImage = (ImageView) itemView.findViewById(R.id.shopping_list_product_delete);
        productDeleteImage.setClickable(true);
        productDeleteImage.setOnClickListener(this);
    }

    public interface ClickListener {
        void onClick(View v, int position, boolean isLongClick);
    }

    public void setClickListener(ShoppingCartListViewHolder.ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onClick(View view) {
        clickListener.onClick(view, getLayoutPosition(), false);
    }

    @Override
    public boolean onLongClick(View view) {
        clickListener.onClick(view, getLayoutPosition(), true);
        return true;
    }
}