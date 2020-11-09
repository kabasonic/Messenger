package com.kabasonic.messenger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kabasonic.messenger.ui.authorization.otpcode.OTPCodeFragment;
import com.kabasonic.messenger.ui.authorization.otpnumber.OTPNumberFragment;
import com.kabasonic.messenger.ui.onboarding.ScreenSlidePagerActivity;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "com.kabasonic.messenger";

    SharedPreferences settings = null;
    FirebaseAuth mAuth;
    FirebaseUser user;
    private BadgeDrawable mContactBudge, mGroupsBudge, mMessagesBudget;
    private BottomNavigationView bottomNavigationView;
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
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        createBudgets();


        NavController navController = Navigation.findNavController(this, R.id.fragment);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.contactsFragment,
                R.id.groupsFragment, R.id.messagesFragment, R.id.profileFragment,R.id.OTPNumberFragment).build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                bottomNavigationView.setVisibility(View.VISIBLE);
                switch (destination.getId()) {
                    case R.id.profileFragment:
                        getSupportActionBar().hide();
                        break;
                    case R.id.OTPNumberFragment:

                        bottomNavigationView.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.OTPCodeFragment:
                        bottomNavigationView.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.registrationFragment:
                        bottomNavigationView.setVisibility(View.INVISIBLE);
                    default:
                        if (!(destination.getId() == R.id.profileFragment)) {
                            setSupportActionBar(toolbar);
                            getSupportActionBar().show();
                        }
                        break;
                }

            }
        });


    }

    private void createBudgets() {
        mContactBudge = bottomNavigationView.getOrCreateBadge(R.id.contactsFragment);
        mGroupsBudge = bottomNavigationView.getOrCreateBadge(R.id.groupsFragment);
        mMessagesBudget = bottomNavigationView.getOrCreateBadge(R.id.messagesFragment);

        mContactBudge.setNumber(23);
        mGroupsBudge.setNumber(1);
        mMessagesBudget.setNumber(5437865);
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
        if (currentFragment instanceof OTPNumberFragment) {
            getSupportFragmentManager().popBackStack();
        } else if (currentFragment instanceof OTPCodeFragment) {
            Toast.makeText(this,"back",Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }

    public void checkStartOnBoarding() {
        settings = getSharedPreferences(getString(R.string.settings), Context.MODE_PRIVATE);
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

}