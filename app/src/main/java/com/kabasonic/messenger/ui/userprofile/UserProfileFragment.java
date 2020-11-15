package com.kabasonic.messenger.ui.userprofile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.models.Contacts;
import com.kabasonic.messenger.models.ContactsRequest;
import com.kabasonic.messenger.ui.adapters.AdapterProfileDoubleItem;
import com.kabasonic.messenger.ui.adapters.items.RowItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserProfileFragment extends Fragment {

    public static final String TAG = "UserProfileFragment";

    private ArrayList<RowItem> mArraList;
    private ListView mLvUser;
    private TextView mUserName, mUserStatus;
    private FloatingActionButton mSendMessage;
    private ImageView mUserImage;
    private String userCurrentProfile = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_user, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getArgumentsFragment();
        initView(view);
    }

    private void initView(View view) {
        mLvUser = (ListView) view.findViewById(R.id.lvUserProfile);
        mUserName = (TextView) view.findViewById(R.id.userNameAccount);
        mUserStatus = (TextView) view.findViewById(R.id.userStatusAccount);
        mUserImage = (ImageView) view.findViewById(R.id.image_user_profile_appbar);
        mSendMessage = (FloatingActionButton) view.findViewById(R.id.send_message_profile);
    }

    private void getArgumentsFragment() {
        if (getArguments() != null) {

            UserProfileFragmentArgs args = UserProfileFragmentArgs.fromBundle(getArguments());
            this.userCurrentProfile = getArguments().getString("uid");
            Toast.makeText(getActivity(), this.userCurrentProfile, Toast.LENGTH_SHORT).show();
            getUserFirebase(this.userCurrentProfile);
        }
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
                    mUserName.setText(userName);
                    break;
                case "lastName":
                    Log.d(TAG, "lastName: " + String.valueOf(item.getValue()));
                    userName += " " + String.valueOf(item.getValue()).trim();
                    mUserName.setText(userName);
                    break;
                case "imageUser":
                    Log.d(TAG, "imageUser: " + String.valueOf(item.getValue()));
                    break;
                case "nickName":
                    Log.d(TAG, "nickName: " + String.valueOf(item.getValue()));
                    //mUserStatus.setText(String.valueOf(item.getValue()));
                    userinfo[0] = String.valueOf(item.getValue());
                    break;
                case "status":
                    Log.d(TAG, "status: " + String.valueOf(item.getValue()));
                    mUserStatus.setText(String.valueOf(item.getValue()));
                    break;
                case "phoneNumber":
                    Log.d(TAG, "phoneNumber: " + String.valueOf(item.getValue()));
                    userinfo[1] = String.valueOf(item.getValue());
                    break;
                case "bio":
                    Log.d(TAG, "bio: " + String.valueOf(item.getValue()));
                    userinfo[2] = String.valueOf(item.getValue());
                    break;
                default:
                    break;
            }
        }

        String[] subtitleLV = getResources().getStringArray(R.array.listOneSubtitleProfile);

        Integer[] imagesOne = {R.drawable.ic_round_alternate_email_24,
                R.drawable.ic_round_call_24,
                R.drawable.ic_round_info_24};

        mArraList = new ArrayList<>();

        for (int i = 0; i < imagesOne.length; i++) {
            mArraList.add(new RowItem(imagesOne[i], userinfo[i], subtitleLV[i]));
        }
        AdapterProfileDoubleItem adapterThreeRow = new AdapterProfileDoubleItem(getContext(), R.layout.double_row_profile, mArraList);
        mLvUser.setAdapter(adapterThreeRow);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.menu_search).setVisible(false);
        menu.findItem(R.id.menu_qr_code_scan).setVisible(false);
        menu.findItem(R.id.menu_logout).setVisible(false);
        menu.findItem(R.id.menu_create_group).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_add_to_contacts:
                Log.d(TAG,"Clicked add to contacts");
                //getCurrentUser();
                addUser();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

//    private void getCurrentUser(){
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
//        mDatabase.child("users").child(userCurrentProfile).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Map<String, Object> userValues = new HashMap<String, Object>();
//                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
//                    userValues.put(dataSnapshot.getKey(),dataSnapshot.getValue());
//                }
//                addUser(userValues);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private void addUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("request").child(userCurrentProfile).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Contacts contacts = new Contacts(userCurrentProfile);
                ContactsRequest contactsRequest = new ContactsRequest("pending");
                mDatabase.child("request").child(userCurrentProfile).child(currentUser.getUid()).setValue(contactsRequest);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, String.valueOf(error));
            }
        });
    }
}
