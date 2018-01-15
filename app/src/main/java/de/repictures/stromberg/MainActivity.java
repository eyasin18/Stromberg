package de.repictures.stromberg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.repictures.stromberg.AsyncTasks.GetFinancialStatusAsyncTask;
import de.repictures.stromberg.Fragments.CompanyLoginDialogFragment;
import de.repictures.stromberg.Helper.LocaleHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "MainActivity";
    String accountKey, webstring;

    @BindView(R.id.financial_status_account_number) TextView accountNumberText;
    @BindView(R.id.financial_status_balance) TextView accountBalanceText;
    @BindView(R.id.financial_status_account_owner) TextView accountOwnerText;
    @BindView(R.id.financial_status_content) RelativeLayout financialStatusContent;
    @BindView(R.id.financial_status_progress_bar) ProgressBar financialStatusProgressBar;
    @BindView(R.id.main_transfer) RelativeLayout transferLayout;
    @BindView(R.id.main_inbox) RelativeLayout inboxLayout;
    @BindView(R.id.main_domain) RelativeLayout domainLayout;
    @BindView(R.id.main_scan) RelativeLayout scanLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        LocaleHelper.onCreate(MainActivity.this);
        SharedPreferences sharedPref = getSharedPreferences(getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        webstring = sharedPref.getString(getResources().getString(R.string.sp_webstring), "");
        accountKey = sharedPref.getString(getResources().getString(R.string.sp_accountkey), "");
        transferLayout.setOnClickListener(this);
        inboxLayout.setOnClickListener(this);
        domainLayout.setOnClickListener(this);
        scanLayout.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        //Rufe Finanzstatus im Hintergrund ab.
        super.onResume();
        GetFinancialStatusAsyncTask getFinancialStatus = new GetFinancialStatusAsyncTask(this);
        getFinancialStatus.execute(webstring);
    }

    //Wenn Finanzstatus erfolgreich eingegangen ist, dann...
    public void setFinancialStatus(String accountnumber, String accountowner, float accountbalance){
        DecimalFormat df = new DecimalFormat("0.00");
        accountBalanceText.setTextColor(getResources().getColor(accountbalance <= 0 ? R.color.balance_minus : R.color.balance_plus));
        accountBalanceText.setText(String.format(getResources().getString(R.string.account_balance_format), df.format(round(accountbalance, 2))));
        accountNumberText.setText(accountnumber);
        String vatString = getResources().getString(R.string.vat) + ": " + accountowner + "%";
        accountOwnerText.setText(vatString);
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
            case R.id.main_inbox:
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
