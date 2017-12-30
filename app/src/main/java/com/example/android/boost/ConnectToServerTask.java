package com.example.android.boost;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static android.content.ContentValues.TAG;


public class ConnectToServerTask extends AsyncTask<URL, Integer, Void> {

    private Activity mMainScreenActivity;
    private JSONObject jsonObject;

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
    protected Void doInBackground(URL... urls) {
        System.out.println("doInBackground");
        try {
            URL url = urls[0];
            InputStream inputStream;
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.connect();

            // response code of RIOT API
            // TODO: create helper for response code answers
            int responseCode = urlConnection.getResponseCode();

            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            String json = convertStreamToString(inputStream);
            System.out.println(json);

            jsonObject = new JSONObject(json);

            int summonerLevel = jsonObject.getInt("summonerLevel");
            System.out.println(summonerLevel);

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

    // hide the progress bar, launch another activity
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mMainScreenActivity.findViewById(R.id.connect_button).setVisibility(View.VISIBLE);
    }

    private String convertStreamToString(InputStream is) {
        try {
            return new java.util.Scanner(is).useDelimiter("\\A").next();
        } catch (java.util.NoSuchElementException e) {
            return "";
        }
    }
}


