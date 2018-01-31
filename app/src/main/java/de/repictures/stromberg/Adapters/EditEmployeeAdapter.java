package de.repictures.stromberg.Adapters;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import de.repictures.stromberg.AsyncTasks.UpdateEmployeeAsyncTask;
import de.repictures.stromberg.CompanyActivity;
import de.repictures.stromberg.Features.EditEmployeeActivity;
import de.repictures.stromberg.Fragments.EditEmployeeWorkTimeDialogFragment;
import de.repictures.stromberg.POJOs.Account;
import de.repictures.stromberg.R;

public class EditEmployeeAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public boolean edited = false;
    private DateFormat textFormat = new SimpleDateFormat("EEEE HH:mm", Locale.GERMANY);
    private DecimalFormat f = new DecimalFormat("0.00");

    private final int VIEW_TYPE_HEADER = 0;
    private final int VIEW_TYPE_BODY = 1;
    private final int VIEW_TYPE_ADD_WORK_TIME = 2;
    private final int VIEW_TYPE_PERMISSION_TITLE = 3;
    private final int VIEW_TYPE_PERMISSION_BODY = 4;
    private final int VIEW_TYPE_SAVE_BUTTON = 5;

    private final EditEmployeeActivity editEmployeeActivity;
    private final String[] allFeaturesNames;

    public EditEmployeeAdapter(EditEmployeeActivity editEmployeeActivity){
        this.editEmployeeActivity = editEmployeeActivity;
        this.allFeaturesNames = editEmployeeActivity.getResources().getStringArray(R.array.featuresNames);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return VIEW_TYPE_HEADER;
        else if (position == allFeaturesNames.length + editEmployeeActivity.account.getStartTimesInt().size() + 3) return VIEW_TYPE_SAVE_BUTTON;
        else if (position == editEmployeeActivity.account.getStartTimesInt().size() + 2) return VIEW_TYPE_PERMISSION_TITLE;
        else if (position > editEmployeeActivity.account.getStartTimesInt().size() + 2 && position < allFeaturesNames.length + editEmployeeActivity.account.getStartTimesInt().size() + 3)
            return VIEW_TYPE_PERMISSION_BODY;
        else if (position == editEmployeeActivity.account.getStartTimesInt().size() +1) return VIEW_TYPE_ADD_WORK_TIME;
        else return VIEW_TYPE_BODY;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType){
            case VIEW_TYPE_HEADER:
               v = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_employee_list_header, parent, false);
               return new HeaderViewHolder(v);
            case VIEW_TYPE_BODY:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_employee_list_body, parent, false);
                return new BodyViewHolder(v);
            case VIEW_TYPE_ADD_WORK_TIME:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_employee_list_add_period, parent, false);
                return new AddPeriodViewHolder(v);
            case VIEW_TYPE_SAVE_BUTTON:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_employee_list_save_button, parent, false);
                return new SaveButtonViewHolder(v);
            case VIEW_TYPE_PERMISSION_TITLE:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_employee_list_permissions_title, parent, false);
                return new PermissionTitleViewHolder(v);
            case VIEW_TYPE_PERMISSION_BODY:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_employee_list_permission_permission, parent, false);
                return new PermissionBodyViewHolder(v);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(holder.getAdapterPosition())){
            case VIEW_TYPE_HEADER:
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                headerViewHolder.accountnumber.setText(editEmployeeActivity.account.getAccountnumber());
                headerViewHolder.netWage.setText(String.format("%1$sS", f.format(getNetWage(editEmployeeActivity.account.getWage()))));
                headerViewHolder.wageEdit.setText(String.valueOf(editEmployeeActivity.account.getWage()));
                break;
            case VIEW_TYPE_BODY:
                BodyViewHolder bodyViewHolder = (BodyViewHolder) holder;
                Calendar startTime = new GregorianCalendar(Locale.GERMANY);
                int startTimeMinutes = editEmployeeActivity.account.getStartTimesInt().get(position-1);
                startTime.set(Calendar.DAY_OF_WEEK, Account.getDaysFromMinutes(startTimeMinutes)+2);
                startTime.set(Calendar.HOUR_OF_DAY, Account.getHoursFromMinutes(startTimeMinutes));
                startTime.set(Calendar.MINUTE, Account.getMinutesOfHourFromMinutes(startTimeMinutes));
                Calendar endTime = new GregorianCalendar(Locale.GERMANY);
                int endTimeMinutes = editEmployeeActivity.account.getEndTimesInt().get(position-1);
                endTime.set(Calendar.DAY_OF_WEEK, Account.getDaysFromMinutes(endTimeMinutes)+2);
                endTime.set(Calendar.HOUR_OF_DAY, Account.getHoursFromMinutes(endTimeMinutes));
                endTime.set(Calendar.MINUTE, Account.getMinutesOfHourFromMinutes(endTimeMinutes));
                String startTimeStr = textFormat.format(startTime.getTime());
                bodyViewHolder.startTimeText.setText(startTimeStr);
                String endTimeStr = textFormat.format(endTime.getTime());
                bodyViewHolder.endTimeText.setText(endTimeStr);
                bodyViewHolder.workTimeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditEmployeeWorkTimeDialogFragment fragment = new EditEmployeeWorkTimeDialogFragment();
                        fragment.setEditEmployeeActivity(editEmployeeActivity);
                        Bundle args = new Bundle();
                        args.putInt("title", R.string.edit_work_time);
                        args.putInt("start_time_minutes", startTimeMinutes);
                        args.putInt("end_time_minutes", endTimeMinutes);
                        args.putInt("position", holder.getAdapterPosition());
                        fragment.setArguments(args);
                        fragment.show(editEmployeeActivity.getFragmentManager(), "blub");
                    }
                });
                break;
            case VIEW_TYPE_ADD_WORK_TIME:
                AddPeriodViewHolder addPeriodViewHolder = (AddPeriodViewHolder) holder;
                addPeriodViewHolder.addPeriodLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditEmployeeWorkTimeDialogFragment fragment = new EditEmployeeWorkTimeDialogFragment();
                        fragment.setEditEmployeeActivity(editEmployeeActivity);
                        Bundle args = new Bundle();
                        args.putInt("title", R.string.add_work_time);
                        args.putInt("start_time_minutes", 0);
                        args.putInt("end_time_minutes", 0);
                        args.putInt("position", holder.getAdapterPosition());
                        fragment.setArguments(args);
                        fragment.show(editEmployeeActivity.getFragmentManager(), "blub");
                    }
                });
                break;
            case VIEW_TYPE_SAVE_BUTTON:
                SaveButtonViewHolder saveButtonViewHolder = (SaveButtonViewHolder) holder;
                saveButtonViewHolder.progressBar.setVisibility(View.INVISIBLE);
                saveButtonViewHolder.progressBar.getIndeterminateDrawable().setColorFilter(editEmployeeActivity.getResources().getColor(R.color.colorAccentYellow), android.graphics.PorterDuff.Mode.SRC_ATOP);
                if (edited){
                    saveButtonViewHolder.saveButton.setEnabled(true);
                    saveButtonViewHolder.saveButton.setText(R.string.save);
                } else {
                    saveButtonViewHolder.saveButton.setEnabled(false);
                    saveButtonViewHolder.saveButton.setText(R.string.saved);
                }
                saveButtonViewHolder.saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveButtonViewHolder.saveButton.setText("");
                        saveButtonViewHolder.progressBar.setVisibility(View.VISIBLE);
                        UpdateEmployeeAsyncTask updateEmployeeAsyncTask = new UpdateEmployeeAsyncTask(editEmployeeActivity);
                        updateEmployeeAsyncTask.execute(editEmployeeActivity.account);
                    }
                });
                break;
            case VIEW_TYPE_PERMISSION_BODY:
                PermissionBodyViewHolder permissionBodyViewHolder = (PermissionBodyViewHolder) holder;
                int permissionPosition = holder.getAdapterPosition() - (editEmployeeActivity.account.getStartTimesInt().size() + 3);
                permissionBodyViewHolder.permissionText.setText(allFeaturesNames[permissionPosition]);
                permissionBodyViewHolder.permissionCheckBox.setOnCheckedChangeListener(null);
                permissionBodyViewHolder.permissionCheckBox.setChecked(editEmployeeActivity.account.getPermissions().contains(permissionPosition));
                permissionBodyViewHolder.permissionLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        permissionBodyViewHolder.permissionCheckBox.setChecked(!permissionBodyViewHolder.permissionCheckBox.isChecked());
                        /*List<Integer> permissions = editEmployeeActivity.account.getPermissions();
                        if (!permissions.contains(permissionPosition)){
                            permissions.add(permissionPosition);
                        } else {
                            int permissionArrayPosition = permissions.indexOf(permissionPosition);
                            permissions.remove(permissionArrayPosition);
                        }
                        editEmployeeActivity.account.setPermissions(permissions);
                        edited = true;
                        notifyItemChanged(allFeaturesNames.length + editEmployeeActivity.account.getStartTimesInt().size() + 3);*/
                    }
                });
                permissionBodyViewHolder.permissionCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        List<Integer> permissions = editEmployeeActivity.account.getPermissions();
                        if (!permissions.contains(permissionPosition)){
                            permissions.add(permissionPosition);
                        } else {
                            int permissionArrayPosition = permissions.indexOf(permissionPosition);
                            permissions.remove(permissionArrayPosition);
                        }
                        editEmployeeActivity.account.setPermissions(permissions);
                        edited = true;
                        notifyItemChanged(allFeaturesNames.length + editEmployeeActivity.account.getStartTimesInt().size() + 3);
                    }
                });
        }
    }

    private double getNetWage(double wage) {
        double fractionalPart = wage % 1;
        int integralPart = (int) (wage - fractionalPart);

        //Prozentsatz berechnen
        double integralPercentage = 0;
        for (int i = 0; i < integralPart; i++){
            if (i < CompanyActivity.WAGE_TAX_ARRAY.length) integralPercentage += CompanyActivity.WAGE_TAX_ARRAY[i];
            else integralPercentage += 100;
        }
        integralPercentage = (integralPercentage/integralPart);
        double fractionPercentage = 0;
        if (integralPart < CompanyActivity.WAGE_TAX_ARRAY.length) fractionPercentage = CompanyActivity.WAGE_TAX_ARRAY[integralPart];
        else fractionPercentage = 100;

        //Brutto in Netto und Abgabe spalten
        double tax = ((((double) integralPart) * (integralPercentage/100)) + (fractionalPart * ( fractionPercentage/100)));
       return (wage - tax);
    }

    @Override
    public int getItemCount() {
        return allFeaturesNames.length + editEmployeeActivity.account.getStartTimesInt().size() + 4;
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder implements TextWatcher {

        TextView accountnumber, netWage;
        TextInputLayout wageLayout;
        EditText wageEdit;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            accountnumber = (TextView) itemView.findViewById(R.id.edit_employee_accountnumber_text);
            netWage = (TextView) itemView.findViewById(R.id.edit_employee_netwage_text);
            wageLayout = (TextInputLayout) itemView.findViewById(R.id.edit_employee_wage_layout);
            wageEdit = (EditText) itemView.findViewById(R.id.edit_employee_wage_edit_text);
            wageEdit.addTextChangedListener(this);
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            int textLength = wageEdit.getText().toString().length();
            String wageStr = wageEdit.getText().toString();
            if (textLength > 0 && wageStr.charAt(textLength-1) != '.' && !wageStr.equals(String.valueOf(editEmployeeActivity.account.getWage()))){
                double wage = Double.valueOf(wageStr);
                netWage.setText(String.format("%1$sS", f.format(getNetWage(wage))));
                editEmployeeActivity.account.setWage(wage);
                if (!edited) {
                    edited = true;
                    notifyItemChanged(allFeaturesNames.length + editEmployeeActivity.account.getStartTimesInt().size() + 3);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    private class BodyViewHolder extends RecyclerView.ViewHolder {

        TextView startTimeText, endTimeText;
        RelativeLayout workTimeLayout;

        public BodyViewHolder(View itemView) {
            super(itemView);
            startTimeText = (TextView) itemView.findViewById(R.id.edit_employee_start_time);
            endTimeText = (TextView) itemView.findViewById(R.id.edit_employee_end_time);
            workTimeLayout = (RelativeLayout) itemView.findViewById(R.id.edit_employee_work_times_layout);
        }
    }

    private class AddPeriodViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout addPeriodLayout;

        public AddPeriodViewHolder(View itemView) {
            super(itemView);
            addPeriodLayout = (RelativeLayout) itemView.findViewById(R.id.edit_employee_add_work_time_layout);
        }
    }

    private class SaveButtonViewHolder extends RecyclerView.ViewHolder {

        Button saveButton;
        ProgressBar progressBar;

        public SaveButtonViewHolder(View itemView) {
            super(itemView);
            saveButton = (Button) itemView.findViewById(R.id.edit_employee_save_button);
            progressBar = (ProgressBar) itemView.findViewById(R.id.edit_employee_progress_bar);
        }
    }

    private class PermissionTitleViewHolder extends RecyclerView.ViewHolder {

        TextView titleText;

        public PermissionTitleViewHolder(View itemView) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.edit_employee_permission_text_label);
        }
    }

    private class PermissionBodyViewHolder extends RecyclerView.ViewHolder {

        CheckBox permissionCheckBox;
        TextView permissionText;
        RelativeLayout permissionLayout;

        public PermissionBodyViewHolder(View itemView) {
            super(itemView);
            permissionCheckBox = (CheckBox) itemView.findViewById(R.id.edit_employee_permission_check_box);
            permissionText = (TextView) itemView.findViewById(R.id.edit_employee_permission_text);
            permissionLayout = (RelativeLayout) itemView.findViewById(R.id.edit_employee_permission_layout);
        }
    }
}