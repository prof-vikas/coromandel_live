package com.sipl.rfidtagscanner;

import static com.sipl.rfidtagscanner.utils.Config.DIALOG_ERROR;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "TestingArea2";
    private ProgressBar progressBar;
    private EditText edtUsername, edtPassword;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnLogin = findViewById(R.id.btn_login);
        edtUsername = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);
        progressBar = findViewById(R.id.login_progressBar);
        imageView = findViewById(R.id.img_view_show_hide_password);

        MaterialCheckBox checkBoxRememberMe = findViewById(R.id.checkbox_login_remember_me);

        isCheckBoxChecked();

       /* String logout = isLogout();
        if (getLoginUseriiiiId() != 0) {

            if (logout != null) {
                Log.i(TAG, "onCreate: in logout if ");
                if (logout.equalsIgnoreCase("logout")) {
                    Log.i(TAG, "onCreate:  in logout if if");
                    logoutApi();
                }
            }
        }*/


        btnLogin.setOnClickListener(view -> {
            if (validateEditText()) {
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
            } else if (checkBox.equals("false")) {
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

    private void processLogin() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        if (!isJWTEnable) {
            validateUser();
        } else {
            showProgress();
            JwtRequest jwtRequest = new JwtRequest(username, password);
            Call<JwtAuthResponse> call = RetrofitController.getInstances(this).getLoadingAdviseApi().login(jwtRequest);
            Log.i(TAG, new Gson().toJson(jwtRequest));
            call.enqueue(new Callback<JwtAuthResponse>() {
                @Override
                public void onResponse(Call<JwtAuthResponse> call, Response<JwtAuthResponse> response) {
                    Log.i(TAG, "onResponse: " + response.raw());
                    hideProgress();
                    if (!response.isSuccessful()) {
                        alert(LoginActivity.this, DIALOG_ERROR, response.errorBody().toString(), null, "OK");
                    }
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equalsIgnoreCase("OK")) {
                            String token = response.body().getToken();
                            SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("userIDSPK", token).apply();
                            getUserDetails(token);
                        } else {
                            Log.i(TAG, "onResponse: Status : " + response.body().getStatus() + "\nMessage : " + response.body().getMessage());
                            alert(LoginActivity.this, DIALOG_ERROR, response.body().getMessage(), null, "OK");
                        }
                    }
                }

                @Override
                public void onFailure(Call<JwtAuthResponse> call, Throwable t) {
                    hideProgress();
                    t.printStackTrace();
                    alert(LoginActivity.this, "error", t.getMessage(), null, "OK");
                }
            });
        }
    }

    private void savingLoginUserToSharedPref(String userID, String username, String role, String token, String userSourceLocation, String userSourceLocationDesc, String userPlantLocation, String userPlantLocationDesc, String userRolesId) {
        SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("userIDSPK", userID).apply();
        editor.putString("usernameSPK", username).apply();
        editor.putString("roleSPK", role).apply();
        editor.putString("tokenSPK", token).apply();
        editor.putString("UserSourceLocationSPK", userSourceLocation).apply();
        editor.putString("UserSourceLocationDescSPK", userSourceLocationDesc).apply();
        editor.putString("userPlantLocationSPK", userPlantLocation).apply();
        editor.putString("userPlantLocationDescSPK", userPlantLocationDesc).apply();
        editor.putString("userLoginStatus", "login").apply();
        editor.putString("userRolesIdSPK", userRolesId).apply();
        Log.i(TAG, "savingLoginUserToSharedPref: userRolesId : " + userRolesId);
        editor.apply();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void SavedId(int id) {
        SharedPreferences sp1 = getSharedPreferences("saveId", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp1.edit();
        editor.putInt("saveUserId", id).apply();
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
        if (dialogType.equalsIgnoreCase("error")) {
            error.setVisibility(View.VISIBLE);
            success.setVisibility(View.GONE);
        } else if (dialogType.equalsIgnoreCase("success")) {
            error.setVisibility(View.GONE);
            success.setVisibility(View.VISIBLE);
        } else {
            Log.i(TAG, "alertBuilder3: Wrong parameter pass in dialogType");
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
        Log.i(TAG, "validateUser: in validateUser()");
        showProgress();
        Call<UserValidateResponseDto> call = RetrofitController.getInstances(this).getLoadingAdviseApi().loginWithOutJwt(edtUsername.getText().toString().trim(), edtPassword.getText().toString().trim());
        call.enqueue(new Callback<UserValidateResponseDto>() {
            @Override
            public void onResponse(Call<UserValidateResponseDto> call, Response<UserValidateResponseDto> response) {
               hideProgress();
                if (!response.isSuccessful()) {
                    alert(LoginActivity.this, "ERROR", response.errorBody().toString(), null, "OK");
                }
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("OK")) {
                        String token = "emlfkemdflkeneknekjdfnekjgcnekjgen";
                        String userID = response.body().getUserDto().getUserId();
                        String id = String.valueOf(response.body().getUserDto().getId());
                        String userRole = response.body().getUserDto().getRole().getName();
                        String userRoleId = String.valueOf(response.body().getUserDto().getRole().getId());
                        String sourceLocationCode = response.body().getUserDto().getStorageLocation().getStrLocationCode();
                        String sourceLocationCodeDesc = response.body().getUserDto().getStorageLocation().getStrLocationDesc();
                        String plantLocationCode = response.body().getUserDto().getPlantMaster().getPlantCode();
                        String plantLocationCodeDesc = response.body().getUserDto().getPlantMaster().getPlantDesc();
                        Log.i(TAG, "onResponse: " + response.raw());
                        if (userRoleId.equalsIgnoreCase(ROLES_LAO)) {
                            savingLoginUserToSharedPref(id, userID, userRole, token, sourceLocationCode, sourceLocationCodeDesc, plantLocationCode, plantLocationCodeDesc, userRoleId);
                        } else if (userRoleId.equalsIgnoreCase(ROLES_CWH)) {
                            savingLoginUserToSharedPref(id, userID, userRole, token, sourceLocationCode, sourceLocationCodeDesc, plantLocationCode, plantLocationCodeDesc, userRoleId);
                        } else if (userRoleId.equalsIgnoreCase(ROLES_BWH)) {
                            savingLoginUserToSharedPref(id, userID, userRole, token, sourceLocationCode, sourceLocationCodeDesc, plantLocationCode, plantLocationCodeDesc, userRoleId);
                        } else if (userRoleId.equalsIgnoreCase(ROLES_ADMIN_PLANT)) {
                            savingLoginUserToSharedPref(id, userID, userRole, token, sourceLocationCode, sourceLocationCodeDesc, plantLocationCode, plantLocationCodeDesc, userRoleId);
                        } else {
                            alert(LoginActivity.this, "ERROR", "User role not allowed", null, "OK");
                        }
                    } else {
                        alert(LoginActivity.this, "ERROR", response.body().getMessage(), null, "OK");
                    }
                }
            }

            @Override
            public void onFailure(Call<UserValidateResponseDto> call, Throwable t) {
                hideProgress();
                alert(LoginActivity.this, "ERROR", t.getMessage().toString(), null, "OK");
                t.printStackTrace();
            }
        });
    }

    private void getUserDetails(String token) {
        showProgress();
        Call<UserValidateResponseDto> call = RetrofitController.getInstances(this).getLoadingAdviseApi().getLoginUserDetails("Bearer " + token, edtUsername.getText().toString().trim());
        call.enqueue(new Callback<UserValidateResponseDto>() {
            @Override
            public void onResponse(Call<UserValidateResponseDto> call, Response<UserValidateResponseDto> response) {
                Log.i(TAG, "onResponse: " + response.raw());
                hideProgress();
                if (!response.isSuccessful()) {
                    alert(LoginActivity.this, DIALOG_ERROR, response.errorBody().toString(), null, "OK");
                }
                if (response.isSuccessful()) {
                    Log.i(TAG, "onResponse: response.raw : " + response.raw());
                    if (response.body().getStatus().equalsIgnoreCase("FOUND")) {
                        progressBar.setVisibility(View.GONE);
                        String role = response.body().getUserDto().getName();
                        String username = response.body().getUserDto().getName();
                        String userID = String.valueOf(response.body().getUserDto().getId());
                        String userSourceLocation = response.body().getUserDto().getStorageLocation().getStrLocationCode();
                        String userSourceLocationDesc = response.body().getUserDto().getStorageLocation().getStrLocationDesc();
                        String userPlantLocation = response.body().getUserDto().getPlantMaster().getPlantCode();
                        String userPlantLocationDesc = response.body().getUserDto().getPlantMaster().getPlantDesc();
                        String userRoleId = String.valueOf(response.body().getUserDto().getRole().getId());
                        SavedId(response.body().getUserDto().getId());
                        if (token != null && role != null && username != null && userSourceLocation != null && userPlantLocation != null && userSourceLocationDesc != null && userPlantLocationDesc != null) {
                            Log.i(TAG, "onResponse: username : " + username + " ");
                            savingLoginUserToSharedPref(userID, username, role, token, userSourceLocation, userSourceLocationDesc, userPlantLocation, userPlantLocationDesc, userRoleId);
                        } else {
                            alert(LoginActivity.this, DIALOG_ERROR, "Something went wrong with this user credentials", "Try login with other user credentials", "OK");
                        }
                    } else {
                        alert(LoginActivity.this, DIALOG_ERROR, response.body().getMessage(), null, "OK");
                    }
                }
            }

            @Override
            public void onFailure(Call<UserValidateResponseDto> call, Throwable t) {
                showProgress();
                t.printStackTrace();
                alert(LoginActivity.this, "error", t.getMessage(), null, "OK");
            }
        });
    }


    public int getLoginUserId() {
        SharedPreferences sp = getSharedPreferences("saveId", MODE_PRIVATE);
        int a = sp.getInt("saveUserId", 0);
        return a;
    }

    private String isLogout() {
        Log.i(TAG, "isLogout:  in logout ");
        SharedPreferences sp = getSharedPreferences("logoutMark", MODE_PRIVATE);
        Log.i(TAG, "isLogout: " + sp.getString("isLogout", null));
        return sp.getString("isLogout", null);
    }


    private void logoutApi() {
        Log.i(TAG, "validateUser: in validateUser()");
//        progressBar.setVisibility(View.VISIBLE);
        Log.i(TAG, "logoutApi: " + getLoginUserId());
        UserMasterDto userMasterDto = new UserMasterDto(getLoginUserId());
        Log.i(TAG, "updateBothraLoadingAdviseDto : Request Dto : <<------- " + new Gson().toJson(userMasterDto));
        try {
            Call<UserMasterDto> call = RetrofitController.getInstances(this).getLoadingAdviseApi().logout(userMasterDto);
            Log.i(TAG, "logoutApi: call pass");
            call.enqueue(new Callback<UserMasterDto>() {
                @Override
                public void onResponse(Call<UserMasterDto> call, Response<UserMasterDto> response) {
                    if (!response.isSuccessful()) {
//                  alert(MainActivity.this,"ERROR",response.errorBody().toString(),null,"OK");
                    }
                    Log.i(TAG, "onResponse: logout response raw : " + response.raw());
               /* if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("OK")) {
                        Log.i(TAG, "onResponse: " + response.body().getMessage());
                    } else {
                        alert(LoginActivity.this, DIALOG_ERROR, response.body().getMessage(), null, "OK");
                    }
                }*/
                }

                @Override
                public void onFailure(Call<UserMasterDto> call, Throwable t) {
//              alert(MainActivity.this,DIALOG_ERROR, t.getMessage().toString(),null,"OK");
                    Log.i(TAG, "onFailure: " + t.getMessage());
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            Log.i(TAG, "logoutApi: " + e.getMessage() + e.getCause() + e.getStackTrace());
        }
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