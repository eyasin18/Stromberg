package de.repictures.stromberg.Features;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.repictures.stromberg.Adapters.EmployeesAdapter;
import de.repictures.stromberg.AsyncTasks.GetEmployeesAsyncTask;
import de.repictures.stromberg.POJOs.Account;
import de.repictures.stromberg.R;

public class EmployeesActivity extends AppCompatActivity {

    public static final int ACCOUNT_REQUEST_CODE = 1;
    @BindView(R.id.employees_list) RecyclerView employeesList;
    @BindView(R.id.employees_progress_bar) ProgressBar progressBar;
    @BindView(R.id.employees_coordinator_layout) public CoordinatorLayout coordinatorLayout;

    private EmployeesAdapter employeeAdapter;
    private List<Account> accounts;
    public int companyPosition;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employees);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        companyPosition = getIntent().getIntExtra("company_array_position", 0);

        employeesList.setHasFixedSize(true);
        employeesList.setLayoutManager(new LinearLayoutManager(this));
        GetEmployeesAsyncTask asyncTask = new GetEmployeesAsyncTask(EmployeesActivity.this);
        asyncTask.execute(companyPosition);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Account account = (Account) data.getSerializableExtra("account");
            int position = data.getIntExtra("position", 0);
            accounts.set(position, account);
            employeeAdapter.notifyDataSetChanged();
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
                employeeAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                employeeAdapter.getFilter().filter(query);
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

    public void setAdapter(List<Account> accounts){
        this.accounts = accounts;
        progressBar.setVisibility(View.GONE);
        employeeAdapter = new EmployeesAdapter(EmployeesActivity.this, this.accounts);
        employeesList.setAdapter(employeeAdapter);
    }
}