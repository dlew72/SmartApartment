package com.dannylewis.smartapartmentapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class SmartHubConnectedActivity extends AppCompatActivity {

    private EditText usern;
    private EditText pass;

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

    public void sendCredentials(View view) throws JSONException {
        Button button = findViewById(R.id.sendCred);
        button.setEnabled(false);
        ProgressBar wait = findViewById(R.id.waitSpin);
        wait.setVisibility(View.VISIBLE);

        usern = findViewById(R.id.networkName);
        pass = findViewById(R.id.passwordNETWORK);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        String url ="http://" + getApIpAddr(this);

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("u###", usern.getText());
        jsonBody.put("p$$$", pass.getText());
        final String requestBody = jsonBody.toString();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url+"/credentials",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("VOLLEY", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.toString());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                    if (responseString.equals("200")) {


                    }
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        SharedPreferences sharedPref = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("previouslySetUp", true);
        editor.commit();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                launchSplash();
            }
        }, 10000);


    }

    public void launchSplash() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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