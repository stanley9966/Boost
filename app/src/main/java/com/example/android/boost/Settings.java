package com.example.android.boost;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

// wraps colorFragment
public class Settings extends AppCompatActivity {

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
}
