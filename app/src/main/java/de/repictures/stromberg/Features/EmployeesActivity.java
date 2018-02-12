package de.repictures.stromberg.Features;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

    private RecyclerView.Adapter employeeAdapter;
    private List<Account> accounts;
    public int companyPosition;

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

    public void setAdapter(List<Account> accounts){
        this.accounts = accounts;
        progressBar.setVisibility(View.GONE);
        employeeAdapter = new EmployeesAdapter(EmployeesActivity.this, this.accounts);
        employeesList.setAdapter(employeeAdapter);
    }
}
