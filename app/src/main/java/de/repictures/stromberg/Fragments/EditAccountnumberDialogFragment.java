package de.repictures.stromberg.Fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.repictures.stromberg.AsyncTasks.AddPurchaseOrderAsyncTask;
import de.repictures.stromberg.OrderListActivity;
import de.repictures.stromberg.POJOs.Product;
import de.repictures.stromberg.POJOs.PurchaseOrder;
import de.repictures.stromberg.R;

public class EditAccountnumberDialogFragment extends DialogFragment implements View.OnClickListener {

    private TextInputLayout accountnumberLayout;
    private EditText accountnumberEdit;
    private String buyerAccountnumber = "";
    private ProgressBar progressBar;
    private Button finishButton;
    private String TAG = "TAG";
    private OrderListActivity orderListActivity;

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
                AddPurchaseOrderAsyncTask asyncTask = new AddPurchaseOrderAsyncTask(EditAccountnumberDialogFragment.this);
                asyncTask.execute(buyerAccountnumber);
                break;
        }
    }

    public void processAddPurchaseOrderResponse(int responseCode, int number) {
        progressBar.setVisibility(View.GONE);
        finishButton.setText(getActivity().getResources().getString(R.string.finish));
        Log.d(TAG, "processAddPurchaseOrderResponse: " + responseCode);
        switch (responseCode){
            case -1:
                //Session abgelaufen
                break;
            case 0:
                //Keine Internet Verbindung
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
                //Angegebener Kunde hat nicht genug Geld für diesen Kaufauftrag
                break;
            case 3:
                //Accountnumber existiert nicht
                accountnumberLayout.setErrorEnabled(true);
                accountnumberLayout.setError(getActivity().getResources().getString(R.string.accountnumber_error));
                break;
        }
    }
}
