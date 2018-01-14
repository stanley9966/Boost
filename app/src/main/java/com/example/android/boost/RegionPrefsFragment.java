package com.example.android.boost;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceFragmentCompat;

public class RegionPrefsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.region_preference_fragment);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // ... dunno what goes here
    }
}

