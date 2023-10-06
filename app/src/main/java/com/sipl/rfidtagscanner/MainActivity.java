package com.sipl.rfidtagscanner;

import static com.sipl.rfidtagscanner.utils.Config.DIALOG_ERROR;
import static com.sipl.rfidtagscanner.utils.Config.DIALOG_SUCCESS;
import static com.sipl.rfidtagscanner.utils.Config.DIALOG_WARNING;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_ADMIN_SUPER;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_ADMIN_PLANT;
import static com.sipl.rfidtagscanner.utils.Config.isPlantDetailsRequiredInSideNav;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.sipl.rfidtagscanner.fragments.ScanFragment;
import com.sipl.rfidtagscanner.fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity {

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


        if (getRoleId() != null) {
            loadMenuBasedOnRoles(getRoleId());
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
                loadFragment2(new ScanFragment(), 1, null);
            } else if (id == R.id.menu_item_loading_advise) {
                loadFragment2(new ScanFragment(), 1, "loadingAdvise");
            } else if (id == R.id.menu_item_bothra_warehouse) {
                loadFragment2(new ScanFragment(), 1, "bothra");
            } else if (id == R.id.menu_item_coromandel_warehouse) {
                loadFragment2(new ScanFragment(), 1, "coromandel");
            } else if (id == R.id.menu_item_setting) {
                loadFragment2(new SettingsFragment(), 1, null);
            } else if (id == R.id.menu_item_setting_admin) {
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
            loadFragment(new ScanFragment(), 1);
        } else {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.menu_loading_advise);
            getMenuNavigation();
            loadFragment(new ScanFragment(), 1);
        }
    }

    /*
     * Method to show user login information in side bar
     * */
    public void showSideBarLoginUsername() {
        View headerView = navigationView.getHeaderView(0);
        TextView login_username = headerView.findViewById(R.id.login_username);
        TextView txtHeaderPlantCode = headerView.findViewById(R.id.login_plantCode);
        LinearLayout headerLayoutPlant = headerView.findViewById(R.id.ll_header_plant_code);
        login_username.setText(getUsername());
        String loginUserPlantCode = getUserPlantCode() + " - " + getUserPlantLocationDesc();
        txtHeaderPlantCode.setText(loginUserPlantCode);

        if (isPlantDetailsRequiredInSideNav) {
            headerLayoutPlant.setVisibility(View.VISIBLE);
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

        SharedPreferences sp1 = getSharedPreferences("logoutMark", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp1.edit();
        editor.putString("isLogout", "logout").apply();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public String getUsername() {
        SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        return sp.getString("usernameSPK", null);
    }

    public String getToken() {
        SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        return sp.getString("tokenSPK", null);
    }

    public String getRoleId() {
        SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        return sp.getString("userRolesIdSPK", null);
    }

    public String getUserSourceLocationCode() {
        SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        return sp.getString("UserSourceLocationSPK", null);
    }

    public String getUserPlantCode() {
        SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        return sp.getString("userPlantLocationSPK", null);
    }

    public String getUserId() {
        SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        return sp.getString("userIDSPK", null);
    }

    public String getUserPlantLocationDesc() {
        SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        return sp.getString("userPlantLocationDescSPK", null);
    }

    public String getUserSourceLocationDesc() {
        SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        return sp.getString("UserSourceLocationDescSPK", null);
    }

    public void alert(Context context, String dialogType, String dialogTitle, String dialogMessage, String dialogBtnText, Boolean isReturnToScanner) {
        Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.custom_alert_dialog_box);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        TextView error = dialog.findViewById(R.id.dialog_type_error);
        TextView success = dialog.findViewById(R.id.dialog_type_success);
        TextView warning = dialog.findViewById(R.id.dialog_type_warning);

        error.setVisibility(dialogType.equalsIgnoreCase(DIALOG_ERROR) ? View.VISIBLE : View.GONE);
        success.setVisibility(dialogType.equalsIgnoreCase(DIALOG_SUCCESS) ? View.VISIBLE : View.GONE);
        warning.setVisibility(dialogType.equalsIgnoreCase(DIALOG_WARNING) ? View.VISIBLE : View.GONE);

        TextView dialogMessageTxt = dialog.findViewById(R.id.text_msg2);
        dialogMessageTxt.setVisibility(dialogMessage == null ? View.GONE : View.VISIBLE);
        TextView dialogTitleTxt = dialog.findViewById(R.id.text_msg);
        TextView btn = dialog.findViewById(R.id.text_btn);

        dialogTitleTxt.setText(dialogTitle);
        dialogMessageTxt.setText(dialogMessage);
        btn.setText(dialogBtnText);

        btn.setOnClickListener(view -> {
            if (isReturnToScanner) {
                dialog.dismiss();
                loadFragment(new ScanFragment(), 1);
            } else {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
