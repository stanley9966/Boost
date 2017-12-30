package com.example.android.boost;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.net.URL;

public class MainScreenActivity extends AppCompatActivity {

    private static final String REGION = "na1";
    private static final String URL_QUERY = "https://na1.api.riotgames.com/lol/summoner/v3/summoners/by-name/";
    private static final String SUMMONER_NAME = "Ankspankin";
    private static final String TEMP_API_KEY = "RGAPI-f2f43144-ec93-4ef9-bf47-a7fa80d68ee8";
    private static final String SUMMONER_ID = "200612155";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        Button connectButton = (Button) findViewById(R.id.connect_button);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {   // creating url with api_key... dunno if need
                    URL endpoint = new URL(URL_QUERY + SUMMONER_NAME + "?api_key=" + TEMP_API_KEY);
                    //System.out.println(URL_QUERY + SUMMONER_NAME + "?api_key=" + TEMP_API_KEY);
                    new ConnectToServerTask(MainScreenActivity.this).execute(endpoint);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
