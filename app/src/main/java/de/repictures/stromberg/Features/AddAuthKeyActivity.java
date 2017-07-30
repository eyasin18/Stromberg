package de.repictures.stromberg.Features;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.repictures.stromberg.R;
import de.repictures.stromberg.uiHelper.QRCode;

public class AddAuthKeyActivity extends AppCompatActivity {
    @Bind(R.id.authkey_edit) EditText authKeyEdit;
    @Bind(R.id.accountnumber_edit) EditText accountnumberedit;
    @Bind(R.id.send_button) Button sendButton;
    @Bind(R.id.qrPreview) ImageView qrPreview;
    @Bind(R.id.getButton) Button getButton;

    private QRCode qrCodeBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_auth_key);
        ButterKnife.bind(this);
        qrCodeBuilder = new QRCode(AddAuthKeyActivity.this);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrCodeBuilder.upload(qrCodeBuilder.generate(authKeyEdit.getText().toString()), accountnumberedit.getText().toString(), authKeyEdit.getText().toString());
            }
        });

        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrCodeBuilder.getQRCode(accountnumberedit.getText().toString(), qrPreview);
            }
        });
    }
}
