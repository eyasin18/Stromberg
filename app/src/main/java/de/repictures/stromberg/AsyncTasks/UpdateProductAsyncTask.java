package de.repictures.stromberg.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.URLEncoder;

import de.repictures.stromberg.Features.AddProductActivity;
import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;

public class UpdateProductAsyncTask extends AsyncTask<String, Void, String> {

    private final String TAG = "skjdfh<dfhk";
    private AddProductActivity addProductActivity;
    private Internet internetHelper = new Internet();

    public UpdateProductAsyncTask(AddProductActivity addProductActivity){
        this.addProductActivity = addProductActivity;
    }

    @Override
    protected void onPreExecute() {
        if (!internetHelper.isNetworkAvailable(addProductActivity)){
            cancel(true);
            onPostExecute("-1");
        }
    }

    @Override
    protected String doInBackground(String... productInfos) {
        try {
            for (int i = 0; i < productInfos.length; i++) {
                productInfos[i] = URLEncoder.encode(productInfos[i], "UTF-8");
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        String baseUrl = LoginActivity.SERVERURL + "/updateproduct?code=" + productInfos[0]
                + "&name=" + productInfos[1]
                + "&price=" + productInfos[2]
                + "&companynumber=" + productInfos[3]
                + "&selfbuy=" + productInfos[4]
                + "&buyable=" + productInfos[5];
        Log.d(TAG, "doInBackground: " + baseUrl);
        return internetHelper.doPostString(baseUrl);
    }

    @Override
    protected void onPostExecute(String response) {
        if (addProductActivity != null) addProductActivity.processResult(Integer.parseInt(response));
    }
}