package com.dannylewis.smartapartmentapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;

public class SmartHubDetectedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_hub_detected);

        //See if currently connected to hub
        //When detected, call continueSetup
    }

    public void goToConnected(View view) {
        //Go To Smart Hub Connected screen
        Intent intent = new Intent(this, SmartHubConnectedActivity.class);
        startActivity(intent);
    }

    public void openWifiSettings(View view) {
        //Open the wifi settings
        //TODO: Make this process automatic
        startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));

    }

    @Override
    public void onBackPressed() {
        //Do nothing if back button is pressed on this screen
    }
}