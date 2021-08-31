package com.dannylewis.smartapartmentapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

public class LightSettingsActivity extends AppCompatActivity {

    //Setting variables
    private int minBright;
    private int maxBright;
    private int minWarmth;
    private int maxWarmth;

    //Seekbars
    private SeekBar minBrightSeek;
    private SeekBar maxBrightSeek;
    private SeekBar minWarmthSeek;
    private SeekBar maxWarmthSeek;

    //Displayed ranges
    private TextView brightRange;
    private TextView warmthRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_settings);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

        //Find view by ID
        {
            minBrightSeek = findViewById(R.id.minBrightSeek);
            maxBrightSeek = findViewById(R.id.maxBrightSeek);
            minWarmthSeek = findViewById(R.id.minWarmSeek);
            maxWarmthSeek = findViewById(R.id.maxWarmSeek);

            brightRange = findViewById(R.id.brightRangeDisp);
            warmthRange = findViewById(R.id.warmRangeDisp);
        }

        SharedPreferences sharedPref = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
        minBright = sharedPref.getInt("minBright", 0);
        maxBright = sharedPref.getInt("maxBright", 100);
        minWarmth = sharedPref.getInt("minWarmth", 0);
        maxWarmth = sharedPref.getInt("maxWarmth", 100);

        minBrightSeek.setProgress(minBright*2);
        maxBrightSeek.setProgress(((maxBright-50)*2));

        minWarmthSeek.setProgress(minWarmth*2);
        maxWarmthSeek.setProgress(((maxWarmth-50)*2));

        setBrightRangeDisplay();
        setWarmthRangeDisplay();

        //Set up listeners
        {
            //Min Bright
            {
                minBrightSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        minBright = progress/2;
                        setBrightRangeDisplay();
                    }

                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //Store new value in sharedPrefs
                        sharedPref.edit().putInt("minBright", minBright).commit();
                    }
                });
            }

            //Max Bright
            {
                maxBrightSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        maxBright = progress/2 + 50;
                        setBrightRangeDisplay();
                    }

                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //Store new value in sharedPrefs
                        sharedPref.edit().putInt("maxBright", maxBright).apply();
                    }
                });
            }

            //Min Warmth
            {
                minWarmthSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        minWarmth = progress/2;
                        setWarmthRangeDisplay();
                    }

                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //Store new value in sharedPrefs
                        sharedPref.edit().putInt("minWarmth", minWarmth).apply();
                    }
                });
            }

            //Max Warmth
            {
                maxWarmthSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        maxWarmth = progress/2 + 50;
                        setWarmthRangeDisplay();
                    }

                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //Store new value in sharedPrefs
                        sharedPref.edit().putInt("maxWarmth", maxWarmth).apply();
                    }
                });
            }
        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setBrightRangeDisplay() {
        brightRange.setText("" + minBright + "-" + maxBright + "%");
        brightRange.getLayout();
    }

    private void setWarmthRangeDisplay() {
        warmthRange.setText("" + minWarmth + "-" + maxWarmth + "%");
        warmthRange.getLayout();
    }
}