package de.repictures.stromberg.uiHelper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.math.BigInteger;
import java.security.SecureRandom;

import de.repictures.stromberg.AsyncTasks.GetQRAsyncTask;
import de.repictures.stromberg.AsyncTasks.UploadQRAsyncTask;
import de.repictures.stromberg.R;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class QRCode {

    private Activity activity;
    private static final int SIZE = 300;

    private String authCode;
    private ImageView qrView;

    public QRCode(Activity activity){
        this.activity = activity;
    }

    public Bitmap generate(String authKey, String accountnumber) {
        if (authKey == null){
            SecureRandom sr = new SecureRandom();
            authCode = accountnumber;
            authCode += new BigInteger(activity.getResources().getInteger(R.integer.auth_key_length)*5, sr).toString(32);
        } else {
            authCode = (accountnumber + authKey);
        }
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(authCode,
                    BarcodeFormat.QR_CODE, SIZE, SIZE, null);
        } catch (IllegalArgumentException | WriterException e) {
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, SIZE, 0, 0, w, h);
        return bitmap;
    }

    public void upload(Bitmap qrCode, String accountnumber){
        UploadQRAsyncTask uploadQRAsyncTask = new UploadQRAsyncTask(activity, accountnumber, authCode);
        uploadQRAsyncTask.execute(qrCode);
    }

    public void getQRCode(String accountnumber, ImageView qrView){
        this.qrView = qrView;
        GetQRAsyncTask getQRAsyncTask = new GetQRAsyncTask(QRCode.this);
        getQRAsyncTask.execute(accountnumber);
    }

    public void setView(Bitmap qrCode){
        qrView.setImageBitmap(qrCode);
    }
}
