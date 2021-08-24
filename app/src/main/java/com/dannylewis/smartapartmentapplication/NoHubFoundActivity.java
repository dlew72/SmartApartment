package com.dannylewis.smartapartmentapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class NoHubFoundActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_hub_found);
    }


    public void reconnect(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    public void reset(View view) {
        //TODO: clear ALL app data
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        settings.edit().clear().commit();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}