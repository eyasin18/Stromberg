package de.repictures.stromberg;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.repictures.stromberg.AsyncTasks.LoginAsyncTask;
import de.repictures.stromberg.Fragments.TermsDialogFragment;
import de.repictures.stromberg.Helper.LocaleHelper;

//LoginScreen. Startactivity der App.
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "LoginActivity";
    private static final int PERMISSION_REQUEST_CAMERA = 42;

    public static String SERVERURL = "https://fingerhut388.appspot.com/";

    public boolean loginButtonClicked = false;

    private String authCode;
    private FirebaseAnalytics mFirebaseAnalytics;
    private long failedLogins = 0;

    //Views aus der XML werden Javaobjekten zugeordnet.
    @BindView(R.id.login_background) ImageView loginBackground;
    @BindView(R.id.login_login_button) Button loginButton;
    @BindView(R.id.login_password_edit_layout) TextInputLayout passwordEditLayout;
    @BindView(R.id.features_add_product_name_input_layout) TextInputLayout accountnumberEditLayout;
    @BindView(R.id.login_password_edit) TextInputEditText passwordEdit;
    @BindView(R.id.login_account_number_edit) TextInputEditText accountnumberEdit;
    @BindView(R.id.login_authenticate) TextView authenticateText;
    @BindView(R.id.login_progress_bar) ProgressBar loginProgressBar;
    @BindView(R.id.activity_login) RelativeLayout loginLayout;
    //@BindView(R.id.login_language_select) TextView languageSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: " + FirebaseInstanceId.getInstance().getToken());
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        LocaleHelper.onCreate(LoginActivity.this);

        if (getIntent().hasExtra("webstring_start")){
            Snackbar snackbar = Snackbar.make(loginLayout, getResources().getString(R.string.session_timed_out), Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(R.string.ok, view -> snackbar.dismiss());
            snackbar.show();
        }

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

        boolean acceptedPrivacePolice = sharedPref.getBoolean(getResources().getString(R.string.sp_privacy_policy_2), false);
        if (!acceptedPrivacePolice){
            TermsDialogFragment fragment = new TermsDialogFragment();
            fragment.setCancelable(false);
            fragment.show(getSupportFragmentManager(), "sfsdf");
        }

        //languageSelect.setOnClickListener(this);
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
                    passwordEditLayout.setError("");
                    passwordEditLayout.setErrorEnabled(false);

                    SharedPreferences sharedPref = getSharedPreferences(getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getResources().getString(R.string.sp_pin), passwordEdit.getText().toString());
                    editor.apply();

                    LoginAsyncTask mAuth = new LoginAsyncTask(accountnumberEditLayout, passwordEditLayout, loginButton, loginProgressBar, LoginActivity.this);
                    if (authCode != null) {
                        String[] authParts = {authCode.substring(0, getResources().getInteger(R.integer.auth_key_length) / 2), authCode.substring(getResources().getInteger(R.integer.auth_key_length) / 2)};
                        mAuth.execute(accountnumberEdit.getText().toString(), passwordEdit.getText().toString(), authParts[0], authParts[1], FirebaseInstanceId.getInstance().getToken());
                    } else {
                        mAuth.execute(accountnumberEdit.getText().toString(), passwordEdit.getText().toString(), null, null, FirebaseInstanceId.getInstance().getToken());
                    }
                } else if (authCode == null && !loginButtonClicked){
                    accountnumberEditLayout.setErrorEnabled(false);
                    accountnumberEditLayout.setError(getResources().getString(R.string.not_authenticated));
                }
                break;
            case R.id.login_authenticate:
                int rc = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
                if (rc == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(LoginActivity.this, AuthScanActivity.class);
                    startActivityForResult(i, 1);
                } else {
                    requestCameraPermission();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if (resultCode == RESULT_OK){
                    authCode = data.getStringExtra("authcode");
                } else {
                    Log.d(TAG, "onActivityResult: failed!");
                }
                break;
            case 2:
                Log.d(TAG, "onActivityResult: webstring abgelaufen");
                break;
            default:
        }
    }

    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{android.Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CAMERA);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        PERMISSION_REQUEST_CAMERA);
            }
        };

        Snackbar.make(loginLayout, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != PERMISSION_REQUEST_CAMERA) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent i = new Intent(LoginActivity.this, AuthScanActivity.class);
            startActivityForResult(i, 1);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.request_camera_title))
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    public void setLanguage(String language) {
        LocaleHelper.setLocale(LoginActivity.this, language);
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    public void startCountdown(long loginAttempts){
        this.failedLogins = loginAttempts;
        if (failedLogins > 3){
            double waitingTime = Math.pow(2, failedLogins) * 1000;
            countDown((long) waitingTime);
        } else {
            passwordEditLayout.setErrorEnabled(false);
            passwordEditLayout.setError(String.format(getResources().getString(R.string.password_wrong_attempts_left), 4 - failedLogins));
        }
    }

    public void startCountdown(String cooldownTime){
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSSS z", Locale.getDefault());
        Calendar cooldownCalendar = new GregorianCalendar();
        try {
            cooldownCalendar.setTime(sdf.parse(cooldownTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar currentCalendar = Calendar.getInstance();
        long waitingTime = cooldownCalendar.getTimeInMillis() - currentCalendar.getTimeInMillis();
        countDown(waitingTime);
    }

    private void countDown(long waitingTimeMilliseconds){
        loginButton.setEnabled(false);
        passwordEditLayout.setErrorEnabled(true);
        new CountDownTimer(waitingTimeMilliseconds, 1000) {
            @Override
            public void onTick(long currentTime) {
                passwordEditLayout.setError(String.format(getResources().getString(R.string.account_locked_attempts), Math.round(currentTime/1000)));
            }

            @Override
            public void onFinish() {
                passwordEditLayout.setErrorEnabled(false);
                passwordEditLayout.setError("");
                loginButton.setEnabled(true);
            }
        }.start();
    }

    public void showAccountLockedMessage(){
        accountnumberEditLayout.setErrorEnabled(true);
        accountnumberEditLayout.setError(getResources().getString(R.string.account_locked));
        loginButtonClicked = false;
        loginButton.setEnabled(false);
    }
}