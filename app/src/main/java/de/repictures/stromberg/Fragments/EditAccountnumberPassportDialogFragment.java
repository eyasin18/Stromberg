package de.repictures.stromberg.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.repictures.stromberg.Features.ScanPassportActivity;
import de.repictures.stromberg.R;
import de.repictures.stromberg.ScanProductActivity;

public class EditAccountnumberPassportDialogFragment extends DialogFragment implements View.OnClickListener {

    private RelativeLayout layout;
    private TextInputLayout accountnumberLayout;
    private EditText accountnumberEdit;
    private String buyerAccountnumber = "";
    private ProgressBar progressBar;
    private Button finishButton;
    private String TAG = "TAG";
    private int companyPosition;
    private ScanPassportActivity scanPassportActivity;

    public void setScanPassportActivity(ScanPassportActivity scanPassportActivity) {
        this.scanPassportActivity = scanPassportActivity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog builder = new AlertDialog.Builder(getActivity())
                .setTitle(getActivity().getResources().getString(R.string.enter_accountnumber_of_buyer))
                .create();

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View parent = layoutInflater.inflate(R.layout.fragment_edit_accountnumber_dialog, null);

        finishButton = (Button) parent.findViewById(R.id.edit_accountnumber_finish_button);
        TextView cancelButton = (TextView) parent.findViewById(R.id.edit_accountnumber_cancel_button);
        accountnumberLayout = (TextInputLayout) parent.findViewById(R.id.edit_accountnumber_accounnumber_layout);
        accountnumberEdit = (EditText) parent.findViewById(R.id.edit_accountnumber_accounnumber_edit);
        layout = (RelativeLayout) parent.findViewById(R.id.edit_accountnumber_layout);
        progressBar = (ProgressBar) parent.findViewById(R.id.edit_accountnumber_progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccentYellow), android.graphics.PorterDuff.Mode.SRC_ATOP);

        finishButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        builder.setView(parent);
        return builder;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_accountnumber_cancel_button:
                dismiss();
                break;
            case R.id.edit_accountnumber_finish_button:
                if (accountnumberEdit.getText().length() == 4) {
                    scanPassportActivity.startControl(accountnumberEdit.getText().toString());
                    dismiss();
                }
                break;
        }
    }
}