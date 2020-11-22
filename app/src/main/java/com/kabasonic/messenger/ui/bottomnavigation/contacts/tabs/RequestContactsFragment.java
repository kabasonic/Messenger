package com.kabasonic.messenger.ui.bottomnavigation.contacts.tabs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kabasonic.messenger.MainActivity;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.models.Contacts;
import com.kabasonic.messenger.models.ContactsRequest;
import com.kabasonic.messenger.models.User;
import com.kabasonic.messenger.ui.adapters.AdapterRequestItem;

import java.util.ArrayList;

public class RequestContactsFragment extends Fragment {

    public static final String TAG = "RequestContactsFragment";

    public ArrayList<User> mRequestList;
    private RecyclerView mRecyclerView;
    private AdapterRequestItem mAdapterRequestItem;
    private RecyclerView.LayoutManager mLayoutManager;
    MainActivity mActivity;

    private ArrayList<String> uidRequest;

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
        View root = inflater.inflate(R.layout.fragment_request_contacts, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //createExampleList();
        //buildRecyclerView();
        mRecyclerView = view.findViewById(R.id.rvRequestContacts);
        getRequestContact();
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
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showRequesetContact(ArrayList<String> uidRequest){
        mRequestList = new ArrayList<>();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                mRequestList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        if(uidRequest.size() > i && String.valueOf(dataSnapshot.getKey()).equals(uidRequest.get(i))){
                            User user = dataSnapshot.getValue(User.class);
                            mRequestList.add(user);
                            i++;
                        }
                }
                buildRecyclerView(mRequestList);
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

    private void addToContact(String uidUser, int position){
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
                Log.e(TAG, String.valueOf(error));
            }
        });
    }

    public void buildRecyclerView(ArrayList<User> mRequestList) {
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mActivity);
        mAdapterRequestItem = new AdapterRequestItem(mRequestList,getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapterRequestItem.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapterRequestItem);
        mAdapterRequestItem.setOnItemClickListener(new AdapterRequestItem.RequestItemRow() {
            @Override
            public void onItemClick(String uid) {
                Toast.makeText(getContext(),"Clicked row",Toast.LENGTH_SHORT).show();
                navigation(uid);
            }

            @Override
            public void onItemAccept(String uid, int position) {
                Log.i(TAG,"Cliked button ACCEPT " + uid);
                mRequestList.remove(position);
                mAdapterRequestItem.notifyItemRemoved(position);
                addToContact(uid,position);
                removeRequest(uid);
                getRequestContact();

            }

            @Override
            public void onItemDecline(String uid, int position) {
                Log.i(TAG,"Cliked button DECLINE " + position);
                removeRequest(uid);
                mRequestList.remove(position);
                mAdapterRequestItem.notifyItemRemoved(position);
                getRequestContact();
            }
        });
    }

    private void navigation(String currentUid){
        Bundle bundle = new Bundle();
        bundle.putString("uid",currentUid);
        Navigation.findNavController(getView()).navigate(R.id.userProfileFragment, bundle);
    }

}
