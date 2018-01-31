package de.repictures.stromberg.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.repictures.stromberg.Features.AddAuthKeyActivity;
import de.repictures.stromberg.Features.AddProductActivity;
import de.repictures.stromberg.CompanyActivity;
import de.repictures.stromberg.Features.EmployeesActivity;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.OrderListActivity;
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
        int featureCode = LoginActivity.FEATURES.get(position);
        switch (featureCode){
            case 0:
                i = new Intent(companyActivity, AddProductActivity.class);
                companyActivity.startActivity(i);
                break;
            case 1:
                i = new Intent(companyActivity, AddAuthKeyActivity.class);
                companyActivity.startActivity(i);
                break;
            case 2:
                i = new Intent(companyActivity, OrderListActivity.class);
                companyActivity.startActivity(i);
                break;
            case 3:
                i = new Intent(companyActivity, EmployeesActivity.class);
                companyActivity.startActivity(i);
                break;
            case 4:
                //Statistiken
                break;
            default:
                i = null;
                break;
        }
    }
}
