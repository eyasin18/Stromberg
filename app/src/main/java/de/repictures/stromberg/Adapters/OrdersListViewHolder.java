package de.repictures.stromberg.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.repictures.stromberg.R;

public class OrdersListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    private OrdersListViewHolder.ClickListener clickListener;
    final RelativeLayout mRelativeLayout;
    final TextView day, time, cents, euros, customer;

    OrdersListViewHolder(View view) {
        super(view);

        day = (TextView) view.findViewById(R.id.order_list_day);
        time = (TextView) view.findViewById(R.id.order_list_time);
        cents = (TextView) view.findViewById(R.id.order_list_amount_cents);
        euros = (TextView) view.findViewById(R.id.order_list_amount_euros);
        customer = (TextView) view.findViewById(R.id.order_list_company_name);
        mRelativeLayout = (RelativeLayout) view.findViewById(R.id.orders_list_relative_layout);
        mRelativeLayout.setClickable(true);
        mRelativeLayout.setOnClickListener(this);
    }

    public interface ClickListener {
        void onClick(View v, int position, boolean isLongClick);
    }

    public void setClickListener(OrdersListViewHolder.ClickListener clickListener) {
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
