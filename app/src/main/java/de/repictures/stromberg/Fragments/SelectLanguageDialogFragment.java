package de.repictures.stromberg.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import java.util.Arrays;

import de.repictures.stromberg.Adapters.LanguageSelectorAdapter;
import de.repictures.stromberg.Adapters.OrderDetailListAdapter;
import de.repictures.stromberg.Helper.LocaleHelper;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.R;

public class SelectLanguageDialogFragment extends DialogFragment{

    public String[] languages = new String[]{"gsw-rCH", "de", "en"};
    public int selectedLanguage = 0;
    private LoginActivity loginActivity;

    public void setLoginActivity(LoginActivity loginActivity){

        this.loginActivity = loginActivity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        selectedLanguage = Arrays.asList(languages).indexOf(LocaleHelper.getLanguage(getContext()));

        //Build the Dialog
        AlertDialog builder = new AlertDialog.Builder(getActivity())
                .setTitle(getActivity().getResources().getString(R.string.select_language))
                .setPositiveButton(getActivity().getResources().getString(R.string.ok), (dialogInterface, i) -> {
                    loginActivity.setLanguage(languages[selectedLanguage]);
                    dismiss();
                })
                .setNegativeButton(getActivity().getResources().getString(R.string.cancel), (dialogInterface, i) -> dismiss())
                .create();

        //Put ProgressBar in the DialogLayout
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View parent = layoutInflater.inflate(R.layout.order_detail, null);

        RecyclerView recyclerView = parent.findViewById(R.id.order_detail);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        LanguageSelectorAdapter mAdapter = new LanguageSelectorAdapter(SelectLanguageDialogFragment.this);
        recyclerView.setAdapter(mAdapter);

        builder.setView(parent);
        return builder;
    }


}
