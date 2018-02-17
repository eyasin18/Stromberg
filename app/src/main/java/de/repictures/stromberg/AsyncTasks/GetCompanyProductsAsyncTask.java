package de.repictures.stromberg.AsyncTasks;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.repictures.stromberg.Features.EmployeesActivity;
import de.repictures.stromberg.Features.ProductsActivity;
import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.POJOs.Product;

public class GetCompanyProductsAsyncTask extends AsyncTask<String, Void, String>{

    private Internet internetHelper = new Internet();
    private ProductsActivity productsActivity;

    public GetCompanyProductsAsyncTask(ProductsActivity productsActivity){
        this.productsActivity = productsActivity;
    }

    @Override
    protected void onPreExecute() {
        if (!internetHelper.isNetworkAvailable(productsActivity)){
            cancel(true);
            onPostExecute("0");
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String baseUrl = LoginActivity.SERVERURL + "/postproducts?companynumber=" + params[0] + "&mbb=false";
        return internetHelper.doGetString(baseUrl);
    }

    @Override
    protected void onPostExecute(String s) {
        try{
            JSONObject responseObject = new JSONObject(s);
            if (responseObject.getInt("response_code") == 0){
                productsActivity.updateProducts(new ArrayList<>());
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
                    product.setBuyable(productObject.getBoolean("is_buyable"));
                    products.add(product);
                }
                productsActivity.updateProducts(products);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}