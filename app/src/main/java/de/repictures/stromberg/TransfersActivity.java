package de.repictures.stromberg;

import android.app.NotificationManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.repictures.stromberg.Adapters.TransferListAdapter;
import de.repictures.stromberg.AsyncTasks.GetTransfersAsyncTask;
import de.repictures.stromberg.Features.TransferDialogActivity;
import de.repictures.stromberg.POJOs.Transfer;

public class TransfersActivity extends AppCompatActivity {

    private static final String TAG = "TransferActivity";
    @BindView(R.id.transfer_recycler) RecyclerView transferRecycler;
    @BindView(R.id.transfer_refresh) SwipeRefreshLayout refreshLayout;
    @BindView(R.id.transfer_layout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.sad_note_layout) RelativeLayout sadNoteLayout;
    SwipeRefreshLayout.OnRefreshListener refreshListener;
    TransferListAdapter transferAdapter;
    List<Transfer> transfers = new ArrayList<>();

    public int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    public boolean isLoading = false;
    private String query = "";
    private SearchView searchView;
    public boolean itemsLeft;
    private LinearLayoutManager linearLayoutManager;
    private boolean noTransfers = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfers);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sharedPref = getSharedPreferences(getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        boolean isPrepaid = sharedPref.getBoolean(getResources().getString(R.string.sp_is_prepaid), true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.activity_scan_fab);
        if (isPrepaid) fab.hide();
        fab.setOnClickListener(view -> {
            Intent i = new Intent(TransfersActivity.this, TransferDialogActivity.class);
            TransfersActivity.this.startActivityForResult(i, 1);
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        transferRecycler.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        transferRecycler.setLayoutManager(linearLayoutManager);
        transferAdapter = new TransferListAdapter(TransfersActivity.this, transfers, linearLayoutManager);
        transferRecycler.setAdapter(transferAdapter);
        transferRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold) && lastVisibleItem > 0) {
                    isLoading = true;
                    Log.d(TAG, "onScrolled: lastVisibleItem: " + lastVisibleItem);
                    loadMoreItems();
                }
            }
        });

        refreshListener = () -> {
            SharedPreferences sharedPref1 = getSharedPreferences(getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
            String savedAccountnumber = sharedPref1.getString(getResources().getString(R.string.sp_accountnumber), "");
            GetTransfersAsyncTask asyncTask = new GetTransfersAsyncTask(TransfersActivity.this);
            refreshLayout.setRefreshing(true);
            transfers = new ArrayList<>();
            transferAdapter.notifyItemRangeRemoved(0, linearLayoutManager.getItemCount());
            asyncTask.execute(savedAccountnumber, String.valueOf(0));
        };
        refreshLayout.setOnRefreshListener(refreshListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(0);
        refreshListener.onRefresh();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data.getBooleanExtra("success", false)){
            Snackbar.make(coordinatorLayout, getResources().getString(R.string.transfer_success), Snackbar.LENGTH_SHORT).show();
        }
    }

    @UiThread
    public void updateRecycler(List<Transfer> transfers, boolean itemsLeft){
        Log.d(TAG, "updateRecycler: with input: " + transfers);
        if (noTransfers){
            showSadNote(false);
        }
        boolean firstSet = false;
        if (this.transfers.size() == 0) firstSet = true;
        this.transfers.addAll(transfers);
        this.itemsLeft = itemsLeft;
        if (firstSet){
            transferAdapter = new TransferListAdapter(TransfersActivity.this, this.transfers, linearLayoutManager);
            transferRecycler.swapAdapter(transferAdapter, false);
        } else {
            transferAdapter.notifyDataSetChanged();
        }
        if (!query.isEmpty()) transferAdapter.getFilter().filter(query);
        refreshLayout.setRefreshing(false);
        isLoading = !itemsLeft;
        totalItemCount = linearLayoutManager.getItemCount();
        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
        Log.d(TAG, "updateRecycler: " + totalItemCount + " & " + lastVisibleItem);
        if (itemsLeft && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
            isLoading = true;
            Log.d(TAG, "onScrolled: lastVisibleItem: " + lastVisibleItem);
            loadMoreItems();
        }
    }

    @UiThread
    public void updateRecycler(){
        Log.d(TAG, "updateRecycler: without input");
        transfers = new ArrayList<>();
        this.itemsLeft = false;
        transferAdapter = new TransferListAdapter(TransfersActivity.this, transfers, linearLayoutManager);
        transferRecycler.swapAdapter(transferAdapter, false);
        refreshLayout.setRefreshing(false);
        isLoading = false;
        showSadNote(true);
    }

    public void loadMoreItems() {
        Log.d(TAG, "loadMoreItems: triggered");
        SharedPreferences sharedPref = getSharedPreferences(getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        String savedAccountnumber = sharedPref.getString(getResources().getString(R.string.sp_accountnumber), "");
        GetTransfersAsyncTask asyncTask = new GetTransfersAsyncTask(TransfersActivity.this);
        asyncTask.execute(savedAccountnumber, String.valueOf(transfers.size()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_employees, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setInputType(InputType.TYPE_CLASS_TEXT);
        searchView.setQueryHint(getResources().getString(R.string.name));
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                TransfersActivity.this.query = query;
                transferAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                TransfersActivity.this.query = query;
                transferAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    private void showSadNote(boolean show) {
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.simple_fade_in_alpha_animation);
        Animation fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.simple_fade_out_alpha_animation);
        fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!show){
                    sadNoteLayout.setVisibility(View.INVISIBLE);
                    transferRecycler.setVisibility(View.VISIBLE);
                    transferRecycler.startAnimation(fadeInAnimation);
                } else{
                    transferRecycler.setVisibility(View.INVISIBLE);
                    sadNoteLayout.setVisibility(View.VISIBLE);
                    sadNoteLayout.startAnimation(fadeInAnimation);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        if (!show) sadNoteLayout.startAnimation(fadeOutAnimation);
        else transferRecycler.startAnimation(fadeOutAnimation);
    }
}