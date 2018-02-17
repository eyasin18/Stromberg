package de.repictures.stromberg.Features;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.repictures.stromberg.AsyncTasks.PostProductAsyncTask;
import de.repictures.stromberg.AsyncTasks.UpdateProductAsyncTask;
import de.repictures.stromberg.POJOs.Product;
import de.repictures.stromberg.R;

public class AddProductActivity extends AppCompatActivity {

    private static final String TAG = "AddProductActivity";
    @BindView(R.id.features_add_product_code_edit_text) TextInputEditText barcodeEditText;
    @BindView(R.id.features_add_product_code_input_layout) TextInputLayout barcodeInputLayout;
    @BindView(R.id.features_add_product_name_edit_text) TextInputEditText nameEditText;
    @BindView(R.id.features_add_product_name_input_layout) TextInputLayout nameInputLayout;
    @BindView(R.id.features_add_price_name_edit_text) TextInputEditText priceEditText;
    @BindView(R.id.features_add_product_price_input_layout) TextInputLayout priceInputLayout;
    @BindView(R.id.features_add_product_floating_action_button) FloatingActionButton sendButton;
    @BindView(R.id.features_add_product_coordinator_layout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.features_add_product_sefbuy_check_box_relative_layout) RelativeLayout checkBoxRelativeLayout;
    @BindView(R.id.features_add_price_self_buy_check_box) CheckBox selfBuyCheckBox;
    @BindView(R.id.features_add_product_buyable_check_box_relative_layout) RelativeLayout buyableCheckBoxLayout;
    @BindView(R.id.features_add_buyable_check_box) CheckBox buyableCheckBox;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private Snackbar sendingSnackbar;
    private int companyPosition;
    private Product product = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        ButterKnife.bind(this);

        companyPosition = getIntent().getIntExtra("company_array_position", 0);
        if (getIntent().hasExtra("product")){
            product = (Product) getIntent().getSerializableExtra("product");

            barcodeEditText.setText(product.getCode());
            barcodeEditText.setEnabled(false);
            barcodeInputLayout.setEnabled(false);
            nameEditText.setText(product.getName());
            priceEditText.setText(String.valueOf(product.getPrice()));
            if (product.isSelfBuy()) selfBuyCheckBox.setChecked(true);
            else selfBuyCheckBox.setChecked(false);
            if (product.isBuyable()) buyableCheckBox.setChecked(true);
            else buyableCheckBox.setChecked(false);
        }

        if (product != null){
            toolbar.setTitle(getResources().getString(R.string.edit_product));
        }
        toolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view ->{
            if (isEdited()) {
                new AlertDialog.Builder(AddProductActivity.this)
                        .setTitle(getResources().getString(R.string.exit_edit_product))
                        .setMessage(getResources().getString(R.string.changed_data_will_not_be_saved))
                        .setPositiveButton(R.string.leave, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            } else {
                finish();
            }
        });

        SharedPreferences sharedPref = getSharedPreferences(getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        List<String> companyNumbers = new ArrayList<>(sharedPref.getStringSet(getResources().getString(R.string.sp_companynumbers), new HashSet<>()));

        sendingSnackbar = Snackbar.make(coordinatorLayout, getResources().getString(R.string.send_product_loading), Snackbar.LENGTH_INDEFINITE);

        checkBoxRelativeLayout.setOnClickListener(view -> selfBuyCheckBox.setChecked(!selfBuyCheckBox.isChecked()));
        buyableCheckBoxLayout.setOnClickListener(view -> buyableCheckBox.setChecked(!buyableCheckBox.isChecked()));

        sendButton.setOnClickListener(view -> {
            if (inputIsValid()){
                sendingSnackbar.show();
                if (product != null){
                    UpdateProductAsyncTask asyncTask = new UpdateProductAsyncTask(AddProductActivity.this);
                    asyncTask.execute(barcodeEditText.getText().toString(), nameEditText.getText().toString(), priceEditText.getText().toString(),
                            companyNumbers.get(companyPosition), String.valueOf(selfBuyCheckBox.isChecked()), String.valueOf(buyableCheckBox.isChecked()));
                } else {
                    PostProductAsyncTask asyncTask = new PostProductAsyncTask(AddProductActivity.this);
                    asyncTask.execute(barcodeEditText.getText().toString(), nameEditText.getText().toString(), priceEditText.getText().toString(),
                            companyNumbers.get(companyPosition), String.valueOf(selfBuyCheckBox.isChecked()), String.valueOf(buyableCheckBox.isChecked()));
                }
            }
        });
    }

