package de.repictures.stromberg.AsyncTasks;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.util.List;

import javax.mail.internet.MimeMultipart;

import de.repictures.stromberg.Helper.GeneralUtils;
import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.OrderListActivity;

public class GetPurchaseOrdersAsyncTask extends AsyncTask<String, Void, MimeMultipart>{

    private Internet internetHelper = new Internet();
    private OrderListActivity orderListActivity;

    public GetPurchaseOrdersAsyncTask(OrderListActivity orderListActivity) {
        this.orderListActivity = orderListActivity;
    }

    @Override
    protected MimeMultipart doInBackground(String... strings) {
        String urlStr = LoginActivity.SERVERURL + "/postpurchaseorders?companynumber=" + LoginActivity.COMPANY_NUMBER
                                + "&accountnumber=" + LoginActivity.ACCOUNTNUMBER
                                + "&webstring=" + LoginActivity.WEBSTRING;
        return internetHelper.doPostMultipart(urlStr, "multipart/x-mixed-replace;boundary=End");
    }

    @Override
    protected void onPostExecute(MimeMultipart resp) {
        int responseCode = Integer.parseInt(internetHelper.parseTextBodyPart(resp, 0));

        switch (responseCode){
            case 1:
                JSONObject jsonObject = internetHelper.parseJsonBodyPart(resp, 1);

                List<int[]> amountsList = GeneralUtils.parseNestedJsonIntArray(jsonObject, "amounts");
                List<String> buyerAccountnumbers = GeneralUtils.parseJsonStringArray(jsonObject, "buyerAccountnumbers");
                List<String> dateTimes = GeneralUtils.parseJsonStringArray(jsonObject, "dateTimes");
                List<boolean[]> isSelfBuys = GeneralUtils.parseNestedJsonBooleanArray(jsonObject, "isSelfBuys");
                List<Integer> numbers = GeneralUtils.parseJsonIntArray(jsonObject, "numbers");
                List<double[]> prices = GeneralUtils.parseNestedJsonDoubleArray(jsonObject, "prices");
                List<String[]> productCodes = GeneralUtils.parseNestedJsonStringArray(jsonObject, "productCodes");
                orderListActivity.refreshAdapter(amountsList, buyerAccountnumbers, dateTimes, isSelfBuys, numbers, prices, productCodes);
                break;
        }
    }
}
