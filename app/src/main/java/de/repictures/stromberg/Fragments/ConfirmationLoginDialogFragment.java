package de.repictures.stromberg.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.repictures.stromberg.CompanyActivity;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.R;

public class ConfirmationLoginDialogFragment extends DialogFragment implements View.OnClickListener {

    TextView accountnumberTextView, cancelTextView;
    TextInputLayout passwordInputLayout;
    TextInputEditText passwordEditText;
    Button loginButton;
    ProgressBar loginProgressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Build the Dialog
        AlertDialog builder = new AlertDialog.Builder(getActivity())
                .create();

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View parent = layoutInflater.inflate(R.layout.fragment_company_login_dialog, null);

        accountnumberTextView = (TextView) parent.findViewById(R.id.company_login_accountnumber_text);
        passwordInputLayout = (TextInputLayout) parent.findViewById(R.id.company_login_password_edit_layout);
        passwordEditText = (TextInputEditText) parent.findViewById(R.id.company_login_password_edit);
        loginButton = (Button) parent.findViewById(R.id.company_login_login_button);
        loginProgressBar = (ProgressBar) parent.findViewById(R.id.company_login_progress_bar);
        cancelTextView = (TextView) parent.findViewById(R.id.company_login_cancel);

        accountnumberTextView.setText(LoginActivity.COMPANY_NUMBER);
        loginProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccentYellow), android.graphics.PorterDuff.Mode.SRC_ATOP);
        loginButton.setOnClickListener(this);
        cancelTextView.setOnClickListener(this);

        builder.setView(parent);
        return builder;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.company_login_login_button:
                loginButton.setText("");
                loginProgressBar.setVisibility(View.VISIBLE);
                passwordInputLayout.setErrorEnabled(false);
                passwordInputLayout.setError("");
                break;
            case R.id.company_login_cancel:
                ConfirmationLoginDialogFragment.this.dismiss();
                break;
            default:
                break;
        }
    }

    public void progressResponse(Integer response) {
        loginButton.setText(getActivity().getResources().getString(R.string.login));
        loginProgressBar.setVisibility(View.INVISIBLE);
        Intent i = null;
        switch (response) {
            case -1:
                //Keine Internetverbindung
                passwordInputLayout.setErrorEnabled(true);
                passwordInputLayout.setError(getActivity().getResources().getString(R.string.internet_problems));
                break;
            case 0:
                //Unternehmen mit dieser Kontonummer wurde nicht gefunden
                passwordInputLayout.setErrorEnabled(true);
                passwordInputLayout.setError(getActivity().getResources().getString(R.string.no_company_found));
                break;
            case 1:
                //Login erfolgreich
                i = new Intent(getActivity(), CompanyActivity.class);
                getActivity().startActivity(i);
                dismiss();
                break;
            case 2:
                //Webstring falsch
                //TODO: Return to login
                i = new Intent(getActivity(), LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                break;
            case 3:
                //Passwort falsch
                passwordInputLayout.setErrorEnabled(true);
                passwordInputLayout.setError(getActivity().getResources().getString(R.string.password_wrong));
                break;
        }
    }
}
