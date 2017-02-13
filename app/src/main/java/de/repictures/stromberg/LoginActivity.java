package de.repictures.stromberg;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.repictures.stromberg.uiHelper.GetPictures;

public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.login_background) ImageView loginBackground;
    @Bind(R.id.login_login_button) Button loginButton;
    @Bind(R.id.login_password_edit_layout) TextInputLayout passwordEditLayout;
    @Bind(R.id.login_account_number_edit_layout) TextInputLayout accountnumberEditLayout;
    @Bind(R.id.login_password_edit) TextInputEditText passwordEdit;
    @Bind(R.id.login_account_number_edit) TextInputEditText accountnumberEditText;
    @Bind(R.id.login_forgot_pin) TextView forgotPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        new Thread(new GetPictures(R.drawable.strombergcover, loginBackground, this, false, true, false)).start();

    }
}
