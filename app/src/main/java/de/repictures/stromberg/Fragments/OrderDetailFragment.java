package de.repictures.stromberg.Fragments;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;

import de.repictures.stromberg.Adapters.OrderDetailListAdapter;
import de.repictures.stromberg.R;

public class OrderDetailFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private OrderDetailListAdapter mAdapter;
    public ArrayList<String> productCodesList;
    public ArrayList<Integer> amountsList;
    public ArrayList<Boolean> isSelfBuysList;
    public ArrayList<Double> pricesList;

    public static final String ARG_ACCOUNTNUMBER_ID = "accountnumber_id",
            ARG_AMOUNTS_ARRAY_ID = "amounts_array_id",
            ARG_IS_SELF_BUYS_ARRAY_ID = "is_self_buys_array_id",
            ARG_NUMBER_ID = "number_id",
            ARG_PRICES_ARRAY_ID = "prices_array_id",
            ARG_PRODUCT_CODES_ID = "product_codes_id";

    public OrderDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ACCOUNTNUMBER_ID)) {
            String accountnumber = getArguments().getString(ARG_ACCOUNTNUMBER_ID);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(accountnumber);
            }
        }
        String[] productCodesArray = getArguments().getStringArray(ARG_PRODUCT_CODES_ID);
        this.productCodesList = new ArrayList<>(Arrays.asList(productCodesArray));
        int[] amountsArray = getArguments().getIntArray(ARG_AMOUNTS_ARRAY_ID);
        this.amountsList = new ArrayList<Integer>() {{ for (int i : amountsArray) add(i); }};
        boolean[] isSelfBuysArray = getArguments().getBooleanArray(ARG_IS_SELF_BUYS_ARRAY_ID);
        this.isSelfBuysList = new ArrayList<Boolean>() {{ for (boolean i : isSelfBuysArray) add(i); }};
        double[] pricesArray = getArguments().getDoubleArray(ARG_PRICES_ARRAY_ID);
        this.pricesList = new ArrayList<Double>() {{ for (double i : pricesArray) add(i); }};

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.order_detail, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.order_detail);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new OrderDetailListAdapter(getActivity(), OrderDetailFragment.this);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    public void updateItem(int position) {
        productCodesList.set(position, "blub");
        mAdapter.notifyDataSetChanged();
    }
}
