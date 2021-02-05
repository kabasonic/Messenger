package com.kabasonic.messenger.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kabasonic.messenger.models.User;

public class AuthorizationRepository {

    private static AuthorizationRepository instance;
    private MutableLiveData<Boolean> action = new MutableLiveData<>();
    private MutableLiveData<String> navigation = new MutableLiveData<>();
    private DatabaseReference mDatabase;

    public static AuthorizationRepository getInstance(){
        if(instance == null){
            instance = new AuthorizationRepository();
        }
        return instance;
    }

    public MutableLiveData<String> getCheckedUser(){
        navigation.setValue("");
        checkedUserUID();
        return navigation;
    }

    public MutableLiveData<Boolean> getCreateUser(String firstName, String lastName){
        action.setValue(false);
        createUser(firstName, lastName);
        return action;
    }

    private void createUser(String firstName, String lastName){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        String userId = user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = new User(userId,"",firstName,lastName,phoneNumber,"","","");
                mDatabase.child("users").child(userId).setValue(user);
                action.postValue(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void checkedUserUID(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean result = false;
                for (DataSnapshot getUid : snapshot.getChildren()) {
                    if(getUid.getKey().equals(userId)){
                        result = true;
                        break;
                    }
                }
                navigation.postValue(String.valueOf(result));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

}
