package de.repictures.stromberg.Features;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.UiThread;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.repictures.stromberg.AsyncTasks.RegisterPersonAsyncTask;
import de.repictures.stromberg.Fragments.EditAccountnumberPassportDialogFragment;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.R;

public class ScanPassportActivity extends AppCompatActivity implements Detector.Processor<Barcode>, View.OnClickListener {

    private final String TAG = "dsfsf";

    @BindView(R.id.passport_scan_camera_view) SurfaceView cameraView;
    @BindView(R.id.passport_scan_presence_time) TextView presenceTimeText;
    @BindView(R.id.passport_scan_minutes_to_go) TextView minutesToGoText;
    @BindView(R.id.passport_scan_color_view) View colorView;
    @BindView(R.id.passport_coordinator) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.passport_fab) FloatingActionButton fab;

    private BarcodeDetector barcodeDetector;
    private CameraSource mCameraSource;
    private boolean hasReceivedResponse = true;
    private String webstring;
    private String accountnumber;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_passport);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        SharedPreferences sharedPref = getSharedPreferences(getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        webstring = sharedPref.getString(getResources().getString(R.string.sp_webstring), "");
        accountnumber = sharedPref.getString(getResources().getString(R.string.sp_accountnumber), null);

        colorView.setOnClickListener(this);
        fab.setOnClickListener(view -> {
            EditAccountnumberPassportDialogFragment fragment = new EditAccountnumberPassportDialogFragment();
            fragment.setScanPassportActivity(ScanPassportActivity.this);
            fragment.show(getSupportFragmentManager(), "sdfjs");
        });
        createCameraSource();
    }

    private void createCameraSource() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        cameraView.getHolder().addCallback(surfaceHolderCallback());
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        barcodeDetector.setProcessor(this);

        mCameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(height/ 2, width / 2)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setAutoFocusEnabled(true)
                .build();
    }

    private SurfaceHolder.Callback surfaceHolderCallback() {
        return new SurfaceHolder.Callback() {

            @SuppressLint("MissingPermission")
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {
                    mCameraSource.start(cameraView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                mCameraSource.stop();
            }
        };
    }

    @Override
    public void release() {

    }

    @Override
    public void receiveDetections(Detector.Detections<Barcode> detections) {
        SparseArray<Barcode> barcodes = detections.getDetectedItems();
        if (barcodes.size() > 0){
            String detection = barcodes.valueAt(0).displayValue;
            if (stringIsAuthString(detection) && hasReceivedResponse){
                hasReceivedResponse = false;
                String detectedAccountnumber = detection.substring(0, getResources().getInteger(R.integer.accountnumberlength));
                RegisterPersonAsyncTask asyncTask = new RegisterPersonAsyncTask(ScanPassportActivity.this);
                asyncTask.execute(accountnumber, webstring, detectedAccountnumber);
            }
        }
    }

    @UiThread
    public void processResponse(int responseCode){
        Intent i;
        switch (responseCode){
            case 0:
                i = new Intent(ScanPassportActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                break;
            case 2:
                //Unklares Ergebnis
                colorView.setBackgroundColor(getResources().getColor(R.color.red));
                minutesToGoText.setText("");
                presenceTimeText.setText("");
                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                long timeNow = SystemClock.uptimeMillis();
                Handler handler = new Handler();
                handler.postAtTime(new Runnable() {
                    @Override
                    public void run() {
                        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 300);
                    }
                }, 50 + timeNow);
                handler.postAtTime(new Runnable() {
                    @Override
                    public void run() {
                        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 300);
                    }
                }, 450 + timeNow);
                handler.postAtTime(new Runnable() {
                    @Override
                    public void run() {
                        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 300);
                    }
                }, 850 + timeNow);
                break;
            case 3:
                break;
        }
    }

    @UiThread
    public void processResponse(boolean entered, int presenceTime, int minutesToGo){
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        if (entered){
            colorView.setBackgroundColor(getResources().getColor(R.color.green));
            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 500);
        } else {
            DecimalFormat decimalFormat = new DecimalFormat("00");
            String presenceStr = presenceTime/60 + "h " + decimalFormat.format(presenceTime%60) + "min";
            if (minutesToGo > 0){
                String minutesToGoStr = minutesToGo/60 + "h " + decimalFormat.format(minutesToGo%60) + "min";
                colorView.setBackgroundColor(getResources().getColor(R.color.purple));
                String messageText = String.format(getResources().getString(R.string.register_message), minutesToGoStr, presenceStr);
                snackbar = Snackbar.make(coordinatorLayout, messageText, Snackbar.LENGTH_INDEFINITE);
                snackbar.show();

                long timeNow = SystemClock.uptimeMillis();
                Handler handler = new Handler();
                handler.postAtTime(new Runnable() {
                    @Override
                    public void run() {
                        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 400);
                    }
                }, 50 + timeNow);
                handler.postAtTime(new Runnable() {
                    @Override
                    public void run() {
                        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 400);
                    }
                }, 550 + timeNow);
            } else {
                colorView.setBackgroundColor(getResources().getColor(R.color.green));
                presenceTimeText.setText(presenceStr);
                toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 500);
            }
        }
    }

    private boolean stringIsAuthString(String detection){
        return detection.length() == 20;
    }

    @Override
    public void onClick(View view) {
        //Scanner freigeben
        if (!hasReceivedResponse){
            hasReceivedResponse = true;
        }
        if (snackbar != null && snackbar.isShown()){
            snackbar.dismiss();
        }
        colorView.setBackgroundColor(getResources().getColor(R.color.transparent));
        minutesToGoText.setText("");
        presenceTimeText.setText("");
    }

    public void startControl(String accountnumber) {
        if (hasReceivedResponse){
            hasReceivedResponse = false;
            RegisterPersonAsyncTask asyncTask = new RegisterPersonAsyncTask(ScanPassportActivity.this);
            asyncTask.execute(accountnumber, webstring, accountnumber);
        }
    }
}