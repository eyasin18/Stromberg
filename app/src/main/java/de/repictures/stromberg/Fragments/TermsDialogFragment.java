package de.repictures.stromberg.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import de.repictures.stromberg.R;

public class TermsDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Build the Dialog
        AlertDialog builder = new AlertDialog.Builder(getActivity())
                .setTitle(getActivity().getResources().getString(R.string.privacy_terms))
                .setPositiveButton(getActivity().getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences sharedPref = getActivity().getSharedPreferences(getResources().getString(R.string.sp_identifier), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean(getActivity().getResources().getString(R.string.sp_privacy_policy_2), true);
                        editor.apply();
                        dialogInterface.dismiss();
                    }
                })
                .create();

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View parent = layoutInflater.inflate(R.layout.fragment_terms_dialog, null);
        builder.setView(parent);
        return builder;
    }
}