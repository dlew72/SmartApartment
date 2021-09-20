package com.dannylewis.smartapartmentapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SmartHubConnectedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_hub_connected);
    }

    public void goToDash(View view) {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        //Do nothing if back button is pressed on this screen
    }

    public void sendCredentials(View view) {
        TextView displayData = findViewById(R.id.dispData);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        String url ="http://" + getApIpAddr(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+"/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        displayData.setText("Response is: "+ response);
                    }
                }, (Response.ErrorListener) error -> displayData.setText("That didn't work! +" + error));

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public static String getApIpAddr(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        byte[] ipAddress = convert2Bytes(dhcpInfo.serverAddress);
        try {
            String apIpAddr = InetAddress.getByAddress(ipAddress).getHostAddress();
            return apIpAddr;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] convert2Bytes(int hostAddress) {
        byte[] addressBytes = { (byte)(0xff & hostAddress),
                (byte)(0xff & (hostAddress >> 8)),
                (byte)(0xff & (hostAddress >> 16)),
                (byte)(0xff & (hostAddress >> 24)) };
        return addressBytes;
    }
}