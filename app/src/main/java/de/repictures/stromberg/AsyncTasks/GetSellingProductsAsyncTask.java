package de.repictures.stromberg.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.mail.internet.MimeMultipart;

import de.repictures.stromberg.CompanyActivity;
import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.POJOs.Product;
import de.repictures.stromberg.LoginActivity;

public class GetSellingProductsAsyncTask extends AsyncTask<String, Void, Product[]> {

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
    protected Product[] doInBackground(String... strings) {
        String url = LoginActivity.SERVERURL + "/postsellingproducts?companynumber=" + LoginActivity.COMPANY_NUMBER;
        MimeMultipart multipart = internet.doGetMultipart(url,"multipart/x-mixed-replace;boundary=End");
        String responseCodeStr = internet.parseTextBodyPart(multipart, 0);
        responseCode = Integer.parseInt(responseCodeStr);

        switch (responseCode){
            case 0:
                return null;
            case 1:
                try {
                    JSONObject productsJsonObject = internet.parseJsonBodyPart(multipart, 1);
                    JSONArray productsJsonArray = productsJsonObject.getJSONArray("products");
                    Product[] products = new Product[productsJsonArray.length()];
                    for (int i = 0; i < productsJsonArray.length(); i++){
                        JSONArray product = productsJsonArray.getJSONArray(i);
                        products[i] = new Product();
                        products[i].setCode(product.getString(0));
                        products[i].setName(product.getString(1));
                        products[i].setPrice(product.getDouble(2));
                        products[i].setSelfBuy(product.getBoolean(3));
                    }
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
