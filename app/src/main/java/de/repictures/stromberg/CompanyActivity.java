package de.repictures.stromberg;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.repictures.stromberg.Adapters.FeaturesListAdapter;

public class CompanyActivity extends AppCompatActivity {

    public List<String> featuresList = new ArrayList<>();
    public List<String> featuresNames = new ArrayList<>();

    @Bind(R.id.features_recycler) RecyclerView featuresRecycler;
    RecyclerView.Adapter featuresAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("Rodi Inc.");

        String[] allFeaturesNames = getResources().getStringArray(R.array.featuresNames);

        SharedPreferences sharedPref = getSharedPreferences(getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        featuresList = new ArrayList<>(sharedPref.getStringSet(getResources().getString(R.string.sp_featureslist), new HashSet<String>()));

        for (int i = 0; i < featuresList.size(); i++){
            featuresNames.add(allFeaturesNames[Integer.parseInt(featuresList.get(i))]);
        }

        featuresRecycler.setHasFixedSize(true);
        featuresRecycler.setLayoutManager(new LinearLayoutManager(this));
        featuresAdapter = new FeaturesListAdapter(CompanyActivity.this);
        featuresRecycler.setAdapter(featuresAdapter);

    }
}
