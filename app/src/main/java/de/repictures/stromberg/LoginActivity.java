package de.repictures.stromberg;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.repictures.stromberg.AsyncTasks.LoginAsyncTask;
import de.repictures.stromberg.uiHelper.GetPictures;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    public static String SERVERURL = "http://fingerhut388.appspot.com";

    @Bind(R.id.login_background) ImageView loginBackground;
    @Bind(R.id.login_login_button) Button loginButton;
    @Bind(R.id.login_password_edit_layout) TextInputLayout passwordEditLayout;
    @Bind(R.id.login_account_number_edit_layout) TextInputLayout accountnumberEditLayout;
    @Bind(R.id.login_password_edit) TextInputEditText passwordEdit;
    @Bind(R.id.login_account_number_edit) TextInputEditText accountnumberEdit;
    @Bind(R.id.login_forgot_pin) TextView forgotPin;
    @Bind(R.id.login_progress_bar) ProgressBar loginProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        new Thread(new GetPictures(R.drawable.strombergcover, loginBackground, this, false, true, false)).start();
        loginButton.setOnClickListener(this);
        accountnumberEdit.requestFocus();
        loginProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccentYellow), android.graphics.PorterDuff.Mode.SRC_ATOP);
        SharedPreferences sharedPref = getSharedPreferences(getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
        String savedAccountnumber = sharedPref.getString(getResources().getString(R.string.sp_accountnumber), "");
        if(savedAccountnumber != ""){
            accountnumberEdit.setText(savedAccountnumber);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_login_button:
                if(accountnumberEdit.getText().toString().length() > 0 && passwordEdit.getText().toString().length() > 0) {
                    loginButton.setText("");
                    loginProgressBar.setVisibility(View.VISIBLE);
                    accountnumberEditLayout.setErrorEnabled(false);
                    accountnumberEditLayout.setError("");
                    passwordEditLayout.setError("");
                    passwordEditLayout.setErrorEnabled(false);
                    LoginAsyncTask mAuth = new LoginAsyncTask(accountnumberEditLayout, passwordEditLayout, loginButton, loginProgressBar, LoginActivity.this);
                    mAuth.execute(accountnumberEdit.getText().toString(), passwordEdit.getText().toString());
                }
        }
    }
}
