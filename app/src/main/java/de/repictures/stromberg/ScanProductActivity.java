package de.repictures.stromberg;

import android.Manifest;
import android.animation.Animator;
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
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.repictures.stromberg.AsyncTasks.GetProductAsyncTask;
import de.repictures.stromberg.uiHelper.GetPictures;

public class ScanProductActivity extends AppCompatActivity implements Detector.Processor<Barcode> {

    private static final int PERMISSION_REQUEST_CAMERA = 42;
    @Bind(R.id.camera_view) SurfaceView cameraView;
    @Bind(R.id.activity_scan_layout) CoordinatorLayout scanLayout;
    @Bind(R.id.product_layout) RelativeLayout productLayout;
    @Bind(R.id.auth_scan_progress_bar) ProgressBar productProgressBar;
    @Bind(R.id.product_second_element) RelativeLayout secondElement;
    @Bind(R.id.product_card) CardView productCard;
    @Bind(R.id.auth_scan_progressbar) ImageView productImage;
    @Bind(R.id.auth_scan_title) TextView productTitle;
    @Bind(R.id.auth_scan_vendor) TextView productVendor;

    private static final String TAG = "ScanProductActivity";
    private static final String imageDummyUrl = "https://c1.staticflickr.com/5/4123/4793188726_5d34ab7120_z.jpg";
    BarcodeDetector barcodeDetector;
    CameraSource mCameraSource;
    ArrayList<String> scanResults = new ArrayList<>();
    private boolean animated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_product);
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
        productCard.setY(cameraView.getHeight()-45f);
    }

    private void createCameraSource() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        cameraView.getHolder().addCallback(surfaceHolderCallback());
        barcodeDetector = new BarcodeDetector.Builder(this).build();
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
                    if (ActivityCompat.checkSelfPermission(ScanProductActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(ScanProductActivity.this, Manifest.permission.CAMERA)){

                        } else {
                            ActivityCompat.requestPermissions(ScanProductActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
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
            try{
                mCameraSource.start(cameraView.getHolder());
            } catch (IOException | SecurityException e) {
                e.printStackTrace();
            }
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

    @Override
    public void release() {

    }

    @Override
    public void receiveDetections(Detector.Detections<Barcode> detections) {
        SparseArray<Barcode> allBarcodes = detections.getDetectedItems();
        ArrayList<Barcode> sortedBarcodes = new ArrayList<>();
        for (int i = 0; i < allBarcodes.size(); i++){
            Barcode barcode = allBarcodes.valueAt(i);
            Log.d(TAG, "receiveDetections: " + barcode.displayValue);
            if (barcodeHasCorrectLength(barcode.displayValue.length()) && !scanResults.contains(barcode.displayValue)){
                sortedBarcodes.add(barcode);
                scanResults.add(barcode.displayValue);
            }
        }
        if (sortedBarcodes.size() > 0){
            if (!animated){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, -productCard.getHeight()/2);
                        translateAnimation.setDuration(250);
                        translateAnimation.setFillAfter(true);
                        translateAnimation.setInterpolator(new LinearOutSlowInInterpolator());
                        productCard.setY(cameraView.getHeight());
                        productCard.setVisibility(View.VISIBLE);
                        productCard.startAnimation(translateAnimation);
                        animated = true;
                        Log.d(TAG, "run: animated");
                    }
                });

            }
            for (int i = 0; i < sortedBarcodes.size(); i++){
                Log.d(TAG, "receiveDetections: " + sortedBarcodes.get(i).displayValue);
                GetProductAsyncTask asyncTask = new GetProductAsyncTask(ScanProductActivity.this);
                asyncTask.execute(sortedBarcodes.get(i).displayValue);
            }
        }
    }

    public void receiveResult(String[][] products){
        productTitle.setText(products[0][0]);
        productVendor.setText(products[0][1]);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                productLayout.setVisibility(View.VISIBLE);
                final Animation animation = new AlphaAnimation(0f, 1f);
                animation.setDuration(250);
                productProgressBar.animate().alpha(0f).setDuration(250).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        productLayout.setVisibility(View.VISIBLE);
                        productLayout.startAnimation(animation);
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                }).start();
            }
        });
        new Thread(new GetPictures(imageDummyUrl, productImage, ScanProductActivity.this, true, true, false)).start();
    }

    private boolean barcodeHasCorrectLength(int length){
        if (length > 7 && length < 17) return true;
        else return false;
    }
}
