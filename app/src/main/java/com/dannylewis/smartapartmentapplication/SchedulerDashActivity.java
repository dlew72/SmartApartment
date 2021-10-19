package com.dannylewis.smartapartmentapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.Map;

public class SchedulerDashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduler_dash);

        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
    }

    public void goToShadeScheduler(View view) {
        Intent intent = new Intent(this, ShadeSchedulerActivity.class);
        startActivity(intent);
    }

    public void goToLightScheduler(View view) {
        Intent intent = new Intent(this, LightSchedulerActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

    public static void syncSchedule(SharedPreferences sP) {
        //Send schedule from shared prefs to Hub
        Log.d("map values1", "\n*********\n");
        Map<String, ?> allEntries = sP.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("map values1", entry.getKey() + ": " + entry.getValue().toString());
        }
        Log.d("map values1", "\n*********\n");

    }
}