package de.repictures.stromberg.Features;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.repictures.stromberg.AsyncTasks.PostProductAsyncTask;
import de.repictures.stromberg.R;

public class AddProductActivity extends AppCompatActivity {

    @Bind(R.id.barcode) EditText barcodeEditText;
    @Bind(R.id.name) EditText nameEditText;
    @Bind(R.id.price) EditText priceEditText;
    @Bind(R.id.company_accountnumber) EditText companyEditText;
    @Bind(R.id.button2) Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        ButterKnife.bind(this);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PostProductAsyncTask asyncTask = new PostProductAsyncTask(AddProductActivity.this);
                asyncTask.execute(barcodeEditText.getText().toString(), nameEditText.getText().toString(), priceEditText.getText().toString(), companyEditText.getText().toString());
            }
        });
    }
}
