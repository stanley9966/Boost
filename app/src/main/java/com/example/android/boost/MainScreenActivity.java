package com.example.android.boost;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public class MainScreenActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // Things on Screen
    private Spinner spinner;
    private AutoCompleteTextView summ1Tv;
    private AutoCompleteTextView summ2Tv;
    private ProgressBar progressBar;
    private NavigationView navigationView;
    private DrawerLayout mdrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    // STATIC DATA
    // final, TODO: change dynamically based on their input
    private static final String dataSet = "dataSet";
    private static final String URL_QUERY = "https://na1.api.riotgames.com/lol/summoner/v3/summoners/by-name/";
    private static final String TEMP_API_KEY = "RGAPI-dc4ed6ee-c6c9-4edb-8437-26029a949165";
    private static String SUMMONER_NAME;
    private static String SECOND_SUMMONER_NAME;

    // PACKAGE PROTECTED STATIC VARS so don't have to copy around everywhere
    static HashMap<Long, Boolean> mGameIdsAndWinLossMap;
    static ArrayList<Long> mMatchArrayList;
    static int numWins = 0;
    static int numMatches = 0;

    // Data for Summoner1
    static String mS1AccountID;

    // Data for Summoner2
    static String mS2AccountID;


    // called when clicking the hamburger
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mActionBarDrawerToggle.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }

    /**
     * creates the mainScreenAcitivity, creates the button and links it
     * @param savedInstanceState performs the query to riot api
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        // setting up navigationdrawer, item selected listener and drawerLayout
        navigationView = findViewById(R.id.navigation);
        mdrawerLayout = findViewById(R.id.drawer_layout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                mdrawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {


            // changing the titles
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Settings");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getSupportActionBar().setTitle("MainScreen");
            }
        };


        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        mdrawerLayout.closeDrawers();      // just closing the drawers right now
                        return true;
                    }
                }
        );
        // setting the drawer toggle as the listener
        mdrawerLayout.addDrawerListener(mActionBarDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mActionBarDrawerToggle.syncState();

        summ1Tv = findViewById(R.id.summoner_1_autocomplete);
        summ2Tv = findViewById(R.id.summoner_2_autocomplete);

        spinner = findViewById(R.id.servers);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.servers_array,
                android.R.layout.simple_spinner_item);    // using default simple spinner item
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        //creates button
        Button connectButton = findViewById(R.id.connect_button);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SUMMONER_NAME = summ1Tv.getText().toString();
                    SECOND_SUMMONER_NAME = summ2Tv.getText().toString();

                    if (SUMMONER_NAME.equals("") || SECOND_SUMMONER_NAME.equals("")) {
                        throw new NoSummonerException();
                    } else {
                        // connect
                        new ConnectServerTask().execute();

                        // remember recent searches
                        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        // adding defaultSet in case there is no savedSet yet
                        Set<String> defaultSet = new HashSet<>();
                        Set<String> savedSet = sharedPreferences.getStringSet(dataSet,
                                defaultSet);
                        savedSet.add(summ1Tv.getText().toString());
                        savedSet.add(summ2Tv.getText().toString());
                        editor.putStringSet(dataSet, savedSet);
                        editor.apply();

                        //summ2Tv.setText("");
                        //summ1Tv.setText("");
                    }
                } catch (NoSummonerException e){
                    Toast.makeText(MainScreenActivity.this, "Please input 2 summoners!",
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        // null there is the default value, if constructing for the first time
        Set<String> set = sharedPreferences.getStringSet(dataSet, null);

        ArrayAdapter<String> adapter;
        if (set != null) {
            String[] setArray = set.toArray(new String[set.size()]);
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.select_dialog_item, setArray);
        } else {
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.select_dialog_item, new String[]{});
        }
        summ1Tv.setAdapter(adapter);
        summ2Tv.setAdapter(adapter);
        summ1Tv.setThreshold(1);    // starts as soon as 1 character typed
        summ2Tv.setThreshold(1);
    }

    // FOR SPINNER
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
        Toast.makeText(parent.getContext(),
                "OnItemSelectedListener : " + parent.getItemAtPosition(i).toString(),
                Toast.LENGTH_SHORT).show();
    }

    // for spinner
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private class ConnectServerTask extends AsyncTask<String, Integer, Void> {

        boolean secondEverAppears = false;
        boolean failed = false;

        /**
         * hides the button in mainScreenActivity
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = findViewById(R.id.ProgressBar);
            progressBar.setVisibility(View.VISIBLE);
        }

        /**
         * Does all the querying to the Riot Api, below is the order in which it does so:
         * 1. Queries summoner_name, storing accountId
         * 2. Queries accountId
         * 3. Queries matchHistory - last 8 games,
         * 4. Queries matchIds, stores results in mGameIdsAndWinLossMap, matchIds and default value of false
         * 5. For each match, queries match, determines which player won, then updates
         *      mGameIdsAndWinLossMap accordingly
         *
         * @param strings TODO: update this when figure out what it does
         * @return  TODO:
         */
        @SuppressLint("UseSparseArrays")
        @Override
        protected Void doInBackground(String... strings) {
            boolean debug = false;
            boolean debug2 = false;
            JSONObject mjsonObject;

            try {
                // for the back button, resetting the static vars so that it doesn't keep track
                if (mGameIdsAndWinLossMap != null) mGameIdsAndWinLossMap.clear();
                if (mMatchArrayList != null) mMatchArrayList.clear();

                InputStream inputStream;
                URL summonerNameUrl = new URL(URL_QUERY + SUMMONER_NAME + "?api_key=" + TEMP_API_KEY);
                HttpsURLConnection summonerNameUrlConnection = (HttpsURLConnection) summonerNameUrl.openConnection();
                summonerNameUrlConnection.connect();
                //String responseCode = Integer.toString(summonerNameUrlConnection.getResponseCode());

                // prints out summoner name query
                // error handled
                inputStream = new BufferedInputStream(summonerNameUrlConnection.getInputStream());
                String json = convertStreamToString(inputStream);

                mjsonObject = new JSONObject(json);

                // getting mS1accountID
                mS1AccountID = mjsonObject.getString("accountId");

                //constructing URL_Query for ranked game match history
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority("na1.api.riotgames.com")
                        .appendPath("lol")
                        .appendPath("match")
                        .appendPath("v3")
                        .appendPath("matchlists")
                        .appendPath("by-account")
                        .appendPath(mS1AccountID)
                        .appendQueryParameter("queue", "420")   // 420 is solo/duo ranked 5v5
                        .appendQueryParameter("endIndex", "4")   // start: 0 -- end: 2 --> only 2 games printed // TODO: DYNAMIC
                        .appendQueryParameter("api_key", TEMP_API_KEY);
                URL rankedGameMatchURL = new URL(builder.build().toString());
                HttpsURLConnection rankedGameHistoryHttpsURLConnection = (HttpsURLConnection) rankedGameMatchURL.openConnection();
                rankedGameHistoryHttpsURLConnection.connect();

                //print match history
                // handles case where summoner not yet lvl 30
                inputStream = new BufferedInputStream(rankedGameHistoryHttpsURLConnection.getInputStream());
                json = convertStreamToString(inputStream);

                // creating and filling the gameIdsandWinLossMap with gameId and defaults to a loss
                mGameIdsAndWinLossMap = new HashMap<>();
                mMatchArrayList = new ArrayList<>();
                numWins = 0;
                numMatches = 0;

                JSONObject jsonObject = new JSONObject(json);
                JSONArray jsonArray = jsonObject.getJSONArray("matches");

                for (int i = 0; i < jsonArray.length(); i++) {
                    long matchId;
                    boolean won = false;
                    JSONObject matchInfo = jsonArray.getJSONObject(i);
                    matchId = matchInfo.getLong("gameId");
                    mGameIdsAndWinLossMap.put(matchId, won);
                    mMatchArrayList.add(i, matchId);
                }

                // prints out contents of gameIds...map

                if (debug2) {
                    System.out.println("original");
                    Set set = mGameIdsAndWinLossMap.entrySet();
                    // Displaying elements of LinkedHashMap
                    Iterator iterator = set.iterator();
                    while (iterator.hasNext()) {
                        Map.Entry me = (Map.Entry) iterator.next();
                        System.out.print("Key is: " + me.getKey() +
                                " & Value is: " + me.getValue() + "\n");
                    }
                }

                // setup for match query
                Set entrySet = mGameIdsAndWinLossMap.entrySet();
                Iterator keyIterator = entrySet.iterator();

                URL matchIdURL;
                HttpsURLConnection matchIdHttpsURLConnection;

                while (keyIterator.hasNext()) {
                    Map.Entry me = (Map.Entry) keyIterator.next();
                    Uri.Builder builder2 = new Uri.Builder();
                    builder2.scheme("https")
                            .authority("na1.api.riotgames.com")
                            .appendPath("lol")
                            .appendPath("match")
                            .appendPath("v3")
                            .appendPath("matches")
                            .appendPath(Long.toString((Long) me.getKey()))
                            .appendQueryParameter("api_key", TEMP_API_KEY);
                    matchIdURL = new URL(builder2.build().toString());
                    matchIdHttpsURLConnection = (HttpsURLConnection) matchIdURL.openConnection();
                    matchIdHttpsURLConnection.connect();

                    // determine if one of players 1-5
                    inputStream = new BufferedInputStream(matchIdHttpsURLConnection.getInputStream());
                    json = convertStreamToString(inputStream);
                    JSONObject matchJsonObject = new JSONObject(json);
                    JSONArray participantIdentities = matchJsonObject.getJSONArray("participantIdentities");

                    boolean firstInFirstFive = false;
                    boolean secondInFirstFive = false;
                    boolean hasSecond = false;
                    boolean sameTeam = false;

                    for (int i = 0; i < 10; i++) {
                        JSONObject playerAndId = participantIdentities.getJSONObject(i);
                        JSONObject player = playerAndId.getJSONObject("player");
                        String summonerName = player.getString("summonerName");

                        // checking for 2nd summoner and finding out if within first 5
                        if (summonerName.equals(SECOND_SUMMONER_NAME)) {
                            secondEverAppears = true;
                            hasSecond = true;
                            if (i < 5) {
                                secondInFirstFive = true;
                            }
                            continue;
                        }

                        // checking if Obstinate is in first 5
                        if (summonerName.equals(SUMMONER_NAME)) {
                            if (i < 5) {
                                firstInFirstFive = true;
                            }
                        }
                        // just gonna let it loop till end
                    }

                    // if doesn't have second, then no way sameTeam is true
                    if (hasSecond) {
                        if (firstInFirstFive == secondInFirstFive) {
                            sameTeam = true;
                        }
                    }

                    // if not both summoners in game, remove that gameId and break out
                    if (!sameTeam) {
                        mGameIdsAndWinLossMap.put((Long) me.getKey(), null);
                        continue;
                    }

                    // sameTeam must be true to get here

                    // see if that team won or lost
                    JSONArray teams = matchJsonObject.getJSONArray("teams");
                    JSONObject team1 = teams.getJSONObject(0);

                    String win = team1.getString("win");
                    boolean won = true;
                    if (win.equals("Fail")) {
                        won = false;
                    }
                    if (firstInFirstFive && won || !firstInFirstFive && !won) {
                        mGameIdsAndWinLossMap.put((Long)me.getKey(), true);
                        numWins++;
                    }
                }

                summonerNameUrlConnection.disconnect();
                rankedGameHistoryHttpsURLConnection.disconnect();
                return null;

            } catch(FileNotFoundException e) {
                failed = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Sets the visibility of the button to VISIBLE, and launches QueryResultsScreen through
         * explicit intent, passing in extra data mGameIdsAndWinLossMap and mMatchArrayList
         *
         * @param avoid // TODO
         */
        @Override
        protected void onPostExecute(Void avoid) {
            boolean debug = false;
            if (debug) System.out.println("onPostExecute");

            super.onPostExecute(avoid);
            progressBar.setVisibility(View.GONE);
            if (!failed) {
                if (secondEverAppears) {
                    // getting rid of the null values in mMatchArrayList and corresponding elements in the map
                    // setting the number of matches
                    Iterator<Long> itr = mMatchArrayList.iterator();
                    while (itr.hasNext()) {
                        numMatches++;
                        Long key = itr.next();
                        if (mGameIdsAndWinLossMap.get(key) == null) {
                            mGameIdsAndWinLossMap.remove(key);
                            numMatches--;
                            itr.remove();
                        }
                    }
                    // launch another activity... passing in that json
                    Intent intent = new Intent(MainScreenActivity.this.getBaseContext(), QueryResultsScreen.class);
                    MainScreenActivity.this.startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(MainScreenActivity.this, "Summoners have not played together recently!",
                            Toast.LENGTH_LONG);
                    toast.show();
                }
            } else {
                Toast toast = Toast.makeText(MainScreenActivity.this, "Summoner1 does not have any ranked games played!",
                        Toast.LENGTH_LONG);
                toast.show();
            }
        }

        /**
         * Converts an inputStream to String
         *
         * @param is inputStream to be converted
         * @return String version of inputStream
         */
        private String convertStreamToString(InputStream is) {
            try {
                return new java.util.Scanner(is).useDelimiter("\\A").next();
            } catch (java.util.NoSuchElementException e) {
                return "";
            }
        }
    }

}
