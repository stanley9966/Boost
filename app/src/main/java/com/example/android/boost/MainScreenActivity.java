package com.example.android.boost;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.net.URL;

public class MainScreenActivity extends AppCompatActivity {

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
                    new ConnectToServerTask(MainScreenActivity.this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
