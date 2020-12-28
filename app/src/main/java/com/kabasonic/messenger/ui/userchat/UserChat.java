package com.kabasonic.messenger.ui.userchat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.kabasonic.messenger.models.Chat;
import com.kabasonic.messenger.models.User;
import com.kabasonic.messenger.notifications.APIService;
import com.kabasonic.messenger.notifications.Client;
import com.kabasonic.messenger.notifications.Data;
import com.kabasonic.messenger.notifications.Response;
import com.kabasonic.messenger.notifications.Sender;
import com.kabasonic.messenger.notifications.Token;
import com.kabasonic.messenger.ui.adapters.AdapterChat;

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
    private TextView userName, mUserStatus;
    private EditText messageText;
    private Button sendButton;
    private Button addFile;
    private ImageView arrowBack;
    private String hisUid = null;
    private String imageUri;
    private List<Chat> chatList;
    private AdapterChat adapterChat;
    private ValueEventListener seenListener;
    private DatabaseReference userRefToSeen;
    private String myUid;

    APIService apiService;
    boolean notify = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_chat);

        toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        initViewElements();


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        getArguments();

        getUserFirebase(hisUid);

        FirebaseUser userLogin = FirebaseAuth.getInstance().getCurrentUser();
        myUid = userLogin.getUid();


        //create api service
        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);




        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                notify = true;

                String messageCheck = messageText.getText().toString().trim();
                String message = messageText.getText().toString();
                if (TextUtils.isEmpty(messageCheck)) {
                    Toast.makeText(context, "Cannot sent the empty message", Toast.LENGTH_SHORT).show();
                } else {

                    //Chat list
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    Log.d(TAG,"ChatList");
                    DatabaseReference chatlist1 = FirebaseDatabase.getInstance().getReference();
                    chatlist1.child("chatlist").child(currentUser.getUid()).child(hisUid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!snapshot.exists()){
                                Log.d(TAG,"chatlist1");
                                chatlist1.child("chatlist").child(currentUser.getUid()).child(hisUid).child("uid").setValue(hisUid);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    DatabaseReference chatlist2 = FirebaseDatabase.getInstance().getReference();
                    chatlist2.child("chatlist").child(hisUid).child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!snapshot.exists()){
                                Log.d(TAG,"chatlist2");
                                chatlist2.child("chatlist").child(hisUid).child(currentUser.getUid()).child("uid").setValue(currentUser.getUid());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    sendMessage(message);

                }
                messageText.setText("");

            }
        });

        addFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private void initViewElements(){
        recyclerView = findViewById(R.id.rvChat);
        imageView = findViewById(R.id.userImage);
        userName = findViewById(R.id.userNameChat);
        mUserStatus = findViewById(R.id.userStatusChat);
        messageText = findViewById(R.id.messageEdit);
        sendButton = (Button) findViewById(R.id.sendButton);
        addFile = (Button) findViewById(R.id.addFile);
        arrowBack = (ImageView) findViewById(R.id.chat_arrowBack);
    }

    private void getArguments(){
        Intent intent = getIntent();
        hisUid = intent.getStringExtra("hisUid");
        this.hisUid = getIntent().getStringExtra("uid");
    }

    @Override
    protected void onResume() {
        super.onResume();
        readMessage();
        seenMessage();
        userStatus("Online");
    }

    private void userStatus(String status) {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user.getUid().equals(currentUser.getUid())) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("status", status);
                        dataSnapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
        if(userRefToSeen!=null){
            userRefToSeen.removeEventListener(seenListener);
            seenListener = null;
            userRefToSeen = null;
        }
        userStatus("Offline");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        if(seenListener != null){
            userRefToSeen.removeEventListener(seenListener);
        }
        Log.d("Listener", "Remove listener");
    }

    private void seenMessage() {
        userRefToSeen = FirebaseDatabase.getInstance().getReference().child("chat");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(myUid)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("seen","true");
                        dataSnapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        userRefToSeen.addValueEventListener(valueEventListener);
        seenListener = valueEventListener;
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
                    if (chat.getReceiver().equals(hisUid) && chat.getSender().equals(myUid) ||
                            chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid)) {
                        chatList.add(chat);
                        Log.d("Chat list is seen"," " + chat.getSeen() );
                    }

                }
                //adapter
                adapterChat = new AdapterChat(UserChat.this, chatList);
                recyclerView.setAdapter(adapterChat);
                adapterChat.notifyDataSetChanged();
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
        hashMap.put("receiver", hisUid);
        hashMap.put("message", message);
        hashMap.put("timestamp", timestamp);
        hashMap.put("seen", "false");
        mDatabase.child("chat").push().setValue(hashMap);

        messageText.setText("");
        String msg = message;
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("users").child(myUid);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(notify){
                    String name = user.getFirstName() + " " + user.getLastName();
                    sendNotification(hisUid,name,message,imageUri);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void sendNotification(String hisUid, String name, String message, String imageUri) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("tokens");
        Query query = allTokens.orderByKey().equalTo(hisUid);
        Log.d(TAG,"imageUri" + imageUri);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                     for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                         Token token = dataSnapshot.getValue(Token.class);
                         Log.d(TAG,"imageUri" + imageUri);
                         Data data = new Data(myUid, name + ": " + message, "New Message",hisUid, imageUri);

                         Sender sender = new Sender(data, token.getToken());
                         apiService.sendNotification(sender).enqueue(new Callback<Response>() {
                             @Override
                             public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                 Toast.makeText(UserChat.this,"" + response.message(), Toast.LENGTH_LONG).show();
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
                    this.userName.setText(userName);
                    break;
                case "lastName":
                    Log.d(TAG, "lastName: " + String.valueOf(item.getValue()));
                    userName += " " + String.valueOf(item.getValue()).trim();
                    this.userName.setText(userName);
                    break;
                case "imageUser":
                    Log.d(TAG, "imageUser: " + String.valueOf(item.getValue()));
                    imageUri = String.valueOf(item.getValue());
                    //Glide.with(ProfileFragment.this).load(Uri).fitCenter().into(imageUser);
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    if(!imageUri.isEmpty()){
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
                    mUserStatus.setText(String.valueOf(item.getValue()));
                    break;
                default:
                    break;
            }
        }
    }
}
