package de.repictures.stromberg.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import de.repictures.stromberg.R;

public class SimpleTextBodyDialogFragment extends DialogFragment {

    private String title = "";
    private String textBody = "";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Build the Dialog
        AlertDialog builder = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setPositiveButton(getActivity().getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();

        //Put EditText in the DialogLayout
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View parent = layoutInflater.inflate(R.layout.fragment_simple_text_body_dialog, null);

        TextView textBodyView = (TextView) parent.findViewById(R.id.simple_text_body_dialog_text_body);
        textBodyView.setText(textBody);

        builder.setView(parent);
        return builder;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTextBody(String textBody) {
        this.textBody = textBody;
    }
}
