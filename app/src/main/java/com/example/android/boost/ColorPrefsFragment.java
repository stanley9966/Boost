package com.example.android.boost;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceFragmentCompat;

public class ColorPrefsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.color_preference_fragment);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // ... dunno what goes here
    }
}
