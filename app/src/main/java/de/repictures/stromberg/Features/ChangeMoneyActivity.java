package de.repictures.stromberg.Features;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.repictures.stromberg.AsyncTasks.ChangeMoneyAsyncTask;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.R;

public class ChangeMoneyActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, TextWatcher {

    @BindView(R.id.change_money_input_layout) TextInputLayout accountnumberLayout;
    @BindView(R.id.change_money_edit_text) EditText accountnumberEditText;
    @BindView(R.id.change_money_euro_to_stromer_layout) RelativeLayout euroToStromerLayout;
    @BindView(R.id.change_money_euro_to_stromer_radio_button) RadioButton euroToStromerRadioButton;
    @BindView(R.id.change_money_stromer_to_euro_layout) RelativeLayout stromerToEuroRelativeLayout;
    @BindView(R.id.change_money_stromer_to_euro_radio_button) RadioButton stromerToEuroRadioButton;
    @BindView(R.id.change_money_5_layout) RelativeLayout fiveLayout;
    @BindView(R.id.change_money_5_radio_button) RadioButton fiveRadioButton;
    @BindView(R.id.change_money_5_text) TextView fiveText;
    @BindView(R.id.change_money_10_layout) RelativeLayout tenLayout;
    @BindView(R.id.change_money_10_radio_button) RadioButton tenRadioButton;
    @BindView(R.id.change_money_10_text) TextView tenText;
    @BindView(R.id.change_money_15_layout) RelativeLayout fifteenLayout;
    @BindView(R.id.change_money_15_radio_button) RadioButton fifteenButton;
    @BindView(R.id.change_money_15_text) TextView fifteenText;
    @BindView(R.id.change_money_20_layout) RelativeLayout twentyLayout;
    @BindView(R.id.change_money_20_radio_button) RadioButton twentyButton;
    @BindView(R.id.change_money_20_text) TextView twentyText;
    @BindView(R.id.change_money_edit_layout) RelativeLayout editLayout;
    @BindView(R.id.change_money_edit) EditText editEditText;
    @BindView(R.id.change_money_edit_radio_button) RadioButton editButton;
    @BindView(R.id.change_money_button) Button button;
    @BindView(R.id.change_money_progress_bar) ProgressBar changeMoneyProgressBar;
    @BindView(R.id.change_money_coordinator_layout) CoordinatorLayout coordinatorLayout;

