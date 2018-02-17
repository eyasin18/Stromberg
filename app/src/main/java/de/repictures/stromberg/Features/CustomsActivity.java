package de.repictures.stromberg.Features;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.repictures.stromberg.AsyncTasks.SquareCustomAsyncTask;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.R;

public class CustomsActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.customs_button) Button button;
    @BindView(R.id.customs_companynumber_edit) EditText companyNumberEditText;
    @BindView(R.id.customs_price_edit) EditText priceEditText;
    @BindView(R.id.customs_progress_bar) ProgressBar customsProgressBar;
    @BindView(R.id.customs_meat_custom__check_box) CheckBox meatCustomCheckBox;
    @BindView(R.id.customs_package_custom_check_box) CheckBox packageCustomCheckBox;
    @BindView(R.id.customs_meat_custom_check_box_relative_layout) RelativeLayout meatCustomRelativeLayout;
    @BindView(R.id.customs_package_custom_check_box_relative_layout) RelativeLayout packageCustomRelativeLayout;
    @BindView(R.id.customs_bio_meat_custom_check_box) CheckBox bioMeatCustomCheckBox;
    @BindView(R.id.customs_bio_meat_custom_check_box_relative_layout) RelativeLayout bioMeatCustomRelativeLayout;
    @BindView(R.id.customs_relative_layout) RelativeLayout customsRelativeLayout;
    private String webstring;
    private String accountnumber;
    private boolean checking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customs);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        customsProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccentYellow), android.graphics.PorterDuff.Mode.SRC_ATOP);

        SharedPreferences sharedPref = getSharedPreferences(getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        webstring = sharedPref.getString(getResources().getString(R.string.sp_webstring), "");
        accountnumber = sharedPref.getString(getResources().getString(R.string.sp_accountnumber), "");

        button.setOnClickListener(this);
        meatCustomRelativeLayout.setOnClickListener(this);
        packageCustomRelativeLayout.setOnClickListener(this);
        bioMeatCustomRelativeLayout.setOnClickListener(this);

        meatCustomCheckBox.setOnCheckedChangeListener(this);
        bioMeatCustomCheckBox.setOnCheckedChangeListener(this);
        packageCustomCheckBox.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.customs_meat_custom_check_box_relative_layout:
                checking = true;
                meatCustomCheckBox.setChecked(!meatCustomCheckBox.isChecked());
                bioMeatCustomCheckBox.setChecked(false);
                packageCustomCheckBox.setChecked(false);
                checking = false;
                break;
            case R.id.customs_package_custom_check_box_relative_layout:
                checking = true;
                packageCustomCheckBox.setChecked(!packageCustomCheckBox.isChecked());
                bioMeatCustomCheckBox.setChecked(false);
                meatCustomCheckBox.setChecked(false);
                checking = false;
                break;
            case R.id.customs_bio_meat_custom_check_box_relative_layout:
                checking = true;
                bioMeatCustomCheckBox.setChecked(!bioMeatCustomCheckBox.isChecked());
                meatCustomCheckBox.setChecked(false);
                packageCustomCheckBox.setChecked(false);
                checking = false;
                break;
            case R.id.customs_button:
                button.setText("");
                customsProgressBar.setVisibility(View.VISIBLE);
                SquareCustomAsyncTask asyncTask = new SquareCustomAsyncTask(this);
                asyncTask.execute(accountnumber,
                        webstring,
                        companyNumberEditText.getText().toString(),
                        priceEditText.getText().toString(),
                        String.valueOf(meatCustomCheckBox.isChecked()),
                        String.valueOf(packageCustomCheckBox.isChecked()),
                        String.valueOf(bioMeatCustomCheckBox.isChecked()));
                break;
        }
    }

    public void processResponse(int responseCode, double amount) {
        button.setText(getResources().getString(R.string.finish));
        customsProgressBar.setVisibility(View.INVISIBLE);
        Snackbar snackbar;
        switch (responseCode){
            case -1:
                //koi Inderned
                Snackbar.make(customsRelativeLayout, getResources().getString(R.string.internet_problems), Snackbar.LENGTH_LONG).show();
                break;
            case 0:
                //session abgelaufen
                Intent i = new Intent(this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("webstring_start", true);
                startActivity(i);
                break;
            case 1:
                //Company existiert nicht
                Snackbar.make(customsRelativeLayout, getResources().getString(R.string.no_company_found), Snackbar.LENGTH_LONG).show();
                break;
            case 2:
                //Company zu wenig Geld/insolvent
                snackbar = Snackbar.make(customsRelativeLayout, getResources().getString(R.string.company_not_enough_money_insolvent), Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction(R.string.ok, view -> snackbar.dismiss());
                snackbar.show();
                break;
            case 3:
                //Abgebucht
                DecimalFormat df = new DecimalFormat("0.00");
                snackbar = Snackbar.make(customsRelativeLayout, String.format(getResources().getString(R.string.squared_custom_message), df.format(amount)), Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction(R.string.ok, view -> snackbar.dismiss());
                snackbar.show();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (!checking)
            switch (compoundButton.getId()){
                case R.id.customs_meat_custom__check_box:
                    checking = true;
                    bioMeatCustomCheckBox.setChecked(false);
                    packageCustomCheckBox.setChecked(false);
                    checking = false;
                    break;
                case R.id.customs_bio_meat_custom_check_box:
                    checking = true;
                    meatCustomCheckBox.setChecked(false);
                    packageCustomCheckBox.setChecked(false);
                    checking = false;
                    break;
                case R.id.customs_package_custom_check_box:
                    checking = true;
                    meatCustomCheckBox.setChecked(false);
                    bioMeatCustomCheckBox.setChecked(false);
                    checking = false;
                    break;
            }
    }
}