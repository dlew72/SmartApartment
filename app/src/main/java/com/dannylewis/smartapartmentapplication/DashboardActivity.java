package com.dannylewis.smartapartmentapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
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
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

public class DashboardActivity extends AppCompatActivity {
    private SeekBar brightSeek;
    private SeekBar warmthSeek;
    private View lightOpac;
    private ImageView setDashLight;
    private ImageView setDashWin;
    private short curWarmth = 0;
    private short curBrightness = 0;
    private short curPos = 0;
    private int tempW = 0;
    private int tempB = 0;
    private int tempP = 0;

    private int minBright = 0;
    private int maxBright = 100;
    private int minWarmth = 0;
    private int maxWarmth = 100;
    private int minShade = 0;
    private int maxShade = 100;


    //Shade:
    private SeekBar shadeSeek;

    //Readout:
    private TextView readOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        SharedPreferences sP = getSharedPreferences("ACTIONS", Context.MODE_PRIVATE);
        ScheduleSyncHelper myHelper = new ScheduleSyncHelper(sP, this);

        try {
            myHelper.syncSchedule();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Load settings
        {
            SharedPreferences sharedPref = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
            minBright = sharedPref.getInt("minBright", 0);
            maxBright = sharedPref.getInt("maxBright", 100);
            minWarmth = sharedPref.getInt("minWarmth", 0);
            maxWarmth = sharedPref.getInt("maxWarmth", 100);
            minShade = sharedPref.getInt("minShade", 0);
            maxShade = sharedPref.getInt("maxShade", 100);
        }
        //Find views by ID
        {
            //Light
            brightSeek = (SeekBar) findViewById(R.id.dashBrightSeek);
            warmthSeek = (SeekBar) findViewById(R.id.dashWarmSeek);
            lightOpac = findViewById(R.id.dashLightOpac);
            setDashLight = (ImageView) findViewById(R.id.setDashLightImg);
            setDashWin = (ImageView) findViewById(R.id.setDashWinImg);


            //Set opac height and width:
            int height = setDashLight.getHeight();
            int width = setDashLight.getWidth();

            lightOpac.getLayoutParams().height = height;
            lightOpac.getLayoutParams().width = width;
            lightOpac.requestLayout();

            //Shade:
            shadeSeek = (SeekBar) findViewById(R.id.dashWinSeek);

            //Readout:
            readOut = (TextView) findViewById(R.id.dashReadoutXX);
        }

        //Initialize dynamic components on screen from HUB VALUES
        {
            //Light
            //TODO: lights
            lightOpac.setAlpha(0);

            //Shade
            updateShadeImage(0, setDashWin);
            //TODO: shade

            //Readout
            setLux(0);
            //TODO: setInterval to check for new lux values and set them
        }

        //Add seekbar listeners
        brightSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                curBrightness = (short)progressChangedValue;

                if (tempB == -1) {
                    tempB = curBrightness;
                }

                if (Math.abs(tempB-curBrightness) > 20) {
                    tempB = curBrightness;
                    try {
                        changeBrightness(curBrightness);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                //Handle dynamic bulb
                if (progressChangedValue == 0)
                    lightOpac.setAlpha((float)(0));
                else
                    lightOpac.setAlpha((float)(progressChangedValue/200.0 + .5));

            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                tempB = -1;
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            changeBrightness((short)progressChangedValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, 300);
            }
        });

        warmthSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                curWarmth = (short)progressChangedValue;
                if (tempW == -1) {
                    tempW = curWarmth;
                }

