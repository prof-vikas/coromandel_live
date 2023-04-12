package com.sipl.rfidtagscanner;

//import static com.sipl.rfidtagscanner.utils.ToastConstants.ROLES_BWH;
import static com.sipl.rfidtagscanner.utils.ErrorCode.ERROR_CODE_E20051;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_BWH;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_CWH;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_LAO;
import static com.sipl.rfidtagscanner.utils.Config.isJWTEnable;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.sipl.rfidtagscanner.dto.request.JwtRequest;
import com.sipl.rfidtagscanner.dto.response.JwtAuthResponse;


public class LoginActivity extends AppCompatActivity {

    private int counter = 0;

    private static final String TAG = "TestingArea2";
    Button btnLogin;
    private ProgressBar progressBar;
    private EditText edtUsername, edtPassword;
    private MaterialCheckBox checkBoxRememberMe;
    private TextView txtErrorMessage;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btn_login);
        edtUsername = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);
        txtErrorMessage = findViewById(R.id.txt_error_message);
        navigationView = findViewById(R.id.navigationView);
        progressBar = findViewById(R.id.login_progressBar);
        checkBoxRememberMe = findViewById(R.id.checkbox_login_remember_me);

        isCheckBoxChecked();
        btnLogin.setOnClickListener(view -> {
//            if (!validateEditText()) {
            processLogin();
//            }
        });

        checkBoxRememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    SharedPreferences sp = getSharedPreferences("rememberMe", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("remember", "true").apply();
                } else if (!compoundButton.isChecked()) {
                    SharedPreferences sp = getSharedPreferences("rememberMe", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("remember", "false").apply();
                }
            }
        });
    }

    public void isCheckBoxChecked() {
        SharedPreferences sp = getSharedPreferences("rememberMe", MODE_PRIVATE);
        SharedPreferences loginCredentials = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        String checkBox = sp.getString("remember", "");
        String loginStatus = loginCredentials.getString("userLoginStatus", "");
        if (loginStatus.equals("login")){
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
        if (isJWTEnable == false){
            hardCodeLogin();
        }else {
            progressBar.setVisibility(View.VISIBLE);
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            JwtRequest jwtRequest = new JwtRequest(username, password);
            Call<JwtAuthResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().login(jwtRequest);
            Log.i(TAG, new Gson().toJson(jwtRequest).toString());
            call.enqueue(new Callback<JwtAuthResponse>() {
                @Override
                public void onResponse(Call<JwtAuthResponse> call, Response<JwtAuthResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    if (!response.isSuccessful()) {
                        Log.i(TAG, "onResponse: " + response.message());
                    }
                    if (response.isSuccessful()) {
                        String token = response.body().getToken();
                        String role = response.body().getUser().getRole().getName();
                        String username = response.body().getUser().getName();
                        String userID = String.valueOf(response.body().getUser().getId());
                        String userSourceLocation = response.body().getUser().getStorageLocation().getStrLocationCode();
                        String userSourceLocationDesc = response.body().getUser().getStorageLocation().getStrLocationDesc();
                        String userPlantLocation = response.body().getUser().getPlantMaster().getPlantCode();
                        String userPlantLocationDesc = response.body().getUser().getPlantMaster().getPlantDesc();
                        Log.i(TAG, "processLogin: Token : " + token + " Username : " + username + " userID : " + userID + " role : " + role + " userSourceLocation : "  + userSourceLocation + " - " + userSourceLocationDesc + " userPlantLocation : " + userPlantLocation + " - " + userPlantLocationDesc);
                        if (token != null && role != null && username != null && userID != null && userSourceLocation != null && userPlantLocation != null && userSourceLocationDesc !=null && userPlantLocationDesc !=null){
                        savingLoginUserToSharedPref(userID, username, role, token, userSourceLocation, userSourceLocationDesc, userPlantLocation, userPlantLocationDesc);
                        }else {
                           alertBuilder(ERROR_CODE_E20051);
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
                    Log.i(TAG, "onFailure: login failure : " + t.getMessage());
                    progressBar.setVisibility(View.GONE);
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
        editor.commit();
        editor.apply();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void hardCodeLogin(){
        progressBar.setVisibility(View.VISIBLE);
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
//        if (username.equals("operator") && password.equals("password")){
        if (username.equals("") && password.equals("")){
            savingLoginUserToSharedPref("6","Vishwanath8990",ROLES_LAO,"apple0masdfohiudfdsfwnjksduirecm,vdfklgimlssdfmxc,fekv","0050","CWC-I Godown", "CFVZ","Corormandel-Vizag");
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        } else if (username.equals("boperator") && password.equals("password")) {
            savingLoginUserToSharedPref("82","boperator",ROLES_LAO,"eajkfdghsdfohiudfdsfwnjksduirecm,vdfklgimlssdfmxc,fekv","0006","Gas Cylinder Shd","BTVZ","Bothra-Vizag");
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        } else if (username.equals("1") && password.equals("")) {
            savingLoginUserToSharedPref("7","CSuperv",ROLES_CWH,"eajkfdghsdfohiudfdsfwnjksduirecm,vdfklgimlssdfmxc,fekv","0010","Western Mezzanin","CFVZ","Corormandel-Vizag");
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }else if (username.equals("2") && password.equals("")) {
            savingLoginUserToSharedPref("8","BSuperv",ROLES_BWH,"eajkfdghsdfohiudfdsfwnjksduirecm,vdfklgimlssdfmxc,fekv","0002","Chemical Godown","BTVZ","Bothra-Vizag");
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "User name or password Mismatch", Toast.LENGTH_SHORT).show();
        }
    }

    private void alertBuilder(String alertMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(alertMessage)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}