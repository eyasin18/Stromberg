package de.repictures.stromberg;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import de.repictures.stromberg.Fragments.OrderDetailFragment;

/**
 * An activity representing a single Order detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link OrderListActivity}.
 */
public class OrderDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(OrderDetailFragment.ARG_ACCOUNTNUMBER_ID, getIntent().getStringExtra(OrderDetailFragment.ARG_ACCOUNTNUMBER_ID));
            arguments.putStringArray(OrderDetailFragment.ARG_PRODUCT_CODES_ID, getIntent().getStringArrayExtra(OrderDetailFragment.ARG_PRODUCT_CODES_ID));
            arguments.putStringArray(OrderDetailFragment.ARG_PRODUCT_NAMES_ID, getIntent().getStringArrayExtra(OrderDetailFragment.ARG_PRODUCT_NAMES_ID));
            arguments.putDoubleArray(OrderDetailFragment.ARG_PRICES_ARRAY_ID, getIntent().getDoubleArrayExtra(OrderDetailFragment.ARG_PRICES_ARRAY_ID));
            arguments.putInt(OrderDetailFragment.ARG_NUMBER_ID, getIntent().getIntExtra(OrderDetailFragment.ARG_NUMBER_ID, 0));
            arguments.putBooleanArray(OrderDetailFragment.ARG_IS_SELF_BUYS_ARRAY_ID, getIntent().getBooleanArrayExtra(OrderDetailFragment.ARG_IS_SELF_BUYS_ARRAY_ID));
            arguments.putIntArray(OrderDetailFragment.ARG_AMOUNTS_ARRAY_ID, getIntent().getIntArrayExtra(OrderDetailFragment.ARG_AMOUNTS_ARRAY_ID));
            OrderDetailFragment fragment = new OrderDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.order_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, OrderListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
