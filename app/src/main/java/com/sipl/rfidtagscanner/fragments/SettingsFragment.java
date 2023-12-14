package com.sipl.rfidtagscanner.fragments;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.sipl.rfidtagscanner.R;


public class SettingsFragment extends PreferenceFragmentCompat {

    private SwitchPreference toggleRFIDBtn;
    private SwitchPreference toggleLoadingDifference;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey);
        toggleRFIDBtn = findPreference("enable_rfid_handle");
        toggleLoadingDifference= findPreference("enable_loading_difference");

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

    public void updateSwitchPreferenceValueForLoading(boolean status) {
        if (toggleLoadingDifference != null) {
            toggleLoadingDifference.setChecked(status);
            PreferenceManager.getDefaultSharedPreferences(requireContext())
                    .edit()
                    .putBoolean(toggleLoadingDifference.getKey(), status)
                    .apply();
        }
    }
}