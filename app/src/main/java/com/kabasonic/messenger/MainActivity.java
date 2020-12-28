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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.kabasonic.messenger.models.Chat;
import com.kabasonic.messenger.models.User;
import com.kabasonic.messenger.notifications.Token;
import com.kabasonic.messenger.ui.adapters.AdapterChat;
import com.kabasonic.messenger.ui.bottomnavigation.contacts.viewmodels.RequestContactsViewModel;
import com.kabasonic.messenger.ui.onboarding.ScreenSlidePagerActivity;
import com.kabasonic.messenger.ui.userchat.UserChat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "com.kabasonic.messenger";

    private BottomNavigationView bottomNavigationView;
    private NavController navController;
    private RequestContactsViewModel requestContactsViewModel;
    private BadgeDrawable mContactBadge;
    private RequestContactsViewModel mViewModel;

    private FirebaseUser user;

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
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userStatus("Online");

            SharedPreferences sharedPreferences = getSharedPreferences("SP_USER",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Current_USERID",user.getUid());
            editor.apply();

        }

        updateToken(FirebaseInstanceId.getInstance().getToken());

    }

    @Override
    protected void onResume() {
        super.onResume();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userStatus("Online");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userStatus("Offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userStatus("Offline");
        }
    }

    private void setBadges() {
        mViewModel = ViewModelProviders.of(this).get(RequestContactsViewModel.class);
        mViewModel.init();
        mViewModel.getCountRequests().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mContactBadge = bottomNavigationView.getOrCreateBadge(R.id.contactsFragment);
                if (integer != 0) {
                    mContactBadge.setVisible(true);
                    mContactBadge.setNumber(integer);
                } else {
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
                    destination.getId() == R.id.editBioFragment ||
                    destination.getId() == R.id.editNicknameFragment ||
                    destination.getId() == R.id.editUsernameFragment ||
                    destination.getId() == R.id.registrationFragment) {
                bottomNavigationView.startAnimation(hideBottomNav);
                bottomNavigationView.setVisibility(View.INVISIBLE);
            } else if (destination.getId() == R.id.messagesFragment ||
                    destination.getId() == R.id.profileFragment ||
                    destination.getId() == R.id.contactsFragment) {
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

    private void userStatus(String status) {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user.getUid().equals(currentUser.getUid())) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("status", status);
                        dataSnapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void updateToken(String token){
        user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("tokens");
        Token mToken = new Token (token);
        if(user != null){
            databaseReference.child(user.getUid()).setValue(mToken);
        }

    }
}