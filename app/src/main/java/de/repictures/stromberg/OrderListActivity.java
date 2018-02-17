package de.repictures.stromberg;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.messaging.FirebaseMessaging;

import org.apache.commons.beanutils.converters.ArrayConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.repictures.stromberg.Adapters.OrdersListAdapter;
import de.repictures.stromberg.AsyncTasks.GetPurchaseOrdersAsyncTask;
import de.repictures.stromberg.Firebase.FingerhutFirebaseMessagingService;
import de.repictures.stromberg.POJOs.Product;
import de.repictures.stromberg.POJOs.PurchaseOrder;

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
    private SearchView searchView;
    private String query = "";

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: MessageReceived");
            int id = intent.getIntExtra("id", 0);
            switch (id){
                case 1:
                    refreshAdapter(intent);
                    break;
                case 2:
                    refreshLayout.setRefreshing(true);
                    GetPurchaseOrdersAsyncTask asyncTask = new GetPurchaseOrdersAsyncTask(OrderListActivity.this);
                    asyncTask.execute(companyPosition);
                    break;
                default:
            }
        }
    };

    private List<PurchaseOrder> purchaseOrders = new ArrayList<>();
    private ArrayList companyNumbers;
    public int companyPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        companyPosition = getIntent().getIntExtra("company_array_position", 0);

        SharedPreferences sharedPref = getSharedPreferences(getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        companyNumbers = new ArrayList<>(sharedPref.getStringSet(getResources().getString(R.string.sp_companynumbers), new HashSet<>()));
        FirebaseMessaging.getInstance().subscribeToTopic(companyNumbers.get(companyPosition) + "-shoppingRequests");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.order_list_refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetPurchaseOrdersAsyncTask asyncTask = new GetPurchaseOrdersAsyncTask(OrderListActivity.this);
                asyncTask.execute(companyPosition);
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
        mAdapter = new OrdersListAdapter(this, mTwoPane, purchaseOrders);
        recyclerView.setAdapter(mAdapter);

        refreshLayout.setRefreshing(true);
        GetPurchaseOrdersAsyncTask asyncTask = new GetPurchaseOrdersAsyncTask(OrderListActivity.this);
        asyncTask.execute(companyPosition);
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

        if (id == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        FirebaseMessaging.getInstance().subscribeToTopic(companyNumbers.get(companyPosition) + "-shoppingRequests");

        IntentFilter filter = new IntentFilter();
        filter.addAction(FingerhutFirebaseMessagingService.ORDERS_UPDATE_BROADCAST_ACTION);
        registerReceiver(broadcastReceiver, filter);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(companyNumbers.get(companyPosition) + "-shoppingRequests");

        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    public void refreshAdapter(List<PurchaseOrder> purchaseOrders) {
        this.purchaseOrders.clear();
        this.purchaseOrders.addAll(purchaseOrders);
        mAdapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
    }

    private void refreshAdapter(Intent intent) {
        ArrayConverter stringToIntConverter = new ArrayConverter(int[].class, new IntegerConverter());

        PurchaseOrder newPurchaseOrder = new PurchaseOrder();

        String[] amountsStr = intent.getStringExtra("amounts").split("ò");
        if (amountsStr.length > 0 && amountsStr[0].length() > 0) newPurchaseOrder.setAmounts(stringToIntConverter.convert(int[].class, amountsStr));
        else newPurchaseOrder.setAmounts(new int[0]);
        newPurchaseOrder.setBuyerAccountnumber(intent.getStringExtra("buyerAccountnumber"));
        newPurchaseOrder.setDateTime(intent.getStringExtra("dateTime"));
        newPurchaseOrder.setNumber(Integer.valueOf(intent.getStringExtra("number")));
        newPurchaseOrder.setCompleted(Boolean.parseBoolean(intent.getStringExtra("completed")));
        newPurchaseOrder.setMadeByUser(Boolean.parseBoolean(intent.getStringExtra("madeByUser")));
        String[] isSelfBuysStr = intent.getStringExtra("isSelfBuys").split("ò");
        String[] pricesStr = intent.getStringExtra("prices").split("ò");
        String[] productCodesStr = intent.getStringExtra("productCodes").split("ò");
        String[] productNamesStr = intent.getStringExtra("productNames").split("ò");
        Product[] products;
        if (isSelfBuysStr.length > 0 && isSelfBuysStr[0].length() > 0
                && pricesStr.length > 0 && pricesStr[0].length() > 0
                && productCodesStr.length > 0 && productCodesStr[0].length() > 0
                && productNamesStr.length > 0 && productNamesStr[0].length() > 0){
            products = new Product[isSelfBuysStr.length];
            for (int i = 0; i < isSelfBuysStr.length; i++){
                Product product = new Product();
                product.setSelfBuy(Boolean.parseBoolean(isSelfBuysStr[i]));
                product.setPrice(Double.parseDouble(pricesStr[i]));
                product.setCode(productCodesStr[i]);
                product.setName(productNamesStr[i]);
                products[i] = product;
            }
        } else {
            products = new Product[0];
        }
        newPurchaseOrder.setProducts(products);

        purchaseOrders.add(0, newPurchaseOrder);
        if (query.isEmpty()) {
            mAdapter.notifyItemInserted(1);
            mAdapter.notifyItemRangeChanged(1, purchaseOrders.size());
        } else {
            mAdapter.notifyDataSetChanged();
            mAdapter.getFilter().filter(query);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_employees, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setInputType(InputType.TYPE_CLASS_NUMBER);
        searchView.setQueryHint(getResources().getString(R.string.account_number));
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                OrderListActivity.this.query = query;
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                OrderListActivity.this.query = query;
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    public void insertPurchaseOrder(PurchaseOrder purchaseOrder){
        /*purchaseOrders.add(1, purchaseOrder);
        mAdapter.notifyItemInserted(1);*/
        /*if (mTwoPane) {
            Bundle arguments = new Bundle();

            OrderDetailFragment fragment = new OrderDetailFragment();
            fragment.purchaseOrder = purchaseOrders.get(0);
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.order_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(OrderListActivity.this, OrderDetailActivity.class);
            intent.putExtra("purchaseOrder", purchaseOrders.get(0));
            startActivity(intent);
        }*/
    }
}
