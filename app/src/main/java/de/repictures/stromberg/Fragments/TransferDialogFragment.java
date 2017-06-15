package de.repictures.stromberg.Fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.repictures.stromberg.AsyncTasks.TransferAsyncTask;
import de.repictures.stromberg.R;
import de.repictures.stromberg.uiHelper.ForbiddenCharactersFilter;

public class TransferDialogFragment extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TransferDialogFragment";

    @Bind(R.id.transfer_fragment_owner_input_layout) TextInputLayout ownerInputLayout;
    @Bind(R.id.transfer_fragment_accountnumber_input_layout) TextInputLayout accountInputLayout;
    @Bind(R.id.transfer_fragment_owner_auto_complete_text) AutoCompleteTextView ownerAutoComplete;
    @Bind(R.id.transfer_fragment_accountnumber_auto_complete_text) TextInputEditText accountnumberEditText;
    @Bind(R.id.transfer_fragment_amount_edit_text) TextInputEditText amountEditText;
    @Bind(R.id.transfer_fragment_amount_layout) TextInputLayout amountInputLayout;
    @Bind(R.id.transfer_fragment_check_box) CheckBox planCheckBox;
    @Bind(R.id.transfer_fragment_date_spinner) TextView dateSpinner;
    @Bind(R.id.transfer_fragment_time_spinner) TextView timeSpinner;
    @Bind(R.id.transfer_fragment_execute_at_text) TextView executeAtText;
    @Bind(R.id.transfer_fragment_purpose_edit_text) TextInputEditText purposeEditText;
    @Bind(R.id.transfer_fragment_coordinator_layout) public CoordinatorLayout coordinatorLayout;

    List<String> owners = Arrays.asList("Yasin Ekinci", "Max Musterman", "Rodi GmbH");
    List<String> accountnumbers = Arrays.asList("0001", "0003", "0002");
    SimpleDateFormat dateFormat, timeFormat;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_transfer);
        ButterKnife.bind(this);

        //Setup Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.transfer_fragment_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(TransferDialogFragment.this)
                        .setTitle(TransferDialogFragment.this.getResources().getString(R.string.delete_transfer))
                        .setMessage(TransferDialogFragment.this.getResources().getString(R.string.you_will_lose_data))
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent();
                                setResult(RESULT_OK, i);
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        });

        //Setup EditTexts
        ArrayAdapter<String> ownersDummy = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, owners);
        ForbiddenCharactersFilter filter = new ForbiddenCharactersFilter(this);
        purposeEditText.setFilters(new InputFilter[]{filter});
        ownerAutoComplete.setFilters(new InputFilter[]{filter});
        ownerAutoComplete.setThreshold(1);
        ownerAutoComplete.setAdapter(ownersDummy);
        ownerAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(owners.contains(charSequence.toString()) && ownerAutoComplete.hasFocus()){
                    accountnumberEditText.setText(accountnumbers.get(owners.indexOf(charSequence.toString())));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        accountnumberEditText.setFilters(new InputFilter[]{filter});
        accountnumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (accountnumbers.contains(charSequence.toString()) && accountnumberEditText.hasFocus()){
                    ownerAutoComplete.setText(owners.get(accountnumbers.indexOf(charSequence.toString())));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //Setup Date-/TimePickers
        dateSpinner.setOnClickListener(this);
        timeSpinner.setOnClickListener(this);
        dateSpinner.setEnabled(false);
        timeSpinner.setEnabled(false);
        executeAtText.setEnabled(false);
        Calendar calendar = Calendar.getInstance(Locale.GERMANY);
        dateFormat = new SimpleDateFormat("EEEE, dd. MM YYYY", Locale.GERMANY);
        timeFormat = new SimpleDateFormat("HH:mm", Locale.GERMANY);
        dateSpinner.setText(dateFormat.format(calendar.getTime()));
        timeSpinner.setText(timeFormat.format(calendar.getTime()) + " " + getResources().getString(R.string.oclock));
        planCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                dateSpinner.setEnabled(isChecked);
                timeSpinner.setEnabled(isChecked);
                executeAtText.setEnabled(isChecked);
            }
        });
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar dateCalendar = Calendar.getInstance();
                dateCalendar.set(year, month, day);
                Log.d(TAG, "onDateSet: " + dateCalendar);
                dateSpinner.setText(dateFormat.format(dateCalendar.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                timeSpinner.setText(hour + ":" + minute + " " + getResources().getString(R.string.oclock));
            }
        }, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fragment_transfer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(dataWithoutError()){
            SharedPreferences sharedPref = getSharedPreferences(getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
            TransferAsyncTask asyncTask = new TransferAsyncTask(TransferDialogFragment.this);
            asyncTask.execute(sharedPref.getString(getResources().getString(R.string.sp_accountnumber), ""), accountnumberEditText.getText().toString(),
                        amountEditText.getText().toString(), purposeEditText.getText().toString());
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean dataWithoutError() {
        boolean noError = true;
        ownerInputLayout.setError("");
        ownerInputLayout.setErrorEnabled(false);
        accountInputLayout.setError("");
        accountInputLayout.setErrorEnabled(false);
        amountInputLayout.setError("");
        amountInputLayout.setErrorEnabled(false);
        Log.d(TAG, "dataWithoutError: " + ownerAutoComplete.getText().toString().length());
        if(ownerAutoComplete.getText().toString().length() < 1){
            ownerInputLayout.setErrorEnabled(true);
            ownerInputLayout.setError(getResources().getString(R.string.error_owner_empty));
            noError = false;
        }
        if(accountnumberEditText.getText().toString().length() < 1){
            accountInputLayout.setErrorEnabled(true);
            accountInputLayout.setError(getResources().getString(R.string.error_account_empty));
            noError = false;
        }
        if(amountEditText.getText().toString().length() < 1){
            amountInputLayout.setErrorEnabled(true);
            amountInputLayout.setError(getResources().getString(R.string.error_amount_empty));
            noError = false;
        }
        return noError;
    }

    @Override
    public void onClick(View view) {
        if(view == dateSpinner){
            datePickerDialog.show();
        } else if(view == timeSpinner){
            timePickerDialog.show();
        }
    }

    public void postResult(int result){
        Log.d(TAG, "postResult: " + result);
        switch (result){
            case 0:
                Snackbar.make(coordinatorLayout, getResources().getString(R.string.server_error), Snackbar.LENGTH_SHORT).show();
                break;
            case 1:
                Snackbar.make(coordinatorLayout, getResources().getString(R.string.transfer_no_money), Snackbar.LENGTH_SHORT).show();
                break;
            case 2:
                Snackbar.make(coordinatorLayout, getResources().getString(R.string.transfer_receiver_not_exist), Snackbar.LENGTH_SHORT).show();
                break;
            case 3:
                Intent i = new Intent();
                i.putExtra("success", true);
                setResult(RESULT_OK, i);
                finish();
                break;
        }
    }
}
