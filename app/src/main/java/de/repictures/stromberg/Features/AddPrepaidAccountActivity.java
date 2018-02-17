package de.repictures.stromberg.Features;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.repictures.stromberg.AsyncTasks.AddPrepaidAccountAsyncTask;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.R;

public class AddPrepaidAccountActivity extends AppCompatActivity {

    @BindView(R.id.add_prepaid_accountnumber_body) TextView accountnumberBody;
    @BindView(R.id.add_prepaid_pin_body) TextView pinBody;
    @BindView(R.id.add_prepaid_create_button) Button createButton;
    @BindView(R.id.add_prepaid_progress_bar) ProgressBar progressBar;

    public int companyPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_prepaid_account);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
        toolbar.setNavigationOnClickListener(view ->{
            if (accountnumberBody.getText().length() > 0 || pinBody.getText().length() > 0){
                showWarningDialog();
            }
        });

        companyPosition = getIntent().getIntExtra("company_array_position", 0);

        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccentYellow), android.graphics.PorterDuff.Mode.SRC_ATOP);
        createButton.setOnClickListener(view -> {
            if (accountnumberBody.getText().length() > 0 || pinBody.getText().length() > 0){
                new AlertDialog.Builder(AddPrepaidAccountActivity.this)
                        .setTitle(AddPrepaidAccountActivity.this.getResources().getString(R.string.complete_creation))
                        .setMessage(AddPrepaidAccountActivity.this.getResources().getString(R.string.prepaid_data_cannot_be_restored))
                        .setPositiveButton(R.string.dismiss, (dialog, which) -> {
                            createButton.setText("");
                            progressBar.setVisibility(View.VISIBLE);
                            AddPrepaidAccountAsyncTask asyncTask = new AddPrepaidAccountAsyncTask(AddPrepaidAccountActivity.this);
                            asyncTask.execute();
                        })
                        .setNegativeButton(android.R.string.cancel, (dialog, which) -> {

                        })
                        .show();
            } else {
                createButton.setText("");
                progressBar.setVisibility(View.VISIBLE);
                AddPrepaidAccountAsyncTask asyncTask = new AddPrepaidAccountAsyncTask(AddPrepaidAccountActivity.this);
                asyncTask.execute();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (accountnumberBody.getText().length() > 0 || pinBody.getText().length() > 0){
            showWarningDialog();
        } else {
            super.onBackPressed();
        }
    }

    private void showWarningDialog(){
        new AlertDialog.Builder(AddPrepaidAccountActivity.this)
                .setTitle(AddPrepaidAccountActivity.this.getResources().getString(R.string.complete_creation))
                .setMessage(AddPrepaidAccountActivity.this.getResources().getString(R.string.prepaid_data_cannot_be_restored))
                .setPositiveButton(R.string.dismiss, (dialog, which) -> {
                    super.onBackPressed();
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    //Do nothing
                })
                .show();
    }

    public void updateFields(JSONObject object) {
        createButton.setText(getResources().getString(R.string.create_account));
        progressBar.setVisibility(View.INVISIBLE);
        try {
            switch (object.getInt("response_code")){
                case -1:
                    //kein Internet
                    break;
                case 0:
                    Intent i = new Intent(AddPrepaidAccountActivity.this, LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtra("webstring_start", true);
                    startActivity(i);
                    break;
                case 1:
                    accountnumberBody.setText(object.getString("accountnumber"));
                    pinBody.setText(object.getString("pin"));
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
