package de.repictures.stromberg;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.mancj.slideup.SlideUp;
import com.mancj.slideup.SlideUpBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.repictures.stromberg.Adapters.ShoppingCartListAdapter;
import de.repictures.stromberg.AsyncTasks.BuyItemsAsyncTask;
import de.repictures.stromberg.AsyncTasks.GetProductAsyncTask;

public class ScanProductActivity extends AppCompatActivity implements Detector.Processor<Barcode>, View.OnClickListener {

    private static final int PERMISSION_REQUEST_CAMERA = 42;
    private static final String TAG = "ScanProductActivity";

    private SlideUp slideUp;

    @BindView(R.id.camera_view) SurfaceView cameraView;
    @BindView(R.id.activity_scan_layout) CoordinatorLayout scanLayout;
    @BindView(R.id.shopping_list_slide_down_view) View slideView;
    @BindView(R.id.activity_scan_fab) FloatingActionButton floatingActionButton;
    @BindView(R.id.shopping_list_recycler_view) RecyclerView shoppingRecycler;
    @BindView(R.id.shopping_list_slide_down_arrow) ImageView slideDownArrow;
    @BindView(R.id.scan_product_progress_bar) ProgressBar scanProgressBar;
    @BindView(R.id.shopping_list_checkout_button) Button checkoutButton;
    @BindView(R.id.shopping_list_checkout_progress_bar) ProgressBar checkoutProgressBar;

