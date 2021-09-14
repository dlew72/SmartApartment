package com.dannylewis.smartapartmentapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class DashboardActivity extends AppCompatActivity {
    private SeekBar brightSeek;
    private SeekBar warmthSeek;
    private View lightOpac;
    private ImageView setDashLight;
    private ImageView setDashWin;


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
                changeBrightness(progressChangedValue);

                //Handle dynamic bulb
                if (progressChangedValue == 0)
                    lightOpac.setAlpha((float)(0));
                else
                    lightOpac.setAlpha((float)(progressChangedValue/200.0 + .5));

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
                lightOpac.setBackgroundColor(Color.parseColor("#" + rVal + gVal + bVal));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        shadeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                updateShadeImage(progressChangedValue, setDashWin);
                changeShadePosition(progressChangedValue);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


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
        readOut.setText(xx + " lux");
    }

    void changeBrightness(int newValue) {
        //TODO: send newValue to arduino
    }

    void changeWarmth(int newValue) {
        //TODO: send newValue to arduino
    }

    void changeShadePosition(int newValue) {
        //TODO: send newValue to arduino
    }

    @Override
    public void onBackPressed() {
        //Do nothing if back button is pressed on dashboard
    }
}