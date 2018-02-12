package de.repictures.stromberg.AsyncTasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.Helper.Cryptor;
import de.repictures.stromberg.R;
import de.repictures.stromberg.uiHelper.QRCode;

public class GetQRAsyncTask extends AsyncTask<String, Void, Bitmap> {

    private Cryptor cryptor = new Cryptor();
    private QRCode qrCode;
    private final String TAG = "GetQRAsyncTask";

    public GetQRAsyncTask(QRCode qrCode){

        this.qrCode = qrCode;
    }

    @Override
    protected Bitmap doInBackground(String... accountnumbers) {
        try {
            String getURLStr = LoginActivity.SERVERURL + "/getqr?accountnumber=" + accountnumbers[0];
            int imgDataLength;
            URL getURL = new URL(getURLStr);
            URLConnection urlConnection = getURL.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            try {
                BufferedReader r = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line);
                }
                String imgDataLengthStr = total.toString();
                imgDataLength = Integer.parseInt(imgDataLengthStr);
            } finally {
                in.close();
            }

            String postURLStr = LoginActivity.SERVERURL + "/getqr?accountnumber=" + accountnumbers[0] + "&reqaccountnumber=" + accountnumbers[1];
            URL postURL = new URL(postURLStr);
            HttpURLConnection httpURLConnection;
            httpURLConnection = (HttpURLConnection) postURL.openConnection();
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");

            DataOutputStream request = new DataOutputStream(httpURLConnection.getOutputStream());

            request.flush();
            request.close();
            InputStream dis = new BufferedInputStream(httpURLConnection.getInputStream());
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[imgDataLength];
            while ((nRead = dis.read(data, 0, data.length)) != -1){
                buffer.write(data, 0, nRead);
            }
            buffer.flush();

            byte[] encryptedImage = buffer.toByteArray();
            Log.d(TAG, "doInBackground: " + new String(encryptedImage));

            SharedPreferences sharedPref = qrCode.activity.getSharedPreferences(qrCode.activity.getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
            String pin = sharedPref.getString(qrCode.activity.getResources().getString(R.string.sp_pin), "");

            byte[] imageData = cryptor.decryptSymmetricToByte(encryptedImage, cryptor.hashToByte(pin));
            return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        qrCode.setView(bitmap);
    }
}
