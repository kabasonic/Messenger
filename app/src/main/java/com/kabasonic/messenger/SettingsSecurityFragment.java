package com.kabasonic.messenger;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsSecurityFragment extends PreferenceFragmentCompat {



    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.security_preferences, rootKey);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Preference pref = findPreference("PREF_CLICK");
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference _pref) {
                // Do what you want
                NavDirections action = SettingsSecurityFragmentDirections.actionSettingsSecurityFragmentToLockScreenFragment();
                Navigation.findNavController(getView()).navigate(action);
                return true;
            }
        });
    }
}