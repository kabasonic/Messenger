package com.kabasonic.messenger.database;

import android.util.Log;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CheckedUser {
    private DatabaseReference mDatabase;
    public FirebaseAuth mAuth;
    public boolean result = false;
    public static final String TAG = "CheckedUser";
    public CheckedUser(){

    }

    public void checkedUserId(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        Log.d(TAG,"Current UserId: "+userId);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot getUid : snapshot.getChildren()) {
                    Log.d(TAG, "Database UID: " + getUid.getKey());
                    if(getUid.getKey().equals(userId)){
                        result = true;
                        break;
                    }
                }
                Log.d(TAG,"Result" + result);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


}
