package de.repictures.stromberg.AsyncTasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
        try {
            JSONArray productCodes = new JSONArray(), amounts = new JSONArray(), prices = new JSONArray(), isSelfBuys = new JSONArray();
            for (int i = 0; i < fragment.purchaseOrder.getAmounts().length; i++) {
                productCodes.put(fragment.purchaseOrder.getProducts()[i].getCode());
                amounts.put(fragment.purchaseOrder.getAmounts()[i]);
                prices.put(fragment.purchaseOrder.getProducts()[i].getPrice());
                isSelfBuys.put(fragment.purchaseOrder.getProducts()[i].isSelfBuy());
            }

            SharedPreferences sharedPref = fragment.getActivity().getSharedPreferences(fragment.getActivity().getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
            String accountnumber = sharedPref.getString(fragment.getActivity().getResources().getString(R.string.sp_accountnumber), "");
            String webstring = sharedPref.getString(fragment.getActivity().getResources().getString(R.string.sp_webstring), "");
            List<String> companyNumbers = new ArrayList<>(sharedPref.getStringSet(fragment.getActivity().getResources().getString(R.string.sp_companynumbers), new HashSet<>()));

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("webstring", webstring);
            jsonObject.put("buyeraccountnumber", fragment.purchaseOrder.getBuyerAccountnumber());
            jsonObject.put("companynumber", companyNumbers.get(fragment.companyPosition));
            jsonObject.put("purchaseOrderNumber", fragment.purchaseOrder.getNumber());
            jsonObject.put("selleraccountnumber", accountnumber);
            jsonObject.put("isselfbuy", isSelfBuys);
            jsonObject.put("prices", prices);
            jsonObject.put("amounts", amounts);
            jsonObject.put("productcodes", productCodes);

            String url = LoginActivity.SERVERURL + "/completepurchaseorder?json=" + URLEncoder.encode(jsonObject.toString(), "UTF-8");
            return internetHelper.doPostString(url).trim();
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String response) {
        if (fragment != null) fragment.handleFinishResponse(Integer.parseInt(response));
    }
}
