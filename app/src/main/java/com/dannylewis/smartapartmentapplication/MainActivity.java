package com.dannylewis.smartapartmentapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //This is the splash screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Determine which screen to send user to
        goSomewhere();
    }

    void goSomewhere() {
        //Get shared preferences
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        if (sharedPref.getBoolean("isConnected", false)) {
            //Go To Dashboard screen

        }
        else {
            //Go To Setup screen
        }
    }
}