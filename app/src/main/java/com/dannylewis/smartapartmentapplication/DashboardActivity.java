package com.dannylewis.smartapartmentapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
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
                lightOpac.setAlpha((float)(progressChangedValue/100.0));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                lightOpac.setAlpha((float)(progressChangedValue/100.0));
            }
        });

        warmthSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                String rVal = "FF";
                String gVal = Integer.toHexString((int)(255-(progressChangedValue*.6)));
                String bVal = Integer.toHexString((int)(255-(progressChangedValue*1.28)));
                lightOpac.setBackgroundColor(Color.parseColor("#" + rVal + gVal + bVal));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                lightOpac.setAlpha((float)(progressChangedValue/100.0));
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

        //Shade
        //TODO: shade

        //Readout
        setLux(0);
        //TODO: setInterval to check for new lux values and set them
    }

    void setLux(int xx) {
        readOut.setText(xx + " lux");
    }
}