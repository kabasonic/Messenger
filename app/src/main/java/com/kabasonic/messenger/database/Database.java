package com.kabasonic.messenger.database;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kabasonic.messenger.models.User;

import java.util.HashMap;
import java.util.Map;

public class Database {

    private DatabaseReference mDatabase;

    public Database(){

    }

    public void updateUser(Map<String,Object> newValues){
        FirebaseUser userId = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(userId.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Object> userValues = new HashMap<String,Object>();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    userValues.put(dataSnapshot.getKey(),dataSnapshot.getValue());
                }

                for(Map.Entry<String, Object> entry :newValues.entrySet()){
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    userValues.put(key,value);
                }

                mDatabase.child("users").child(userId.getUid()).updateChildren(userValues);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}
