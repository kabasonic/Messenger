package com.kabasonic.messenger.repositories;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AllContactsRepository {

    private static AllContactsRepository instance;
    private ArrayList<User> dataSet = new ArrayList<>();

    private ArrayList<String> uidContacts;
    private String uidContact;
    private String mQuery;

    public static AllContactsRepository getInstance(){
        if(instance == null ){
            instance = new AllContactsRepository();
        }
        return instance;
    }

    @SuppressLint("StaticFieldLeak")
    public MutableLiveData<List<User>> getAllContacts(){
        MutableLiveData<List<User>> data = new MutableLiveData<>();
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                data.postValue(dataSet);
            }
            @Override
            protected Void doInBackground(Void... voids) {
                getMyContacts();
                try{
                    TimeUnit.SECONDS.sleep(1);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                data.setValue(dataSet);
            }
        }.execute();
        return data;
    }

    public MutableLiveData<List<User>> getSearchedUsers(){
        searchUsers();
        MutableLiveData<List<User>> data = new MutableLiveData<>();
        data.postValue(dataSet);
        return data;
    }


    public void setUidContact(String uidContact) {
        this.uidContact = uidContact;
        deleteContacts();
    }
    private void getMyContacts() {
        uidContacts = new ArrayList<>();
        uidContacts.clear();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("contact").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //Log.d("Requests UIDS: ", String.valueOf(dataSnapshot.getKey()));
                    uidContacts.add(String.valueOf(dataSnapshot.getKey()));
                }
                if (!uidContacts.isEmpty()) {
                    //Log.d(TAG,"LIST EMPTY");
                    showMyContact(uidContacts);
                }else{
                    dataSet.clear();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void showMyContact(ArrayList<String> uidContacts) {
        dataSet.clear();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (uidContacts.size() > i && String.valueOf(dataSnapshot.getKey()).equals(uidContacts.get(i))) {
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
    private void deleteContacts() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("contact").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mDatabase.child("contact").child(currentUser.getUid()).child(uidContact).removeValue();
                getMyContacts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void setmQuery(String mQuery) {
        this.mQuery = mQuery;
    }
    private void searchUsers() {
        FirebaseUser userId = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataSet.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (!user.getUid().equals(userId.getUid())) {
                        if (user.getFirstName().toLowerCase().contains(mQuery.toLowerCase())
                                || user.getLastName().toLowerCase().contains(mQuery.toLowerCase()) ||
                                user.getNickName().toLowerCase().contains(mQuery.toLowerCase())) {
                            dataSet.add(user);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}
