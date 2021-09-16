package com.dannylewis.smartapartmentapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_PERMISSION_ACCESS_WIFI_STATE = 1;
    private final int REQUEST_PERMISSION_ACCESS_FINE_LOCATION = 2;
    private final int REQUEST_PERMISSION_CHANGE_WIFI_STATE = 3;


    private boolean wifiAccessPerm = false;
    private boolean wifiChangePerm = false;
    private boolean locPerm = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //This is the splash screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Determine which screen to send user to
        getPermissions();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                goSomewhere();
            }
        }, 5000);
    }

    void goSomewhere() {
        //Get shared preferences
        SharedPreferences sharedPref = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
        if (sharedPref.getBoolean("isConnected", false)) {
            //Hub is supposedly set up
            //Test Connection
            if (testConnection()) {
                //Go To Dashboard screen
                Intent intent = new Intent(this, DashboardActivity.class);
                startActivity(intent);
            } else {
                //Go To NoHubFound Screen
                Intent intent = new Intent(this, NoHubFoundActivity.class);
                startActivity(intent);
            }

        } else if (wifiAccessPerm && locPerm && wifiChangePerm) { //Hub has not been set up, attempt automatic setup
            if (autoConnectHub()) {
                //Go To Smart Hub Connected screen
                Intent intent = new Intent(this, SmartHubConnectedActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, SmartHubManualSetup.class);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(this, SmartHubManualSetup.class);
            startActivity(intent);
        }
    }

    //Hub is supposedly set up, see if it is reachable
    boolean testConnection() {
        //TODO: Try sending a hello packet
        Random rd = new Random();
        return rd.nextBoolean();
    }

    void getPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.ACCESS_ACCESS_WIFI_STATE"}, REQUEST_PERMISSION_ACCESS_WIFI_STATE);
        } else {
            this.wifiAccessPerm = true;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.CHANGE_ACCESS_WIFI_STATE"}, REQUEST_PERMISSION_CHANGE_WIFI_STATE);
        } else {
            this.wifiChangePerm = true;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_ACCESS_WIFI_STATE"}, REQUEST_PERMISSION_ACCESS_FINE_LOCATION);
        } else {
            this.locPerm = true;
        }

    }


    //Hub is NOT set up, see if it is detectable
    boolean autoConnectHub() {
        //TODO: Fix this, it doesnt work. netid = -1, issue starts there
        String networkSSID = "ECE Smart Hub";
        String networkPass = "seniordesign";

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain ssid in quotes
        conf.preSharedKey = "\"" + networkPass + "\"";

        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int netid = wifiManager.addNetwork(conf);

        wifiManager.enableNetwork(netid, true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();

        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                break;
            }
            else {
            }
        }
        if (wifiManager.getConnectionInfo().getSSID() == "ECE Smart Hub") {
            return true; //it worked
        }
        else
            return false; //resort to manual setup
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_ACCESS_FINE_LOCATION) {

            // Checking whether user granted the permission or not.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.locPerm = true;
            }
            else {
                Toast.makeText(MainActivity.this, "Auto-setup disabled", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == REQUEST_PERMISSION_ACCESS_WIFI_STATE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.wifiAccessPerm = true;
            }
            else {
                Toast.makeText(MainActivity.this, "Auto-setup disabled", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == REQUEST_PERMISSION_CHANGE_WIFI_STATE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.wifiChangePerm = true;
            }
            else {
                Toast.makeText(MainActivity.this, "Auto-setup disabled", Toast.LENGTH_SHORT).show();
            }
        }
    }
}