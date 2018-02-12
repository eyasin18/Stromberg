package de.repictures.stromberg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.repictures.stromberg.AsyncTasks.GetFinancialStatusAsyncTask;
import de.repictures.stromberg.Fragments.CompanyLoginDialogFragment;
import de.repictures.stromberg.Helper.LocaleHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "MainActivity";
    private String accountKey, webstring;
    private List<String> companyNumbers, companySectors;

    @BindView(R.id.financial_status_account_number) TextView accountNumberText;
    @BindView(R.id.financial_status_balance) TextView accountBalanceText;
    @BindView(R.id.financial_status_account_owner) TextView vatTextView;
    @BindView(R.id.financial_status_content) RelativeLayout financialStatusContent;
    @BindView(R.id.financial_status_progress_bar) ProgressBar financialStatusProgressBar;
    @BindView(R.id.financial_status_account_type) TextView financialStatusAccountType;
    @BindView(R.id.main_domain_text) TextView mainDomainText;
    @BindView(R.id.main_domain_image) ImageView mainDomainImageView;
    @BindView(R.id.main_transfer) RelativeLayout transferLayout;
    @BindView(R.id.main_manual) RelativeLayout manualLayout;
    @BindView(R.id.main_domain) RelativeLayout domainLayout;
    @BindView(R.id.main_scan) RelativeLayout scanLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: called");
        ButterKnife.bind(this);
        LocaleHelper.onCreate(MainActivity.this);
        SharedPreferences sharedPref = getSharedPreferences(getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        webstring = sharedPref.getString(getResources().getString(R.string.sp_webstring), "");
        accountKey = sharedPref.getString(getResources().getString(R.string.sp_accountkey), "");
        companyNumbers = new ArrayList<>(sharedPref.getStringSet(getResources().getString(R.string.sp_companynumbers), new HashSet<>()));
        companySectors = new ArrayList<>(sharedPref.getStringSet(getResources().getString(R.string.sp_companysectors), new HashSet<>()));
        boolean isPrepaid = sharedPref.getBoolean(getResources().getString(R.string.sp_is_prepaid), false);
        if (isPrepaid) financialStatusAccountType.setText(getResources().getString(R.string.prepaid_account));
        else financialStatusAccountType.setText(getResources().getString(R.string.citizen_account));
        transferLayout.setOnClickListener(this);
        manualLayout.setOnClickListener(this);
        domainLayout.setOnClickListener(this);
        scanLayout.setOnClickListener(this);

        if (companyNumbers.size() < 1){
            mainDomainText.setEnabled(false);
            mainDomainImageView.setImageDrawable(getResources().getDrawable(R.drawable.domain_grey));
            domainLayout.setEnabled(false);
        }
        else if (companyNumbers.size() < 2){
            switch (companySectors.get(0)) {
                case "0":
                    mainDomainText.setText(getResources().getString(R.string.domain));
                    break;
                case "1":
                    mainDomainText.setText(getResources().getString(R.string.fcb_long));
                    break;
                case "2":
                    mainDomainText.setText(getResources().getString(R.string.ministry));
                    break;
                case "3":
                    mainDomainText.setText(getResources().getString(R.string.parliament));
                    break;
                case "4":
                    mainDomainText.setText(getResources().getString(R.string.police));
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        //Rufe Finanzstatus im Hintergrund ab.
        super.onResume();
        GetFinancialStatusAsyncTask getFinancialStatus = new GetFinancialStatusAsyncTask(this);
        getFinancialStatus.execute(webstring);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPref = getSharedPreferences(getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(getResources().getString(R.string.sp_featureslist));
        editor.remove(getResources().getString(R.string.sp_pin));
        editor.remove(getResources().getString(R.string.sp_companynumbers));
        editor.remove(getResources().getString(R.string.sp_webstring));
        editor.remove(getResources().getString(R.string.sp_vat));
        editor.remove(getResources().getString(R.string.sp_device_token));
        editor.remove(getResources().getString(R.string.sp_companysectors));
        editor.remove(getResources().getString(R.string.sp_companynames));
        editor.apply();
    }

    //Wenn Finanzstatus erfolgreich eingegangen ist, dann...
    public void setFinancialStatus(String accountnumber, String vat, float accountbalance){
        DecimalFormat df = new DecimalFormat("0.00");
        accountBalanceText.setTextColor(getResources().getColor(accountbalance <= 0 ? R.color.balance_minus : R.color.balance_plus));
        accountBalanceText.setText(String.format(getResources().getString(R.string.account_balance_format), df.format(round(accountbalance, 2))));
        accountNumberText.setText(accountnumber);
        SharedPreferences sharedPref = getSharedPreferences(getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getResources().getString(R.string.sp_vat), Integer.valueOf(vat));
        editor.apply();
        String vatString = getResources().getString(R.string.vat) + ": " + vat + "%";
        vatTextView.setText(vatString);
        financialStatusProgressBar.setVisibility(View.GONE);
        financialStatusContent.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()){
            case R.id.main_transfer:
                i = new Intent(MainActivity.this, TransfersActivity.class);
                startActivity(i);
                break;
            case R.id.main_manual:
                i = new Intent(MainActivity.this, ManualActivity.class);
                startActivity(i);
                break;
            case R.id.main_domain:
                CompanyLoginDialogFragment fragment = new CompanyLoginDialogFragment();
                fragment.show(getSupportFragmentManager(), "CompanyLoginDialogFragment");
                break;
            case R.id.main_scan:
                i = new Intent(MainActivity.this, ScanProductActivity.class);
                startActivity(i);
                break;
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}