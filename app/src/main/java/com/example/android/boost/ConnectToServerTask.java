package com.example.android.boost;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static android.content.ContentValues.TAG;


public class ConnectToServerTask extends AsyncTask<URL, Integer, Void>{

    // show the progress bar
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    // gonna be passed the url...
    // call riot rest api and handle the response
    @Override
    protected Void doInBackground(URL... urls) {
        try {
            URL url = urls[0];
            InputStream inputStream;
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_ACCEPTED) {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                Log.e(TAG, "doInBackground: \n" + convertStreamToString(inputStream));
            }
        } catch (Exception e) {
            e.printStackTrace();

        }


        return null;
    }

    private String convertStreamToString(InputStream is) {
        try {
            return new java.util.Scanner(is).useDelimiter("\\A").next();
        } catch (java.util.NoSuchElementException e) {
            return "";
        }
    }

//    // update the progress bar
//    @Override
//    protected void onProgressUpdate(Integer... values) {
//        super.onProgressUpdate(values);
//    }
//
//    // hide the progress bar, launch another activity
//    @Override
//    protected void onPostExecute(Void aVoid) {
//        super.onPostExecute(aVoid);
//    }
}


