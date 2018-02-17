package de.repictures.stromberg.Adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import de.repictures.stromberg.Features.EditEmployeeActivity;
import de.repictures.stromberg.Features.EmployeesActivity;
import de.repictures.stromberg.POJOs.Account;
import de.repictures.stromberg.R;

public class EmployeesAdapter extends RecyclerView.Adapter<EmployeesViewHolder> implements Filterable {

    private final EmployeesActivity employeesActivity;
    private final List<Account> accounts;
    private List<Account> finishedFilteredAccounts;

    public EmployeesAdapter(EmployeesActivity employeesActivity, List<Account> accounts){

        this.employeesActivity = employeesActivity;
        this.accounts = accounts;
        this.finishedFilteredAccounts = accounts;
    }

    @Override
    public EmployeesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.employees_list, parent, false);
        return new EmployeesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(EmployeesViewHolder holder, int position) {
        holder.accountnumber.setText(finishedFilteredAccounts.get(position).getAccountnumber());
        holder.employeeLayout.setOnClickListener(view -> {
            Intent i = new Intent(employeesActivity, EditEmployeeActivity.class);
            i.putExtra("company_array_position", employeesActivity.companyPosition);
            i.putExtra("account", finishedFilteredAccounts.get(position));
            i.putExtra("position", position);
            employeesActivity.startActivityForResult(i, EmployeesActivity.ACCOUNT_REQUEST_CODE);
        });
    }

    @Override
    public int getItemCount() {
        return finishedFilteredAccounts.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()){
                    finishedFilteredAccounts = accounts;
                } else {
                    List<Account> filteredAccounts = new ArrayList<>();
                    for (Account account : accounts){
                        if (account.getAccountnumber().contains(charString)){
                            filteredAccounts.add(account);
                        }
                    }
                    finishedFilteredAccounts = filteredAccounts;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = finishedFilteredAccounts;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                finishedFilteredAccounts = (ArrayList<Account>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
