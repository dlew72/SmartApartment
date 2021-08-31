package com.dannylewis.smartapartmentapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

public class ShadeSettingsActivity extends AppCompatActivity {

    //Setting variables
    private int minShade;
    private int maxShade;

    //Seekbars
    private SeekBar minShadeSeek;
    private SeekBar maxShadeSeek;

    //Displayed ranges
    private TextView shadeRange;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shade_settings);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

        //Find view by ID
        {
            minShadeSeek = findViewById(R.id.minShadeSeek);
            maxShadeSeek = findViewById(R.id.maxShadeSeek);

            shadeRange = findViewById(R.id.shadeRangeDisp);
        }

        SharedPreferences sharedPref = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
        minShade = sharedPref.getInt("minShade", 0);
        maxShade = sharedPref.getInt("maxShade", 100);

        minShadeSeek.setProgress(minShade*2);
        maxShadeSeek.setProgress(((maxShade-50)*2));

        setShadeRangeDisplay();

        //Set up listeners
        {
            //Min Shade
            {
                minShadeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        minShade = progress / 2;
                        setShadeRangeDisplay();
                    }

                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //Store new value in sharedPrefs
                        sharedPref.edit().putInt("minShade", minShade).commit();
                    }
                });
            }

            //Max Shade
            {
                maxShadeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        maxShade = progress / 2 + 50;
                        setShadeRangeDisplay();
                    }

                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //Store new value in sharedPrefs
                        sharedPref.edit().putInt("maxShade", maxShade).apply();
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

    private void setShadeRangeDisplay() {
        shadeRange.setText("" + minShade + "-" + maxShade + "%");
        shadeRange.getLayout();
    }
}