    BarcodeDetector barcodeDetector;
    CameraSource mCameraSource;
    public ArrayList<String> scanResults = new ArrayList<>();
    public ArrayList<String[]> productsList = new ArrayList<>();
    public List<Integer> productAmounts = new ArrayList<>();
    RecyclerView.Adapter shoppingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_product);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
        floatingActionButton.setOnClickListener(this);
        slideView.setOnClickListener(this);
        slideDownArrow.setOnClickListener(this);
        checkoutButton.setOnClickListener(this);

        slideUp = new SlideUpBuilder(slideView)
                .withStartState(SlideUp.State.HIDDEN)
                .withStartGravity(Gravity.BOTTOM)
                .withGesturesEnabled(false)
                .withListeners(new SlideUp.Listener.Events(){

                    @Override
                    public void onVisibilityChanged(int visibility) {
                        if (visibility == View.GONE){
                            floatingActionButton.show();
                        }
                    }

                    @Override
                    public void onSlide(float percent) {

                    }
                })
                .build();

        shoppingRecycler.setHasFixedSize(true);
        shoppingRecycler.setLayoutManager(new LinearLayoutManager(this));
        shoppingAdapter = new ShoppingCartListAdapter(ScanProductActivity.this, productsList);
        shoppingRecycler.setAdapter(shoppingAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Log.d(TAG, "onMove: moded");
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                int productIndex = scanResults.indexOf(productsList.get(position)[6]);
                if (productIndex > -1) scanResults.remove(productIndex);
                productsList.remove(position);
                shoppingAdapter.notifyItemRemoved(position);
                shoppingAdapter.notifyItemRangeChanged(position, productsList.size());
                if (productsList.size() < 1){
                    enableCheckoutButton(false);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(shoppingRecycler);

        scanProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccentYellow), android.graphics.PorterDuff.Mode.SRC_ATOP);
        scanProgressBar.bringToFront();
        checkoutProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccentYellow), android.graphics.PorterDuff.Mode.SRC_ATOP);
        checkoutProgressBar.bringToFront();
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
            for (int i = 0; i < sortedBarcodes.size(); i++){
                Log.d(TAG, "receiveDetections: " + sortedBarcodes.get(i).displayValue);
                showScanProgressBar(true);
                GetProductAsyncTask asyncTask = new GetProductAsyncTask(ScanProductActivity.this);
                asyncTask.execute(sortedBarcodes.get(i).displayValue);
            }
        }
    }

    public void receiveResult(String[][] products){
        for (final String[] product : products){
            if (productsList.size() > 0) Log.d(TAG, "receiveResult: \n" + product[2] + "\n" + productsList.get(0)[2]);
            if (productsList.size() > 0 && !product[2].equals(productsList.get(0)[2])){
                AlertDialog.Builder builder = new AlertDialog.Builder(ScanProductActivity.this);
                builder.setTitle(getResources().getString(R.string.product_invalid))
                        .setMessage(getResources().getString(R.string.only_one_seller_message))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                int productIndex = scanResults.indexOf(product[6]);
                                if (productIndex > -1) scanResults.remove(productIndex);
                                if (productsList.size() < 1){
                                    enableCheckoutButton(false);
                                }
                            }
                        })
                        .show();
            } else {
                productsList.add(product);
            }
        }
        shoppingAdapter.notifyDataSetChanged();
        showScanProgressBar(false);
        if (shoppingAdapter.getItemCount() > 0){
            checkoutButton.setEnabled(true);
        }
    }

    public void receiveResult(){
        showScanProgressBar(false);
    }

    private boolean barcodeHasCorrectLength(int length){
        return length > 7 && length < 17;
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: " + view.getId() + " was clicked");
        switch (view.getId()){
            case R.id.activity_scan_fab:
                Log.d(TAG, "onClick: FAB was clicked");
                floatingActionButton.hide();
                slideUp.show();
                break;
            case R.id.shopping_list_slide_down_view:
                Log.d(TAG, "onClick: SlideView was clicked");
                break;
            case R.id.shopping_list_slide_down_arrow:
                slideUp.hide();
                break;
            case R.id.shopping_list_checkout_button:
                float priceSum = 0.0f;
                for (int i = 0; i < productsList.size(); i++){
                    float productPrice = Float.parseFloat(productsList.get(i)[3]);
                    int count = productAmounts.get(i);
                    priceSum += (float) (count*productPrice);
                }
                Log.d(TAG, "onClick: " + priceSum);
                AlertDialog.Builder builder = new AlertDialog.Builder(ScanProductActivity.this);
                builder.setTitle(getResources().getString(R.string.finish_purchase))
                        .setMessage(String.format(Locale.getDefault(), getResources().getString(R.string.buy_products_in_cart), priceSum))
                        .setPositiveButton(R.string.finish, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                sendShoppingList();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
                break;
            default:
                Log.d(TAG, "onClick: Clicked anywhere else");
                break;
        }
    }

    private void sendShoppingList() {
        checkoutButton.setText("");
        checkoutProgressBar.setVisibility(View.VISIBLE);
        SharedPreferences sharedPref = getSharedPreferences(getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        String webString = sharedPref.getString(getResources().getString(R.string.sp_webstring), "");
        String companyNumber = productsList.get(0)[2];

        StringBuilder shoppingListBuilder = new StringBuilder();
        for (int i = 0; i < productsList.size(); i++){
            String[] product = productsList.get(i);

            shoppingListBuilder.append(product[6]);
            shoppingListBuilder.append("ò");
            shoppingListBuilder.append(product[3]);
            shoppingListBuilder.append("ò");
            shoppingListBuilder.append(product[5]);
            shoppingListBuilder.append("ò");
            shoppingListBuilder.append(productAmounts.get(i));
            shoppingListBuilder.append("ň");
        }

        BuyItemsAsyncTask asyncTask = new BuyItemsAsyncTask(ScanProductActivity.this);
        asyncTask.execute(webString, LoginActivity.ACCOUNTNUMBER, companyNumber, shoppingListBuilder.toString());
    }

    public void buyItemResult(int responsecode){
        checkoutProgressBar.setVisibility(View.INVISIBLE);
        checkoutButton.setText(getResources().getString(R.string.checkout));
        AlertDialog.Builder builder;
        switch (responsecode){
            case -1:
                builder = new AlertDialog.Builder(ScanProductActivity.this);
                builder.setTitle(getResources().getString(R.string.session_invalid))
                        .setMessage(getResources().getString(R.string.loged_in_on_other_device))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                break;
            case 0:
                break;
            case 1:
                for (int i = 0; i < productsList.size(); i++){
                    shoppingAdapter.notifyItemRemoved(i);
                    shoppingAdapter.notifyItemRangeChanged(i, productsList.size() - (i+1));
                }
                scanResults.clear();
                productsList.clear();
                if (productsList.size() < 1){
                    enableCheckoutButton(false);
                }
                break;
            case 2:
                builder = new AlertDialog.Builder(ScanProductActivity.this);
                builder.setTitle(getResources().getString(R.string.not_enough_money))
                        .setMessage(getResources().getString(R.string.not_enough_money_message))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                break;
            default:
                break;
        }
    }

    private void showScanProgressBar(final boolean show){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                floatingActionButton.setImageDrawable(show ? null : getFloatingActionButtonIcon());
                scanProgressBar.setVisibility(show && !slideUp.isVisible() ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (slideUp.isVisible()){
            slideUp.hide();
        } else {
            super.onBackPressed();
        }
    }

    public void enableCheckoutButton(boolean enabled){
        checkoutButton.setEnabled(enabled);
    }

    private Drawable getFloatingActionButtonIcon(){
        int iconId;
        switch (shoppingAdapter.getItemCount()){
            case 0:
                iconId = R.drawable.ic_cart_0;
                break;
            case 1:
                iconId = R.drawable.ic_cart_1;
                break;
            case 2:
                iconId = R.drawable.ic_cart_2;
                break;
            case 3:
                iconId = R.drawable.ic_cart_3;
                break;
            case 4:
                iconId = R.drawable.ic_cart_4;
                break;
            case 5:
                iconId = R.drawable.ic_cart_5;
                break;
            case 6:
                iconId = R.drawable.ic_cart_6;
                break;
            case 7:
                iconId = R.drawable.ic_cart_7;
                break;
            case 8:
                iconId = R.drawable.ic_cart_8;
                break;
            case 9:
                iconId = R.drawable.ic_cart_9;
                break;
            default:
                iconId = R.drawable.ic_cart_9_plus;
                break;
        }
        return getResources().getDrawable(iconId);
    }
}