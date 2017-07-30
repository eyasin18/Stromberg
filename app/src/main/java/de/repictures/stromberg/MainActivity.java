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

import butterknife.Bind;
import butterknife.ButterKnife;
import de.repictures.stromberg.AsyncTasks.GetFinancialStatusAsyncTask;
import de.repictures.stromberg.Features.AddProductActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    long pausedTime = 0;
    String accountKey;

    @Bind(R.id.financial_status_account_number) TextView accountNumberText;
    @Bind(R.id.financial_status_balance) TextView accountBalanceText;
    @Bind(R.id.financial_status_account_owner) TextView accountOwnerText;
    @Bind(R.id.financial_status_content) RelativeLayout financialStatusContent;
    @Bind(R.id.financial_status_progress_bar) ProgressBar financialStatusProgressBar;
    @Bind(R.id.main_transfer) RelativeLayout transferLayout;
    @Bind(R.id.main_inbox) RelativeLayout inboxLayout;
    @Bind(R.id.main_domain) RelativeLayout domainLayout;
    @Bind(R.id.main_scan) RelativeLayout scanLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        SharedPreferences sharedPref = getSharedPreferences(getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        accountKey = sharedPref.getString(getResources().getString(R.string.sp_accountkey), "");
        transferLayout.setOnClickListener(this);
        inboxLayout.setOnClickListener(this);
        domainLayout.setOnClickListener(this);
        scanLayout.setOnClickListener(this);

        GetFinancialStatusAsyncTask getFinancialStatus = new GetFinancialStatusAsyncTask(this);
        getFinancialStatus.execute(accountKey);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pausedTime = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(pausedTime != 0 && System.currentTimeMillis() - pausedTime > 30000){
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            this.finish();
        }
    }

    public void setFinancialStatus(String accountnumber, String accountowner, float accountbalance){

        DecimalFormat df = new DecimalFormat("#.00");
        accountBalanceText.setTextColor(getResources().getColor(accountbalance <= 0 ? R.color.balance_minus : R.color.balance_plus));
        accountBalanceText.setText(String.format(getResources().getString(R.string.account_balance_format), df.format(round(accountbalance, 2))));
        accountNumberText.setText(accountnumber);
        accountOwnerText.setText(accountowner);
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
                i = new Intent(MainActivity.this, CompanyActivity.class);
                startActivity(i);
                break;
            case R.id.main_scan:
                i = new Intent(MainActivity.this, ScanActivity.class);
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
