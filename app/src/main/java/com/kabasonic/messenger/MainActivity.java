package com.kabasonic.messenger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.kabasonic.messenger.ui.onboarding.ScreenSlidePagerActivity;
import com.kabasonic.messenger.ui.onboarding.pages.ScreenSlidePageFragmentOne;

public class MainActivity extends AppCompatActivity  {
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

        //Set toolbar
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Set bottom navigation with fragment
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this,R.id.fragment);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.contactsFragment,
                R.id.groupsFragment,R.id.messagesFragment,R.id.profileFragment).build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView,navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if(destination.getId() == R.id.profileFragment){
                    getSupportActionBar().hide();

                }else{
                    setSupportActionBar(toolbar);
                    getSupportActionBar().show();
                }
            }
        });


    }

    public void checkStartOnBoarding(){
        settings = getSharedPreferences(getString(R.string.settings), Context.MODE_PRIVATE);
        //Create variable settings for preferences
        if(!settings.getBoolean("StartOnBoarding",false)){
            //First run application
            //Write the fact that the app has been started at least once
            Log.i(TAG,"First run application");
            //settings.edit().putBoolean("StartOnBoarding",true).apply();
            startActivity(new Intent(this, ScreenSlidePagerActivity.class));
        }else{
            Log.i(TAG,"Not first run application");
        }
    }

}