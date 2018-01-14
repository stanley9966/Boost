package com.example.android.boost;

import android.content.SharedPreferences;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

// wraps colorFragment and regionFragment
public class SettingsActivity extends AppCompatActivity
            implements SharedPreferences.OnSharedPreferenceChangeListener{
    private static final String TAG = SettingsActivity.class.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        int i = getIntent().getIntExtra("type", 0);
        if (i == 0) {
            // color
            getSupportFragmentManager().beginTransaction().add(R.id.color_huge, new ColorPrefsFragment()).commit();
        } else {
            // region
            getSupportFragmentManager().beginTransaction().add(R.id.color_huge, new RegionPrefsFragment()).commit();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("region_list_preference_key")) {
            MainScreenActivity.region = sharedPreferences.getString("region_list_preference_key", "");
        }
    }
}
