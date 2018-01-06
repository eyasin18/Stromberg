package de.repictures.stromberg.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;

import de.repictures.stromberg.Adapters.OrderDetailListAdapter;
import de.repictures.stromberg.AsyncTasks.CompletePurchaseOrderAsyncTask;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.POJOs.PurchaseOrder;
import de.repictures.stromberg.R;

public class OrderDetailFragment extends Fragment {

    private OrderDetailListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private LoadingDialogFragment loadingDialogFragment;
    private View rootView;

    public PurchaseOrder purchaseOrder;
    public boolean valuesChanged = false;

    public static final String ARG_ACCOUNTNUMBER_ID = "accountnumber_id";
    private String TAG = "OrderDetailFragment";

    public OrderDetailFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity activity = this.getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(purchaseOrder.getBuyerAccountnumber());
        }

        loadingDialogFragment = new LoadingDialogFragment();
        Bundle args = new Bundle();
        args.putInt(LoadingDialogFragment.ARG_TITLE, R.string.complete_purchase_order);
        loadingDialogFragment.setArguments(args);
        loadingDialogFragment.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.order_detail, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.order_detail);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new OrderDetailListAdapter(getActivity(), OrderDetailFragment.this, Arrays.asList(purchaseOrder.getProducts()));
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    public void updateAdapter() {
        mAdapter.notifyDataSetChanged();
    }

    public void showConfirmationDialog(){
        ConfirmationLoginDialogFragment confirmationLoginDialogFragment = new ConfirmationLoginDialogFragment();
        confirmationLoginDialogFragment.setOrderDetailFragment(OrderDetailFragment.this);
        Bundle args = new Bundle();
        args.putString(ARG_ACCOUNTNUMBER_ID, purchaseOrder.getBuyerAccountnumber());
        confirmationLoginDialogFragment.setArguments(args);
        FragmentManager fm = getFragmentManager();
        confirmationLoginDialogFragment.show(fm, "ShowConfirmationLoginDialogFragment");
    }

    public void finishOrder(){
        FragmentManager fm = getFragmentManager();
        loadingDialogFragment.show(fm, "showLoadingDialogFragment");
        CompletePurchaseOrderAsyncTask asyncTask = new CompletePurchaseOrderAsyncTask(OrderDetailFragment.this);
        asyncTask.execute();
    }

    public void handleFinishResponse(int responseCode){
        Log.d(TAG, "handleFinishResponse: " + responseCode);
        loadingDialogFragment.dismiss();
        switch (responseCode){
            case -1: //Koi Inderned
                Snackbar.make(rootView, R.string.internet_problems, Snackbar.LENGTH_LONG)
                        .show();
                break;
            case 0: //Daten nicht richtig übertragen
                Snackbar.make(rootView, R.string.server_error, Snackbar.LENGTH_LONG)
                        .show();
                break;
            case 1: //Alles gut
                purchaseOrder.setCompleted(true);
            insertAdapterItem();
                Snackbar.make(rootView, R.string.confirmation_completed, Snackbar.LENGTH_LONG)
                        .show();
                break;
            case 2: //Webstring nicht aktuell
                Snackbar.make(rootView, R.string.session_invalid, Snackbar.LENGTH_LONG)
                        .setAction(R.string.login_again, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(getActivity(), LoginActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }
                        })
                        .show();
                break;
        }
    }

    public int getPurchaseOrderNumber(){
        return purchaseOrder.getNumber();
    }

    public String getAccountnumber(){
        return purchaseOrder.getBuyerAccountnumber();
    }

    public void insertAdapterItem() {
        mAdapter = new OrderDetailListAdapter(getActivity(), OrderDetailFragment.this, Arrays.asList(purchaseOrder.getProducts()));
        mRecyclerView.setAdapter(mAdapter);
    }
}