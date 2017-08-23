package de.repictures.stromberg.Features;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.Formatter;
import java.util.Locale;

import de.repictures.stromberg.R;

public class ShowTransferDetailDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String day = getArguments().getString("day");
        String time = getArguments().getString("time");
        String titleRaw = getActivity().getResources().getString(R.string.show_transfer_detail_dialog_title);
        StringBuilder titleBuilder = new StringBuilder();
        Formatter formatter = new Formatter(titleBuilder, Locale.getDefault());
        formatter.format(Locale.getDefault(), titleRaw, day, time);
        String isSenderStr = getArguments().getString("isSenderStr");
        Boolean isSender = Boolean.parseBoolean(isSenderStr);
        String person = getArguments().getString("person");
        String purpose = getArguments().getString("purpose");
        String type = getArguments().getString("type");

        //Build the Dialog
        AlertDialog builder = new AlertDialog.Builder(getActivity())
                .setTitle(titleBuilder.toString())
                .setPositiveButton(getActivity().getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();

        //Put EditText in the DialogLayout
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View parent = layoutInflater.inflate(R.layout.fragment_show_transfer_detail_dialog, null);

        TextView personTitle = (TextView) parent.findViewById(R.id.transfer_detail_person_title);
        TextView personBody = (TextView) parent.findViewById(R.id.transfer_detail_person_body);
        TextView purposeBody = (TextView) parent.findViewById(R.id.transfer_detail_purpose_body);
        TextView typeBody = (TextView) parent.findViewById(R.id.transfer_detail_type_body);

        if (isSender) personTitle.setText(getActivity().getResources().getString(R.string.transfer_sender));
        else personTitle.setText(getActivity().getResources().getString(R.string.transfer_receiver));

        personBody.setText(person);
        purposeBody.setText(purpose);
        typeBody.setText(type);

        builder.setView(parent);
        return builder;
    }
}
