package de.repictures.stromberg.AsyncTasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.mail.internet.MimeMultipart;

import de.repictures.stromberg.CompanyActivity;
import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.POJOs.Product;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.R;

public class GetSellingProductsAsyncTask extends AsyncTask<Integer, Void, Product[]> {

    private CompanyActivity companyActivity;
    private Internet internet = new Internet();
    private String TAG = "GetProducts";
    private int responseCode = 0;

    public GetSellingProductsAsyncTask(CompanyActivity companyActivity){
        this.companyActivity = companyActivity;
    }

    @Override
    protected void onPreExecute() {
        if (!internet.isNetworkAvailable(companyActivity)){
            cancel(true);
            responseCode = -1;
        }
    }

    @Override
    protected Product[] doInBackground(Integer... params) {
        SharedPreferences sharedPref = companyActivity.getSharedPreferences(companyActivity.getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        List<String> companyNumbers = new ArrayList<>(sharedPref.getStringSet(companyActivity.getResources().getString(R.string.sp_companynumbers), new HashSet<>()));
        String url = LoginActivity.SERVERURL + "postsellingproducts?companynumber=" + companyNumbers.get(params[0]);
        MimeMultipart multipart = internet.doGetMultipart(url,"multipart/x-mixed-replace;boundary=End");
        String responseCodeStr = internet.parseTextBodyPart(multipart, 0);
        responseCode = Integer.parseInt(responseCodeStr);

        switch (responseCode){
            case 0:
                return null;
            case 1:
                try {
                    JSONObject jsonObject = internet.parseJsonBodyPart(multipart, 1);

                    JSONArray productsJsonArray = jsonObject.getJSONArray("products");
                    Product[] products = new Product[productsJsonArray.length()];
                    for (int i = 0; i < productsJsonArray.length(); i++){
                        JSONArray product = productsJsonArray.getJSONArray(i);
                        products[i] = new Product();
                        products[i].setCode(product.getString(0));
                        products[i].setName(product.getString(1));
                        products[i].setPrice(product.getDouble(2));
                        products[i].setSelfBuy(product.getBoolean(3));
                    }

                    JSONArray taxJsonArray = jsonObject.getJSONArray("wage_taxes");
                    int[] taxArray = new int[taxJsonArray.length()];
                    for (int i = 0; i < taxJsonArray.length(); i++) {
                        taxArray[i] = taxJsonArray.getInt(i);
                    }
                    CompanyActivity.WAGE_TAX_ARRAY = taxArray;
                    return products;
                } catch (JSONException e) {
                    Log.e(TAG, "doInBackground: ", e);
                    responseCode = 0;
                    return null;
                }
            default:
                return null;
        }
    }

    @Override
    protected void onPostExecute(Product[] products) {
        CompanyActivity.SELLING_PRODUCTS = products;
        if (companyActivity != null) companyActivity.processResponse(responseCode);
    }
}