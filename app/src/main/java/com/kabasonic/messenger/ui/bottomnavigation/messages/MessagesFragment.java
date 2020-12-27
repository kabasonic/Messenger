package com.kabasonic.messenger.ui.bottomnavigation.messages;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kabasonic.messenger.MainActivity;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.models.Chat;
import com.kabasonic.messenger.models.ChatList;
import com.kabasonic.messenger.models.User;
import com.kabasonic.messenger.notifications.Data;
import com.kabasonic.messenger.ui.adapters.AdapterMessageItem;

import java.util.ArrayList;

public class MessagesFragment extends Fragment {
    public static final String TAG = "MessagesFragment";

    private RecyclerView mRecyclerView;
    public ArrayList<User> userArrayList;
    private ArrayList<ChatList> chatListArrayList;
    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private AdapterMessageItem mAdapterMessageItem;
    private RecyclerView.LayoutManager mLayoutManager;
    MainActivity mActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mActivity = (MainActivity) context;
        }
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.messages_fragment,container,false);
        return root;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Toast.makeText(getActivity(),"User is signed in",Toast.LENGTH_SHORT).show();
            Log.d(TAG,"User Uid sidn in" + user.getUid());
        } else {
            // No user is signed in
            Toast.makeText(getActivity(),"No user is signed in",Toast.LENGTH_SHORT).show();
            NavDirections action = MessagesFragmentDirections.actionMessagesFragmentToOTPNumberFragment();
            Navigation.findNavController(view).navigate(action);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mRecyclerView = view.findViewById(R.id.rvMessages);
        chatListArrayList = new ArrayList<>();
        if(firebaseUser!=null){
            mDatabase = FirebaseDatabase.getInstance().getReference("chatlist").child(firebaseUser.getUid());
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    chatListArrayList.clear();
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        ChatList chatList = dataSnapshot.getValue(ChatList.class);
                        chatListArrayList.add(chatList);
                    }
                    loadChats();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    private void loadChats() {
        userArrayList = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userArrayList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    for(ChatList chatList:  chatListArrayList){
                        if(user.getUid() != null && user.getUid().equals(chatList.getUid())){
                            userArrayList.add(user);
                            break;
                        }
                    }
                    mRecyclerView.setHasFixedSize(true);
                    mLayoutManager = new LinearLayoutManager(mActivity);
                    mAdapterMessageItem = new AdapterMessageItem(userArrayList,getContext());
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(mAdapterMessageItem);
                    mAdapterMessageItem.notifyDataSetChanged();
                    for(int i=0;i<userArrayList.size();i++){
                        lastMessage(userArrayList.get(i).getUid());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void lastMessage(String uid) {
        Log.d(TAG,"lastMessage UID: " + uid);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("chat");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String theLastMessage = "default";
                String theTime = "default";
                String theStatusMessage = "default";
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    if(chat==null){
                        Log.d(TAG,"lastMessage chat null");
                        continue;
                    }
                    String sender = chat.getSender();
                    String receiver = chat.getReceiver();
                    if(sender == null || receiver == null){
                        Log.d(TAG,"lastMessage sender, receiver null");
                        continue;
                    }
                    if (chat.getReceiver().equals(firebaseUser.getUid()) &&
                        chat.getSender().equals(uid) ||
                        chat.getReceiver().equals(uid) &&
                        chat.getSender().equals(firebaseUser.getUid())){
                        theLastMessage = chat.getMessage();
                        theTime = chat.getTimestamp();
                        Log.d(TAG,"theStatusMessage: " + chat.isSeen());
//                        if(chat.isSeen()){
//                            theStatusMessage = "true";
//                        } else {
//                            theStatusMessage = "false";
//                        }
                        Log.d(TAG,"theStatusMessage: " + theStatusMessage);
                    }

                }
                mAdapterMessageItem.setStatusMessageMap(uid,theStatusMessage);
                mAdapterMessageItem.setTimeStampMap(uid,theTime);
                mAdapterMessageItem.setLastMessageMap(uid,theLastMessage);
                mAdapterMessageItem.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


//    private void buildRecyclerView() {
//        mRecyclerView = getView().findViewById(R.id.rvMessages);
//        mRecyclerView.setHasFixedSize(true);
//        mLayoutManager = new LinearLayoutManager(mActivity);
//        mAdapterMessageItem = new AdapterMessageItem(mRowItem);
//        mRecyclerView.setLayoutManager(mLayoutManager);
//        mRecyclerView.setAdapter(mAdapterMessageItem);
//        mAdapterMessageItem.setOnItemClickListener(new AdapterMessageItem.ItemRow() {
//            @Override
//            public void onItemClick(int position) {
//                Toast.makeText(getContext(), "Clicked row", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    //Create app top bar menu
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu,menu);
        menu.findItem(R.id.menu_add_to_contacts).setVisible(false);
        menu.findItem(R.id.menu_settings).setVisible(false);
        menu.findItem(R.id.menu_logout).setVisible(false);
        menu.findItem(R.id.menu_create_group).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int idMenuItem = item.getItemId();
        switch (idMenuItem){
            case R.id.menu_search:
                Log.i(TAG,"Click search button");
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
