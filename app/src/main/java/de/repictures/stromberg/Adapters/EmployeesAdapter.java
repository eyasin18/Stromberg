package de.repictures.stromberg.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.repictures.stromberg.Features.EditEmployeeActivity;
import de.repictures.stromberg.Features.EmployeesActivity;
import de.repictures.stromberg.POJOs.Account;
import de.repictures.stromberg.R;

public class EmployeesAdapter extends RecyclerView.Adapter<EmployeesViewHolder> {

    private final EmployeesActivity employeesActivity;
    private final List<Account> accounts;

    public EmployeesAdapter(EmployeesActivity employeesActivity, List<Account> accounts){

        this.employeesActivity = employeesActivity;
        this.accounts = accounts;
    }

    @Override
    public EmployeesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.employees_list, parent, false);
        return new EmployeesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EmployeesViewHolder holder, int position) {
        holder.accountnumber.setText(accounts.get(position).getAccountnumber());
        holder.employeeLayout.setOnClickListener(view -> {
            Intent i = new Intent(employeesActivity, EditEmployeeActivity.class);
            i.putExtra("company_array_position", employeesActivity.companyPosition);
            i.putExtra("account", accounts.get(position));
            i.putExtra("position", position);
            employeesActivity.startActivityForResult(i, EmployeesActivity.ACCOUNT_REQUEST_CODE);
        });
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }
}
