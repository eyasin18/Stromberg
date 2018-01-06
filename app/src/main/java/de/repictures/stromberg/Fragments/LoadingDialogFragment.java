package de.repictures.stromberg.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import de.repictures.stromberg.R;

public class LoadingDialogFragment extends DialogFragment{

    public static String ARG_TITLE = "title";
    private int titleId = R.string.title;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_TITLE)){
            titleId = getArguments().getInt(ARG_TITLE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Build the Dialog
        AlertDialog builder = new AlertDialog.Builder(getActivity())
                .setTitle(getActivity().getResources().getString(titleId))
                .setCancelable(false)
                .create();

        //Put ProgressBar in the DialogLayout
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View parent = layoutInflater.inflate(R.layout.fragment_loading_dialog, null);
        builder.setView(parent);
        return builder;
    }


}
