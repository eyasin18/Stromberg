package de.repictures.stromberg.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.repictures.stromberg.AuthScanActivity;
import de.repictures.stromberg.Features.AddAuthKeyActivity;
import de.repictures.stromberg.Features.AddEmployeeActivity;
import de.repictures.stromberg.Features.AddPrepaidAccountActivity;
import de.repictures.stromberg.Features.AddProductActivity;
import de.repictures.stromberg.CompanyActivity;
import de.repictures.stromberg.Features.ChangeMoneyActivity;
import de.repictures.stromberg.Features.CustomsActivity;
import de.repictures.stromberg.Features.EmployeesActivity;
import de.repictures.stromberg.Features.ProductsActivity;
import de.repictures.stromberg.Features.ScanPassportActivity;
import de.repictures.stromberg.Features.StatsActivity;
import de.repictures.stromberg.Features.TransferWageActivity;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.OrderListActivity;
import de.repictures.stromberg.POJOs.Account;
import de.repictures.stromberg.R;

public class FeaturesListAdapter extends RecyclerView.Adapter<FeaturesListViewHolder> implements FeaturesListViewHolder.ClickListener {

    private static final String TAG = "TransferListAdapter";
    private CompanyActivity companyActivity;

    public FeaturesListAdapter(CompanyActivity companyActivity) {
        this.companyActivity = companyActivity;
    }

    @Override
    public FeaturesListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.featureslist, parent, false);
        return new FeaturesListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FeaturesListViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: executed");
        holder.setClickListener(this);
        holder.feature.setText(companyActivity.featuresNames.get(position));
    }

    @Override
    public int getItemCount() {
        return companyActivity.featuresNames.size();
    }

    @Override
    public void onClick(View v, int position, boolean isLongClick) {
        Intent i;
        int featureCode = companyActivity.features.get(position);
        switch (featureCode){
            case 0:
                i = new Intent(companyActivity, ProductsActivity.class);
                i.putExtra("company_array_position", companyActivity.companyPosition);
                companyActivity.startActivity(i);
                break;
            case 1:
                i = new Intent(companyActivity, AddAuthKeyActivity.class);
                companyActivity.startActivity(i);
                break;
            case 2:
                i = new Intent(companyActivity, OrderListActivity.class);
                i.putExtra("company_array_position", companyActivity.companyPosition);
                companyActivity.startActivity(i);
                break;
            case 3:
                i = new Intent(companyActivity, EmployeesActivity.class);
                i.putExtra("company_array_position", companyActivity.companyPosition);
                companyActivity.startActivity(i);
                break;
            case 4:
                i = new Intent(companyActivity, StatsActivity.class);
                i.putExtra("company_array_position", companyActivity.companyPosition);
                companyActivity.startActivity(i);
                break;
            case 5:
                i = new Intent(companyActivity, ChangeMoneyActivity.class);
                i.putExtra("company_array_position", companyActivity.companyPosition);
                companyActivity.startActivity(i);
                break;
            case 6:
                i = new Intent(companyActivity, AddEmployeeActivity.class);
                i.putExtra("company_array_position", companyActivity.companyPosition);
                companyActivity.startActivity(i);
                break;
            case 7:
                i = new Intent(companyActivity, AddPrepaidAccountActivity.class);
                i.putExtra("company_array_position", companyActivity.companyPosition);
                companyActivity.startActivity(i);
                break;
            case 8:
                i = new Intent(companyActivity, ScanPassportActivity.class);
                i.putExtra("company_array_position", companyActivity.companyPosition);
                companyActivity.startActivity(i);
                break;
            case 9:
                i = new Intent(companyActivity, CustomsActivity.class);
                i.putExtra("company_array_position", companyActivity.companyPosition);
                companyActivity.startActivity(i);
                break;
            case 10:
                i = new Intent(companyActivity, TransferWageActivity.class);
                i.putExtra("company_array_position", companyActivity.companyPosition);
                companyActivity.startActivity(i);
                break;
            default:
                i = null;
                break;
        }
    }
}
