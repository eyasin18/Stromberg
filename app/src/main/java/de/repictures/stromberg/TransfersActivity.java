package de.repictures.stromberg;

import android.app.NotificationManager;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.repictures.stromberg.Adapters.TransferListAdapter;
import de.repictures.stromberg.AsyncTasks.GetTransfersAsyncTask;
import de.repictures.stromberg.Features.TransferDialogActivity;

public class TransfersActivity extends AppCompatActivity {

    private static final String TAG = "TransferActivity";
    @Bind(R.id.transfer_recycler) RecyclerView transferRecycler;
    @Bind(R.id.transfer_refresh) SwipeRefreshLayout refreshLayout;
    @Bind(R.id.transfer_layout) CoordinatorLayout coordinatorLayout;
    SwipeRefreshLayout.OnRefreshListener refreshListener;
    RecyclerView.Adapter transferAdapter;
    String[][] transfers = new String[0][0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfers);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.activity_scan_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TransfersActivity.this, TransferDialogActivity.class);
                TransfersActivity.this.startActivityForResult(i, 1);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        transferRecycler.setHasFixedSize(true);
        transferRecycler.setLayoutManager(new LinearLayoutManager(this));
        transferAdapter = new TransferListAdapter(TransfersActivity.this, transfers);
        transferRecycler.setAdapter(transferAdapter);

        refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SharedPreferences sharedPref = getSharedPreferences(getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
                String savedAccountnumber = sharedPref.getString(getResources().getString(R.string.sp_accountnumber), "");
                GetTransfersAsyncTask asyncTask = new GetTransfersAsyncTask(TransfersActivity.this);
                refreshLayout.setRefreshing(true);
                asyncTask.execute(savedAccountnumber);
            }
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
    public void updateRecycler(String[][] transfers){
        Log.d(TAG, "updateRecycler: with input: " + transfers);
        this.transfers = new String[0][0];
        this.transfers = transfers;
        transferAdapter = new TransferListAdapter(TransfersActivity.this, transfers);
        transferRecycler.swapAdapter(transferAdapter, false);
        refreshLayout.setRefreshing(false);
    }

    @UiThread
    public void updateRecycler(){
        Log.d(TAG, "updateRecycler: without input");
        transfers = new String[0][0];
        transferAdapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
    }
}