                if (Math.abs(tempW-curWarmth) > 20) {
                    tempW = curWarmth;
                    try {
                        changeWarmth(curWarmth);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //Handle dynamic bulb
                String rVal = "FF";
                String gVal = Integer.toHexString((int)(255-(progressChangedValue*.6)));
                String bVal = Integer.toHexString((int)(255-(progressChangedValue*1.28)));
                lightOpac.setBackgroundColor(Color.parseColor("#" + rVal + gVal + bVal));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                tempW = -1;
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            changeWarmth((short)curWarmth);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, 300);

            }
        });

        shadeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                curPos = (short)progressChangedValue;
                updateShadeImage(progressChangedValue, setDashWin);

                if (tempP == -1) {
                    tempP = curPos;
                }

                /*if (Math.abs(tempP -curPos) > 20) {
                    tempP = curPos;
                    try {
                        changeShadePosition(curPos);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }*/
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                tempP = -1;
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            changeShadePosition((short)curPos);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, 300);
            }
        });

        try {
            getStates();
        } catch (JSONException e) {
            e.printStackTrace();
        }

       /* new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                try {
                    Log.d("BRIGHTNESSTAG", "Trying to get brightnes...");
                    getBrightness();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },0,5000);*/


    }

    private void updateShadeImage(int progressChangedValue, ImageView i) {
        if (progressChangedValue < 13) {
            i.setImageResource(R.drawable.window_shade_icon_0);
        } else if (progressChangedValue < 38) {
            i.setImageResource(R.drawable.window_shade_icon_25);
        } else if (progressChangedValue < 63) {
            i.setImageResource(R.drawable.window_shade_icon_50);
        } else if (progressChangedValue < 88) {
            i.setImageResource(R.drawable.window_shade_icon_75);
        } else {
            i.setImageResource(R.drawable.window_shade_icon_100);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_calendar) {
            Intent intent = new Intent(this, SchedulerDashActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsDashActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    void setLux(int xx) {
        readOut.setText("----");
        //readOut.setText(xx + " lux");
    }

    void changeBrightness(short newValue) throws JSONException {
        SharedPreferences sharedPref = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
        minBright = sharedPref.getInt("minBright", 0);
        maxBright = sharedPref.getInt("maxBright", 100);
        int actualBrightnessScale = (maxBright - minBright);
        newValue = (short) (newValue/100.0*actualBrightnessScale + minBright);

        //char wd, short h, short m, char aT, short p1, short p2) {
        ActionClass tempAction = new ActionClass('X', (short) 0, (short) 0, 'L', newValue, curWarmth);

        sendPacket(tempAction);

        tempAction = null;
    }

    void changeWarmth(int newValue) throws JSONException {
        SharedPreferences sharedPref = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
        minWarmth = sharedPref.getInt("minWarmth", 0);
        maxWarmth = sharedPref.getInt("maxWarmth", 100);
        int actualWarmthScale = (maxWarmth - minWarmth);
        newValue = (short) (newValue/100.0*actualWarmthScale + minWarmth);

        //char wd, short h, short m, char aT, short p1, short p2) {
        ActionClass tempAction = new ActionClass('X', (short) 0, (short) 0, 'L', curBrightness, (short)newValue);

        sendPacket(tempAction);

        tempAction = null;

    }

    void changeShadePosition(int newValue) throws JSONException {

        SharedPreferences sharedPref = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
        minShade = sharedPref.getInt("minShade", 0);
        maxShade = sharedPref.getInt("maxShade", 100);
        int actualShadeScale = (maxShade - minShade);
        newValue = (short) (newValue/100.0*actualShadeScale + minShade);

        //char wd, short h, short m, char aT, short p1, short p2) {
        ActionClass tempAction = new ActionClass('X', (short) 0, (short) 0, 'S', (short)newValue, (short)0);

        sendPacket(tempAction);

        tempAction = null;
    }

    @Override
    public void onBackPressed() {
        //Do nothing if back button is pressed on dashboard
    }

    void sendPacket(ActionClass myAction) throws JSONException {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        String url ="http://192.168.0.177"; //hardcoded hub ip

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("a###", myAction.convertActionToString());
        final String requestBody = jsonBody.toString();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url+"/action",
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
    }

    void getStates() throws JSONException {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        String url ="http://192.168.0.177"; //hardcoded hub ip


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+"/getStates",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("VOLLEY1", "Response:" + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY1", "error:" + error.toString());
            }
        }) {


            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {

                    responseString = String.valueOf(response.statusCode);
                    Log.d("volley1", "responseString:" + responseString);

                    if (responseString.equals("200")) {

                        Log.d("volley1", "response.data:" + response.data);
                        String s = new String(response.data, StandardCharsets.UTF_8);
                        Log.d("volley1", "response.dataCONVERT: " + s);
                        String brightness = s.substring(3, s.indexOf("#W"));
                        String warmth = s.substring(s.indexOf("#W") + 2, s.indexOf("$S"));
                        String pos = s.substring(s.indexOf("S:") + 2);
                        Log.d("volley1", "brightness: " + brightness);
                        Log.d("volley1", "warmth: " + warmth);
                        Log.d("volley1", "pos: " + pos);

                        brightSeek.setProgress(Integer.parseInt(brightness));
                        warmthSeek.setProgress(Integer.parseInt(warmth));
                        shadeSeek.setProgress(Integer.parseInt(pos)/20);

                    }
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        // Add the request to the RequestQueue.

        queue.add(stringRequest);
        Log.d("volley1", "stringRequest to add:" + stringRequest.toString());

    }





    /*void getBrightness() throws JSONException {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        String url ="http://192.168.0.177"; //hardcoded hub ip

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("b###", "brightness");
        final String requestBody = jsonBody.toString();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+"/brightness",
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
    }*/
}