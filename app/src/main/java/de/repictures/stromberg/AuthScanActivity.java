package de.repictures.stromberg;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.repictures.stromberg.AsyncTasks.TryAuthAsyncTask;

public class AuthScanActivity extends AppCompatActivity implements Detector.Processor<Barcode> {

    private static final int PERMISSION_REQUEST_CAMERA = 42;
    private static final String TAG = "AuthScanActivity";

    @Bind(R.id.auth_scan_camera_view) SurfaceView cameraView;
    @Bind(R.id.activity_auth_scan_layout) CoordinatorLayout scanLayout;
    @Bind(R.id.auth_scan_card) CardView authScanCard;
    @Bind(R.id.auth_scan_progressbar) ProgressBar authScanProgressBar;
    @Bind(R.id.auth_scan_title) TextView authScanTitle;

    private BarcodeDetector barcodeDetector;
    private CameraSource mCameraSource;
    private boolean animated = false;
    private ArrayList<String> sortedBarcodeValues = new ArrayList<>();
    private int sortedBarcodesSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_scan);
        ButterKnife.bind(this);

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onCreate: " + cameraView.getHeight());
        authScanCard.setY(cameraView.getHeight()-45f);
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
                .setAutoFocusEnabled(true)
                .build();
    }

    private SurfaceHolder.Callback surfaceHolderCallback() {
        return new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(AuthScanActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(AuthScanActivity.this, Manifest.permission.CAMERA)){

                        } else {
                            ActivityCompat.requestPermissions(AuthScanActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                        }
                    }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != PERMISSION_REQUEST_CAMERA) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // We have permission, so create the camerasource
            //TODO: Kamera aktiviert sich nicht, nachdem der Zugriff zugelassen wurde
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.request_camera_title))
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    @Override
    public void release() {

    }

    @Override
    public void receiveDetections(Detector.Detections<Barcode> detections) {
        SparseArray<Barcode> allBarcodes = detections.getDetectedItems();
        for (int i = 0; i < allBarcodes.size(); i++){
            Barcode barcode = allBarcodes.valueAt(i);
            if (barcode.displayValue.length() == getResources().getInteger(R.integer.accountnumberlength)+ getResources().getInteger(R.integer.auth_key_length) && !sortedBarcodeValues.contains(barcode.displayValue)){
                sortedBarcodeValues.add(barcode.displayValue);
                Log.d(TAG, "receiveDetections: Barcode " + barcode.displayValue + " added");
            }
        }
        if (sortedBarcodeValues.size() > sortedBarcodesSize){
            if (!animated){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, -authScanCard.getHeight());
                        translateAnimation.setDuration(250);
                        translateAnimation.setFillAfter(true);
                        translateAnimation.setInterpolator(new LinearOutSlowInInterpolator());
                        authScanCard.setY(cameraView.getHeight());
                        authScanCard.setVisibility(View.VISIBLE);
                        authScanCard.startAnimation(translateAnimation);
                        animated = true;
                        Log.d(TAG, "run: animated");
                    }
                });
            }
            authScanProgressBar.setVisibility(View.VISIBLE);
            authScanTitle.setText(getResources().getString(R.string.authenticate_progress));
            for (int i = 0; i < sortedBarcodeValues.size(); i++){
                Log.d(TAG, "receiveDetections: " + sortedBarcodeValues.get(i));
                TryAuthAsyncTask asyncTask = new TryAuthAsyncTask(AuthScanActivity.this);
                asyncTask.execute(sortedBarcodeValues.get(i));
            }
            Log.d(TAG, "receiveDetections: " + sortedBarcodesSize);
            sortedBarcodesSize = sortedBarcodeValues.size();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CAMERA);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        PERMISSION_REQUEST_CAMERA);
            }
        };

        Snackbar.make(scanLayout, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    public void getAuthResult(Boolean result, String authKey){
        if (result){
            Intent intent = new Intent();
            intent.putExtra("authcode", authKey);
            Log.d(TAG, "receiveDetections: " + authKey);
            AuthScanActivity.this.setResult(RESULT_OK, intent);
            finish();
        } else {
            authScanProgressBar.setVisibility(View.INVISIBLE);
            authScanTitle.setText(getResources().getString(R.string.no_such_authkey));
        }
    }
}
