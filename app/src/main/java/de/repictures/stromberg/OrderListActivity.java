package de.repictures.stromberg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.converters.ArrayConverter;
import org.apache.commons.beanutils.converters.BooleanConverter;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.StringConverter;

import de.repictures.stromberg.Adapters.OrdersListAdapter;
import de.repictures.stromberg.AsyncTasks.GetPurchaseOrdersAsyncTask;
import de.repictures.stromberg.Firebase.FingerhutFirebaseMessagingService;
import de.repictures.stromberg.Fragments.OrderDetailFragment;
import de.repictures.stromberg.dummy.DummyContent;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.app.NavUtils.navigateUpFromSameTask;

/**
 * An activity representing a list of Orders. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link OrderDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class OrderListActivity extends AppCompatActivity {

    private static final String TAG = "OrderListActivity";
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private RecyclerView recyclerView;
    private OrdersListAdapter mAdapter;
    private SwipeRefreshLayout refreshLayout;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: MessageReceived");
            refreshAdapter(intent);
        }
    };

    private List<int[]> amountsList = new ArrayList<>();
    private List<String> buyerAccountnumbers = new ArrayList<>();
    private List<String> dateTimes = new ArrayList<>();
    private List<boolean[]> isSelfBuys = new ArrayList<>();
    private List<Integer> numbers = new ArrayList<>();
    private List<double[]> prices = new ArrayList<>();
    private List<String[]> productCodes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        FirebaseMessaging.getInstance().subscribeToTopic(LoginActivity.COMPANY_NUMBER + "-shoppingRequests");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.order_list_refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetPurchaseOrdersAsyncTask asyncTask = new GetPurchaseOrdersAsyncTask(OrderListActivity.this);
                asyncTask.execute();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (findViewById(R.id.order_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        recyclerView = findViewById(R.id.order_list);
        assert recyclerView != null;
        mAdapter = new OrdersListAdapter(this, mTwoPane, amountsList, buyerAccountnumbers, dateTimes, isSelfBuys, numbers, prices, productCodes);
        recyclerView.setAdapter(mAdapter);

        refreshLayout.setRefreshing(true);
        GetPurchaseOrdersAsyncTask asyncTask = new GetPurchaseOrdersAsyncTask(OrderListActivity.this);
        asyncTask.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        FirebaseMessaging.getInstance().subscribeToTopic(LoginActivity.COMPANY_NUMBER + "-shoppingRequests");

        IntentFilter filter = new IntentFilter();
        filter.addAction(FingerhutFirebaseMessagingService.ORDERS_UPDATE_BROADCAST_ACTION);
        registerReceiver(broadcastReceiver, filter);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(LoginActivity.COMPANY_NUMBER + "-shoppingRequests");

        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    public void refreshAdapter(List<int[]> amountsList,
                               List<String> buyerAccountnumbers,
                               List<String> dateTimes,
                               List<boolean[]> isSelfBuys,
                               List<Integer> numbers,
                               List<double[]> prices,
                               List<String[]> productCodes) {

        this.amountsList.clear();
        this.amountsList.addAll(amountsList);
        this.buyerAccountnumbers.clear();
        this.buyerAccountnumbers.addAll(buyerAccountnumbers);
        this.dateTimes.clear();
        this.dateTimes.addAll(dateTimes);
        this.isSelfBuys.clear();
        this.isSelfBuys.addAll(isSelfBuys);
        this.numbers.clear();
        this.numbers.addAll(numbers);
        this.prices.clear();
        this.prices.addAll(prices);
        this.productCodes.clear();
        this.productCodes.addAll(productCodes);
        mAdapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
    }

    private void refreshAdapter(Intent intent) {
        ArrayConverter stringToIntConverter = new ArrayConverter(int[].class, new IntegerConverter());
        ArrayConverter stringToDoubleConverter = new ArrayConverter(double[].class, new DoubleConverter());
        ArrayConverter stringToBooleanConverter = new ArrayConverter(Boolean[].class, new BooleanConverter());

        String[] amountsStr = intent.getStringExtra("amounts").split("ò");
        amountsList.add(0, stringToIntConverter.convert(int[].class, amountsStr));
        buyerAccountnumbers.add(0, intent.getStringExtra("buyerAccountnumber"));
        dateTimes.add(0, intent.getStringExtra("dateTime"));
        String[] isSelfBuysStr = intent.getStringExtra("isSelfBuys").split("ò");
        isSelfBuys.add(0, stringToBooleanConverter.convert(boolean[].class, isSelfBuysStr));
        numbers.add(0, Integer.parseInt(intent.getStringExtra("number")));
        String[] pricesStr = intent.getStringExtra("prices").split("ò");
        prices.add(0, stringToDoubleConverter.convert(double[].class, pricesStr));
        productCodes.add(0, intent.getStringExtra("productCodes").split("ò"));

        mAdapter.notifyItemInserted(0);
    }
}
