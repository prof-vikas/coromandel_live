package com.sipl.rfidtagscanner;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sipl.rfidtagscanner.dto.response.AndroidApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressBar = findViewById(R.id.progressBar);

        checkOfUpdate();

        progressBar.setVisibility(View.VISIBLE);
     /*   new Handler().postDelayed(() -> {
            Intent id = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(id);
            finish();
            progressBar.setVisibility(View.GONE);
        }, 2000);*/

    }

    private void checkOfUpdate() {
        String appId = getString(R.string.app_id);
        String appVersion = getString(R.string.login_app_version);
//        String appVersion = "1.0.0";
        Call<AndroidApiResponse> call = RetrofitController.getInstances(this).getLoadingAdviseApi().getAppVersion(appId, appVersion);
        call.enqueue(new Callback<AndroidApiResponse>() {
            @Override
            public void onResponse(Call<AndroidApiResponse> call, Response<AndroidApiResponse> response) {
                if (response.body() != null && response.body().getStatus() != null && response.body().getMessage() != null && response.body().getVersionChanged() != null) {
                    if (response.body().getVersionChanged()){
                        progressBar.setVisibility(View.GONE);
                        updateDialogPrompt(response.body().getMessage(), response.body().getUrl());
                    }else {
                        new Handler().postDelayed(() -> {
                            progressBar.setVisibility(View.GONE);
                            Intent id = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(id);
                            finish();
                        }, 1000);
                    }
                }
            }

            @Override
            public void onFailure(Call<AndroidApiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "onFailure: " +t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void updateDialogPrompt(String message, String url) {
        Dialog dialog = new Dialog(SplashActivity.this);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog_update);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        TextView dialogMessageTxt = dialog.findViewById(R.id.dialog_update_message);
        Button btnUpdate = dialog.findViewById(R.id.dialog_update_btn_update);
        TextView btnCancel = dialog.findViewById(R.id.dialog_update_btn_cancel);

        dialogMessageTxt.setText(message);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        dialog.show();

    }
}