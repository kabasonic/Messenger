package com.kabasonic.messenger.repositories.contacts;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

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

public class ContactsOnlineRepository {

    public static final String TAG = "ContactsOnlineRepository";
    private static ContactsOnlineRepository instance;

    private ArrayList<User> dataSet = new ArrayList<>();

    private ArrayList<String> uidContacts;
    private String uidContact;
    private String mQuery;

    public static ContactsOnlineRepository getInstance() {
        if (instance == null) {
            instance = new ContactsOnlineRepository();
        }
        return instance;
    }

    @SuppressLint("StaticFieldLeak")
    public MutableLiveData<List<User>> getOnlineContacts(){
        MutableLiveData<List<User>> data = new MutableLiveData<>();
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                data.postValue(dataSet);
            }
            @Override
            protected Void doInBackground(Void... voids) {
                getMyOnlineContacts();
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

    public void setUidContact(String uidContact) {
        this.uidContact = uidContact;
        deleteOnlineContacts();
    }


    private void searchUsers(String query) {
        FirebaseUser userId = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataSet.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (!user.getUid().equals(userId.getUid())) {
                        if (user.getFirstName().toLowerCase().contains(query.toLowerCase())
                                || user.getLastName().toLowerCase().contains(query.toLowerCase()) ||
                                user.getNickName().toLowerCase().contains(query.toLowerCase())) {
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
    private void deleteOnlineContacts() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("contact").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mDatabase.child("contact").child(currentUser.getUid()).child(uidContact).removeValue();
                getMyOnlineContacts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getMyOnlineContacts() {
        uidContacts = new ArrayList<>();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("contact").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uidContacts.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.d("Requests UIDS: ", String.valueOf(dataSnapshot.getKey()));
                    uidContacts.add(String.valueOf(dataSnapshot.getKey()));
                }
                if (!uidContacts.isEmpty()) {

                    showMyOnlineContact(uidContacts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void showMyOnlineContact(ArrayList<String> uidContacts) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                dataSet.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (uidContacts.size() > i && String.valueOf(dataSnapshot.getKey()).equals(uidContacts.get(i))) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user.getStatus().equals("Online")) {
                            dataSet.add(user);
                        }
                        i++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
