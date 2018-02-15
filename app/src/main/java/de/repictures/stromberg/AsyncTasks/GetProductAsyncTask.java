package de.repictures.stromberg.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.POJOs.Product;
import de.repictures.stromberg.ScanProductActivity;

public class GetProductAsyncTask extends AsyncTask<String, Void, String>{

    private ScanProductActivity scanProductActivity;
    private Internet internetHelper = new Internet();
    private String TAG = "GetProductAsyncTask";

    public GetProductAsyncTask(ScanProductActivity scanProductActivity){
        this.scanProductActivity = scanProductActivity;
    }

    @Override
    protected void onPreExecute() {
        if (!internetHelper.isNetworkAvailable(scanProductActivity)){
            cancel(true);
            onPostExecute("0");
        }
    }

    @Override
    protected String doInBackground(String... codes) {
        String baseUrl = LoginActivity.SERVERURL + "/postproducts?code=" + codes[0] + "&mbb=true";
        return internetHelper.doGetString(baseUrl);
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute: " + s);
        try {
            JSONObject responseObject = new JSONObject(s);
            if (responseObject.getInt("response_code") == 0){
                scanProductActivity.receiveResult();
            } else {
                JSONArray productsArray = responseObject.getJSONArray("products");
                List<Product> products = new ArrayList<>();
                for (int i = 0; i < productsArray.length(); i++) {
                    JSONObject productObject = productsArray.getJSONObject(i);
                    Product product = new Product();
                    product.setName(productObject.getString("name"));
                    product.setCompanyname(productObject.getString("company_name"));
                    product.setCompanynumber(productObject.getString("company_accountnumber"));
                    product.setPrice(productObject.getDouble("price"));
                    product.setSelfBuy(productObject.getBoolean("is_self_buy"));
                    product.setCode(productObject.getString("code"));
                    products.add(product);
                }
                scanProductActivity.receiveResult(products);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}