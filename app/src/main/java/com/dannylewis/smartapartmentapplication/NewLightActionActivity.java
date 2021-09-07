package com.dannylewis.smartapartmentapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class NewLightActionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_light_action);

        //REGEX FOR TIME HH:MM (OPTIONAL LEADING 0)
        //   /^(0?[1-9]|1[0-2]):[0-5][0-9]$/
    }
}