package de.repictures.stromberg;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.repictures.stromberg.AsyncTasks.GetFinancialStatusAsyncTask;

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
    @Bind(R.id.main_deposit) RelativeLayout depositLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        accountKey = getIntent().getStringExtra("account_key");
        transferLayout.setOnClickListener(this);
        inboxLayout.setOnClickListener(this);
        domainLayout.setOnClickListener(this);
        depositLayout.setOnClickListener(this);

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
        accountBalanceText.setText(String.format(getResources().getString(R.string.account_balance_format), df.format(accountbalance)));
        accountNumberText.setText(accountnumber);
        accountOwnerText.setText(accountowner);
        financialStatusProgressBar.setVisibility(View.GONE);
        financialStatusContent.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.main_transfer:
                break;
            case R.id.main_inbox:
                break;
            case R.id.main_domain:
                break;
            case R.id.main_deposit:
                break;
        }
    }
}
