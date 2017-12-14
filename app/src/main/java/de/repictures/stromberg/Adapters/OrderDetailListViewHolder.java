package de.repictures.stromberg.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.repictures.stromberg.R;

public class OrderDetailListViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

    private OrderDetailListViewHolder.ClickListener clickListener;
    public TextView product, amount;
    public ImageView productDelete;
    public RelativeLayout listLayout;

    public OrderDetailListViewHolder(View itemView) {
        super(itemView);
        amount = (TextView) itemView.findViewById(R.id.order_detail_list_amount);
        product = (TextView) itemView.findViewById(R.id.order_detail_list_product_text_big);
        productDelete = (ImageView) itemView.findViewById(R.id.order_detail_list_product_delete);
        productDelete.setOnClickListener(this);
        listLayout = (RelativeLayout) itemView.findViewById(R.id.order_detail_list_product_layout);
        listLayout.setClickable(true);
        listLayout.setTag(this);
        listLayout.setOnClickListener(this);
        listLayout.setOnLongClickListener(this);
    }

    public interface ClickListener{
        void onClick(View v, int position, boolean isLongClick);
    }

    public void setClickListener(OrderDetailListViewHolder.ClickListener clickListener){
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

