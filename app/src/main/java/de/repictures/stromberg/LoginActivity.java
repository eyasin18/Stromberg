package de.repictures.stromberg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.repictures.stromberg.AsyncTasks.LoginAsyncTask;
import io.fabric.sdk.android.Fabric;

//LoginScreen. Startactivity der App.
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    //TODO Wenn LoginActivity aufgrund eines Timeouts gestartet wurde, soll die Ursprüngliche Activity wieder aufgerufen werden (BUG!)

    private final String TAG = "LoginActivity";
    public static String SERVERURL = "https://fingerhut388.appspot.com";
    public static String PIN = "";
    public static String ACCOUNTNUMBER = "";
    public static String DEVICE_TOKEN = "";

    public boolean loginButtonClicked = false;

    private String authCode;
    private FirebaseAnalytics mFirebaseAnalytics;

    //Views aus der XML werden Javaobjekten zugeordnet.
    @Bind(R.id.login_background) ImageView loginBackground;
    @Bind(R.id.login_login_button) Button loginButton;
    @Bind(R.id.login_password_edit_layout) TextInputLayout passwordEditLayout;
    @Bind(R.id.login_account_number_edit_layout) TextInputLayout accountnumberEditLayout;
    @Bind(R.id.login_password_edit) TextInputEditText passwordEdit;
    @Bind(R.id.login_account_number_edit) TextInputEditText accountnumberEdit;
    @Bind(R.id.login_authenticate) TextView authenticateText;
    @Bind(R.id.login_progress_bar) ProgressBar loginProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: " + FirebaseInstanceId.getInstance().getToken());
        Fabric.with(this, new Crashlytics());
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        //Die Buttons sollen triggern, wenn sie gedrückt werden.
        loginButton.setOnClickListener(this);
        authenticateText.setOnClickListener(this);

        accountnumberEdit.requestFocus();
        loginProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccentYellow), android.graphics.PorterDuff.Mode.SRC_ATOP);
        //Gespeicherte Kontonummer wird in vorgesehenen EditText geschrieben.
        SharedPreferences sharedPref = getSharedPreferences(getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        String savedAccountnumber = sharedPref.getString(getResources().getString(R.string.sp_accountnumber), "");
        authCode = sharedPref.getString(getResources().getString(R.string.sp_authcode), null);
        if(savedAccountnumber != ""){
            accountnumberEdit.setText(savedAccountnumber);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_login_button:
                //Hat der Nutzer in die Felder Daten eingetragen und ist authentifiziert?
                if(accountnumberEdit.getText().toString().length() > 0 && passwordEdit.getText().toString().length() > 0 && authCode != null && !loginButtonClicked) {
                    Log.d(TAG, "onClick: clicked");
                    loginButtonClicked = true;
                    loginButton.setText("");
                    loginProgressBar.setVisibility(View.VISIBLE);
                    accountnumberEditLayout.setErrorEnabled(false);
                    accountnumberEditLayout.setError("");
                    ACCOUNTNUMBER = accountnumberEdit.getText().toString();
                    passwordEditLayout.setError("");
                    passwordEditLayout.setErrorEnabled(false);
                    PIN = passwordEdit.getText().toString();

                    LoginAsyncTask mAuth = new LoginAsyncTask(accountnumberEditLayout, passwordEditLayout, loginButton, loginProgressBar, LoginActivity.this);
                    String[] authParts = {authCode.substring(0, getResources().getInteger(R.integer.auth_key_length)/2), authCode.substring(getResources().getInteger(R.integer.auth_key_length)/2)};
                    mAuth.execute(accountnumberEdit.getText().toString(), passwordEdit.getText().toString(), authParts[0], authParts[1], FirebaseInstanceId.getInstance().getToken());
                } else if (authCode == null && !loginButtonClicked){
                    accountnumberEditLayout.setErrorEnabled(false);
                    accountnumberEditLayout.setError(getResources().getString(R.string.not_authenticated));
                }
                break;
            case R.id.login_authenticate:
                Intent i = new Intent(LoginActivity.this, AuthScanActivity.class);
                startActivityForResult(i, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
            authCode = data.getStringExtra("authcode");
        } else {
            Log.d(TAG, "onActivityResult: failed!");
        }
    }

    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
}
