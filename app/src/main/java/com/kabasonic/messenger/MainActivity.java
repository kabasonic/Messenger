package com.kabasonic.messenger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kabasonic.messenger.ui.bottomnavigation.contacts.viewmodels.RequestContactsViewModel;
import com.kabasonic.messenger.ui.onboarding.ScreenSlidePagerActivity;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "com.kabasonic.messenger";

    private BottomNavigationView bottomNavigationView;
    private NavController navController;
    private RequestContactsViewModel requestContactsViewModel;
    private BadgeDrawable mContactBadge;
    private RequestContactsViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set theme for Splash screen
        setTheme(R.style.AppTheme);
        //Check and show onboarding
        checkStartOnBoarding();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initNavigation();
        setBadges();
    }

    private void setBadges() {
        mViewModel = ViewModelProviders.of(this).get(RequestContactsViewModel.class);
        mViewModel.init();
        mViewModel.getCountRequests().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mContactBadge = bottomNavigationView.getOrCreateBadge(R.id.contactsFragment);
                if(integer != 0){
                    mContactBadge.setVisible(true);
                    mContactBadge.setNumber(integer);
                }else{
                    mContactBadge.setVisible(false);
                }
            }
        });
    }


    private void initNavigation() {
        //Set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Set bottom navigation with fragment
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        //Configuration AppBar
        navController = Navigation.findNavController(this, R.id.fragment);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.contactsFragment,
                R.id.messagesFragment,
                R.id.profileFragment).build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        //Listener destination between fragments
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            bottomNavigationView.setVisibility(View.VISIBLE);
            Animation hideBottomNav = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_bottom);
            Animation showBottomNav = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_bottom);
            if (destination.getId() == R.id.OTPNumberFragment ||
                    destination.getId() == R.id.OTPCodeFragment ||
                    destination.getId() == R.id.ediBioFragment ||
                    destination.getId() == R.id.editNicknameFragment ||
                    destination.getId() == R.id.editUsernameFragment ||
                    destination.getId() == R.id.registrationFragment){
                bottomNavigationView.startAnimation(hideBottomNav);
                bottomNavigationView.setVisibility(View.INVISIBLE);
            } else if( destination.getId() == R.id.messagesFragment ||
                        destination.getId() == R.id.profileFragment ||
                        destination.getId() == R.id.contactsFragment){
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });

    }

    /*
        This method is called whenever the user chooses to navigate Up
        within your application's activity hierarchy from the action bar.
         */
    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this, R.id.fragment).navigateUp()
                || super.onSupportNavigateUp();
    }

    private void checkStartOnBoarding() {
        SharedPreferences settings = getSharedPreferences(getString(R.string.settings), Context.MODE_PRIVATE);
        //Create variable settings for preferences
        if (!settings.getBoolean("StartOnBoarding", false)) {
            //First run application
            //Write the fact that the app has been started at least once
            Log.i(TAG, "First run application");
            settings.edit().putBoolean("StartOnBoarding", true).apply();
            startActivity(new Intent(this, ScreenSlidePagerActivity.class));
        } else {
            Log.i(TAG, "Not first run application");
        }
    }

    @Override
    public void onBackPressed() {

        int id = Objects.requireNonNull(navController.getCurrentDestination()).getId();
        Log.d(TAG, "Back button pressed id: " + id);
        switch (id) {
            case R.id.OTPNumberFragment:
                Log.d(TAG, "Finish application");
                finish();
                break;
            case R.id.OTPCodeFragment:
                Log.d(TAG, "Back to OTPNumber fragment");
                navController.navigateUp();
                break;
            case R.id.registrationFragment:
                Log.d(TAG, "Back to OTPCodeFragment");
                navController.navigateUp();
                break;

            default:
                Log.d(TAG, "onBackPressed return NULL");
                break;
        }
    }
}