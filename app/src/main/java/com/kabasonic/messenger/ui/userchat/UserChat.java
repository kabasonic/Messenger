package com.kabasonic.messenger.ui.userchat;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.database.Database;
import com.kabasonic.messenger.models.Chat;
import com.kabasonic.messenger.models.User;
import com.kabasonic.messenger.notifications.APIService;
import com.kabasonic.messenger.notifications.Client;
import com.kabasonic.messenger.notifications.Data;
import com.kabasonic.messenger.notifications.Response;
import com.kabasonic.messenger.notifications.Sender;
import com.kabasonic.messenger.notifications.Token;
import com.kabasonic.messenger.ui.adapters.AdapterChat;
import com.kabasonic.messenger.ui.bottomnavigation.profile.ProfileFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class UserChat extends AppCompatActivity {

    public static final String TAG = "UserChat";

    Context context;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ImageView imageView;
    private TextView nameTv, statusTv;
    private EditText messageEt;
    private Button sendBt;
    private ImageView arrowBack;
    private String userCurrentProfile = null;
    private View view;


    APIService apiService;
    boolean notify = false;

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
        arrowBack = (ImageView) findViewById(R.id.chat_arrowBack);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);



        Intent intent = getIntent();
        userCurrentProfile = intent.getStringExtra("hisUid");

        this.userCurrentProfile = getIntent().getStringExtra("uid");
        getUserFirebase(userCurrentProfile);
        FirebaseUser userLogin = FirebaseAuth.getInstance().getCurrentUser();
        currentUser = userLogin.getUid();


        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sendBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String messageCheck = messageEt.getText().toString().trim();
                String message = messageEt.getText().toString();
                if (TextUtils.isEmpty(messageCheck)) {
                    Toast.makeText(context, "Cannot sent the empty message", Toast.LENGTH_SHORT).show();
                } else {

                    //Chat list
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    Log.d(TAG,"ChatList");
                    DatabaseReference chatlist1 = FirebaseDatabase.getInstance().getReference();
                    chatlist1.child("chatlist").child(currentUser.getUid()).child(userCurrentProfile).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!snapshot.exists()){
                                Log.d(TAG,"chatlist1");
                                chatlist1.child("chatlist").child(currentUser.getUid()).child(userCurrentProfile).child("uid").setValue(userCurrentProfile);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    DatabaseReference chatlist2 = FirebaseDatabase.getInstance().getReference();
                    chatlist2.child("chatlist").child(userCurrentProfile).child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!snapshot.exists()){
                                Log.d(TAG,"chatlist2");
                                chatlist2.child("chatlist").child(userCurrentProfile).child(currentUser.getUid()).child("uid").setValue(currentUser.getUid());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    sendMessage(message);

                }
                messageEt.setText("");

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
        mDatabase.child("users").child(userCurrentProfile).updateChildren(hashMap);
    }


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
//                    Log.d(TAG,"User profile UID: " + userCurrentProfile);
//                    Log.d(TAG,"User profile UID: " + userCurrentProfile);
//                    Log.d(TAG,"My profile UID: " + currentUser);
//                    Log.d(TAG,"Receiver UID: " + chat.getReceiver());
//                    Log.d(TAG,"Receiver UID: " + chat.getSender());

                    if (chat.getReceiver().equals(userCurrentProfile) && chat.getSender().equals(currentUser) ||
                            chat.getReceiver().equals(currentUser) && chat.getSender().equals(userCurrentProfile)) {
                        chatList.add(chat);
                    }
                    //adapter
                    adapterChat = new AdapterChat(UserChat.this, chatList);
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


        String msg = message;
        Log.d(TAG,"sendMessage: currentUser" + String.valueOf(currentUser.getUid()));
        mDatabase.child("users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Log.d(TAG,"sendMessage: notify" + String.valueOf(notify));
                if(notify){
                    sendNotification(userCurrentProfile,user.getFirstName(),message);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        //Chat list
//        Log.d(TAG,"ChatList");
//        DatabaseReference chatlist1 = FirebaseDatabase.getInstance().getReference();
//        chatlist1.child("chatlist").child(currentUser.getUid()).child(userCurrentProfile).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(!snapshot.exists()){
//                    chatlist1.child("chatlist").child("uid").child(userCurrentProfile);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        DatabaseReference chatlist2 = FirebaseDatabase.getInstance().getReference();
//        chatlist2.child("chatlist").child(userCurrentProfile).child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(!snapshot.exists()){
//                    chatlist2.child("chatlist").child("uid").child(currentUser.getUid());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

    private void sendNotification(String userCurrentProfile, String firstName, String message) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("tokens");
        Log.d(TAG,"sendNotification: userCurrentProfile "  + userCurrentProfile);
        Query query = allTokens.orderByKey().equalTo(userCurrentProfile);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Token token = dataSnapshot.getValue(Token.class);
                    Data data = new Data(currentUser,firstName+":"+message,"New message",userCurrentProfile,R.drawable.default_user_image);

                    Log.d(TAG,"sendNotification: token "  + token.getToken());
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender).enqueue(new Callback<Response>() {
                        @Override
                        public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                            Toast.makeText(UserChat.this,""+response.message(),Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Response> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                    String Uri = String.valueOf(item.getValue());
                    //Glide.with(ProfileFragment.this).load(Uri).fitCenter().into(imageUser);
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    if(!Uri.isEmpty()){
                        storageRef.child("uploadsUserIcon/").child(String.valueOf(item.getValue())).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<android.net.Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Got the download URL for 'users/me/profile.png'
                                Glide.with(UserChat.this).load(uri).centerInside().into(imageView);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                            }
                        });
                    }
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
