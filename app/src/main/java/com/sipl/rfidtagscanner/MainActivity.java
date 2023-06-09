package com.sipl.rfidtagscanner;

import static com.sipl.rfidtagscanner.utils.Config.ROLES_ADMIN_SUPER;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_ADMIN_PLANT;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_BWH;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_CWH;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_LAO;
import static com.sipl.rfidtagscanner.utils.Config.isPlantDetailsRequiredInSideNav;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.sipl.rfidtagscanner.dto.dtos.UserMasterDto;
import com.sipl.rfidtagscanner.dto.response.UserValidateResponseDto;
import com.sipl.rfidtagscanner.fragments.ScanFragment;
import com.sipl.rfidtagscanner.fragments.SettingsFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TestingArea";
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationView);
        Toolbar toolbar = findViewById(R.id.toolbar);

        //Setting custom toolbar and navigation bar and drawer
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.OpenDrawer, R.string.ClosedDrawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        String userRoles = getLoginUserRole();
        if (userRoles != null) {
            loadMenuBasedOnRoles(userRoles);
            showSideBarLoginUsername();
        }
        showSideBarLoginUsername();

    }

    public void loadFragment(Fragment fragment, int flag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (flag == 0) {
            ft.add(R.id.main_container, fragment);
        } else {
            ft.replace(R.id.main_container, fragment);
        }
        ft.commit();
    }

    private void setScreenData(String screenData){
        SharedPreferences sp = getSharedPreferences("adminScreen", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("screen", screenData).apply();
    }

    public void loadFragment2(Fragment fragment, int flag, String screen) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Log.i(TAG, "loadFragment2: screen : " + screen);
        setScreenData(screen);
        if (flag == 0) {
            ft.add(R.id.main_container, fragment);
        } else {
            ft.replace(R.id.main_container, fragment);
        }
        ft.commit();
    }

    /*
     * Method where side bar navigation menu get loaded
     * */
    private void getMenuNavigation() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_item_scan_rfid) {
                Log.i(TAG, "getMenuNavigation: menu_item_scan_rfid" + id);
                loadFragment2(new ScanFragment(id), 1, null);
            } else if (id == R.id.menu_item_loading_advise) {
                Log.i(TAG, "getMenuNavigation: menu_item_loading_advise" + id);
                loadFragment2(new ScanFragment(id), 1, "loadingAdvise");
            } else if (id == R.id.menu_item_bothra_warehouse) {
                Log.i(TAG, "getMenuNavigation: menu_item_bothra_warehouse" + id);
                loadFragment2(new ScanFragment(id), 1, "bothra");
            } else if (id == R.id.menu_item_coromandel_warehouse) {
                Log.i(TAG, "getMenuNavigation: menu_item_coromandel_warehouse" + id);
                loadFragment2(new ScanFragment(id), 1, "coromandel");
            } else if (id == R.id.menu_item_setting) {
                loadFragment2(new SettingsFragment(), 1, "settingAdmin");
            } else if (id == R.id.menu_item_logout) {
                logout();
            } else {
                Toast.makeText(MainActivity.this, "click outside of menu", Toast.LENGTH_SHORT).show();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void loadMenuBasedOnRoles(String userRole) {
        if (userRole.equalsIgnoreCase(ROLES_ADMIN_PLANT) || userRole.equalsIgnoreCase(ROLES_ADMIN_SUPER)) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.menu_admin);
            getMenuNavigation();
            loadFragment(new ScanFragment(0), 1);
        } else {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.menu_loading_advise);
            getMenuNavigation();
            loadFragment(new ScanFragment(0), 1);
        }
    }

    /*
     * Method to show user login information in side bar
     * */
    public void showSideBarLoginUsername() {
        View headerView = navigationView.getHeaderView(0);
        TextView login_username = headerView.findViewById(R.id.login_username);
        TextView txtHeaderStorageLocation = headerView.findViewById(R.id.login_Storage_Location);
        TextView txtHeaderPlantCode = headerView.findViewById(R.id.login_plantCode);
        LinearLayout headerLayoutPlant = headerView.findViewById(R.id.ll_header_plant_code);
        LinearLayout headerLayoutStorage = headerView.findViewById(R.id.ll_header_source_code);
        login_username.setText(getLoginUsername());
        String loginUserStorageLocation = getLoginUserStorageCode() + " - " + getLoginUserSourceLocationDesc();
        txtHeaderStorageLocation.setText(loginUserStorageLocation);
        String loginUserPlantCode = getLoginUserPlantCode() + " - " + getLoginUserPlantLocationDesc();
        txtHeaderPlantCode.setText(loginUserPlantCode);

        if (isPlantDetailsRequiredInSideNav) {
            headerLayoutPlant.setVisibility(View.VISIBLE);
            headerLayoutStorage.setVisibility(View.VISIBLE);
        }
    }

    private void logout() {
        SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        SharedPreferences checkBox = getSharedPreferences("rememberMe", MODE_PRIVATE);
        checkBox.edit().putString("remember", "false").apply();
        sp.edit().remove("userIDSPK").apply();
        sp.edit().remove("usernameSPK").apply();
        sp.edit().remove("roleSPK").apply();
        sp.edit().remove("UserSourceLocationDescSPK").apply();
        sp.edit().remove("userPlantLocationDescSPK").apply();
        sp.edit().remove("tokenSPK").apply();
        sp.edit().remove("userLoginStatus").apply();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public String getLoginUsername() {
        SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        return sp.getString("usernameSPK", null);
    }

    public String getLoginToken() {
        SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        return sp.getString("tokenSPK", null);
    }

    public String getLoginUserRole() {
        SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        return sp.getString("userRolesIdSPK", null);
    }

    public String getLoginUserRoleName() {
        SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        return sp.getString("roleSPK", null);
    }

    public String getLoginUserStorageCode() {
        SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        return sp.getString("UserSourceLocationSPK", null);
    }

    public String getLoginUserPlantCode() {
        SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        return sp.getString("userPlantLocationSPK", null);
    }

    public int getLoginUserId() {
        SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
//        Log.i(TAG, "getLoginUserId: " + sp.getString("userIDSPK", null));
        return Integer.parseInt(sp.getString("userIDSPK", null));
    }

    public String getLoginUserPlantLocationDesc() {
        SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        return sp.getString("userPlantLocationDescSPK", null);
    }

    public String getLoginUserSourceLocationDesc() {
        SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        return sp.getString("UserSourceLocationDescSPK", null);
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
        if (dialogType.equalsIgnoreCase("error")) {
            error.setVisibility(View.VISIBLE);
            success.setVisibility(View.GONE);
            warning.setVisibility(View.GONE);
        } else if (dialogType.equalsIgnoreCase("success")) {
            error.setVisibility(View.GONE);
            warning.setVisibility(View.GONE);
            success.setVisibility(View.VISIBLE);
        } else if (dialogType.equalsIgnoreCase("warning")) {
            error.setVisibility(View.GONE);
            success.setVisibility(View.GONE);
            warning.setVisibility(View.VISIBLE);
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


    private void logoutApi() {
        Log.i(TAG, "validateUser: in validateUser()");
//        progressBar.setVisibility(View.VISIBLE);
        UserMasterDto userMasterDto = new UserMasterDto(getLoginUserId());
        Call<UserValidateResponseDto> call = RetrofitController.getInstances(this).getLoadingAdviseApi().logout(userMasterDto);
      call.enqueue(new Callback<UserValidateResponseDto>() {
          @Override
          public void onResponse(Call<UserValidateResponseDto> call, Response<UserValidateResponseDto> response) {
              if (!response.isSuccessful()){
                  alert(MainActivity.this,"ERROR",response.errorBody().toString(),null,"OK");
              }
              Log.i(TAG, "onResponse: logout response raw : " + response.raw());
              if (response.isSuccessful()){
                  if (response.body().getStatus().equalsIgnoreCase("FOUND")){
                      Log.i(TAG, "onResponse: " + response.body().getMessage());
                      logout();
                  }else {
                      alert(MainActivity.this,"ERROR", response.body().getMessage(),null,"OK");
                  }
              }
          }

          @Override
          public void onFailure(Call<UserValidateResponseDto> call, Throwable t) {
              alert(MainActivity.this,"ERROR", t.getMessage().toString(),null,"OK");
          }
      });
    }
}
