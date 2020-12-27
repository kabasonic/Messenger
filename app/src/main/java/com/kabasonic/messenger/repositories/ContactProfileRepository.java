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
import com.kabasonic.messenger.models.ContactsRequest;

import java.util.HashMap;
import java.util.Map;

public class ContactProfileRepository {

    private static ContactProfileRepository instance;

    private Map<String, Object> dataSet = new HashMap<String, Object>();
    private MutableLiveData<Map<String, Object> > data = new MutableLiveData<>();
    private MutableLiveData<Boolean> addToFriend = new MutableLiveData<>();
    private MutableLiveData<Boolean> checkToFriend = new MutableLiveData<>();

    public static ContactProfileRepository getInstance(){
        if(instance == null){
            instance = new ContactProfileRepository();
        }
        return instance;
    }

    public MutableLiveData<Map<String, Object> > getProfileUser(String uid){
        data.setValue(dataSet);
        if(uid != null && !uid.isEmpty()){
            getUserFirebase(uid);
        }
        return data;
    }

    public MutableLiveData<Boolean> getAddContact(String uid){
        addToFriend.setValue(false);
        addUser(uid);
        return addToFriend;
    }

    public MutableLiveData<Boolean> getCheckToFriend(String uid){
        checkToFriend.setValue(false);
        checkContactFriend(uid);
        return checkToFriend;
    }

    private void checkContactFriend(String uid){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("contact").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    if(uid.equals(dataSnapshot.getKey())){
                        checkToFriend.postValue(true);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserFirebase(String uid) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(uid).addValueEventListener(new ValueEventListener() {
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

    private void addUser(String uid) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("request").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ContactsRequest contactsRequest = new ContactsRequest("pending");
                addToFriend.postValue(true);
                mDatabase.child("request").child(uid).child(currentUser.getUid()).setValue(contactsRequest);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
