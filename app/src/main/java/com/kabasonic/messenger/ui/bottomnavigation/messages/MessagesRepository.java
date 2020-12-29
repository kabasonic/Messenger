package com.kabasonic.messenger.ui.bottomnavigation.messages;

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
import com.kabasonic.messenger.models.Chat;
import com.kabasonic.messenger.models.ChatList;
import com.kabasonic.messenger.models.User;

import java.util.ArrayList;
import java.util.List;

public class MessagesRepository {
    public static final String TAG = "MessagesRepository";
    private static MessagesRepository instance;


    private MutableLiveData<List<Chat>> mDataMessage = new MutableLiveData<>();
    private MutableLiveData<List<ChatList>> mDataChatList = new MutableLiveData<>();
    private MutableLiveData<List<User>> mDataUseChat = new MutableLiveData<>();
    private MutableLiveData<Boolean> userAuth = new MutableLiveData<>();
    private List<ChatList> chatListArrayList = new ArrayList<>();
    private List<User> userArrayList = new ArrayList<>();
    private List<Chat> list = new ArrayList<>();

    public static MessagesRepository getInstance() {
        if (instance == null) {
            instance = new MessagesRepository();
        }
        return instance;
    }


//    public MutableLiveData<Boolean> checkAuthUser() {
//
//        userAuth.setValue(false);
//        checkAuthFirebaseUser();
//        return userAuth;
//    }

    public MutableLiveData<List<User>> getUserChat() {
        getCheckFirebaseUserChatList();
        return mDataUseChat;
    }

    public MutableLiveData<List<Chat>> getLastMessage() {
        mDataMessage.setValue(list);
        return mDataMessage;
    }

    public boolean checkAuthFirebaseUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        return user == null;
    }

    private void getCheckFirebaseUserChatList() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase;
        if (firebaseUser != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference("chatlist").child(firebaseUser.getUid());
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ChatList chatList = dataSnapshot.getValue(ChatList.class);
                        chatListArrayList.add(chatList);
                    }
                    getLoadFirebaseChats();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    private void getLoadFirebaseChats() {
        userArrayList.clear();
        mDataUseChat.setValue(userArrayList);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "I have is chats: " + chatListArrayList.size());
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);

                    for (ChatList chatList : chatListArrayList) {
                        if (chatList.getUid().equals(user.getUid())) {
                            userArrayList.add(user);
                            break;
                        }
                    }

                }
                mDataUseChat.postValue(userArrayList);
                list.clear();
                for (int i = 0; i < userArrayList.size(); i++) {
                    lastMessage(userArrayList.get(i).getUid());
                }
                chatListArrayList.clear();
                Log.d(TAG, "ChatList cleat: " + userArrayList.size());
                Log.d(TAG, "Users finish: " + userArrayList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void lastMessage(String uid) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("chat");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String theLastMessage = "default";
                String theTime = "default";
                String theStatusMessage = "default";
                String theReceiver = "default";

                int countMessage = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat == null) {
                        Log.d(TAG, "lastMessage chat null");
                        continue;
                    }
                    String sender = chat.getSender();
                    String receiver = chat.getReceiver();
                    if (sender == null || receiver == null) {
                        Log.d(TAG, "lastMessage sender, receiver null");
                        continue;
                    }
                    if (chat.getReceiver().equals(user.getUid()) &&
                            chat.getSender().equals(uid) ||
                            chat.getReceiver().equals(uid) &&
                                    chat.getSender().equals(user.getUid())) {
                        // get data

                        if (chat.getSeen().equals("false") ) {
                            countMessage++;
                        }

                        theLastMessage = chat.getMessage();
                        theStatusMessage = chat.getSeen();
                        theTime = chat.getTimestamp();
                        theReceiver = chat.getReceiver();
                    }

                }

                Chat chat = new Chat();

                chat.setUid(uid);
                chat.setMyUid(user.getUid());
                chat.setReceiver(theReceiver);
                chat.setCountMessage(String.valueOf(countMessage));
                chat.setMessage(theLastMessage);
                chat.setSeen(theStatusMessage);
                chat.setTimestamp(theTime);
                list.add(chat);
                mDataMessage.postValue(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void deleteAllMessages(String uid) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("chat");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    if (chat.getReceiver().equals(user.getUid()) &&
                            chat.getSender().equals(uid) ||
                            chat.getReceiver().equals(uid) &&
                                    chat.getSender().equals(user.getUid())) {
                        //Log.d(TAG,"SNAPSOT" + dataSnapshot.getKey());
                        mDatabase.child(dataSnapshot.getKey()).removeValue();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    public void getFirebaseDeleteChatForMe(String hisUid) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String myUid = user.getUid();

        DatabaseReference chatlist1 = FirebaseDatabase.getInstance().getReference();
        chatlist1.child("chatlist").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatlist1.child("chatlist").child(myUid).child(hisUid).removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    public void getFirebaseDeleteChatForAll(String hisUid) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String myUid = user.getUid();

        DatabaseReference chatlist1 = FirebaseDatabase.getInstance().getReference();
        chatlist1.child("chatlist").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatlist1.child("chatlist").child(myUid).child(hisUid).removeValue();
                chatlist1.child("chatlist").child(hisUid).child(myUid).removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

}
