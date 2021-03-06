package de.repictures.stromberg.AsyncTasks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.mail.internet.MimeMultipart;

import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.OrderListActivity;
import de.repictures.stromberg.POJOs.Product;
import de.repictures.stromberg.POJOs.PurchaseOrder;
import de.repictures.stromberg.R;

public class GetPurchaseOrdersAsyncTask extends AsyncTask<Integer, Void, MimeMultipart>{

    private Internet internetHelper = new Internet();
    private OrderListActivity orderListActivity;
    private SharedPreferences sharedPref;

    public GetPurchaseOrdersAsyncTask(OrderListActivity orderListActivity) {
        this.orderListActivity = orderListActivity;
    }

    @Override
    protected MimeMultipart doInBackground(Integer... params) {
        sharedPref = orderListActivity.getSharedPreferences(orderListActivity.getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        String accountnumber = sharedPref.getString(orderListActivity.getResources().getString(R.string.sp_accountnumber), "");
        String webstring = sharedPref.getString(orderListActivity.getResources().getString(R.string.sp_webstring), "");
        List<String> companyNumbers = new ArrayList<>(sharedPref.getStringSet(orderListActivity.getResources().getString(R.string.sp_companynumbers), new HashSet<>()));

        String urlStr = LoginActivity.SERVERURL + "/postpurchaseorders?companynumber=" + companyNumbers.get(params[0])
                                + "&accountnumber=" + accountnumber
                                + "&webstring=" + webstring;
        return internetHelper.doPostMultipart(urlStr, "multipart/x-mixed-replace;boundary=End");
    }

    @Override
    protected void onPostExecute(MimeMultipart resp) {
        int responseCode = Integer.parseInt(internetHelper.parseTextBodyPart(resp, 0));

        if (orderListActivity != null)
            switch (responseCode){
                case 1:
                    JSONObject jsonObject = internetHelper.parseJsonBodyPart(resp, 1);

                    try {
                        JSONArray nestedAmountsJsonArray = jsonObject.getJSONArray("amounts");
                        JSONArray buyerAccountnumbersJsonArray = jsonObject.getJSONArray("buyerAccountnumbers");
                        JSONArray dateTimesJsonArray = jsonObject.getJSONArray("dateTimes");
                        JSONArray nestedIsSelfBuysJsonArray = jsonObject.getJSONArray("isSelfBuys");
                        JSONArray numbersJsonArray = jsonObject.getJSONArray("numbers");
                        JSONArray nestedPricesJsonArray = jsonObject.getJSONArray("prices");
                        JSONArray nestedProductCodesJsonArray = jsonObject.getJSONArray("productCodes");
                        JSONArray nestedProductNamesJsonArray = jsonObject.getJSONArray("productNames");
                        JSONArray completedJsonArray = jsonObject.getJSONArray("completed");
                        JSONArray madeByUserJsonArray = jsonObject.getJSONArray("madeByUser");
                        JSONArray sellingProducts = jsonObject.getJSONArray("selling_products");

                        ArrayList<PurchaseOrder> purchaseOrders = new ArrayList<>();
                        for (int i = 0; i < nestedAmountsJsonArray.length(); i++){
                            PurchaseOrder purchaseOrder = new PurchaseOrder();
                            JSONArray amountsJsonArray = nestedAmountsJsonArray.getJSONArray(i);
                            JSONArray isSelfBuyJsonArray = nestedIsSelfBuysJsonArray.getJSONArray(i);
                            JSONArray pricesJsonArray = nestedPricesJsonArray.getJSONArray(i);
                            JSONArray productCodesJsonArray = nestedProductCodesJsonArray.getJSONArray(i);
                            JSONArray productNamesJsonArray = nestedProductNamesJsonArray.getJSONArray(i);
                            int[] amounts = new int[amountsJsonArray.length()];
                            Product[] products = new Product[isSelfBuyJsonArray.length()];
                            for (int o = 0; o < amountsJsonArray.length(); o++){
                                amounts[o] = amountsJsonArray.getInt(o);
                                products[o] = new Product();
                                products[o].setCode(productCodesJsonArray.getString(o));
                                products[o].setName(productNamesJsonArray.getString(o));
                                products[o].setPrice(pricesJsonArray.getDouble(o));
                                products[o].setSelfBuy(isSelfBuyJsonArray.getBoolean(o));
                            }
                            purchaseOrder.setAmounts(amounts);
                            purchaseOrder.setBuyerAccountnumber(buyerAccountnumbersJsonArray.getString(i));
                            purchaseOrder.setCompleted(completedJsonArray.getBoolean(i));
                            purchaseOrder.setDateTime(dateTimesJsonArray.getString(i));
                            purchaseOrder.setNumber(numbersJsonArray.getInt(i));
                            purchaseOrder.setProducts(products);
                            purchaseOrder.setMadeByUser(madeByUserJsonArray.getBoolean(i));
                            purchaseOrders.add(purchaseOrder);
                        }

                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(orderListActivity.getResources().getString(R.string.sp_selling_products), sellingProducts.toString());
                        editor.apply();

                        orderListActivity.refreshAdapter(purchaseOrders);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    //Account existiert nicht
                    break;
                case 2:
                    //Webstring nicht aktuell
                    Intent i = new Intent(orderListActivity, LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtra("webstring_start", true);
                    orderListActivity.startActivity(i);
                    break;
                case 4:
                    //Unternehmen existiert nicht
                    break;
            }
    }
}