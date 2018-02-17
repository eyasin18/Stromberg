package de.repictures.stromberg.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.URLEncoder;

import de.repictures.stromberg.Features.AddProductActivity;
import de.repictures.stromberg.Helper.Internet;
import de.repictures.stromberg.LoginActivity;

public class PostProductAsyncTask extends AsyncTask<String, Void, String>{

    private AddProductActivity addProductActivity;
    private String TAG = "PostProductAsyncTask";
    private Internet internetHelper = new Internet();

    public PostProductAsyncTask(AddProductActivity addProductActivity){
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
        String baseUrl = LoginActivity.SERVERURL + "/getproduct?code=" + productInfos[0]
                + "&name=" + productInfos[1]
                + "&price=" + productInfos[2]
                + "&accountnumber=" + productInfos[3]
                + "&selfbuy=" + productInfos[4]
                + "&buyable=" + productInfos[5];
        Log.d(TAG, "doInBackground: " + baseUrl);
        return internetHelper.doGetString(baseUrl);
    }

    @Override
    protected void onPostExecute(String response) {
        if (addProductActivity != null) addProductActivity.processResult(Integer.parseInt(response));
    }
}
