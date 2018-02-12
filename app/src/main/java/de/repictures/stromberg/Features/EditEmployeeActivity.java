package de.repictures.stromberg.Features;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.repictures.stromberg.Adapters.EditEmployeeAdapter;
import de.repictures.stromberg.CompanyActivity;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.MainActivity;
import de.repictures.stromberg.POJOs.Account;
import de.repictures.stromberg.R;

public class EditEmployeeActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getName();
    @BindView(R.id.edit_employee_list) RecyclerView editEmployeeList;
    @BindView(R.id.edit_employee_coordinator_layout) CoordinatorLayout coordinatorLayout;
    public Account account;
    private int position;
    public int companyPosition;
    private EditEmployeeAdapter editEmployeeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_employee);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
        toolbar.setNavigationOnClickListener(view -> finishEditing());
        ButterKnife.bind(this);

        companyPosition = getIntent().getIntExtra("company_array_position", 0);
        position = getIntent().getIntExtra("position", 0);
        account = (Account) getIntent().getSerializableExtra("account");
        editEmployeeAdapter = new EditEmployeeAdapter(this);
        editEmployeeList.setHasFixedSize(true);
        editEmployeeList.setLayoutManager(new LinearLayoutManager(this));
        editEmployeeList.setAdapter(editEmployeeAdapter);
    }

    @Override
    public void onBackPressed() {
        finishEditing();
    }

    private void finishEditing() {
        if (editEmployeeAdapter.edited)
            new AlertDialog.Builder(EditEmployeeActivity.this)
                    .setTitle(EditEmployeeActivity.this.getResources().getString(R.string.dismiss_edit))
                    .setMessage(EditEmployeeActivity.this.getResources().getString(R.string.changed_data_will_not_be_saved))
                    .setPositiveButton(R.string.cancel, (dialog, which) -> {
                        Intent i = new Intent();
                        setResult(RESULT_CANCELED, i);
                        finish();
                    })
                    .setNegativeButton(R.string.continue_editing, (dialog, which) -> {
                        // do nothing
                    })
                    .show();
        else {
            Intent i = new Intent();
            i.putExtra("account", account);
            i.putExtra("position", position);
            setResult(RESULT_OK, i);
            finish();
        }
    }

    public void editWorkingTimes(int startTime, int endTime, int position){
        List<Integer> startTimes = account.getStartTimesInt();
        List<Integer> endTimes = account.getEndTimesInt();
        if (startTimes.size() < position) {
            startTimes.add(startTime);
            endTimes.add(endTime);
        } else {
            startTimes.set(position-1, startTime);
            endTimes.set(position-1, endTime);
        }
        account.setStartTimesInt(startTimes);
        account.setEndTimesInt(endTimes);
        editEmployeeAdapter.edited = true;
        editEmployeeAdapter.notifyDataSetChanged();
    }

    public void processServerResponse(int responseCode){
        switch (responseCode){
            case -1:
                Snackbar.make(coordinatorLayout, R.string.internet_problems, BaseTransientBottomBar.LENGTH_INDEFINITE)
                        .show();
                break;
            case 0:
                Intent i = new Intent(this, CompanyActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                break;
            case 1:
                i = new Intent(this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                break;
            case 2:
                editEmployeeAdapter.edited = false;
                editEmployeeAdapter.notifyDataSetChanged();
                break;
            case 3:
                i = new Intent(this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                break;
        }
    }
}
