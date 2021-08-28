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
        if (sharedPref.getBoolean("isConnected", false)) {
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
            //Search for the Hub --> if found, go to HubDetectedActivity... else, NoHubFoundScreen
            if (detectHub()) {
                //Go To Smart Hub Connected screen
                Intent intent = new Intent(this, SmartHubDetectedActivity.class);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(this, NoHubFoundActivity.class);
                startActivity(intent);
            }
        }
    }

    //Hub is supposedly set up, see if it is reachable
    boolean testConnection() {
        //TODO: Try sending a hello packet
        Random rd = new Random();
        return rd.nextBoolean();
    }

    //Hub is NOT set up, see if it is detectable
    boolean detectHub() {
        //TODO: Scan wifi for hardcoded name
        return true;
    }
}