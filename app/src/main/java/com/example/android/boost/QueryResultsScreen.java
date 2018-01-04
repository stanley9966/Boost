package com.example.android.boost;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class QueryResultsScreen extends AppCompatActivity {

    private ArrayList<Long> mMatchArrayList;
    private HashMap<Long, Boolean> mGameIdsAndWinLossMap;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_results_screen);

        mMatchArrayList = (ArrayList<Long>) getIntent().getSerializableExtra("matchArrayList");
        mGameIdsAndWinLossMap = (HashMap<Long, Boolean>) getIntent().getSerializableExtra("gameIdsAndWinLossMap");

        boolean debug2 = true;
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
    }
}
