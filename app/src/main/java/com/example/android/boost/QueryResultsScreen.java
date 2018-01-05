package com.example.android.boost;

import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class QueryResultsScreen extends AppCompatActivity {

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

        // setting up RecyclerView
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        // setting up LayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new Adapter((ArrayList<Long>) getIntent().getSerializableExtra("matchArrayList"),
                (HashMap<Long, Boolean>) getIntent().getSerializableExtra("gameIdsAndWinLossMap"));
        mRecyclerView.setAdapter(mAdapter);



        // setting values of map and arraylist
        mMatchArrayList = (ArrayList<Long>) getIntent().getSerializableExtra("matchArrayList");
        // mGameIdsAndWinLossMap = (HashMap<Long, Boolean>) getIntent().getSerializableExtra("gameIdsAndWinLossMap");
        // might not need ^^ because was only being used in RecyclerView

        // to test print out values of map
        boolean debug2 = false;
        if (debug2) {
            Set set = mGameIdsAndWinLossMap.entrySet();
            // Displaying elements of LinkedHashMap
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                Map.Entry me = (Map.Entry) iterator.next();
                System.out.print("Key is: " + me.getKey() +
                        " & Value is: " + me.getValue() + "\n");
            }
        }

        // adds back button in action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
