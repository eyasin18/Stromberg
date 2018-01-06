package de.repictures.stromberg.Features;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.repictures.stromberg.LoginActivity;
import de.repictures.stromberg.R;
import de.repictures.stromberg.uiHelper.QRCode;

public class AddAuthKeyActivity extends AppCompatActivity {
    @BindView(R.id.authkey_edit) EditText authKeyEdit;
    @BindView(R.id.accountnumber_edit) EditText accountnumberedit;
    @BindView(R.id.send_button) Button sendButton;
    @BindView(R.id.qrPreview) ImageView qrPreview;
    @BindView(R.id.getButton) Button getButton;

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
                String authKey = null;
                if (authKeyEdit.getText().toString().trim().length() != 0){
                    authKey = authKeyEdit.getText().toString().trim();
                }
                String accountnumber = accountnumberedit.getText().toString();
                qrCodeBuilder.upload(qrCodeBuilder.generate(authKey, accountnumber), accountnumber);
            }
        });

        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrCodeBuilder.getQRCode(accountnumberedit.getText().toString(), LoginActivity.ACCOUNTNUMBER, qrPreview);
            }
        });
    }
}
