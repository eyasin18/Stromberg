package de.repictures.stromberg;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.repictures.stromberg.Adapters.FeaturesListAdapter;
import de.repictures.stromberg.AsyncTasks.GetWageTaxesAsyncTask;
import de.repictures.stromberg.Fragments.LoadingDialogFragment;
import de.repictures.stromberg.POJOs.Account;

public class CompanyActivity extends AppCompatActivity {

    public List<String> featuresNames = new ArrayList<>();
    public int companyPosition = 0;
    public List<Integer> features;

    @BindView(R.id.features_recycler) RecyclerView featuresRecycler;
    RecyclerView.Adapter featuresAdapter;
    private String TAG = "CompanyActivity";
    private LoadingDialogFragment loadingDialogFragment = new LoadingDialogFragment();
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);
        ButterKnife.bind(this);

        companyPosition = getIntent().getIntExtra("company_array_position", 0);

        sharedPref = getSharedPreferences(getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);

        List<String> companyNames = new ArrayList<>(sharedPref.getStringSet(getResources().getString(R.string.sp_companynames), new HashSet<>()));
        List<String> companyNumbers = new ArrayList<>(sharedPref.getStringSet(getResources().getString(R.string.sp_companynumbers), new HashSet<>()));
        getSupportActionBar().setTitle(companyNames.get(companyPosition));

        String[] allFeaturesNames = getResources().getStringArray(R.array.featuresNames);

        String featuresJsonStr = sharedPref.getString(getResources().getString(R.string.sp_featureslist), "");
        features = Account.getSpecificFeaturesLongListFromString(featuresJsonStr, companyNumbers.get(companyPosition));

        for (int i = 0; i < features.size(); i++){
            featuresNames.add(allFeaturesNames[features.get(i)]);
        }

        featuresRecycler.setHasFixedSize(true);
        featuresRecycler.setLayoutManager(new LinearLayoutManager(this));
        featuresAdapter = new FeaturesListAdapter(CompanyActivity.this);
        featuresRecycler.setAdapter(featuresAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (features.contains(0) || features.contains(3)) {
            Bundle args = new Bundle();
            args.putInt(LoadingDialogFragment.ARG_TITLE, R.string.updating_taxes);
            loadingDialogFragment.setArguments(args);
            loadingDialogFragment.show(getSupportFragmentManager(), "showLoadingDialogFragment");
            GetWageTaxesAsyncTask asyncTask = new GetWageTaxesAsyncTask(CompanyActivity.this);
            asyncTask.execute(companyPosition);
        }
    }

    public void processResponse(int responseCode) {
        Log.d(TAG, "processResponse: " + responseCode);
        loadingDialogFragment.dismissAllowingStateLoss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(getResources().getString(R.string.sp_wage_tax));
        editor.apply();
    }
}