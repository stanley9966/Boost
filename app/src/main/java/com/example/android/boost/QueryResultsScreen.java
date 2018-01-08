package com.example.android.boost;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;

public class QueryResultsScreen extends AppCompatActivity {

    private WinRateCircle winRateCircle;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<Long> mMatchArrayList;
    private HashMap<Long, Boolean> mGameIdsAndWinLossMap;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_results_screen);

        winRateCircle = findViewById(R.id.winRateCircleId);

        // setting up RecyclerView
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        // setting up LayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new Adapter();
        mRecyclerView.setAdapter(mAdapter);

        // adds back button in action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
