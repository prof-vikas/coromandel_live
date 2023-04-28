package com.sipl.rfidtagscanner;

import static com.sipl.rfidtagscanner.utils.Config.ROLES_ADMIN;
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
import android.widget.ImageView;
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

    private static final String TAG = "TestingArea";
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    LinearLayout headerLayoutPlant, headerLayoutStorage;
    TextView toolbarTitle, login_username, txtHeaderPlantCode, txtHeaderStorageLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        ImageView refesh = findViewById(R.id.refresh);

        //Setting custom toolbar and navigation bar and drawer
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.OpenDrawer, R.string.ClosedDrawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        refesh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        String userRoles = getLoginUserRole();
        Log.i(TAG, "onCreate: userRoles : " + userRoles);
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

    /*
     * Method where side bar navigation menu get loaded
     * */
    private void getMenuNavigation() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_item_scan_rfid) {
                loadFragment(new ScanFragment(), 1);
            } else if (id == R.id.menu_item_setting) {
                loadFragment(new SettingsFragment(), 1);
            } else if (id == R.id.menu_item_logout) {
                logout();
            } else {
                Toast.makeText(MainActivity.this, "click outside of menu", Toast.LENGTH_SHORT).show();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    /*
     * Method to add custom header title (In our case we are not using)
     * */
    public void setActionBarTitle(String title) {
        toolbarTitle.setText(title);
        Toast.makeText(this, "title" + title, Toast.LENGTH_SHORT).show();
    }

  /*  private void loadMenuBasedOnRoles(String userRole) {
        if (userRole.equalsIgnoreCase(ROLES_ADMIN)) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.menu_admin);
            getMenuNavigation();
            loadFragment(new LoadingAdviseFragment(), 1);
        } else {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.menu_logout);
            getMenuNavigation();
            if (userRole.equalsIgnoreCase(ROLES_LAO)) {
                loadFragment(new LoadingAdviseFragment(), 1);
            }
            if (userRole.equalsIgnoreCase(ROLES_CWH)) {
                loadFragment(new CWHFragment(), 1);
            }
            if (userRole.equalsIgnoreCase(ROLES_BWH)) {
                loadFragment(new BWHFragment(), 1);
            } else {
                Log.i(TAG, "No User found : " + userRole);
            }
        }
    }*/

    private void loadMenuBasedOnRoles(String userRole) {
        if (userRole.equalsIgnoreCase(ROLES_ADMIN)) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.menu_admin);
            getMenuNavigation();
            loadFragment(new ScanFragment(), 1);
        } else if (userRole.equalsIgnoreCase(ROLES_LAO)) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.menu_loading_advise);
            getMenuNavigation();
            loadFragment(new ScanFragment(), 1);
        } else if (userRole.equalsIgnoreCase(ROLES_CWH)) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.menu_loading_advise);
            getMenuNavigation();
            loadFragment(new ScanFragment(), 1);
        } else if (userRole.equalsIgnoreCase(ROLES_BWH)) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.menu_loading_advise);
            getMenuNavigation();
            loadFragment(new ScanFragment(), 1);
        } else {
            Log.i(TAG, "loadMenuBasedOnRoles: No roles available");
        }
    }

    /*
     * Method to show user login information in side bar
     * */
    public void showSideBarLoginUsername() {
        View headerView = navigationView.getHeaderView(0);
        login_username = headerView.findViewById(R.id.login_username);
        txtHeaderStorageLocation = headerView.findViewById(R.id.login_Storage_Location);
        txtHeaderPlantCode = headerView.findViewById(R.id.login_plantCode);
        headerLayoutPlant = headerView.findViewById(R.id.ll_header_plant_code);
        headerLayoutStorage = headerView.findViewById(R.id.ll_header_source_code);
        login_username.setText(getLoginUsername());
        txtHeaderStorageLocation.setText(getLoginUserStorageCode() + " - " + getLoginUserSourceLocationDesc());
        txtHeaderPlantCode.setText(getLoginUserPlantCode() + " - " + getLoginUserPlantLocationDesc());

        if (isPlantDetailsRequiredInSideNav == true) {
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
        String username = sp.getString("usernameSPK", null);
        return username;
    }

    public String getLoginToken() {
        SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        String token = sp.getString("tokenSPK", null);
        return token;
    }

    public String getLoginUserRole() {
        SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        String userRole = sp.getString("roleSPK", null);
        return userRole;
    }

    public String getLoginUserStorageCode() {
        SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        String loginUserStorageLocation = sp.getString("UserSourceLocationSPK", null);
        return loginUserStorageLocation;
    }

    public String getLoginUserPlantCode() {
        SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        String loginUserPlantCode = sp.getString("userPlantLocationSPK", null);
        return loginUserPlantCode;
    }

    public int getLoginUserId() {
        SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        int loginUserId = Integer.parseInt(sp.getString("userIDSPK", null));
        return loginUserId;
    }

    public String getLoginUserPlantLocationDesc() {
        SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        String loginUserPlantLocationDesc = sp.getString("userPlantLocationDescSPK", null);
        return loginUserPlantLocationDesc;
    }

    public String getLoginUserSourceLocationDesc() {
        SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        String loginSourceKLocationDesc = sp.getString("UserSourceLocationDescSPK", null);
        return loginSourceKLocationDesc;
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
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
