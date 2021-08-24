package com.dannylewis.smartapartmentapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class DashboardActivity extends AppCompatActivity {
    private SeekBar brightSeek;
    private SeekBar warmthSeek;
    private View lightOpac;

    //Shade:
    private SeekBar shadeSeek;

    //Readout:
    private TextView readOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        //Find views by ID
        {
            //Light
            brightSeek = (SeekBar) findViewById(R.id.dashBrightSeek);
            warmthSeek = (SeekBar) findViewById(R.id.dashWarmSeek);
            lightOpac = findViewById(R.id.dashLightOpac);

            //Shade:
            shadeSeek = (SeekBar) findViewById(R.id.dashWinSeek);

            //Readout:
            readOut = (TextView) findViewById(R.id.dashReadoutXX);
        }

        //Inititalize dynamic components on screen
        initComponents();

        brightSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                changeBrightness(progressChangedValue);

                //Handle dynamic bulb
                lightOpac.setAlpha((float)(progressChangedValue/100.0));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        warmthSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                changeWarmth(progressChangedValue);

                //Handle dynamic bulb
                String rVal = "FF";
                String gVal = Integer.toHexString((int)(255-(progressChangedValue*.6)));
                String bVal = Integer.toHexString((int)(255-(progressChangedValue*1.28)));
                Log.d("dannyDebug", rVal + gVal + bVal);
                lightOpac.setBackgroundColor(Color.parseColor("#" + rVal + gVal + bVal));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_calendar) {

            // Do something
            return true;
        }
        if (id == R.id.action_settings) {

            // Do something
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initComponents() {
        //Light
        //TODO: lights
        lightOpac.setAlpha(0);

        //Shade
        //TODO: shade

        //Readout
        setLux(0);
        //TODO: setInterval to check for new lux values and set them
    }

    void setLux(int xx) {
        readOut.setText(xx + " lux");
    }

    void changeBrightness(int newValue) {
        //TODO: send newValue to arduino
    }

    void changeWarmth(int newValue) {
        //TODO: send newValue to arduino
    }
}