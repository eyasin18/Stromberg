package de.repictures.stromberg.Features;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.repictures.stromberg.AsyncTasks.TransferWageAsyncTask;
import de.repictures.stromberg.Helper.GeneralUtils;
import de.repictures.stromberg.R;

public class TransferWageActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private static final String TAG = "dffgiojdoc";
    private String companyNumber;
    private List<Integer> wageTaxArray;
    private DecimalFormat f = new DecimalFormat("0.00");
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.transfer_wage_accountnumber_layout) TextInputLayout accountnummberInputLayout;
    @BindView(R.id.transfer_wage_accountnumber_edit_text) EditText accountnumberEditText;
    @BindView(R.id.transfer_wage_layout) TextInputLayout bruttoInputLayout;
    @BindView(R.id.transfer_wage_edit_text) EditText bruttoEditText;
    @BindView(R.id.transfer_wage_netwage_text) TextView netWage;
    @BindView(R.id.transfer_wage_hours_layout) TextInputLayout hoursInputLayout;
    @BindView(R.id.transfer_wage_hours_edit_text) EditText hoursEditText;
    @BindView(R.id.transfer_wage_pay_button) Button payButton;
    @BindView(R.id.transfer_wage_progress_bar) ProgressBar payProgressBar;
    @BindView(R.id.transfer_wage_layout_layout) CoordinatorLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_wage);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        payButton.setOnClickListener(this);
        SharedPreferences sharedPref = getSharedPreferences(getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        List<String> companyNumbers = new ArrayList<>(sharedPref.getStringSet(getResources().getString(R.string.sp_companynumbers), new HashSet<>()));
        int companyPosition = getIntent().getIntExtra("company_array_position", 0);
        companyNumber = companyNumbers.get(companyPosition);
        String wageTaxStr = sharedPref.getString(getResources().getString(R.string.sp_wage_tax), "");
        wageTaxArray = GeneralUtils.parseJsonIntArray(wageTaxStr);
        payProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccentYellow), android.graphics.PorterDuff.Mode.SRC_ATOP);
        bruttoEditText.addTextChangedListener(this);
    }

    @Override
    public void onClick(View v) {
        accountnummberInputLayout.setErrorEnabled(false);
        accountnumberEditText.setError("");
        if (allDataFilled()){
            payProgressBar.setVisibility(View.VISIBLE);
            payButton.setText("");
            TransferWageAsyncTask asyncTask = new TransferWageAsyncTask(TransferWageActivity.this);
            asyncTask.execute(companyNumber, accountnumberEditText.getText().toString(), bruttoEditText.getText().toString(), hoursEditText.getText().toString());
        } else {
            accountnummberInputLayout.setErrorEnabled(true);
            accountnumberEditText.setError("Du hast die Daten nicht richtig ausgefüllt");
        }
    }

    public void postResponse(int responseCode){
        payProgressBar.setVisibility(View.INVISIBLE);
        payButton.setText(getResources().getString(R.string.pay_wage));
        switch (responseCode){
            case -2:
                Snackbar.make(layout, "Ein Fehler ist aufgetreten. Wenn der Lohn noch nicht überwiesen wurde, versuche es erneut.", BaseTransientBottomBar.LENGTH_LONG).show();
                break;
            case -1:
                Snackbar.make(layout, "Netzwerkfehler! Überprüfe deine Internetverbindung", BaseTransientBottomBar.LENGTH_LONG).show();
                break;
            case 0:
                Log.d(TAG, "postResponse: Daten nicht richtig übertragen");
                break;
            case 1:
                Snackbar.make(layout, "Das angegebene Angestelltenkonto existiert nicht", BaseTransientBottomBar.LENGTH_LONG).show();
                break;
            case 2:
                Snackbar.make(layout, "Das angegebene Unternehmenskonto existiert nicht", BaseTransientBottomBar.LENGTH_LONG).show();
                break;
            case 3:
                Snackbar.make(layout, "Auszahlung erfolgreich abgeschlossen", BaseTransientBottomBar.LENGTH_LONG).show();
                break;
            case 4:
                Snackbar.make(layout, "Das angegebene Angestelltenkonto arbeitet nicht in diesem Unternehmen", BaseTransientBottomBar.LENGTH_LONG).show();
                break;
            case 5:
                Snackbar.make(layout, "Du darfst nur für maximal 7 Stunden Lohn an ein Privatkonto überweisen", BaseTransientBottomBar.LENGTH_LONG).show();
                break;
            case 6:
                Snackbar.make(layout, "Du darfst nicht an ein Prepaidkonto Lohn auszahlen", BaseTransientBottomBar.LENGTH_LONG).show();
                break;
        }
    }

    private boolean allDataFilled() {
        boolean isAllDataFilled = true;
        if (accountnumberEditText.length() != 4){
            isAllDataFilled = false;
        }
        Double wage = Double.parseDouble(bruttoEditText.getText().toString());
        if (wage < 1){
            isAllDataFilled = false;
        }
        if (hoursEditText.getText().toString().length() < 1 && Integer.parseInt(hoursEditText.getText().toString()) > 0){
            isAllDataFilled = false;
        }
        return isAllDataFilled;
    }

    private double getNetWage(double wage) {
        double fractionalPart = wage % 1;
        int integralPart = (int) (wage - fractionalPart);

        //Prozentsatz berechnen
        double integralPercentage = 0;
        for (int i = 0; i < integralPart; i++){
            if (i < wageTaxArray.size()) integralPercentage +=wageTaxArray.get(i);
            else integralPercentage += 100;
        }
        integralPercentage = (integralPercentage/integralPart);
        double fractionPercentage = 0;
        if (integralPart < wageTaxArray.size()) fractionPercentage = wageTaxArray.get(integralPart);
        else fractionPercentage = 100;

        //Brutto in Netto und Abgabe spalten
        double tax = ((((double) integralPart) * (integralPercentage/100)) + (fractionalPart * ( fractionPercentage/100)));
        return (wage - tax);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        int textLength = bruttoEditText.getText().toString().length();
        String wageStr = bruttoEditText.getText().toString();
        if (textLength > 0 && wageStr.charAt(textLength-1) != '.'){
            double wage = Double.valueOf(wageStr);
            netWage.setText(String.format("%1$sS", f.format(getNetWage(wage))));
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}