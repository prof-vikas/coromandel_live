package com.sipl.rfidtagscanner;

//import static com.sipl.rfidtagscanner.utils.ToastConstants.ROLES_BWH;

import static com.sipl.rfidtagscanner.utils.ErrorCode.ERROR_CODE_E20052;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_BWH;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_CWH;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_LAO;
import static com.sipl.rfidtagscanner.utils.Config.isPlantDetailsRequiredInSideNav;

import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.sipl.rfidtagscanner.fragments.BWHFragment;
import com.sipl.rfidtagscanner.fragments.CWHFragment;
import com.sipl.rfidtagscanner.fragments.LoadingAdviseFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TestingArea";
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    ImageView titleCoromandelLogo;

    LinearLayout headerLayoutPlant, headerLayoutStorage, plant_linearLayout;
    TextView toolbarTitle, login_username, txtPlantCode, txtStorageLocation, txtHeaderPlantCode, txtHeaderStorageLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        txtPlantCode = findViewById(R.id.txt_plant_code);
        txtStorageLocation = findViewById(R.id.txt_storage_location);
        plant_linearLayout = findViewById(R.id.plant_linearLayout);

        //Setting custom toolbar and navigation bar and drawer
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.OpenDrawer, R.string.ClosedDrawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //loading user screen based on their roles
        String userRoles = getLoginUserRole();
        Log.i(TAG, "onCreate: userRoles : " + userRoles);
        if (userRoles != null) {
            loadMenuBasedOnRoles(userRoles);
            showSideBarLoginUsername();
        } else {
            alertBuilder("User Role not undefined \n Error code : " + ERROR_CODE_E20052);
        }

//        show plant details to header bar
        if (isPlantDetailsRequiredInSideNav == false) {
            plant_linearLayout.setVisibility(View.VISIBLE);
            txtPlantCode.setText(getLoginUserPlantCode());
            txtStorageLocation.setText(getLoginUserStorageCode());
        }
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
            if (id == R.id.logout) {
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

    private void loadMenuBasedOnRoles(String userRole) {
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.menu_logout);
        getMenuNavigation();
        if (userRole.equalsIgnoreCase(ROLES_LAO)) {
            Log.i(TAG, "loadMenuBasedOnRoles: " + userRole);
            loadFragment(new LoadingAdviseFragment(), 1);
        }
        if (userRole.equalsIgnoreCase(ROLES_CWH)) {
            Log.i(TAG, "loadMenuBasedOnRoles: " + userRole);
            loadFragment(new CWHFragment(), 1);
        }
        if (userRole.equalsIgnoreCase(ROLES_BWH)) {
            Log.i(TAG, "loadMenuBasedOnRoles: " + userRole);
            loadFragment(new BWHFragment(), 1);
        } else {
            Log.i(TAG, "No User found : " + userRole);
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

/*    public String getSharedPrefsValues(String key) {
        Log.i(TAG, "getSharedPrefsValues: <<Start>>");
        SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        String userIDSPK = sp.getString("userIDSPK", null);
        String usernameSPK = sp.getString("usernameSPK", null);
        String roleSPK = sp.getString("roleSPK", null);
        String tokenSPK = sp.getString("tokenSPK", null);
        String userSourceLocationSPK = sp.getString("UserSourceLocationSPK", null);
        String userPlantLocationSPK = sp.getString("userPlantLocationSPK", null);
        if (userIDSPK != null || usernameSPK != null || roleSPK != null || tokenSPK != null || userSourceLocationSPK != null || userPlantLocationSPK != null) {
            if (key.equalsIgnoreCase(USER_ID)) {
                Log.i(TAG, "getSharedPrefsValues: returing userIDSPK " + userIDSPK);
                return userIDSPK;
            } else if (key.equalsIgnoreCase(USERNAME)) {
                Log.i(TAG, "getSharedPrefsValues: returing usernameSPK " + usernameSPK);
                return usernameSPK;
            } else if (key.equalsIgnoreCase(USER_ROLE)) {
                Log.i(TAG, "getSharedPrefsValues: returing roles " + roleSPK);
                return roleSPK;
            } else if (key.equalsIgnoreCase(USER_TOKEN)) {
                Log.i(TAG, "getSharedPrefsValues: returing tokenSPK " + tokenSPK);
                return tokenSPK;
            } else if (key.equalsIgnoreCase(USER_SOURCE_LOCATION)) {
                Log.i(TAG, "getSharedPrefsValues: returing userSourceLocationSPK " + userSourceLocationSPK);
                return userSourceLocationSPK;
            } else if (key.equalsIgnoreCase(USER_PLANT_LOCATION)) {
                Log.i(TAG, "getSharedPrefsValues: returing userPlantLocationSPK " + userPlantLocationSPK);
                return userPlantLocationSPK;
            } else {
                return "No valid key";
            }
        } else {
            Log.i(TAG, "getSharedPrefsValues: in else block ");
            //TODO error handling code
            alertBuilder("Data Inconsistency \n Error code : " + ERROR_CODE_E20051);
            return null;
        }
    }*/

    public void alertBuilder(String alertMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(alertMessage)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public String getLoginUsername() {
        SharedPreferences sp = getSharedPreferences("loginCredentials", MODE_PRIVATE);
        String username = sp.getString("usernameSPK", null);
        return username;
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
}
