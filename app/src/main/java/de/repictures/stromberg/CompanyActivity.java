package de.repictures.stromberg;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.repictures.stromberg.Adapters.FeaturesListAdapter;

public class CompanyActivity extends AppCompatActivity {
    @Bind(R.id.features_recycler) RecyclerView featuresRecycler;
    RecyclerView.Adapter featuresAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);
        ButterKnife.bind(this);

        featuresRecycler.setHasFixedSize(true);
        featuresRecycler.setLayoutManager(new LinearLayoutManager(this));
        featuresAdapter = new FeaturesListAdapter(CompanyActivity.this);
        featuresRecycler.setAdapter(featuresAdapter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.pausedTime = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MainActivity.pausedTime != 0 && System.currentTimeMillis() - MainActivity.pausedTime > 30000){
            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            this.finish();
        }
    }
}
