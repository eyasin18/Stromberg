package de.repictures.stromberg.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.repictures.stromberg.CompanyActivity;
import de.repictures.stromberg.Helper.GeneralUtils;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.POJOs.Account;
import de.repictures.stromberg.POJOs.Product;
import de.repictures.stromberg.R;

public class EditOrderItemDialogFragment extends DialogFragment {

    private int position;
    private int productIndex = 0;
    private boolean newProduct = false;
    private OrderDetailFragment orderDetailFragment;
    private List<String> productNames = new ArrayList<>();
    private Product[] sellingProducts;

    public void setOrderDetailFragment(OrderDetailFragment orderDetailFragment){
        this.orderDetailFragment = orderDetailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("position");
        newProduct = getArguments().getBoolean("newProduct", false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        String sellingProductsArrayStr = sharedPreferences.getString(getActivity().getResources().getString(R.string.sp_selling_products), null);
        try {
            JSONArray sellingProductsArray = new JSONArray(sellingProductsArrayStr);
            sellingProducts = new Product[sellingProductsArray.length()];
            for (int i = 0; i < sellingProductsArray.length(); i++){
                JSONObject productObject = sellingProductsArray.getJSONObject(i);
                sellingProducts[i] = new Product();
                sellingProducts[i].setCode(productObject.getString("code"));
                sellingProducts[i].setName(productObject.getString("name"));
                productNames.add(productObject.getString("name"));
                sellingProducts[i].setPrice(productObject.getDouble("price"));
                sellingProducts[i].setSelfBuy(productObject.getBoolean("is_self_buy"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!newProduct) productIndex = productNames.indexOf(orderDetailFragment.purchaseOrder.getProducts()[position].getName());
        else productIndex = 0;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        SharedPreferences sharedPref = getActivity().getSharedPreferences(getActivity().getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        String featuresJsonStr = sharedPref.getString(getActivity().getResources().getString(R.string.sp_featureslist), "");
        List<String> companyNumbers = new ArrayList<>(sharedPref.getStringSet(getResources().getString(R.string.sp_companynumbers), new HashSet<>()));
        List<Integer> features = Account.getSpecificFeaturesLongListFromString(featuresJsonStr, companyNumbers.get(orderDetailFragment.companyPosition));

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View parent = layoutInflater.inflate(R.layout.fragment_edit_order_item_dialog, null);

        TextInputLayout amountLayout = (TextInputLayout) parent.findViewById(R.id.edit_order_item_amount_edit_layout);
        TextInputLayout priceLayout = (TextInputLayout) parent.findViewById(R.id.edit_order_item_price_edit_layout);
        EditText amountEdit = (EditText) parent.findViewById(R.id.edit_order_item_amount_edit);
        EditText priceEdit = (EditText) parent.findViewById(R.id.edit_order_item_price_edit);
        Spinner productSpinner = (Spinner) parent.findViewById(R.id.edit_order_item_product_spinner);

        if (!features.contains(0)){
            priceEdit.setEnabled(false);
            priceLayout.setEnabled(false);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, productNames);
        productSpinner.setAdapter(adapter);
        productSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                productIndex = position;
                priceEdit.setText(String.valueOf(sellingProducts[position].getPrice()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        productSpinner.setSelection(productIndex, false);
        if (!newProduct) amountEdit.setText(String.valueOf(orderDetailFragment.purchaseOrder.getAmounts()[position]));
        else amountEdit.setText("1");
        if (!newProduct) priceEdit.setText(String.valueOf(orderDetailFragment.purchaseOrder.getProducts()[position].getPrice()));
        else {
            if (sellingProducts.length > 0) priceEdit.setText(String.valueOf(sellingProducts[0].getPrice()));
        }

        //Build the Dialog
        AlertDialog builder = new AlertDialog.Builder(getActivity())
                .setTitle(newProduct ? getActivity().getResources().getString(R.string.title_add_product) : getActivity().getResources().getString(R.string.update_item))
                .setPositiveButton(newProduct ? getActivity().getResources().getString(R.string.title_add_product) : getActivity().getResources().getString(R.string.change), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        priceLayout.setErrorEnabled(false);
                        priceLayout.setError("");
                        int newAmount = Integer.parseInt(amountEdit.getText().toString());
                        double newPrice = Double.parseDouble(priceEdit.getText().toString());

                        if (!features.contains(0)){
                            priceLayout.setErrorEnabled(true);
                            priceLayout.setError(getActivity().getResources().getString(R.string.price_cannot_be_changed));
                        } else {
                            orderDetailFragment.valuesChanged = true;
                            if (newProduct) {
                                Product product = sellingProducts[productIndex];
                                product.setSelfBuy(false);
                                orderDetailFragment.purchaseOrder.setProducts(GeneralUtils.appendProduct(orderDetailFragment.purchaseOrder.getProducts(), product));
                                orderDetailFragment.purchaseOrder.setAmounts(GeneralUtils.appendInt(orderDetailFragment.purchaseOrder.getAmounts(), newAmount));
                                orderDetailFragment.insertAdapterItem();
                            } else {
                                orderDetailFragment.purchaseOrder.getProducts()[position] = sellingProducts[productIndex];
                                orderDetailFragment.purchaseOrder.getProducts()[position].setPrice(newPrice);
                                orderDetailFragment.purchaseOrder.getAmounts()[position] = newAmount;
                                orderDetailFragment.updateAdapter();
                            }
                        }
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