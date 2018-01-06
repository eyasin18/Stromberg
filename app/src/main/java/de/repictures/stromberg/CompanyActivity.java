package de.repictures.stromberg;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.repictures.stromberg.Adapters.FeaturesListAdapter;
import de.repictures.stromberg.AsyncTasks.GetSellingProductsAsyncTask;
import de.repictures.stromberg.Fragments.LoadingDialogFragment;
import de.repictures.stromberg.POJOs.Product;

public class CompanyActivity extends AppCompatActivity {

    public static Product[] SELLING_PRODUCTS;

    public List<String> featuresNames = new ArrayList<>();

    @BindView(R.id.features_recycler) RecyclerView featuresRecycler;
    RecyclerView.Adapter featuresAdapter;
    private String TAG = "CompanyActivity";
    private LoadingDialogFragment loadingDialogFragment = new LoadingDialogFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("Rodi Inc.");

        String[] allFeaturesNames = getResources().getStringArray(R.array.featuresNames);

        for (int i = 0; i < LoginActivity.FEATURES.size(); i++){
            featuresNames.add(allFeaturesNames[LoginActivity.FEATURES.get(i)]);
        }

        featuresRecycler.setHasFixedSize(true);
        featuresRecycler.setLayoutManager(new LinearLayoutManager(this));
        featuresAdapter = new FeaturesListAdapter(CompanyActivity.this);
        featuresRecycler.setAdapter(featuresAdapter);

        if (LoginActivity.FEATURES.contains(0) || LoginActivity.FEATURES.contains(3)) {
            Bundle args = new Bundle();
            args.putInt(LoadingDialogFragment.ARG_TITLE, R.string.downloading_products);
            loadingDialogFragment.setArguments(args);
            loadingDialogFragment.show(getSupportFragmentManager(), "showLoadingDialogFragment");
            GetSellingProductsAsyncTask asyncTask = new GetSellingProductsAsyncTask(CompanyActivity.this);
            asyncTask.execute();
        }
    }

    public void processResponse(int responseCode) {
        Log.d(TAG, "processResponse: " + responseCode);
        loadingDialogFragment.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SELLING_PRODUCTS = null;
    }
}
