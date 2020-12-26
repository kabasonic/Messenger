package com.kabasonic.messenger.repositories;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.internal.Sleeper;
import com.kabasonic.messenger.models.Contacts;
import com.kabasonic.messenger.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ContactsRequestRepository {

    private static ContactsRequestRepository instance;

    private ArrayList<User> dataSet = new ArrayList<>();

    private ArrayList<String> uidRequest;

    private MutableLiveData<Integer> request = new MutableLiveData<>();

    private String uid;

    public static ContactsRequestRepository getInstance(){
        if(instance == null){
            instance = new ContactsRequestRepository();
        }
        return instance;
    }
    @SuppressLint("StaticFieldLeak")
    public MutableLiveData<List<User>> getRequests(){
        MutableLiveData<List<User>> data = new MutableLiveData<>();

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                data.setValue(dataSet);
            }
            @Override
            protected Void doInBackground(Void... voids) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                getRequestContact();
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                data.postValue(dataSet);
            }
        }.execute();
        return data;
    }

    public void actionRequest(String uid, boolean action) {
        this.uid = uid;
        if(action){
            addToContact(uid);
            removeRequest(uid);
        }else{
            removeRequest(uid);
        }
    }

    private void getRequestContact(){
        uidRequest = new ArrayList<>();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("request").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Log.d("Requests UIDS: " ,String.valueOf(dataSnapshot.getKey()));
                    uidRequest.add(String.valueOf(dataSnapshot.getKey()));
                }
                if(!uidRequest.isEmpty()){
                    showRequesetContact(uidRequest);
                }else {
                    dataSet.clear();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showRequesetContact(ArrayList<String> uidRequest){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                dataSet.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    if(uidRequest.size() > i && String.valueOf(dataSnapshot.getKey()).equals(uidRequest.get(i))){
                        User user = dataSnapshot.getValue(User.class);
                        dataSet.add(user);
                        i++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void removeRequest(String uid){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("request").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mDatabase.child("request").child(currentUser.getUid()).child(uid).removeValue();
                //getRequestContact();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addToContact(String uidUser){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("contact").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Contacts contacts = new Contacts(uidUser);
                mDatabase.child("contact").child(currentUser.getUid()).child(uidUser).setValue(contacts);
                contacts = new Contacts(currentUser.getUid());
                mDatabase.child("contact").child(uidUser).child(currentUser.getUid()).setValue(contacts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Log.e(TAG, String.valueOf(error));
            }
        });
    }

    public MutableLiveData<Integer> getCountRequest() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            countRequest();
        }
        request.setValue(0);
        return request;
    }

    private void countRequest(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("request").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int countRequest = 0;

                countRequest = (int) snapshot.getChildrenCount();
                //Log.d(TAG,"Count request: " + countRequest);
                request.postValue(countRequest);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
