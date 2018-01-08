package com.example.android.boost;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public class MainScreenActivity extends AppCompatActivity {

    // STATIC DATA
    // final, TODO: change dynamically based on their input
    private static final String URL_QUERY = "https://na1.api.riotgames.com/lol/summoner/v3/summoners/by-name/";
    private static final String TEMP_API_KEY = "RGAPI-9321305e-c0f7-47f3-b2eb-3bd0157b5cf1";
    private static final String SUMMONER_NAME = "Obstinate";
    private static final String SECOND_SUMMONER_NAME = "Gooben";

    // PACKAGE PROTECTED STATIC VARS so don't have to copy around everywhere
    static HashMap<Long, Boolean> mGameIdsAndWinLossMap;
    static ArrayList<Long> mMatchArrayList = new ArrayList<>();
    static int numWins = 0;
    static int numMatches = 0;

    // Data for Summoner1
    static String mS1AccountID;

    // Data for Summoner2
    static String mS2AccountID;

    /**
     * creates the mainScreenAcitivity, creates the button and links it
     * @param savedInstanceState performs the query to riot api
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        //creates button
        Button connectButton = findViewById(R.id.connect_button);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    new ConnectServerTask().execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private class ConnectServerTask extends AsyncTask<String, Integer, Void> {
        /**
         * hides the button in mainScreenActivity
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MainScreenActivity.this.findViewById(R.id.connect_button).setVisibility(View.INVISIBLE);
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

                int responseCode = summonerNameUrlConnection.getResponseCode();

                // prints out summoner name query
                inputStream = new BufferedInputStream(summonerNameUrlConnection.getInputStream());
                String json = convertStreamToString(inputStream);
                if (debug) System.out.println(json);

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
                inputStream = new BufferedInputStream(rankedGameHistoryHttpsURLConnection.getInputStream());
                json = convertStreamToString(inputStream);
                if (debug) System.out.println(json);

                // creating and filling the gameIdsandWinLossMap with gameId and defaults to a loss
                mGameIdsAndWinLossMap = new HashMap<>();
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
                            .appendPath(Long.toString((Long) me.getKey()))  // MIGHT NOT BE RIGHT
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

            } catch (Exception e) {
                e.printStackTrace();

            }
            return null;
        }


        /**
         *
         * @param values // TODO:
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }

        // hide the progress bar
        // launches another activity, passing the required information over..

        /**
         * Sets the visibility of the button to VISIBLE, and launches QueryResultsScreen through
         * explicit intent, passing in extra data mGameIdsAndWinLossMap and mMatchArrayList
         *
         * @param avoid // TODO
         */
        @Override
        protected void onPostExecute(Void avoid) {
            boolean debug = false;
            if (debug) System.out.println("onPostExecute\n");

            super.onPostExecute(avoid);
            MainScreenActivity.this.findViewById(R.id.connect_button).setVisibility(View.VISIBLE);

            // getting rid of the null values in mMatchArrayList and corresponding elements in the map
            // setting the number of matches
            Iterator<Long> itr = mMatchArrayList.iterator();
            while(itr.hasNext()) {
                numMatches++;
                Long key = itr.next();
                if (mGameIdsAndWinLossMap.get(key) == null) {
                    mGameIdsAndWinLossMap.remove(key);
                    itr.remove();
                }
            }

            // launch another activity... passing in that json
            Intent intent = new Intent(MainScreenActivity.this.getBaseContext(), QueryResultsScreen.class);
            MainScreenActivity.this.startActivity(intent);
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
