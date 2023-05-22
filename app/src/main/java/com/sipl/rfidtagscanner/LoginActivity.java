package com.sipl.rfidtagscanner;

import static com.sipl.rfidtagscanner.utils.Config.ROLES_ADMIN_SUPER;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_ADMIN_PLANT;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_BWH;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_CWH;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_LAO;
import static com.sipl.rfidtagscanner.utils.Config.WRONG_CREDENTIALS;
import static com.sipl.rfidtagscanner.utils.Config.isJWTEnable;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.gson.Gson;
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
    private TextView txtErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnLogin = findViewById(R.id.btn_login);
        edtUsername = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);
        txtErrorMessage = findViewById(R.id.txt_error_message);
        progressBar = findViewById(R.id.login_progressBar);
        MaterialCheckBox checkBoxRememberMe = findViewById(R.id.checkbox_login_remember_me);

        isCheckBoxChecked();
        btnLogin.setOnClickListener(view -> {
//            if (!validateEditText()) {
            processLogin();
//            }
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
                Log.i(TAG, "isCheckBoxChecked: false");
            }
        }
    }

    private boolean validateEditText() {
        if (edtUsername.length() == 0) {
            edtUsername.setError("This field is required");
            return false;
        }
        if (edtUsername.length() <= 2) {
            edtUsername.setError("username must be minimum 3 characters");
            return false;
        }
        if (edtPassword.length() == 0) {
            edtPassword.setError("This field is required");
            return false;
        }
        if (edtPassword.length() <= 7) {
            edtPassword.setError("password must be minimum 8 characters");
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
            progressBar.setVisibility(View.VISIBLE);
            JwtRequest jwtRequest = new JwtRequest(username, password);
            Call<JwtAuthResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().login(jwtRequest);
            Log.i(TAG, new Gson().toJson(jwtRequest));
            call.enqueue(new Callback<JwtAuthResponse>() {
                @Override
                public void onResponse(Call<JwtAuthResponse> call, Response<JwtAuthResponse> response) {
                    if (!response.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        Log.i(TAG, "onResponse: " + response.raw());
                        alert(LoginActivity.this, "error", response.errorBody().toString(), null, "OK");
                    }
                    Log.i(TAG, "onResponse: " + response.raw());
                    if (response.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        String token = response.body().getToken();
                        String role = response.body().getUser().getRole().getName();
                        String username = response.body().getUser().getName();
                        String userID = String.valueOf(response.body().getUser().getId());
                        String userSourceLocation = response.body().getUser().getStorageLocation().getStrLocationCode();
                        String userSourceLocationDesc = response.body().getUser().getStorageLocation().getStrLocationDesc();
                        String userPlantLocation = response.body().getUser().getPlantMaster().getPlantCode();
                        String userPlantLocationDesc = response.body().getUser().getPlantMaster().getPlantDesc();
                        Log.i(TAG, "processLogin: Token : " + token + " Username : " + username + " userID : " + userID + " role : " + role + " userSourceLocation : " + userSourceLocation + " - " + userSourceLocationDesc + " userPlantLocation : " + userPlantLocation + " - " + userPlantLocationDesc);
                        if (token != null && role != null && username != null && userSourceLocation != null && userPlantLocation != null && userSourceLocationDesc != null && userPlantLocationDesc != null) {
                            savingLoginUserToSharedPref(userID, username, role, token, userSourceLocation, userSourceLocationDesc, userPlantLocation, userPlantLocationDesc);
                        } else {
                            alert(LoginActivity.this, "error", "Something went wrong with this user credentials", "Try login with other user credentials", "OK");
                            return;
                        }
                    }
                    if (response.code() != 200) {
                        Log.i(TAG, "onResponse: response code : " + response.code() + " response message" + response.message() + response.raw());
                        txtErrorMessage.setText(response.message());
                    }
                }

                @Override
                public void onFailure(Call<JwtAuthResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    t.printStackTrace();
                    alert(LoginActivity.this, "error", t.getMessage(), null, "OK");
                }
            });
        }
    }

    private void savingLoginUserToSharedPref(String userID, String username, String role, String token, String userSourceLocation, String userSourceLocationDesc, String userPlantLocation, String userPlantLocationDesc) {
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
        editor.apply();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void hardCodeLogin(String username, String password) {
        progressBar.setVisibility(View.VISIBLE);
        if (username.equals("la") && password.equals("")) {
            savingLoginUserToSharedPref("2110", "CLoadingAdvise", ROLES_LAO, "apple0masdfohiudfdsfwnjksduirecm,vdfklgimlssdfmxc,fekv", "0058", "Port Area Godown", "CFVZ", "Corormandel-Vizag");
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (username.equals("bla") && password.equals("")) {
            savingLoginUserToSharedPref("2111", "BLoadingAdvise", ROLES_LAO, "eajkfdghsdfohiudfdsfwnjksduirecm,vdfklgimlssdfmxc,fekv", "100", "GODOWN1", "CFVZ", "Corormandel-Vizag");
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (username.equals("cws") && password.equals("")) {
            savingLoginUserToSharedPref("2113", "CWH@098", ROLES_CWH, "eajkfdghsdfohiudfdsfwnjksduirecm,vdfklgimlssdfmxc,fekv", "0010", "Western Mezzanin", "CFVZ", "Corormandel-Vizag");
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (username.equals("bws") && password.equals("")) {
            savingLoginUserToSharedPref("2215", "BWH@098", ROLES_BWH, "eajkfdghsdfohiudfdsfwnjksduirecm,vdfklgimlssdfmxc,fekv", "0046", "Bothra Godown", "CFVZ", "Corormandel-Vizag");
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (username.equals("2") && password.equals("")) {
            savingLoginUserToSharedPref("7", "CSuperv", ROLES_ADMIN_PLANT, "eajkfdghsdfohiudfdsfwnjksduirecm,vdfklgimlssdfmxc,fekv", "0010", "Western Mezzanin", "CFVZ", "Corormandel-Vizag");
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (username.equals("1") && password.equals("")) {
            savingLoginUserToSharedPref("8", "BSuperv", ROLES_ADMIN_PLANT, "eajkfdghsdfohiudfdsfwnjksduirecm,vdfklgimlssdfmxc,fekv", "0046", "Bothra Godown", "CFVZ", "Corormandel-Vizag");
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            progressBar.setVisibility(View.GONE);
            txtErrorMessage.setText(WRONG_CREDENTIALS);
        }
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
        progressBar.setVisibility(View.VISIBLE);
        Call<UserValidateResponseDto> call = RetrofitController.getInstance().getLoadingAdviseApi().loginWithOutJwt(edtUsername.getText().toString().trim(), edtPassword.getText().toString().trim());
        call.enqueue(new Callback<UserValidateResponseDto>() {
            @Override
            public void onResponse(Call<UserValidateResponseDto> call, Response<UserValidateResponseDto> response) {
                if (!response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    alert(LoginActivity.this, "ERROR", response.errorBody().toString(), null, "OK");
                }
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    if (response.body().getStatus().equalsIgnoreCase("OK")) {
                        String token = "emlfkemdflkeneknekjdfnekjgcnekjgen";
                        String userID = response.body().getUserDto().getUserId();
                        String id = String.valueOf(response.body().getUserDto().getId());
                        String userRole = response.body().getUserDto().getRole().getName();
                        String sourceLocationCode = response.body().getUserDto().getStorageLocation().getStrLocationCode();
                        String sourceLocationCodeDesc = response.body().getUserDto().getStorageLocation().getStrLocationDesc();
                        String plantLocationCode = response.body().getUserDto().getPlantMaster().getPlantCode();
                        String plantLocationCodeDesc = response.body().getUserDto().getPlantMaster().getPlantDesc();
                        Log.i(TAG, "onResponse: " + response.raw());
                        if (userRole.equalsIgnoreCase(ROLES_LAO)) {
                            savingLoginUserToSharedPref(id, userID, userRole, token, sourceLocationCode, sourceLocationCodeDesc, plantLocationCode, plantLocationCodeDesc);
                        } else if (userRole.equalsIgnoreCase(ROLES_CWH)) {
                            savingLoginUserToSharedPref(id, userID, userRole, token, sourceLocationCode, sourceLocationCodeDesc, plantLocationCode, plantLocationCodeDesc);
                        } else if (userRole.equalsIgnoreCase(ROLES_BWH)) {
                            savingLoginUserToSharedPref(id, userID, userRole, token, sourceLocationCode, sourceLocationCodeDesc, plantLocationCode, plantLocationCodeDesc);
                        } else if(userRole.equalsIgnoreCase(ROLES_ADMIN_PLANT)){
                            savingLoginUserToSharedPref(id, userID, userRole, token, sourceLocationCode, sourceLocationCodeDesc, plantLocationCode, plantLocationCodeDesc);
                        }else{
                            alert(LoginActivity.this, "ERROR", "User role not allowed", null, "OK");
                            return;

                        }
                    } else {
                        alert(LoginActivity.this, "ERROR", response.body().getMessage(), null, "OK");
                        return;
                    }
                }
            }

            @Override
            public void onFailure(Call<UserValidateResponseDto> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                alert(LoginActivity.this, "ERROR", t.getMessage().toString(), null, "OK");
                t.printStackTrace();
            }
        });
    }
}