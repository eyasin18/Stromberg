package de.repictures.stromberg.Features;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.repictures.stromberg.Adapters.ProductsAdapter;
import de.repictures.stromberg.AsyncTasks.GetCompanyProductsAsyncTask;
import de.repictures.stromberg.POJOs.Product;
import de.repictures.stromberg.R;

public class ProductsActivity extends AppCompatActivity {

    private int companyPosition;
    private RecyclerView recyclerView;
    private ProductsAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Product> products = new ArrayList<>();
    private List<String> companyNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        companyPosition = getIntent().getIntExtra("company_array_position", 0);

        SharedPreferences sharedPref = getSharedPreferences(getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        companyNumbers = new ArrayList<>(sharedPref.getStringSet(getResources().getString(R.string.sp_companynumbers), new HashSet<>()));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent i = new Intent(ProductsActivity.this, AddProductActivity.class);
            i.putExtra("company_array_position", companyPosition);
            startActivity(i);
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.products_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                GetCompanyProductsAsyncTask asyncTask = new GetCompanyProductsAsyncTask(ProductsActivity.this);
                asyncTask.execute(companyNumbers.get(companyPosition));
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.products_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductsAdapter(ProductsActivity.this, products);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        swipeRefreshLayout.setRefreshing(true);
        GetCompanyProductsAsyncTask asyncTask = new GetCompanyProductsAsyncTask(ProductsActivity.this);
        asyncTask.execute(companyNumbers.get(companyPosition));
    }

    public void updateProducts(List<Product> products){
        swipeRefreshLayout.setRefreshing(false);
        this.products.clear();
        this.products.addAll(products);
        adapter.notifyDataSetChanged();
    }

    public void noInternet(){
        Snackbar.make(swipeRefreshLayout, getResources().getString(R.string.internet_problems), Snackbar.LENGTH_LONG).show();
    }

    public void editProduct(Product product) {
        Intent i = new Intent(ProductsActivity.this, AddProductActivity.class);
        i.putExtra("company_array_position", companyPosition);
        i.putExtra("product", product);
        startActivity(i);
    }
}