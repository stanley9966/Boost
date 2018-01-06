package com.example.android.boost;

import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

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

        mMatchArrayList = (ArrayList<Long>) getIntent().getSerializableExtra("matchArrayList");
        mGameIdsAndWinLossMap = (HashMap<Long, Boolean>) getIntent().getSerializableExtra("gameIdsAndWinLossMap");

        mAdapter = new Adapter(mMatchArrayList, mGameIdsAndWinLossMap);
        mRecyclerView.setAdapter(mAdapter);

        // adds back button in action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public ArrayList<Long> getmMatchArrayList() {
        return mMatchArrayList;
    }

    public HashMap<Long, Boolean> getmGameIdsAndWinLossMap() {
        return mGameIdsAndWinLossMap;
    }
}
