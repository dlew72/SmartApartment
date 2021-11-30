package com.dannylewis.smartapartmentapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class SmartHubManualSetup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_setup);
    }

    public void testConnection(View view) {
        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager.getConnectionInfo().getSSID().equals("\"ECE Smart Hub\"")) {
            Intent intent = new Intent(this, SmartHubConnectedActivity.class);
            startActivity(intent);
        }
        else {
            //Toast.makeText(SmartHubManualSetup.this, "Error... Check WiFi Network", Toast.LENGTH_SHORT).show();
            Toast.makeText(SmartHubManualSetup.this, wifiManager.getConnectionInfo().getSSID(), Toast.LENGTH_SHORT).show();

        }

    }


    public void openWifiSettings(View view) {
        //Open the wifi settings
        startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));

    }

    @Override
    public void onBackPressed() {
        //Do nothing if back button is pressed on this screen
    }
}