package de.repictures.stromberg.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.repictures.stromberg.R;

public class EmployeesViewHolder extends RecyclerView.ViewHolder {

    public TextView accountnumber;
    public RelativeLayout employeeLayout;

    public EmployeesViewHolder(View itemView) {
        super(itemView);
        accountnumber = (TextView) itemView.findViewById(R.id.employees_list_accountnumber);
        employeeLayout = (RelativeLayout) itemView.findViewById(R.id.employees_list_layout);
        employeeLayout.setClickable(true);
        employeeLayout.setFocusable(true);
    }
}
