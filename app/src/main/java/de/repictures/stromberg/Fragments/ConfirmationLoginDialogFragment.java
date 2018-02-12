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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.repictures.stromberg.AsyncTasks.ConfirmPasswordAsyncTask;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.R;

public class ConfirmationLoginDialogFragment extends DialogFragment implements View.OnClickListener {

    TextView accountnumberTextView, cancelTextView;
    TextInputLayout passwordInputLayout;
    TextInputEditText passwordEditText;
    Button loginButton;
    ProgressBar loginProgressBar;
    String accountnumber;
    int accountnumberLabelTextId = R.string.company_number;
    private String TAG = "ConfirmDialog";
    private OrderDetailFragment orderDetailFragment;
    private TextView accountnumberLabelTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountnumber = getArguments().getString(OrderDetailFragment.ARG_ACCOUNTNUMBER_ID);
        accountnumberLabelTextId = getArguments().getInt("label_id");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Build the Dialog
        AlertDialog builder = new AlertDialog.Builder(getActivity())
                .create();

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View parent = layoutInflater.inflate(R.layout.fragment_confirm_login_dialog, null);

        accountnumberTextView = (TextView) parent.findViewById(R.id.company_login_accountnumber_text);
        accountnumberLabelTextView = (TextView) parent.findViewById(R.id.company_login_accountnumber_text_label);
        passwordInputLayout = (TextInputLayout) parent.findViewById(R.id.company_login_password_edit_layout);
        passwordEditText = (TextInputEditText) parent.findViewById(R.id.company_login_password_edit);
        loginButton = (Button) parent.findViewById(R.id.company_login_login_button);
        loginProgressBar = (ProgressBar) parent.findViewById(R.id.company_login_progress_bar);
        cancelTextView = (TextView) parent.findViewById(R.id.company_login_cancel);

        accountnumberTextView.setText(accountnumber);
        accountnumberLabelTextView.setText(getResources().getString(accountnumberLabelTextId));
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
                ConfirmPasswordAsyncTask confirmPasswordAsyncTask = new ConfirmPasswordAsyncTask(ConfirmationLoginDialogFragment.this);
                confirmPasswordAsyncTask.execute(accountnumber, passwordEditText.getText().toString());
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
                Log.d(TAG, "progressResponse: success");
                orderDetailFragment.finishOrder();
                dismiss();
                break;
            case 2:
                //Webstring falsch
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

    public void setOrderDetailFragment(OrderDetailFragment orderDetailFragment) {
        this.orderDetailFragment = orderDetailFragment;
    }
}
