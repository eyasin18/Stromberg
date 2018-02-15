package de.repictures.stromberg.Fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.repictures.stromberg.AsyncTasks.AddPurchaseOrderAsyncTask;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.OrderListActivity;
import de.repictures.stromberg.POJOs.Product;
import de.repictures.stromberg.POJOs.PurchaseOrder;
import de.repictures.stromberg.R;

public class EditAccountnumberDialogFragment extends DialogFragment implements View.OnClickListener {

    private RelativeLayout layout;
    private TextInputLayout accountnumberLayout;
    private EditText accountnumberEdit;
    private String buyerAccountnumber = "";
    private ProgressBar progressBar;
    private Button finishButton;
    private String TAG = "TAG";
    private OrderListActivity orderListActivity;
    private int companyPosition;

    public void setOrderListActivity(OrderListActivity orderListActivity){
        this.orderListActivity = orderListActivity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog builder = new AlertDialog.Builder(getActivity())
                .setTitle(getActivity().getResources().getString(R.string.enter_accountnumber_of_buyer))
                .create();

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View parent = layoutInflater.inflate(R.layout.fragment_edit_accountnumber_dialog, null);

        finishButton = (Button) parent.findViewById(R.id.edit_accountnumber_finish_button);
        TextView cancelButton = (TextView) parent.findViewById(R.id.edit_accountnumber_cancel_button);
        accountnumberLayout = (TextInputLayout) parent.findViewById(R.id.edit_accountnumber_accounnumber_layout);
        accountnumberEdit = (EditText) parent.findViewById(R.id.edit_accountnumber_accounnumber_edit);
        layout = (RelativeLayout) parent.findViewById(R.id.edit_accountnumber_layout);
        progressBar = (ProgressBar) parent.findViewById(R.id.edit_accountnumber_progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccentYellow), android.graphics.PorterDuff.Mode.SRC_ATOP);

        finishButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        builder.setView(parent);
        return builder;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_accountnumber_cancel_button:
                dismiss();
                break;
            case R.id.edit_accountnumber_finish_button:
                finishButton.setText("");
                progressBar.setVisibility(View.VISIBLE);
                buyerAccountnumber = accountnumberEdit.getText().toString();
                accountnumberLayout.setErrorEnabled(false);
                accountnumberLayout.setError("");
                AddPurchaseOrderAsyncTask asyncTask = new AddPurchaseOrderAsyncTask(EditAccountnumberDialogFragment.this, companyPosition);
                asyncTask.execute(buyerAccountnumber);
                break;
        }
    }

    public void processAddPurchaseOrderResponse(int responseCode, int number) {
        progressBar.setVisibility(View.GONE);
        if (finishButton != null) finishButton.setText(getActivity().getResources().getString(R.string.finish));
        Log.d(TAG, "processAddPurchaseOrderResponse: " + responseCode);
        switch (responseCode){
            case -1:
                Intent i = new Intent(getActivity(), LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getActivity().startActivity(i);
                break;
            case 0:
                Snackbar.make(layout, getResources().getString(R.string.internet_problems), Snackbar.LENGTH_LONG).show();
                break;
            case 1:
                //Alles gut
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSSS z", Locale.getDefault());

                PurchaseOrder purchaseOrder = new PurchaseOrder();
                purchaseOrder.setMadeByUser(false);
                purchaseOrder.setAmounts(new int[0]);
                purchaseOrder.setProducts(new Product[0]);
                purchaseOrder.setCompleted(false);
                purchaseOrder.setBuyerAccountnumber(buyerAccountnumber);
                purchaseOrder.setDateTime(sdf.format(calendar.getTime()));
                purchaseOrder.setNumber(number);

                orderListActivity.insertPurchaseOrder(purchaseOrder);
                dismiss();
                break;
            case 2:
                Snackbar.make(layout, getResources().getString(R.string.customer_has_not_enough_money), Snackbar.LENGTH_LONG).show();
                break;
            case 3:
                //Accountnumber existiert nicht
                accountnumberLayout.setErrorEnabled(true);
                accountnumberLayout.setError(getActivity().getResources().getString(R.string.accountnumber_error));
                break;
        }
    }

    public void setCompanyPosition(int companyPosition) {
        this.companyPosition = companyPosition;
    }
}