    private boolean inputIsValid() {
        boolean noError = true;
        barcodeInputLayout.setError("");
        barcodeInputLayout.setErrorEnabled(false);
        nameInputLayout.setError("");
        nameInputLayout.setErrorEnabled(false);
        priceInputLayout.setError("");
        priceInputLayout.setErrorEnabled(false);
        if(!barcodeHasCorrectLength(barcodeEditText.getText().toString().length())){
            barcodeInputLayout.setErrorEnabled(true);
            barcodeInputLayout.setError(getResources().getString(R.string.error_barcode_wrong_length));
            noError = false;
        }
        if(nameEditText.getText().toString().length() < 1){
            nameInputLayout.setErrorEnabled(true);
            nameInputLayout.setError(getResources().getString(R.string.error_product_name_empty));
            noError = false;
        }
        if(priceEditText.getText().toString().length() < 1){
            priceInputLayout.setErrorEnabled(true);
            priceInputLayout.setError(getResources().getString(R.string.error_price_empty));
            noError = false;
        }
        return noError;
    }

    public void processResult(int responseCode){
        sendingSnackbar.dismiss();
        switch (responseCode){
            case -1:
                Snackbar.make(coordinatorLayout, getResources().getString(R.string.internet_problems), Snackbar.LENGTH_LONG).show();
                break;
            case 1:
                Snackbar.make(coordinatorLayout, getResources().getString(R.string.send_product_success), Snackbar.LENGTH_LONG).show();
                break;
            case 2:
                Snackbar.make(coordinatorLayout, getResources().getString(R.string.barcode_not_available), Snackbar.LENGTH_LONG).show();
                break;
        }
    }

    private boolean barcodeHasCorrectLength(int length){
        Log.d(TAG, "barcodeHasCorrectLength: " + length);
        return length > 7 && length < 17;
    }

    @Override
    public void onBackPressed() {
        if (isEdited()){
            new AlertDialog.Builder(AddProductActivity.this)
                    .setTitle(getResources().getString(R.string.exit_edit_product))
                    .setMessage(getResources().getString(R.string.changed_data_will_not_be_saved))
                    .setPositiveButton(R.string.leave, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();
        } else finish();
    }

    private boolean isEdited(){
        String codeStr = "";
        String nameStr = "";
        double price = 0;
        boolean isSelfBuy = true;
        boolean isBuyable = true;
        if (product != null){
            codeStr = product.getCode();
            nameStr = product.getName();
            price = product.getPrice();
            isSelfBuy = product.isSelfBuy();
            isBuyable = product.isBuyable();
        }

        boolean barcodeEdited = !barcodeEditText.getText().toString().equals(codeStr);
        boolean nameEdited = !nameEditText.getText().toString().equals(nameStr);
        boolean priceEdited = false;
        if (priceEditText.getText().toString().length() > 0) priceEdited = Double.valueOf(priceEditText.getText().toString()) != price;
        boolean selfBuyEdited = selfBuyCheckBox.isChecked() != isSelfBuy;
        boolean buyableEdited = buyableCheckBox.isChecked() != isBuyable;

       return barcodeEdited || nameEdited ||priceEdited || selfBuyEdited || buyableEdited;
    }
}