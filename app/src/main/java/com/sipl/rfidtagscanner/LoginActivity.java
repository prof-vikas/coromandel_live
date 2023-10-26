package com.sipl.rfidtagscanner;

import static com.sipl.rfidtagscanner.utils.Config.BTN_OK;
import static com.sipl.rfidtagscanner.utils.Config.DIALOG_ERROR;
import static com.sipl.rfidtagscanner.utils.Config.DIALOG_SUCCESS;
import static com.sipl.rfidtagscanner.utils.Config.DIALOG_WARNING;
import static com.sipl.rfidtagscanner.utils.Config.RESPONSE_FORBIDDEN;
import static com.sipl.rfidtagscanner.utils.Config.RESPONSE_FOUND;
import static com.sipl.rfidtagscanner.utils.Config.RESPONSE_OK;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_ADMIN_PLANT;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_ADMIN_SUPER;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_BWH;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_B_LAO;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_CWH;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_C_LAO;

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
import com.sipl.rfidtagscanner.dto.dtos.GenericData;
import com.sipl.rfidtagscanner.dto.dtos.GenericIntegerData;
import com.sipl.rfidtagscanner.dto.dtos.UserPermissionsResponseDto;
import com.sipl.rfidtagscanner.dto.request.JwtRequest;
import com.sipl.rfidtagscanner.dto.response.GenericeApiResponse;
import com.sipl.rfidtagscanner.dto.response.JwtAuthResponse;
import com.sipl.rfidtagscanner.utils.CustomErrorMessage;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "TestingArea2";
    private ProgressBar progressBar;
    private EditText edtUsername, edtPassword;
    private View colorOverlay;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btn_login);
        edtUsername = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);
        colorOverlay = findViewById(R.id.view_pre_gate);
        progressBar = findViewById(R.id.login_progressBar);
        ImageView imageView = findViewById(R.id.img_view_show_hide_password);
        MaterialCheckBox checkBoxRememberMe = findViewById(R.id.checkbox_login_remember_me);
        isCheckBoxChecked();

        btnLogin.setOnClickListener(view -> {
            if (validateEditText()) {
                showProgress();
                processLogin();
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
            }
        }
    }

    private boolean validateEditText() {
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

    private void showProgress() {
        btnLogin.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        colorOverlay.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideProgress() {
        btnLogin.setEnabled(true);
        progressBar.setVisibility(View.GONE);
        colorOverlay.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void processLogin() {
        JwtRequest jwtRequest = new JwtRequest(edtUsername.getText().toString(), edtPassword.getText().toString());
        Call<JwtAuthResponse> call = RetrofitController.getInstances(this).getLoadingAdviseApi().login(jwtRequest);
        Log.i(TAG, new Gson().toJson(jwtRequest));
        call.enqueue(new Callback<JwtAuthResponse>() {
            @Override
            public void onResponse(Call<JwtAuthResponse> call, Response<JwtAuthResponse> response) {
                Log.d(TAG, "onResponse: processLogin() : Raw : " + response.raw());
                hideProgress();
             /*   if (!response.isSuccessful()) {
                    alert(LoginActivity.this, DIALOG_ERROR, response.errorBody().toString(), null, BTN_OK);
                    return;
                }*/
                if (response.body() != null) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase(RESPONSE_OK)) {
                        String token = response.body().getToken() != null ? response.body().getToken() : null;
                        if (token != null) {
                            getUserDetailsWithAllPermission(token);
                        } else {
                            alert(LoginActivity.this, DIALOG_ERROR, "An error occurs when attempting to login with this user", null, BTN_OK);
                        }
                    } else {
                        alert(LoginActivity.this, DIALOG_ERROR, response.body() != null && response.body().getMessage() != null ? response.body().getMessage() : "An error occurs when attempting to log in with this user", null, BTN_OK);
                    }
                }else {
                    alert(LoginActivity.this, DIALOG_ERROR, "Server is down", null, BTN_OK);
                }
            }

            @Override
            public void onFailure(Call<JwtAuthResponse> call, Throwable t) {
                hideProgress();
                alert(LoginActivity.this, DIALOG_ERROR, CustomErrorMessage.setErrorMessage(t.getMessage()), null, BTN_OK);
            }
        });
    }

    private void getUserDetailsWithAllPermission(String token) {
        showProgress();
        Call<GenericeApiResponse> call = RetrofitController.getInstances(this).getLoadingAdviseApi().getLoginUserDetail("Bearer " + token, edtUsername.getText().toString().trim());
        call.enqueue(new Callback<GenericeApiResponse>() {
            @Override
            public void onResponse(Call<GenericeApiResponse> call, Response<GenericeApiResponse> response) {
                Log.e(TAG, "onResponse: response.raw : UserMaster : Mapping : " + response.raw());
                hideProgress();
                if (!response.isSuccessful()) {
                    alert(LoginActivity.this, DIALOG_ERROR, response.errorBody().toString(), null, BTN_OK);
                }

                if (response.body() != null && response.body().getStatus() != null) {
                    if (response.body().getStatus().equalsIgnoreCase(RESPONSE_FOUND) && response.body().getResponse() != null) {
                        UserPermissionsResponseDto userPermissionsResponseDto = response.body().getResponse();
                        String userId = userPermissionsResponseDto.getUserMasterId().toString();
                        String username = userPermissionsResponseDto.getUserId();
                        String userRoleId = userPermissionsResponseDto.getRoleId().toString();
                        String roleName = userPermissionsResponseDto.getRoleName();
                        String plantCode = userPermissionsResponseDto.getPlantCode();

                        if (username == null || roleName == null || plantCode == null) {
                            alert(LoginActivity.this, DIALOG_ERROR, "Something went wrong with is user", "some key field value is missing \ncontact with admin...", BTN_OK);
                            return;
                        }

                        Boolean isBerthAssign = userPermissionsResponseDto.getBerth() != null ? userPermissionsResponseDto.getBerth() : false;
                        List<GenericIntegerData> berthList = userPermissionsResponseDto.getAllBerth();

                        Boolean isSourceLocationAssign = userPermissionsResponseDto.getSourceLocation() != null ? userPermissionsResponseDto.getSourceLocation() : false;
                        List<GenericData> sourceLocationList = userPermissionsResponseDto.getSourceLocationDto();

                        Boolean isDestinationLocationAssign = userPermissionsResponseDto.getDestinationLocation() != null ? userPermissionsResponseDto.getDestinationLocation() : false;
                        List<GenericData> destinationLocationList = userPermissionsResponseDto.getDestinationLocationDto();

                        Gson gson = new Gson();
                        String strBerthList = berthList != null ? gson.toJson(berthList) : null;
                        String strSourceLocationList = sourceLocationList != null ? gson.toJson(sourceLocationList) : null;
                        String strDestinationList = destinationLocationList != null ? gson.toJson(destinationLocationList) : null;

                        if (userRoleId.equalsIgnoreCase(ROLES_C_LAO) || userRoleId.equalsIgnoreCase(ROLES_CWH) || userRoleId.equalsIgnoreCase(ROLES_BWH) || userRoleId.equalsIgnoreCase(ROLES_ADMIN_PLANT) || userRoleId.equalsIgnoreCase(ROLES_ADMIN_SUPER) || userRoleId.equalsIgnoreCase(ROLES_B_LAO)) {
                            saveLoginUserDetails(userId, username, userRoleId, roleName, plantCode, isBerthAssign, strBerthList, isSourceLocationAssign, strSourceLocationList, isDestinationLocationAssign, strDestinationList, token);
                        } else {
                            alert(LoginActivity.this, DIALOG_ERROR, "User role not allowed", null, BTN_OK);
                        }
                    } else if (response.body().getStatus().equalsIgnoreCase(RESPONSE_FORBIDDEN)) {
                        alert(LoginActivity.this, DIALOG_WARNING, response.body().getMessage(), null, BTN_OK);
                    } else {
                        alert(LoginActivity.this, DIALOG_ERROR, response.body().getMessage(), null, BTN_OK);
                    }
                }
            }

            @Override
            public void onFailure(Call<GenericeApiResponse> call, Throwable t) {
                hideProgress();
                alert(LoginActivity.this, DIALOG_ERROR, CustomErrorMessage.setErrorMessage(t.getMessage()), null, BTN_OK);
            }
        });
    }

    private void saveLoginUserDetails(String userId, String userName, String roleId, String roleName, String plantCode, Boolean isBerthAssign, String berthDtoList, Boolean sourceLocationIsAssign, String sourceLocationDtoList, Boolean isDestinationLocationIsAssign, String destinationLocationDtoList, String token) {
        SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("userIdSPK", userId).apply();
        editor.putString("userNameSPK", userName).apply();
        editor.putString("roleIdSPK", roleId).apply();
        editor.putString("roleNameSPK", roleName).apply();
        editor.putString("plantCodeSPK", plantCode).apply();
        editor.putBoolean("isBerthAssignSPK", isBerthAssign).apply();
        editor.putString("berthDtoListSPK", berthDtoList).apply();
        editor.putBoolean("sourceLocationIsAssignSPK", sourceLocationIsAssign).apply();
        editor.putString("sourceLocationDtoListSPK", sourceLocationDtoList).apply();
        editor.putBoolean("isDestinationLocationIsAssignSPK", isDestinationLocationIsAssign).apply();
        editor.putString("destinationLocationDtoListSPK", destinationLocationDtoList).apply();
        editor.putString("tokenSPK", token).apply();
        editor.putString("userLoginStatus", "login").apply();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void alert(Context context, String dialogType, String dialogTitle, String dialogMessage, String dialogBtnText) {
        Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.custom_alert_dialog_box);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView error = dialog.findViewById(R.id.dialog_type_error);
        TextView success = dialog.findViewById(R.id.dialog_type_success);
        TextView warning = dialog.findViewById(R.id.dialog_type_warning);

        if (dialogType.equalsIgnoreCase(DIALOG_ERROR)) {
            error.setVisibility(View.VISIBLE);
            success.setVisibility(View.GONE);
            warning.setVisibility(View.GONE);
        } else if (dialogType.equalsIgnoreCase(DIALOG_SUCCESS)) {
            error.setVisibility(View.GONE);
            success.setVisibility(View.VISIBLE);
            warning.setVisibility(View.GONE);
        } else if (dialogType.equalsIgnoreCase(DIALOG_WARNING)) {
            error.setVisibility(View.GONE);
            success.setVisibility(View.GONE);
            warning.setVisibility(View.VISIBLE);
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
}
