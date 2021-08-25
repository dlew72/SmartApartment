package com.dannylewis.smartapartmentapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SettingsDashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_dash);
    }

    public void goToLightSettings(View view) {
        Intent intent = new Intent(this, LightSettingsActivity.class);
        startActivity(intent);
    }

    public void goToShadeSettings(View view) {
        Intent intent = new Intent(this, ShadeSettingsActivity.class);
        startActivity(intent);
    }
}