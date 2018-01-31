package de.repictures.stromberg.AsyncTasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import de.repictures.stromberg.Fragments.OrderDetailFragment;
import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.R;

public class CompletePurchaseOrderAsyncTask extends AsyncTask<String, Void, String> {

    private Internet internetHelper = new Internet();
    private OrderDetailFragment fragment;

    public CompletePurchaseOrderAsyncTask(OrderDetailFragment fragment){
        this.fragment = fragment;
    }

    @Override
    protected void onPreExecute() {
        if (!internetHelper.isNetworkAvailable(fragment.getContext())){
            cancel(true);
            onPostExecute("-1");
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String url = LoginActivity.SERVERURL + "/completepurchaseorder";

        StringBuilder productCodesBuilder = new StringBuilder();
        StringBuilder amountsBuilder = new StringBuilder();
        StringBuilder pricesBuilder = new StringBuilder();
        StringBuilder isSelfBuysBuilder = new StringBuilder();

        for (int i = 0; i < fragment.purchaseOrder.getAmounts().length; i++){
            productCodesBuilder.append(fragment.purchaseOrder.getProducts()[i].getCode()).append("ò");
            amountsBuilder.append(fragment.purchaseOrder.getAmounts()[i]).append("ò");
            pricesBuilder.append(fragment.purchaseOrder.getProducts()[i].getPrice()).append("ò");
            isSelfBuysBuilder.append(fragment.purchaseOrder.getProducts()[i].isSelfBuy()).append("ò");
        }

        SharedPreferences sharedPref = fragment.getActivity().getSharedPreferences(fragment.getActivity().getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        String accountnumber = sharedPref.getString(fragment.getActivity().getResources().getString(R.string.sp_accountnumber), "");

        HttpEntity entity = MultipartEntityBuilder.create()
                .addTextBody("webstring", LoginActivity.WEBSTRING)
                .addTextBody("buyeraccountnumber", fragment.purchaseOrder.getBuyerAccountnumber())
                .addTextBody("companynumber", LoginActivity.COMPANY_NUMBER)
                .addTextBody("purchaseOrderNumber", String.valueOf(fragment.purchaseOrder.getNumber()))
                .addTextBody("productcodes", productCodesBuilder.toString())
                .addTextBody("amounts", amountsBuilder.toString())
                .addTextBody("prices", pricesBuilder.toString())
                .addTextBody("isselfbuy", isSelfBuysBuilder.toString())
                .addTextBody("selleraccountnumber", accountnumber)
                .build();

        return internetHelper.doMultipartRequest(Internet.POST, url, entity).trim();
    }

    @Override
    protected void onPostExecute(String response) {
        if (fragment != null) fragment.handleFinishResponse(Integer.parseInt(response));
    }
}
