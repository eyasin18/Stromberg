package de.repictures.stromberg;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final Intent i = new Intent(SplashActivity.this, LoginActivity.class);
        Picasso.with(getApplicationContext()).load(R.drawable.strombergcover).fetch(new Callback() {
            @Override
            public void onSuccess() {
                startActivity(i);
            }

            @Override
            public void onError() {
                startActivity(i);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.pausedTime = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MainActivity.pausedTime != 0 && System.currentTimeMillis() - MainActivity.pausedTime > 30000){
            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            this.finish();
        }
    }
}
