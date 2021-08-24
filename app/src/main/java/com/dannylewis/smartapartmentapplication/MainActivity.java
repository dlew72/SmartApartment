package com.dannylewis.smartapartmentapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //This is the splash screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Determine which screen to send user to
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                goSomewhere();
            }
        }, 3000);

    }

    void goSomewhere() {
        //Get shared preferences
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        if (sharedPref.getBoolean("isConnected", true)) {
            //Hub is supposedly set up
            //Test Connection
            if (testConnection()) {
                //Go To Dashboard screen
                Intent intent = new Intent(this, DashboardActivity.class);
                startActivity(intent);
            }
            else {
                //Go To NoHubFound Screen
                Intent intent = new Intent(this, NoHubFoundActivity.class);
                startActivity(intent);
            }

        }
        else { //Hub has not been set up
            //Go To Search For Hub screen
            Intent intent = new Intent(this, SearchForHubActivity.class);
            startActivity(intent);
        }
    }

    boolean testConnection() {
        Random rd = new Random();
        return rd.nextBoolean();
    }
}