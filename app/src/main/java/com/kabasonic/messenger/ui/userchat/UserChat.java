package com.kabasonic.messenger.ui.userchat;

import android.app.DownloadManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.database.Database;
import com.kabasonic.messenger.models.Chat;
import com.kabasonic.messenger.ui.adapters.AdapterChat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserChat extends AppCompatActivity {

    public static final String TAG = "UserChat";

    Context context;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ImageView imageView;
    private TextView nameTv, statusTv;
    private EditText messageEt;
    private Button sendBt;

    private String userCurrentProfile = null;


    List<Chat> chatList;
    AdapterChat adapterChat;
    ValueEventListener seenListener;
    DatabaseReference userRefToSeen;
    String currentUser;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_chat);
        toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        recyclerView = findViewById(R.id.rvChat);
        imageView = findViewById(R.id.userImage);
        nameTv = findViewById(R.id.userNameChat);
        statusTv = findViewById(R.id.userStatusChat);
        messageEt = findViewById(R.id.messageEdit);
        sendBt = (Button) findViewById(R.id.sendButton);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        this.userCurrentProfile = getIntent().getStringExtra("uid");
        getUserFirebase(userCurrentProfile);
        FirebaseUser userLogin = FirebaseAuth.getInstance().getCurrentUser();
        currentUser = userLogin.getUid();


        sendBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageCheck = messageEt.getText().toString().trim();
                String message = messageEt.getText().toString();
                if (TextUtils.isEmpty(messageCheck)) {
                    Toast.makeText(context, "Cannot sent the empty message", Toast.LENGTH_SHORT).show();
                } else {
                    sendMessage(message);
                }
            }
        });



        readMessage();
        seenMessage();

        //checkTypingStatus("noOne");

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkOnlineStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkOnlineStatus("offline");
        userRefToSeen.removeEventListener(seenListener);
    }

    private void checkOnlineStatus(String status){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap = new HashMap<>();

        hashMap.put("status", status);
        mDatabase.child("users").child(currentUser).updateChildren(hashMap);
    }

//    private void checkTypingStatus(String typing){
//        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
//        HashMap<String,Object> hashMap = new HashMap<>();
//
//        hashMap.put("typingTo", typing);
//        mDatabase.child("users").child(currentUser).updateChildren(hashMap);
//    }

    private void seenMessage() {
         userRefToSeen = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        seenListener = userRefToSeen.child("chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(currentUser.getUid()) && chat.getSender().equals(userCurrentProfile)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen",true);
                        dataSnapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMessage() {
        chatList = new ArrayList<>();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("chat");
        //FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    Log.d(TAG,"User profile UID: " + userCurrentProfile);
                    Log.d(TAG,"My profile UID: " + currentUser);
                    Log.d(TAG,"Receiver UID: " + chat.getReceiver());
                    Log.d(TAG,"Receiver UID: " + chat.getSender());

                    if (chat.getReceiver().equals(userCurrentProfile) && chat.getSender().equals(currentUser) ||
                            chat.getReceiver().equals(currentUser) && chat.getSender().equals(userCurrentProfile)) {
                        chatList.add(chat);
                    }
                    //adapter
                    adapterChat = new AdapterChat(getApplicationContext(), chatList);
                    adapterChat.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String message) {
        String timestamp = String.valueOf(System.currentTimeMillis());

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", currentUser.getUid());
        hashMap.put("receiver", userCurrentProfile);
        hashMap.put("message", message);
        hashMap.put("timestamp", timestamp);
        hashMap.put("isSeen", false);
        mDatabase.child("chat").push().setValue(hashMap);

        messageEt.setText("");
    }

    private void getUserFirebase(String uid) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Object> userValues = new HashMap<String, Object>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    userValues.put(dataSnapshot.getKey(), dataSnapshot.getValue());
                }
                setValues(userValues);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Firebase: " + error);
            }
        });
    }

    private void setValues(Map<String, Object> userValues) {
        String userName = null;
        String[] userinfo = new String[3];
        for (Map.Entry<String, Object> item : userValues.entrySet()) {
            switch (item.getKey()) {
                case "firstName":
                    Log.d(TAG, "firstName: " + String.valueOf(item.getValue()));
                    userName = String.valueOf(item.getValue()).trim();
                    nameTv.setText(userName);
                    break;
                case "lastName":
                    Log.d(TAG, "lastName: " + String.valueOf(item.getValue()));
                    userName += " " + String.valueOf(item.getValue()).trim();
                    nameTv.setText(userName);
                    break;
                case "imageUser":
                    Log.d(TAG, "imageUser: " + String.valueOf(item.getValue()));
                    break;
                case "status":
                    Log.d(TAG, "status: " + String.valueOf(item.getValue()));
                    statusTv.setText(String.valueOf(item.getValue()));
                    break;
                default:
                    break;
            }
        }
    }
}
