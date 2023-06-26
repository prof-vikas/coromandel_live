package com.sipl.rfidtagscanner.fragments;


import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.sipl.rfidtagscanner.R;


public class SettingsFragment extends PreferenceFragmentCompat {

    private static final String TAG = "SettingsFragment";
    private SwitchPreference toggleRFIDBtn;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        Log.i(TAG, "onCreatePreferences:  <<Start>>");
        setPreferencesFromResource(R.xml.settings_preference, rootKey);
        toggleRFIDBtn = findPreference("enable_rfid_handle");
        Log.i(TAG, "onCreatePreferences: <<END>>");
    }

    public void updateSwitchPreferenceValue(boolean status) {
        if (toggleRFIDBtn != null) {
            toggleRFIDBtn.setChecked(status);
            PreferenceManager.getDefaultSharedPreferences(requireContext())
                    .edit()
                    .putBoolean(toggleRFIDBtn.getKey(), status)
                    .apply();
        }
    }
}