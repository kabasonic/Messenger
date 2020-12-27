package com.kabasonic.messenger.repositories;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfileRepository {

    private static ProfileRepository instance;

    private Map<String, Object> dataSet = new HashMap<String, Object>();
    private MutableLiveData<Map<String, Object> > data = new MutableLiveData<>();
    private MutableLiveData<String> updateDataUser = new MutableLiveData<>();
    private MutableLiveData<String> infoDeleteImage = new MutableLiveData<>();

    private DatabaseReference mDatabase;

    public static ProfileRepository getInstance(){
        if(instance == null){
            instance = new ProfileRepository();
        }
        return instance;
    }
    public MutableLiveData<Map<String, Object> > getMyProfile(){
        data.setValue(dataSet);
        getUserFirebase();
        return data;
    }

    public MutableLiveData<String> getDeleteImage(){
        infoDeleteImage.setValue("");
        deleteImage();
        return infoDeleteImage;
    }

    public MutableLiveData<String> getUpdateUser(Map<String, Object> newValues){
        updateDataUser.setValue("Not update");
        updateUser(newValues);
        return updateDataUser;
    }
    private void updateUser(Map<String, Object> newValues) {
        FirebaseUser userId = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(userId.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Object> userValues = new HashMap<String, Object>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    userValues.put(dataSnapshot.getKey(), dataSnapshot.getValue());
                }
                for (Map.Entry<String, Object> entry : newValues.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    userValues.put(key, value);
                }
                mDatabase.child("users").child(userId.getUid()).updateChildren(userValues);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        updateDataUser.postValue("Values is updating");
    }
    private void deleteImage() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Object> newValues = new HashMap<>();
                newValues.put("imageUser","");
                mDatabase.child("users").child(user.getUid()).updateChildren(newValues);
                infoDeleteImage.postValue("Image is deleted");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getUserFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    if(dataSnapshot.getKey().equals("imageUser")){
//                        //Download image User
//                    }
                    dataSet.put(dataSnapshot.getKey(), dataSnapshot.getValue());
                }
                data.postValue(dataSet);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
