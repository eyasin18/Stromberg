package de.repictures.stromberg.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.repictures.stromberg.Fragments.OrderDetailFragment;
import de.repictures.stromberg.OrderDetailActivity;
import de.repictures.stromberg.OrderListActivity;
import de.repictures.stromberg.R;

public class OrdersListAdapter extends RecyclerView.Adapter<OrdersListViewHolder> implements OrdersListViewHolder.ClickListener {

    private final boolean mTwoPane;
    private final List<int[]> amountsList;
    private final List<String> buyerAccountnumbers;
    private final List<String> dateTimes;
    private final List<boolean[]> isSelfBuys;
    private final List<Integer> numbers;
    private final List<double[]> prices;
    private final List<String[]> productCodes;
    private List<String[]> productNames;
    private final OrderListActivity mParentActivity;

    public OrdersListAdapter(OrderListActivity orderListActivity,
                             boolean mTwoPane,
                             List<int[]> amountsList,
                             List<String> buyerAccountnumbers,
                             List<String> dateTimes,
                             List<boolean[]> isSelfBuys,
                             List<Integer> numbers,
                             List<double[]> prices,
                             List<String[]> productCodes, List<String[]> productNames) {

        this.mParentActivity = orderListActivity;
        this.mTwoPane = mTwoPane;
        this.amountsList = amountsList;
        this.buyerAccountnumbers = buyerAccountnumbers;
        this.dateTimes = dateTimes;
        this.isSelfBuys = isSelfBuys;
        this.numbers = numbers;
        this.prices = prices;
        this.productCodes = productCodes;
        this.productNames = productNames;
    }

    @Override
    public OrdersListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_list_content, parent, false);
        return new OrdersListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OrdersListViewHolder holder, int position) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSSS z", Locale.GERMANY);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(dateTimes.get(position)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.day.setText(mParentActivity.getResources().getStringArray(R.array.weekdays_short)[calendar.get(Calendar.DAY_OF_WEEK) - 1]);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
        holder.time.setText(time);

        double priceSum = 0.0;

        for (int i = 0; i < prices.get(position).length; i++) {
            double price = prices.get(position)[i];
            int amount = amountsList.get(position)[i];
            priceSum += (price * amount);
        }
        String amountWholeStr;
        if (priceSum <= 0.0) {
            amountWholeStr = "-";
            holder.cents.setTextColor(mParentActivity.getResources().getColor(R.color.balance_minus));
            holder.euros.setTextColor(mParentActivity.getResources().getColor(R.color.balance_minus));
        } else {
            amountWholeStr = "+";
            holder.cents.setTextColor(mParentActivity.getResources().getColor(R.color.balance_plus));
            holder.euros.setTextColor(mParentActivity.getResources().getColor(R.color.balance_plus));
        }
        double priceSumFrac = priceSum % 1;
        long priceSumWhole = (long) Math.abs(priceSum - priceSumFrac);
        long priceSumFraction = Math.abs(Math.round((priceSumFrac) * 100));
        amountWholeStr += String.valueOf(priceSumWhole);
        String amountFractionStr = String.format(Locale.getDefault(), "%02d", priceSumFraction);
        holder.cents.setText(amountFractionStr);
        holder.euros.setText(amountWholeStr);

        holder.customer.setText(buyerAccountnumbers.get(position));

        holder.setClickListener(this);
    }

    @Override
    public int getItemCount() {
        return buyerAccountnumbers.size();
    }

    @Override
    public void onClick(View view, int position, boolean isLongClick) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(OrderDetailFragment.ARG_ACCOUNTNUMBER_ID, buyerAccountnumbers.get(position));
            arguments.putIntArray(OrderDetailFragment.ARG_AMOUNTS_ARRAY_ID, amountsList.get(position));
            arguments.putBooleanArray(OrderDetailFragment.ARG_IS_SELF_BUYS_ARRAY_ID, isSelfBuys.get(position));
            arguments.putInt(OrderDetailFragment.ARG_NUMBER_ID, numbers.get(position));
            arguments.putDoubleArray(OrderDetailFragment.ARG_PRICES_ARRAY_ID, prices.get(position));
            arguments.putStringArray(OrderDetailFragment.ARG_PRODUCT_CODES_ID, productCodes.get(position));
            arguments.putStringArray(OrderDetailFragment.ARG_PRODUCT_NAMES_ID, productNames.get(position));

            OrderDetailFragment fragment = new OrderDetailFragment();
            fragment.setArguments(arguments);
            mParentActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.order_detail_container, fragment)
                    .commit();
        } else {
            Context context = view.getContext();
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra(OrderDetailFragment.ARG_ACCOUNTNUMBER_ID, buyerAccountnumbers.get(position));
            intent.putExtra(OrderDetailFragment.ARG_AMOUNTS_ARRAY_ID, amountsList.get(position));
            intent.putExtra(OrderDetailFragment.ARG_IS_SELF_BUYS_ARRAY_ID, isSelfBuys.get(position));
            intent.putExtra(OrderDetailFragment.ARG_NUMBER_ID, numbers.get(position));
            intent.putExtra(OrderDetailFragment.ARG_PRICES_ARRAY_ID, prices.get(position));
            intent.putExtra(OrderDetailFragment.ARG_PRODUCT_CODES_ID, productCodes.get(position));
            intent.putExtra(OrderDetailFragment.ARG_PRODUCT_NAMES_ID, productNames.get(position));

            context.startActivity(intent);
        }
    }
}