package de.repictures.stromberg.Features;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.repictures.stromberg.AsyncTasks.PostProductAsyncTask;
import de.repictures.stromberg.LoginActivity;
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
    @BindView(R.id.features_add_product_check_box_relative_layout) RelativeLayout checkBoxRelativeLayout;
    @BindView(R.id.features_add_price_self_buy_check_box) CheckBox selfBuyCheckBox;

    Snackbar sendingSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        ButterKnife.bind(this);

        sendingSnackbar = Snackbar.make(coordinatorLayout, getResources().getString(R.string.send_product_loading), Snackbar.LENGTH_INDEFINITE);

        checkBoxRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selfBuyCheckBox.setChecked(!selfBuyCheckBox.isChecked());
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputIsValid()){
                    sendingSnackbar.show();
                    PostProductAsyncTask asyncTask = new PostProductAsyncTask(AddProductActivity.this);
                    asyncTask.execute(barcodeEditText.getText().toString(), nameEditText.getText().toString(), priceEditText.getText().toString(),
                            LoginActivity.COMPANY_NUMBER, String.valueOf(selfBuyCheckBox.isChecked()));
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

}