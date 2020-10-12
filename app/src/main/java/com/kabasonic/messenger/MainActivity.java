package com.kabasonic.messenger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.kabasonic.messenger.ui.onboarding.ScreenSlidePagerActivity;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "com.kabasonic.messenger";

    SharedPreferences  settings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set theme for Splash screen
        setTheme(R.style.AppTheme);

        //Check and show onboarding
        checkStartOnBoarding();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void checkStartOnBoarding(){
        settings = getSharedPreferences(getString(R.string.settings), Context.MODE_PRIVATE);
        //Create variable settings for preferences
        if(!settings.getBoolean("StartOnBoarding",false)){
            //First run application
            //Write the fact that the app has been started at least once
            Log.i(TAG,"First run application");
            settings.edit().putBoolean("StartOnBoarding",true).apply();
            startActivity(new Intent(this, ScreenSlidePagerActivity.class));
        }else{
            Log.i(TAG,"Not first run application");
        }
    }

}