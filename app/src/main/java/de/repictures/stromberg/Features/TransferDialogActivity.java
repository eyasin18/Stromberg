package de.repictures.stromberg.Features;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.repictures.stromberg.AsyncTasks.TransferAsyncTask;
import de.repictures.stromberg.Fragments.LoadingDialogFragment;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.R;

public class TransferDialogActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TransferDialogActivity";

    private boolean sendButtonClicked = false;

    @Bind(R.id.transfer_fragment_owner_input_layout) TextInputLayout ownerInputLayout;
    @Bind(R.id.transfer_fragment_accountnumber_input_layout) TextInputLayout accountInputLayout;
    @Bind(R.id.transfer_fragment_owner_auto_complete_text) AutoCompleteTextView ownerAutoComplete;
    @Bind(R.id.transfer_fragment_accountnumber_auto_complete_text) TextInputEditText accountnumberEditText;
    @Bind(R.id.transfer_fragment_amount_edit_text) TextInputEditText amountEditText;
    @Bind(R.id.transfer_fragment_amount_layout) TextInputLayout amountInputLayout;
    @Bind(R.id.transfer_fragment_purpose_edit_text) TextInputEditText purposeEditText;
    @Bind(R.id.transfer_fragment_coordinator_layout) public CoordinatorLayout coordinatorLayout;

    LoadingDialogFragment loadingDialogFragment;

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
                new AlertDialog.Builder(TransferDialogActivity.this)
                        .setTitle(TransferDialogActivity.this.getResources().getString(R.string.delete_transfer))
                        .setMessage(TransferDialogActivity.this.getResources().getString(R.string.you_will_lose_data))
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

        //Setup the Loading Dialog
        loadingDialogFragment = new LoadingDialogFragment();
        loadingDialogFragment.setCancelable(false);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(TransferDialogActivity.this)
                .setTitle(TransferDialogActivity.this.getResources().getString(R.string.delete_transfer))
                .setMessage(TransferDialogActivity.this.getResources().getString(R.string.you_will_lose_data))
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fragment_transfer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(dataWithoutError() && !sendButtonClicked){
            sendButtonClicked = true;

            FragmentManager fm = getSupportFragmentManager();
            loadingDialogFragment.show(fm, "loadingDialogFragment");

            SharedPreferences sharedPref = getSharedPreferences(getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
            String webString = sharedPref.getString(getResources().getString(R.string.sp_webstring), "");
            TransferAsyncTask asyncTask = new TransferAsyncTask(TransferDialogActivity.this);
            asyncTask.execute(LoginActivity.ACCOUNTNUMBER, accountnumberEditText.getText().toString(), webString,
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
    }

    public void postResult(int result){
        Log.d(TAG, "postResult: " + result);
        sendButtonClicked = false;
        loadingDialogFragment.dismiss();
        switch (result){
            case -1:
                Snackbar.make(coordinatorLayout, getResources().getString(R.string.internet_problems), Snackbar.LENGTH_LONG).show();
                break;
            case 0:
                Snackbar.make(coordinatorLayout, getResources().getString(R.string.server_error), Snackbar.LENGTH_LONG).show();
                break;
            case 1:
                Snackbar.make(coordinatorLayout, getResources().getString(R.string.transfer_no_money), Snackbar.LENGTH_LONG).show();
                break;
            case 2:
                Snackbar.make(coordinatorLayout, getResources().getString(R.string.transfer_receiver_not_exist), Snackbar.LENGTH_LONG).show();
                break;
            case 3:
                Intent i = new Intent();
                i.putExtra("success", true);
                setResult(RESULT_OK, i);
                finish();
                break;
            case 4:
                Snackbar.make(coordinatorLayout, getResources().getString(R.string.transfer_receiver_equals_sender), Snackbar.LENGTH_LONG).show();
                break;
        }
    }
}
