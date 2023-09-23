package com.sipl.rfidtagscanner;

import static com.sipl.rfidtagscanner.utils.Config.BTN_OK;
import static com.sipl.rfidtagscanner.utils.Config.DIALOG_ERROR;
import static com.sipl.rfidtagscanner.utils.Config.DIALOG_SUCCESS;
import static com.sipl.rfidtagscanner.utils.Config.RESPONSE_FOUND;
import static com.sipl.rfidtagscanner.utils.Config.RESPONSE_OK;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_ADMIN_PLANT;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_BWH;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_CWH;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_LAO;
import static com.sipl.rfidtagscanner.utils.Config.isJWTEnable;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.gson.Gson;
import com.sipl.rfidtagscanner.dto.dtos.UserMasterDto;
import com.sipl.rfidtagscanner.dto.request.JwtRequest;
import com.sipl.rfidtagscanner.dto.response.JwtAuthResponse;
import com.sipl.rfidtagscanner.dto.response.UserValidateResponseDto;
import com.sipl.rfidtagscanner.utils.CustomErrorMessage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "TestingArea2";
    private ProgressBar progressBar;
    private EditText edtUsername, edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnLogin = findViewById(R.id.btn_login);
        edtUsername = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);
        progressBar = findViewById(R.id.login_progressBar);
        ImageView imageView = findViewById(R.id.img_view_show_hide_password);

        MaterialCheckBox checkBoxRememberMe = findViewById(R.id.checkbox_login_remember_me);
        isCheckBoxChecked();

        btnLogin.setOnClickListener(view -> {
            if (validateInput()) {
                logIn();
            }
        });

        checkBoxRememberMe.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                SharedPreferences sp = getSharedPreferences("rememberMe", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("remember", "true").apply();
            } else if (!compoundButton.isChecked()) {
                SharedPreferences sp = getSharedPreferences("rememberMe", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("remember", "false").apply();
            }
        });

        imageView.setOnClickListener(view -> {
            ImageView showHideImageView = (ImageView) view;
            if (edtPassword.getTransformationMethod() instanceof PasswordTransformationMethod) {
                showHideImageView.setImageResource(R.drawable.baseline_show_password_24);
                edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                showHideImageView.setImageResource(R.drawable.baseline_visibility_off_24);
                edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            edtPassword.setSelection(edtPassword.getText().length());
        });
    }


    public void isCheckBoxChecked() {
        SharedPreferences sp = getSharedPreferences("rememberMe", MODE_PRIVATE);
        SharedPreferences loginCredentials = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        String checkBox = sp.getString("remember", "");
        String loginStatus = loginCredentials.getString("userLoginStatus", "");
        if (loginStatus.equals("login")) {
            if (checkBox.equals("true")) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else if (checkBox.equals("false")) {
            }
        }
    }

    private boolean validateInput() {
        if (edtUsername.length() == 0) {
            edtUsername.setError("This field is required");
            return false;
        }
        if (edtPassword.length() == 0) {
            edtPassword.setError("This field is required");
            return false;
        }
        return true;
    }

    private void logIn() {
        showProgress();
        if (!isJWTEnable) {
            validateUser();
        } else {
            JwtRequest jwtRequest = new JwtRequest(edtUsername.getText().toString().trim(), edtPassword.getText().toString().trim());
            Call<JwtAuthResponse> call = RetrofitController.getInstances(this).getLoadingAdviseApi().login(jwtRequest);
            Log.i(TAG, new Gson().toJson(jwtRequest));
            call.enqueue(new Callback<JwtAuthResponse>() {
                @Override
                public void onResponse(Call<JwtAuthResponse> call, Response<JwtAuthResponse> response) {
                    Log.i(TAG, "onResponse: SignIn URL : " + response.raw());
                    if (!response.isSuccessful()) {
                        hideProgress();
                        alert(LoginActivity.this, DIALOG_ERROR, response.errorBody().toString(), null, BTN_OK);
                    }
                    if (response.body().getStatus().equalsIgnoreCase(RESPONSE_OK)) {
                        String token = response.body().getToken();
                        SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("userIDSPK", token).apply();
                        getUserDetails(token);
                    } else {
                        alert(LoginActivity.this, DIALOG_ERROR, response.body().getMessage(), null, BTN_OK);
                    }
                }

                @Override
                public void onFailure(Call<JwtAuthResponse> call, Throwable t) {
                    hideProgress();
                    alert(LoginActivity.this, DIALOG_ERROR, CustomErrorMessage.setErrorMessage(t.getMessage()), null, BTN_OK);
                }
            });
        }
    }

    private void savingLoginUserToSharedPref(String userID, String username, String token, String userSourceLocation, String userSourceLocationDesc, String userPlantLocation, String userPlantLocationDesc, String userRolesId) {
        SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("tokenSPK", token).apply();
        editor.putString("usernameSPK", username).apply();
        editor.putString("userIDSPK", userID).apply();
        editor.putString("userRolesIdSPK", userRolesId).apply();
        editor.putString("UserSourceLocationSPK", userSourceLocation).apply();
        editor.putString("UserSourceLocationDescSPK", userSourceLocationDesc).apply();
        editor.putString("userPlantLocationSPK", userPlantLocation).apply();
        editor.putString("userPlantLocationDescSPK", userPlantLocationDesc).apply();
        editor.putString("userLoginStatus", "login").apply();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void alert(Context context, String dialogType, String dialogTitle, String dialogMessage, String dialogBtnText) {
        Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.custom_alert_dialog_box);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView error = dialog.findViewById(R.id.dialog_type_error);
        TextView success = dialog.findViewById(R.id.dialog_type_success);
        if (dialogType.equalsIgnoreCase(DIALOG_ERROR)) {
            error.setVisibility(View.VISIBLE);
            success.setVisibility(View.GONE);
        } else if (dialogType.equalsIgnoreCase(DIALOG_SUCCESS)) {
            error.setVisibility(View.GONE);
            success.setVisibility(View.VISIBLE);
        }
        TextView dialogMessageTxt = dialog.findViewById(R.id.text_msg2);
        if (dialogMessage == null) {
            dialogMessageTxt.setVisibility(View.GONE);
        }
        TextView dialogTitleTxt = dialog.findViewById(R.id.text_msg);
        TextView btn = dialog.findViewById(R.id.text_btn);
        dialogTitleTxt.setText(dialogTitle);
        dialogMessageTxt.setText(dialogMessage);
        btn.setText(dialogBtnText);
        btn.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    private void validateUser() {
        Call<UserValidateResponseDto> call = RetrofitController.getInstances(this).getLoadingAdviseApi().loginWithOutJwt(edtUsername.getText().toString().trim(), edtPassword.getText().toString().trim());
        call.enqueue(new Callback<UserValidateResponseDto>() {
            @Override
            public void onResponse(Call<UserValidateResponseDto> call, Response<UserValidateResponseDto> response) {
                hideProgress();
                if (!response.isSuccessful()) {
                    alert(LoginActivity.this, DIALOG_ERROR, response.errorBody().toString(), null, BTN_OK);
                }
                if (response.body().getStatus().equalsIgnoreCase(RESPONSE_OK)) {
                    String token = "demoTokenUserFromPassingOnly";
                    String userID = response.body().getUserDto().getUserId();
                    String id = String.valueOf(response.body().getUserDto().getId());
                    String userRoleId = String.valueOf(response.body().getUserDto().getRole().getId());
                    String sourceLocationCode = response.body().getUserDto().getStorageLocation().getStrLocationCode();
                    String sourceLocationCodeDesc = response.body().getUserDto().getStorageLocation().getStrLocationDesc();
                    String plantLocationCode = response.body().getUserDto().getPlantMaster().getPlantCode();
                    String plantLocationCodeDesc = response.body().getUserDto().getPlantMaster().getPlantDesc();
                    if (userRoleId.equalsIgnoreCase(ROLES_LAO) ||
                            userRoleId.equalsIgnoreCase(ROLES_CWH) ||
                            userRoleId.equalsIgnoreCase(ROLES_BWH) ||
                            userRoleId.equalsIgnoreCase(ROLES_ADMIN_PLANT)) {
                        savingLoginUserToSharedPref(id, userID, token, sourceLocationCode, sourceLocationCodeDesc, plantLocationCode, plantLocationCodeDesc, userRoleId);
                    } else {
                        alert(LoginActivity.this, DIALOG_ERROR, "Insufficient permissions for user's current role", null, BTN_OK);
                    }
                } else {
                    alert(LoginActivity.this, DIALOG_ERROR, response.body().getMessage(), null, BTN_OK);
                }
            }

            @Override
            public void onFailure(Call<UserValidateResponseDto> call, Throwable t) {
                hideProgress();
                alert(LoginActivity.this, DIALOG_ERROR, CustomErrorMessage.setErrorMessage(t.getMessage()), null, BTN_OK);
            }
        });
    }

    private void getUserDetails(String token) {
        Call<UserValidateResponseDto> call = RetrofitController.getInstances(this).getLoadingAdviseApi().getLoginUserDetails("Bearer " + token, edtUsername.getText().toString().trim());
        call.enqueue(new Callback<UserValidateResponseDto>() {
            @Override
            public void onResponse(Call<UserValidateResponseDto> call, Response<UserValidateResponseDto> response) {
                hideProgress();
                if (!response.isSuccessful()) {
                    alert(LoginActivity.this, DIALOG_ERROR, response.errorBody().toString(), null, BTN_OK);
                }
                try {
                    if (response.body() != null && response.body().getUserDto() != null) {
                        UserMasterDto userMasterDto = response.body().getUserDto();
                        if (response.body().getStatus().equalsIgnoreCase(RESPONSE_FOUND)) {
                            String username = userMasterDto.getName();
                            String userID = String.valueOf(userMasterDto.getId());
                            String userSourceLocation = userMasterDto.getStorageLocation().getStrLocationCode();
                            String userSourceLocationDesc = userMasterDto.getStorageLocation().getStrLocationDesc();
                            String userPlantLocation = userMasterDto.getPlantMaster().getPlantCode();
                            String userPlantLocationDesc = userMasterDto.getPlantMaster().getPlantDesc();
                            String userRoleId = String.valueOf(userMasterDto.getRole().getId());
                            if (token != null && username != null && userSourceLocation != null && userPlantLocation != null && userSourceLocationDesc != null && userPlantLocationDesc != null) {
                                savingLoginUserToSharedPref(userID, username, token, userSourceLocation, userSourceLocationDesc, userPlantLocation, userPlantLocationDesc, userRoleId);
                            } else {
                                alert(LoginActivity.this, DIALOG_ERROR, "Something went wrong with this user credentials", "Try login with other user credentials", BTN_OK);
                            }
                        } else {
                            alert(LoginActivity.this, DIALOG_ERROR, response.body().getMessage(), null, BTN_OK);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "getUserDetails:  Exception in getUserDetails : " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<UserValidateResponseDto> call, Throwable t) {
                showProgress();
                alert(LoginActivity.this, DIALOG_ERROR, CustomErrorMessage.setErrorMessage(t.getMessage()), null, BTN_OK);
            }
        });
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

}