    private boolean checker = false;
    private int companyPosition;
    private int modeSelector = 0;
    private int amountSelector = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_money);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        companyPosition = getIntent().getIntExtra("company_array_position", 0);

        changeMoneyProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccentYellow), android.graphics.PorterDuff.Mode.SRC_ATOP);

        setEuroTexts();

        euroToStromerLayout.setOnClickListener(this);
        stromerToEuroRelativeLayout.setOnClickListener(this);
        fiveLayout.setOnClickListener(this);
        tenLayout.setOnClickListener(this);
        fifteenLayout.setOnClickListener(this);
        twentyLayout.setOnClickListener(this);
        editLayout.setOnClickListener(this);

        editEditText.addTextChangedListener(this);

        fiveRadioButton.setOnCheckedChangeListener(this);
        tenRadioButton.setOnCheckedChangeListener(this);
        fifteenButton.setOnCheckedChangeListener(this);
        twentyButton.setOnCheckedChangeListener(this);
        editButton.setOnCheckedChangeListener(this);
        euroToStromerRadioButton.setOnCheckedChangeListener(this);
        stromerToEuroRadioButton.setOnCheckedChangeListener(this);
        button.setOnClickListener(this);
    }

    private void setStromerTexts() {
        fiveText.setText(String.format(getResources().getString(R.string.format_stromer_short), 5));
        tenText.setText(String.format(getResources().getString(R.string.format_stromer_short), 10));
        fifteenText.setText(String.format(getResources().getString(R.string.format_stromer_short), 15));
        twentyText.setText(String.format(getResources().getString(R.string.format_stromer_short), 20));
    }

    private void setEuroTexts() {
        fiveText.setText(String.format(getResources().getString(R.string.format_euro_short), 5));
        tenText.setText(String.format(getResources().getString(R.string.format_euro_short), 10));
        fifteenText.setText(String.format(getResources().getString(R.string.format_euro_short), 15));
        twentyText.setText(String.format(getResources().getString(R.string.format_euro_short), 20));
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (!checker) {
            checker = true;
            switch (compoundButton.getId()) {
                case R.id.change_money_euro_to_stromer_radio_button:
                    setEuroTexts();
                    stromerToEuroRadioButton.setChecked(false);
                    euroToStromerRadioButton.setChecked(true);
                    modeSelector = 0;
                    break;
                case R.id.change_money_stromer_to_euro_radio_button:
                    setStromerTexts();
                    stromerToEuroRadioButton.setChecked(true);
                    euroToStromerRadioButton.setChecked(false);
                    modeSelector = 1;
                    break;
                case R.id.change_money_5_radio_button:
                    fiveRadioButton.setChecked(true);
                    tenRadioButton.setChecked(false);
                    fifteenButton.setChecked(false);
                    twentyButton.setChecked(false);
                    editButton.setChecked(false);
                    amountSelector = 5;
                    break;
                case R.id.change_money_10_radio_button:
                    fiveRadioButton.setChecked(false);
                    tenRadioButton.setChecked(true);
                    fifteenButton.setChecked(false);
                    twentyButton.setChecked(false);
                    editButton.setChecked(false);
                    amountSelector = 10;
                    break;
                case R.id.change_money_15_radio_button:
                    fiveRadioButton.setChecked(false);
                    tenRadioButton.setChecked(false);
                    fifteenButton.setChecked(true);
                    twentyButton.setChecked(false);
                    editButton.setChecked(false);
                    amountSelector = 15;
                    break;
                case R.id.change_money_20_radio_button:
                    fiveRadioButton.setChecked(false);
                    tenRadioButton.setChecked(false);
                    fifteenButton.setChecked(false);
                    twentyButton.setChecked(true);
                    editButton.setChecked(false);
                    amountSelector = 20;
                    break;
                case R.id.change_money_edit_radio_button:
                    fiveRadioButton.setChecked(false);
                    tenRadioButton.setChecked(false);
                    fifteenButton.setChecked(false);
                    twentyButton.setChecked(false);
                    editButton.setChecked(true);
                    amountSelector = 0;
                    break;
            }
        }
        checker = false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.change_money_stromer_to_euro_layout:
                stromerToEuroRadioButton.setChecked(true);
                break;
            case R.id.change_money_euro_to_stromer_layout:
                euroToStromerRadioButton.setChecked(true);
                break;
            case R.id.change_money_5_layout:
                fiveRadioButton.setChecked(true);
                break;
            case R.id.change_money_10_layout:
                tenRadioButton.setChecked(true);
                break;
            case R.id.change_money_15_layout:
                fifteenButton.setChecked(true);
                break;
            case R.id.change_money_20_layout:
                twentyButton.setChecked(true);
                break;
            case R.id.change_money_edit_layout:
                editButton.setChecked(true);
                break;
            case R.id.change_money_button:
                button.setText("");
                changeMoneyProgressBar.setVisibility(View.VISIBLE);
                accountnumberLayout.setError("");
                accountnumberLayout.setErrorEnabled(false);
                try{
                    SharedPreferences sharedPref = getSharedPreferences(getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
                    String userAccountnumber = sharedPref.getString(getResources().getString(R.string.sp_accountnumber), null);
                    String webstring = sharedPref.getString(getResources().getString(R.string.sp_webstring), null);
                    List<String> companyNumbers = new ArrayList<>(sharedPref.getStringSet(getResources().getString(R.string.sp_companynumbers), new HashSet<>()));
                    String companyNumber = companyNumbers.get(companyPosition);

                    JSONObject requestObject = new JSONObject();
                    requestObject.put("user_accountnumber", userAccountnumber);
                    requestObject.put("accountnumber", accountnumberEditText.getText().toString());
                    requestObject.put("webstring", webstring);
                    requestObject.put("companynumber", companyNumber);
                    requestObject.put("mode", modeSelector);
                    if (amountSelector != 0){
                        requestObject.put("amount", amountSelector);
                    } else {
                        requestObject.put("amount", editEditText.getText().toString());
                    }

                    ChangeMoneyAsyncTask asyncTask = new ChangeMoneyAsyncTask(ChangeMoneyActivity.this);
                    asyncTask.execute(requestObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void processServerResponse(Integer responseCode) {
        button.setText(getResources().getString(R.string.title_activity_change_money));
        changeMoneyProgressBar.setVisibility(View.INVISIBLE);
        switch (responseCode){
            case -1:
                //koi inderned
                Snackbar.make(coordinatorLayout, R.string.internet_problems, Snackbar.LENGTH_LONG).show();
                break;
            case 0:
                //Daten nicht richtig übertragen/Authstring falsch
                Intent i = new Intent(ChangeMoneyActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("webstring_start", true);
                startActivity(i);
                break;
            case 1:
                //Alles gut
                Snackbar.make(coordinatorLayout, R.string.successful, Snackbar.LENGTH_LONG).show();
                break;
            case 2:
                //Es kann nur von Unternehmenskonten Geld ausgezahlt werden
                accountnumberLayout.setError(getResources().getString(R.string.pay_off_only_from_company_account));
                accountnumberLayout.setErrorEnabled(true);
                break;
            case 3:
                //Für Prepaidkonten können keine krummen Beträge eingezahlt werden
                accountnumberLayout.setError(getResources().getString(R.string.prepaid_accounts_cannot_pay_in_free_amounts));
                accountnumberLayout.setErrorEnabled(true);
                break;
            case 4:
                //Du hast nicht die Berechtigung um diese Funktion auszuführen
                Snackbar.make(coordinatorLayout, R.string.you_dont_have_the_permission_to_do_this_task, Snackbar.LENGTH_INDEFINITE).show();
                break;
            case 5:
                //Angegebenes Konto existiert nicht
                accountnumberLayout.setError(getResources().getString(R.string.account_does_not_exist));
                accountnumberLayout.setErrorEnabled(true);
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        editButton.setChecked(true);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}