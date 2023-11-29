package com.sipl.rfidtagscanner;

import static android.view.View.GONE;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sipl.rfidtagscanner.dto.response.AndroidApiResponse;
import com.sipl.rfidtagscanner.utils.ApkDownloader;
import com.sipl.rfidtagscanner.utils.CustomErrorMessage;
import com.sipl.rfidtagscanner.utils.NetworkManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 200;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        progressBar = findViewById(R.id.progressBar);

        NetworkManager networkManager = NetworkManager.getInstance(this);
        if (networkManager.isConnected()) {
            checkForUpdate();
        } else {
            errorAlert(SplashActivity.this, "No internet connection, Please connect to internet and try again");
        }
    }

    private void checkForUpdate() {
        progressBar.setVisibility(View.VISIBLE);
        String appId = getString(R.string.app_id);
        String appVersion = getString(R.string.login_app_version);
        Call<AndroidApiResponse> call = RetrofitController.getInstances(this).getLoadingAdviseApi().getAppVersion(appId, appVersion);
        call.enqueue(new Callback<AndroidApiResponse>() {
            @Override
            public void onResponse(Call<AndroidApiResponse> call, Response<AndroidApiResponse> response) {
                if (response.body() != null && response.body().getStatus() != null && response.body().getMessage() != null && response.body().getVersionChanged() != null) {
                    if (response.body().getVersionChanged()) {
                        progressBar.setVisibility(GONE);
                        checkForStoragePermission();
                        updateDialogPrompt(response.body().getMessage(), response.body().getUrl(), response.body().getNewVersion());
                    } else {
                        new Handler().postDelayed(() -> {
                            progressBar.setVisibility(GONE);
                            Intent id = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(id);
                            finish();
                        }, 1000);
                    }
                }
            }

            @Override
            public void onFailure(Call<AndroidApiResponse> call, Throwable t) {
                progressBar.setVisibility(GONE);
                errorAlert(SplashActivity.this, CustomErrorMessage.setErrorMessage(t.getMessage()));
            }
        });
    }


    private void updateDialogPrompt(String message, String url, String newVersion) {
        Dialog dialog = new Dialog(SplashActivity.this);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog_update);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView dialogMessageTxt = dialog.findViewById(R.id.dialog_update_message);
        TextView txtDownloading = dialog.findViewById(R.id.dialog_update_txt_downloading);
        Button btnUpdate = dialog.findViewById(R.id.dialog_update_btn_update);
        Button btnCancel = dialog.findViewById(R.id.dialog_update_btn_cancel);
        ProgressBar progressBar1 = dialog.findViewById(R.id.dialog_update_progress);
        View view1 = dialog.findViewById(R.id.dialog_update_view);
        dialogMessageTxt.setText(message);

        btnUpdate.setOnClickListener(view -> {
            showProgressError(progressBar1, view1, btnUpdate, btnCancel);
            String txtMsg = "Downloading started ...";
            txtDownloading.setText(txtMsg);
            txtDownloading.setVisibility(View.VISIBLE);
            String updateVersion = "rmms v." + newVersion;
            ApkDownloader.downloadApk(
                    SplashActivity.this,
                    url,
                    updateVersion,
                    "Coromandel International Limited",
                    (filePath, status) -> {
                        hideProgressError(progressBar1, view1, btnUpdate, btnCancel);
                        String txt = getDownloadFileStatus(status);
                        txtDownloading.setText(txt);
                    }
            );
        });

        btnCancel.setOnClickListener(view -> {
            dialog.dismiss();
            errorAlert(SplashActivity.this, "Are you sure to close the app");
        });
        dialog.show();
    }

    private String getDownloadFileStatus(int statusCode) {
        switch (statusCode) {
            case 8:
                return "RMMS app downloaded successfully";
            case 16:
                return "Download failed!";
            case 4:
                return "Download paused!";
            case 1:
                return "Download waiting!";
            case 2:
                return "Downloading!";
            default:
                return "Something went wrong";
        }
    }

    private void errorAlert(Context context, String message) {
        Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog_retry);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView dialogMessageTxt = dialog.findViewById(R.id.dialog_error_msg);
        Button btnRetry = dialog.findViewById(R.id.dialog_error_btn_retry);
        Button btnCloseApp = dialog.findViewById(R.id.dialog_error_btn_close_app);
        dialogMessageTxt.setText(message);
        if (message.equalsIgnoreCase("Are you sure to close the app")) {
            String no = "NO";
            btnRetry.setText(no);
        }

        btnRetry.setOnClickListener(view -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
                checkForUpdate();
            }
        });

        btnCloseApp.setOnClickListener(view -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }

    private void showProgressError(ProgressBar p, View v, Button btnUpdate, Button btnCancel) {
        p.setVisibility(View.VISIBLE);
        v.setVisibility(View.VISIBLE);
        btnUpdate.setEnabled(false);
        btnCancel.setEnabled(false);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideProgressError(ProgressBar p, View v, Button btnUpdate, Button btnCancel) {
        p.setVisibility(View.GONE);
        v.setVisibility(View.GONE);
        btnUpdate.setEnabled(true);
        btnCancel.setEnabled(true);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void checkForStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(this)
                        .setTitle("Permission Needed")
                        .setMessage("This app needs storage permission to save downloaded files.")
                        .setPositiveButton("OK", (dialog, which) -> requestStoragePermission())
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
            } else {
                requestStoragePermission();
            }
        } else {
            Log.e(TAG, "checkForStoragePermission: storage permission already granted");
        }
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Storage permission denied. Please allow storage permission", Toast.LENGTH_SHORT).show();
                checkForStoragePermission();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}