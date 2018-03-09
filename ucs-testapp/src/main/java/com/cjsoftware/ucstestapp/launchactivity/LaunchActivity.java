package com.cjsoftware.ucstestapp.launchactivity;

import com.cjsoftware.ucstestapp.R;
import com.cjsoftware.ucstestapp.ucsactivity.impl.UcsUiActivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launchactivity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent demo = new Intent(this, UcsUiActivity.class);
        startActivity(demo);
    }
}
