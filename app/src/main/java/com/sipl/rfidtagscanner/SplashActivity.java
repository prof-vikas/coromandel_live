package com.sipl.rfidtagscanner;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;


public class SplashActivity extends AppCompatActivity {
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            Intent id = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(id);
            finish();
            progressBar.setVisibility(View.GONE);
        }, 2000);

    }
}