package com.kabasonic.messenger.ui.userchat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
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
import com.kabasonic.messenger.models.Chat;
import com.kabasonic.messenger.models.User;
import com.kabasonic.messenger.notifications.APIService;
import com.kabasonic.messenger.notifications.Client;
import com.kabasonic.messenger.notifications.Data;
import com.kabasonic.messenger.notifications.Response;
import com.kabasonic.messenger.notifications.Sender;
import com.kabasonic.messenger.notifications.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class UserChatRepository {
    public static final String TAG = "UserProfileRepository";
    public static final String STATUS_ONLINE = "Online";
    public static final String STATUS_OFFLINE = "Offline";

    private boolean notify = false;
    private APIService apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);

    private ValueEventListener seenListener;
    private DatabaseReference userRefToSeen;

    private MutableLiveData<HashMap<String, Object>> mData = new MutableLiveData<>();
    private MutableLiveData<Bitmap> mDataImage = new MutableLiveData<>();
    private MutableLiveData<List<Chat>> mDataMessages = new MutableLiveData<>();
    private List<Chat> chatList = new ArrayList<>();
    private String imageUri;
    private static UserChatRepository instance;


    public static UserChatRepository getInstance() {
        if (instance == null) {
            instance = new UserChatRepository();
        }
        return instance;
    }

    public MutableLiveData<HashMap<String, Object>> getUserValues(String uid) {
        getFirebaseUserValue(uid);
        return mData;
    }
    public MutableLiveData<Bitmap> getUserImage(String imageUri, Context context) {
        if (imageUri != null) {
            getFirebaseUserImage(imageUri, context);
        }
        return mDataImage;
    }
    public MutableLiveData<List<Chat>> getUserMessages(String hisUid, String myUid){
        mDataMessages.setValue(chatList);
        getFirebaseMessages(hisUid, myUid);
        return mDataMessages;
    }
    public void updateStatusMessages(String myUid){
        getFirebaseSeenMessages(myUid);
    }
    public void setUserStatus(boolean status) {
        if (status) {
            setFirebaseUserStatus(STATUS_ONLINE);
        }else{
            setFirebaseUserStatus(STATUS_OFFLINE);
        }
    }
    public void setRemoveListenerToSeen(boolean action){
        if(action){
            setFirebaseRemoveListener();
        }
    }


    private void getFirebaseUserValue(String uid) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Object> userValues = new HashMap<String, Object>();
                mData.setValue(userValues);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    userValues.put(dataSnapshot.getKey(), dataSnapshot.getValue());
                }
                mData.postValue(userValues);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void getFirebaseUserImage(String imageUri, Context context) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        if (!imageUri.isEmpty()) {
            storageRef.child("uploadsUserIcon/").child(imageUri).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL for 'users/me/profile.png'
                    Glide.with(context)
                            .asBitmap()
                            .load(uri)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    mDataImage.setValue(resource);
                                    mDataImage.postValue(resource);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                }
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }
    }
    private void setFirebaseUserStatus(String status) {
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
    private void getFirebaseMessages(String hisUid, String myUid) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("chat");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(hisUid) && chat.getSender().equals(myUid) ||
                            chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid)) {
                        chatList.add(chat);
                        Log.d(TAG,"Chat list");
                        mDataMessages.postValue(chatList);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getFirebaseSeenMessages(String myUid) {

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
    private void setFirebaseRemoveListener(){
        if(userRefToSeen!=null){
            userRefToSeen.removeEventListener(seenListener);
            seenListener = null;
            userRefToSeen = null;
        }
    }

    public void sentActionMessage(String hisUid, String myUid, String message){
        notify = true;
        createFirebaseChatLists(hisUid,myUid);
        sendFirebaseMessage(hisUid,myUid,message);
    }



    public void createFirebaseChatLists(String hisUid, String myUid){
        DatabaseReference chatlist1 = FirebaseDatabase.getInstance().getReference();
        chatlist1.child("chatlist").child(myUid).child(hisUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    Log.d(TAG,"chatlist1");
                    chatlist1.child("chatlist").child(myUid).child(hisUid).child("uid").setValue(hisUid);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        DatabaseReference chatlist2 = FirebaseDatabase.getInstance().getReference();
        chatlist2.child("chatlist").child(hisUid).child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    Log.d(TAG,"chatlist2");
                    chatlist2.child("chatlist").child(hisUid).child(myUid).child("uid").setValue(myUid);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    private void sendFirebaseMessage(String hisUid, String myUid, String message) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myUid);
        hashMap.put("receiver", hisUid);
        hashMap.put("message", message);
        hashMap.put("timestamp", timestamp);
        hashMap.put("seen", "false");
        mDatabase.child("chat").push().setValue(hashMap);

        mDatabase.child("users").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if(dataSnapshot.getKey().equals("imageUser")){
                        imageUri = String.valueOf(dataSnapshot.getValue());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        DatabaseReference database = FirebaseDatabase.getInstance().getReference("users").child(myUid);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(notify){
                    String name = user.getFirstName() + " " + user.getLastName();
                    sendNotification(hisUid,myUid,name,message,imageUri);
                }
                notify = false;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(String hisUid, String myUid, String name, String message, String imageUri) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("tokens");
        Query query = allTokens.orderByKey().equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Token token = dataSnapshot.getValue(Token.class);
                    Data data = new Data(myUid, name + ": " + message, "New Message",hisUid, imageUri);
                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender).enqueue(new Callback<Response>() {
                        @Override
                        public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

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

}
