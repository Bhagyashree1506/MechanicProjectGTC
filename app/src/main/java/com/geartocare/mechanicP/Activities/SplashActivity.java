package com.geartocare.mechanicP.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.geartocare.mechanicP.R;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 800;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setStatusBarColor(ContextCompat.getColor(SplashActivity.this,R.color.yelight));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this,LoginActivity.class ));
                finish();
            }
        },SPLASH_TIME_OUT);



    }
}