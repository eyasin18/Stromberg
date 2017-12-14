package de.repictures.stromberg.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import de.repictures.stromberg.AuthScanActivity;
import de.repictures.stromberg.R;

public class EditAuthCodeDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Put ProgressBar in the DialogLayout
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View parent = layoutInflater.inflate(R.layout.fragment_edit_auth_code_dialog, null);

        final EditText authEditText = (EditText) parent.findViewById(R.id.auth_scan_edit_text);

        //Build the Dialog
        AlertDialog builder = new AlertDialog.Builder(getActivity())
                .setTitle(getActivity().getResources().getString(R.string.edit_auth_code))
                .setPositiveButton(getActivity().getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((AuthScanActivity)getActivity()).addBarcode(authEditText.getText().toString());
                        if (((AuthScanActivity)getActivity()).sortedBarcodeValues.size() > 1)
                            ((AuthScanActivity)getActivity()).sendAuthRequest();
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton(getActivity().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create();

        builder.setView(parent);
        return builder;
    }
}
