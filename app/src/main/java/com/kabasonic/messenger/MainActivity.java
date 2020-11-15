package com.kabasonic.messenger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kabasonic.messenger.ui.onboarding.ScreenSlidePagerActivity;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "com.kabasonic.messenger";

    private BottomNavigationView bottomNavigationView;
    private BadgeDrawable mContactBudge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set theme for Splash screen
        setTheme(R.style.AppTheme);
        //Check and show onboarding
        checkStartOnBoarding();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initNavigation();

        createBudgets();
        initNavigation();

        mContactBudge = bottomNavigationView.getOrCreateBadge(R.id.contactsFragment);
        countRequestContact();
    }

    private void initNavigation() {
        //Set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Set bottom navigation with fragment
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        //Configuration AppBar
        NavController navController = Navigation.findNavController(this,R.id.fragment);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.contactsFragment,
                R.id.groupsFragment,
                R.id.messagesFragment,
                R.id.profileFragment).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        //Listener destination between fragments
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            bottomNavigationView.setVisibility(View.VISIBLE);
            switch (destination.getId()) {
                case R.id.contactsFragment:
                    break;
                case R.id.groupsFragment:
                    break;
                case R.id.messagesFragment:
                    break;
                case R.id.profileFragment:
                    break;
                case R.id.userProfileFragment:
                    break;
                default:
                    //Sets animation, when bottom navigation need hide
                    Animation hideBottomNav = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_bottom);
                    bottomNavigationView.startAnimation(hideBottomNav);
                    bottomNavigationView.setVisibility(View.INVISIBLE);
                    break;
            }
        });
    }


//    @Override
//    public void onBackPressed() {
////        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
////        if (currentFragment instanceof OTPNumberFragment) {
////            getSupportFragmentManager().popBackStack();
////        } else if (currentFragment instanceof OTPCodeFragment) {
////            Toast.makeText(this, "back", Toast.LENGTH_SHORT).show();
////        } else {
////            finish();
////        }
//    }

    /*
    This method is called whenever the user chooses to navigate Up
    within your application's activity hierarchy from the action bar.
     */
    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this, R.id.fragment).navigateUp()
                || super.onSupportNavigateUp();
    }

    private void countRequestContact() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("request").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int countRequest = 0;

                countRequest = (int) snapshot.getChildrenCount();
                Log.d(TAG, "Count request: " + countRequest);
                contactRequest(countRequest);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void contactRequest(int countRequest){
        if(countRequest !=0){
            mContactBudge.setVisible(true);
            mContactBudge.setNumber(countRequest);
        }else{
            mContactBudge.setVisible(false);
        }
    }

    private void createBudgets() {
        BadgeDrawable mGroupsBudge = bottomNavigationView.getOrCreateBadge(R.id.groupsFragment);
        BadgeDrawable mMessagesBudget = bottomNavigationView.getOrCreateBadge(R.id.messagesFragment);

        mGroupsBudge.setNumber(1);
        mMessagesBudget.setNumber(5437865);
    }

    public void checkStartOnBoarding() {
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

}