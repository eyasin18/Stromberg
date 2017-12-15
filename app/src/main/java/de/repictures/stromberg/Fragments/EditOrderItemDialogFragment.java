package de.repictures.stromberg.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import de.repictures.stromberg.R;

public class EditOrderItemDialogFragment extends DialogFragment {

    private int position;
    private OrderDetailFragment orderDetailFragment;

    public void setOrderDetailFragment(OrderDetailFragment orderDetailFragment){

        this.orderDetailFragment = orderDetailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("position");

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View parent = layoutInflater.inflate(R.layout.fragment_edit_order_item_dialog, null);

        TextInputLayout barcodeLayout = (TextInputLayout) parent.findViewById(R.id.edit_order_item_product_code_edit_layout);
        TextInputLayout amountLayout = (TextInputLayout) parent.findViewById(R.id.edit_order_item_amount_edit_layout);
        TextInputLayout priceLayout = (TextInputLayout) parent.findViewById(R.id.edit_order_item_price_edit_layout);
        EditText barcodeEdit = (EditText) parent.findViewById(R.id.edit_order_item_product_code_edit);
        EditText amountEdit = (EditText) parent.findViewById(R.id.edit_order_item_amount_edit);
        EditText priceEdit = (EditText) parent.findViewById(R.id.edit_order_item_price_edit);

        barcodeEdit.setText(orderDetailFragment.productCodesList.get(position));
        amountEdit.setText(String.valueOf(orderDetailFragment.amountsList.get(position)));
        priceEdit.setText(String.valueOf(orderDetailFragment.pricesList.get(position)));

        //Build the Dialog
        AlertDialog builder = new AlertDialog.Builder(getActivity())
                .setTitle(getActivity().getResources().getString(R.string.update_item))
                .setPositiveButton(getActivity().getResources().getString(R.string.change), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newBarcode = barcodeEdit.getText().toString();
                        int newAmount = Integer.parseInt(amountEdit.getText().toString());
                        double newPrice = Double.parseDouble(priceEdit.getText().toString());

                        orderDetailFragment.productCodesList.set(position, newBarcode);
                        orderDetailFragment.productNamesList.set(position, newBarcode);
                        orderDetailFragment.amountsList.set(position, newAmount);
                        orderDetailFragment.pricesList.set(position, newPrice);
                        orderDetailFragment.valuesChanged = true;
                        orderDetailFragment.updateAdapter();
                    }
                })
                .setNegativeButton(getActivity().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .create();


        builder.setView(parent);
        return builder;
    }
}
