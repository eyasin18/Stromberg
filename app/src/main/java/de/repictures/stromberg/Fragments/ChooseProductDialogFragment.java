package de.repictures.stromberg.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import de.repictures.stromberg.Adapters.ChooseProductAdapter;
import de.repictures.stromberg.Adapters.ShoppingCartListAdapter;
import de.repictures.stromberg.AuthScanActivity;
import de.repictures.stromberg.POJOs.Product;
import de.repictures.stromberg.R;
import de.repictures.stromberg.ScanProductActivity;

public class ChooseProductDialogFragment extends DialogFragment {

    private ScanProductActivity scanProductActivity;
    private List<Product> products;

    public void setScanProductActivity(ScanProductActivity scanProductActivity){
        this.scanProductActivity = scanProductActivity;
    }

    public void setProducts(List<Product> products){
        this.products = products;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Put ProgressBar in the DialogLayout
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View parent = layoutInflater.inflate(R.layout.fragment_choose_product_dialog, null);

        RecyclerView recyclerView = parent.findViewById(R.id.fragment_choose_product_dialog_recylcler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ChooseProductAdapter adapter = new ChooseProductAdapter(scanProductActivity, products);
        recyclerView.setAdapter(adapter);

        //Build the Dialog
        AlertDialog builder = new AlertDialog.Builder(getActivity())
                .setTitle(getActivity().getResources().getString(R.string.multiple_companies_sell_this_product_choose_one))
                .setNegativeButton(getActivity().getResources().getString(R.string.cancel), (dialogInterface, i) -> {
                    scanProductActivity.deleteResult(products.get(0));
                    dialogInterface.cancel();
                })
                .create();

        builder.setView(parent);
        return builder;
    }
}
