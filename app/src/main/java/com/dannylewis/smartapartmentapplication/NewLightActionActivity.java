package com.dannylewis.smartapartmentapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;

import java.util.Map;

public class NewLightActionActivity extends AppCompatActivity {

    TextView warmthReadout;
    TextView brightReadout;
    TextView timeReadout;

    RadioButton AM;
    RadioButton PM;

    SeekBar hourSeek;
    SeekBar minuteSeek;
    SeekBar warmthSeek;
    SeekBar brightSeek;

    CheckBox sunCheck;
    CheckBox monCheck;
    CheckBox tueCheck;
    CheckBox wedCheck;
    CheckBox thuCheck;
    CheckBox friCheck;
    CheckBox satCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_light_action);

        //find views by id
        {
            warmthReadout = findViewById(R.id.warmthReadout);
            brightReadout = findViewById(R.id.brightReadout);
            timeReadout = findViewById(R.id.timeReadout);
            AM = findViewById(R.id.radioButtonAM);
            PM = findViewById(R.id.radioButtonPM);
            hourSeek = findViewById(R.id.hour_seek);
            minuteSeek = findViewById(R.id.min_seek);
            warmthSeek = findViewById(R.id.warmth_seek);
            brightSeek = findViewById(R.id.bright_seek);
            sunCheck = findViewById(R.id.checkBoxSUN);
            monCheck = findViewById(R.id.checkBoxMON);
            tueCheck = findViewById(R.id.checkBoxTUES);
            wedCheck = findViewById(R.id.checkBoxWED);
            thuCheck = findViewById(R.id.checkBoxTHURS);
            friCheck = findViewById(R.id.checkBoxFRI);
            satCheck = findViewById(R.id.checkBoxSAT);
        }


        //Add seekbar listeners
        brightSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                brightReadout.setText("" + progressChangedValue + "%");
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
                warmthReadout.setText("" + progressChangedValue + "%");
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        hourSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                setTimeReadout(timeReadout, progressChangedValue, minuteSeek.getProgress());

            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        minuteSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                setTimeReadout(timeReadout, hourSeek.getProgress(), progressChangedValue);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        AM.setOnClickListener(v -> PM.setChecked(false));

        PM.setOnClickListener(v -> AM.setChecked(false));


    }

    public void setTimeReadout(TextView tR, int hour, int min) {
        String newReadout = "" + (hour + 1) + ":";
        if (min < 10)
            newReadout += "0";
        newReadout += "" + min;
        tR.setText(newReadout);

    }

    public void createAction(View view) throws JSONException {
        SharedPreferences sharedPref = getSharedPreferences("ACTIONS", Context.MODE_PRIVATE);
        // Actions are stored in shared preferences in the following format:
        // Key: $[id]
        // Value: [action_class_string]
        // [id] is the action id (integer)
        // see ActionClass.java for more info on [action_class_string]

        short hour = (short) hourSeek.getProgress();
        if (PM.isChecked())
            hour += 12;
        short minute = (short) minuteSeek.getProgress();
        short bright = (short) brightSeek.getProgress();
        short warm = (short) warmthSeek.getProgress();


        if (sunCheck.isChecked()) {
            int nextId = getNextId(sharedPref);
            ActionClass newSunAction = new ActionClass('U', hour, minute, 'L', bright, warm);
            sharedPref.edit().putString("$" + nextId, newSunAction.convertActionToString()).commit();
        }
        if (monCheck.isChecked()) {
            int nextId = getNextId(sharedPref);
            ActionClass newMonAction = new ActionClass('M', hour, minute, 'L', bright, warm);
            sharedPref.edit().putString("$" + nextId, newMonAction.convertActionToString()).commit();
        }
        if (tueCheck.isChecked()) {
            int nextId = getNextId(sharedPref);
            ActionClass newTuesAction = new ActionClass('T', hour, minute, 'L', bright, warm);
            sharedPref.edit().putString("$" + nextId, newTuesAction.convertActionToString()).commit();
        }
        if (wedCheck.isChecked()) {
            int nextId = getNextId(sharedPref);
            ActionClass newWedAction = new ActionClass('W', hour, minute, 'L', bright, warm);
            sharedPref.edit().putString("$" + nextId, newWedAction.convertActionToString()).commit();
        }
        if (thuCheck.isChecked()) {
            int nextId = getNextId(sharedPref);
            ActionClass newThursAction = new ActionClass('R', hour, minute, 'L', bright, warm);
            sharedPref.edit().putString("$" + nextId, newThursAction.convertActionToString()).commit();
        }
        if (friCheck.isChecked()) {
            int nextId = getNextId(sharedPref);
            ActionClass newFriAction = new ActionClass('F', hour, minute, 'L', bright, warm);
            sharedPref.edit().putString("$" + nextId, newFriAction.convertActionToString()).commit();
        }
        if (satCheck.isChecked()) {
            int nextId = getNextId(sharedPref);
            ActionClass newSatAction = new ActionClass('A', hour, minute, 'L', bright, warm);
            sharedPref.edit().putString("$" + nextId, newSatAction.convertActionToString()).commit();
        }

        ScheduleSyncHelper myHelper = new ScheduleSyncHelper(sharedPref, this);

        myHelper.syncSchedule();

        Intent intent = new Intent(this, LightSchedulerActivity.class);
        startActivity(intent);

    }

    int getNextId(SharedPreferences sP) {
        int nextId = 0;

        Map<String, ?> allEntries = sP.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().charAt(0) == '$') {
                nextId++;
            }
        }

        return nextId;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}