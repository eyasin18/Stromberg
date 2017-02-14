package de.repictures.stromberg;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    long pausedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pausedTime = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(pausedTime != 0 && System.currentTimeMillis() - pausedTime > 30000){
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            this.finish();
        }
    }

}
