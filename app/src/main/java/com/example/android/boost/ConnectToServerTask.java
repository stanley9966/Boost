package com.example.android.boost;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

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

import static android.content.ContentValues.TAG;

// TODO: Perform same queries on 2nd summoner
public class ConnectToServerTask extends AsyncTask<String, Integer, Void> {

    //URL_QUERY + SUMMONER_NAME + "?api_key=" + TEMP_API_KEY
    private static final String URL_QUERY = "https://na1.api.riotgames.com/lol/summoner/v3/summoners/by-name/";
    private static final String SUMMONER_NAME = "Obstinate";
    private static final String TEMP_API_KEY = "RGAPI-19e26648-83e1-4f1a-b1b5-d2fe9c2be82c";

    private Activity mMainScreenActivity;
    private HashMap<Long, Boolean> mGameIdsAndWinLossMap;
    private ArrayList<Long> mMatchArrayList = new ArrayList<>();

    // Data for Summoner1
    private String mS1AccountID;

    // Data for Summoner2
    private String mS2AccountID;

    public ConnectToServerTask(Activity main) {
        mMainScreenActivity = main;
    }


    // show the progress bar
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mMainScreenActivity.findViewById(R.id.connect_button).setVisibility(View.INVISIBLE);
    }

    // gonna be passed the url...
    // call riot rest api and handle the response
    @Override
    protected Void doInBackground(String... strings) {    // NO STRINGS PASSED
        Log.d(TAG, "doInBackground: ");
        boolean debug = false;
        boolean debug2 = true;
        JSONObject mjsonObject;

        try {
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

            // printing summoner1 ID
            System.out.println(mS1AccountID);

            //constructing URL_Query for 20 ranked game match history
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
                    .appendQueryParameter("endIndex", "8")   // start: 0 -- end: 2 --> only 2 games printed
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
                boolean inFirstFive = false;
                for (int i = 0; i < 5; i++) {
                    JSONObject playerAndId = participantIdentities.getJSONObject(i);
                    JSONObject player = playerAndId.getJSONObject("player");
                    String summonerName = player.getString("summonerName");
                    if (summonerName.equals(SUMMONER_NAME)) {
                        inFirstFive = true;
                        break;
                    }
                }

                // see if that team won or lost
                JSONArray teams = matchJsonObject.getJSONArray("teams");
                JSONObject team1 = teams.getJSONObject(0);
                String win = team1.getString("win");
                boolean won = true;
                if (win.equals("Fail")) {
                    won = false;
                }
                if (inFirstFive && won || !inFirstFive && !won) {
                    mGameIdsAndWinLossMap.put((Long)me.getKey(), true);
                }
            }

            // prints out values of updated map
            if (debug2) {
                System.out.println("changed");
                Set set = mGameIdsAndWinLossMap.entrySet();
                // Displaying elements of LinkedHashMap
                Iterator iterator = set.iterator();
                while (iterator.hasNext()) {
                    Map.Entry me = (Map.Entry) iterator.next();
                    System.out.print("Key is: " + me.getKey() +
                            " & Value is: " + me.getValue() + "\n");
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


    // update the progress bar
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

    }

    // hide the progress bar
    // launches another activity, passing the required information over..
    @Override
    protected void onPostExecute(Void avoid) {
        boolean debug = true;
        if (debug) System.out.println("onPostExecute\n");

        super.onPostExecute(avoid);
        mMainScreenActivity.findViewById(R.id.connect_button).setVisibility(View.VISIBLE);

        // launch another activity... passing in that json
        Intent intent = new Intent(mMainScreenActivity.getApplicationContext(), QueryResultsScreen.class);
        intent.putExtra("gameIdsAndWinLossMap", mGameIdsAndWinLossMap);
        intent.putExtra("matchArrayList", mMatchArrayList);
        mMainScreenActivity.startActivity(intent);
    }

    private String convertStreamToString(InputStream is) {
        try {
            return new java.util.Scanner(is).useDelimiter("\\A").next();
        } catch (java.util.NoSuchElementException e) {
            return "";
        }
    }
}


