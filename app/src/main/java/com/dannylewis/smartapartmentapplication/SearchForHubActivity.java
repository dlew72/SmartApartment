package com.dannylewis.smartapartmentapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SearchForHubActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_hub);

        //Scan WiFi for Hub
        //When Detected, call continueSetup
    }

    public void goToDetected(View view) {
        //Go To Smart Hub Connected screen
        Intent intent = new Intent(this, SmartHubDetectedActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        //Do nothing if back button is pressed on this screen
    }
}