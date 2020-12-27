package com.kabasonic.messenger.ui.userprofile;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kabasonic.messenger.MainActivity;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.models.ContactsRequest;
import com.kabasonic.messenger.ui.adapters.AdapterMyProfile;
import com.kabasonic.messenger.ui.adapters.items.RowItem;
import com.kabasonic.messenger.ui.bottomnavigation.profile.ProfileFragmentDirections;
import com.kabasonic.messenger.ui.bottomnavigation.profile.viewmodels.ProfileViewModel;
import com.kabasonic.messenger.ui.userchat.UserChat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserProfileFragment extends Fragment {

    public static final String TAG = "UserProfileFragment";

    private ArrayList<RowItem> mListAdapter = new ArrayList<>();
    private ListView mListView;
    private TextView mUserName, mUserStatus;
    private FloatingActionButton mSendMessage;
    private ImageView mUserImage;
    private String userCurrentProfile = null;
    private UserProfileViewModel mViewModel;
    private AppBarLayout appBarLayout;
    private ProgressBar progressBar;
    private AdapterMyProfile mAdapter;
    private String mNickName;
    private String mPhoneNumber;
    private String mBio;
    private String mUsername;
    private MainActivity mActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mActivity = (MainActivity) context;
        }
    }

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

        mViewModel = ViewModelProviders.of(this).get(UserProfileViewModel.class);
        mViewModel.init();
        mViewModel.getUserProfile(userCurrentProfile).observe(getViewLifecycleOwner(), new Observer<Map<String, Object>>() {
            @Override
            public void onChanged(Map<String, Object> stringObjectMap) {
                if (!stringObjectMap.isEmpty()) {
                    progressView(true);
                    mAdapter.clear();
                    dataListView(stringObjectMap);
                } else {
                    progressView(false);
                }
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                alertDialogListView(position);
            }
        });

        mSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserChat.class);
                intent.putExtra("uid",userCurrentProfile);
                getContext().startActivity(intent);
            }
        });

        mAdapter = new AdapterMyProfile(getContext(), R.layout.double_row_profile, mListAdapter);
        mListView.setAdapter(mAdapter);

    }

    private void getArgumentsFragment() {
        if (getArguments() != null) {
            UserProfileFragmentArgs args = UserProfileFragmentArgs.fromBundle(getArguments());
            this.userCurrentProfile = getArguments().getString("uid");
            Log.d(TAG,"Recive uid" + userCurrentProfile);
        }
    }
    private void initView(View view) {
        progressBar = view.findViewById(R.id.progressBar_UserProfile);
        appBarLayout = (AppBarLayout) view.findViewById(R.id.app_bar_profileContact);
        mListView = (ListView) view.findViewById(R.id.lvUserProfile);
        mUserName = (TextView) view.findViewById(R.id.userNameAccount);
        mUserStatus = (TextView) view.findViewById(R.id.userStatusAccount);
        mUserImage = (ImageView) view.findViewById(R.id.image_user_profile_appbar);
        mSendMessage = (FloatingActionButton) view.findViewById(R.id.send_message_profile);
    }
    private void progressView(boolean action) {
        if (action) {
            progressBar.setVisibility(View.INVISIBLE);
            mUserName.setVisibility(View.VISIBLE);
            mUserStatus.setVisibility(View.VISIBLE);
            appBarLayout.setExpanded(true, true);
            mSendMessage.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.VISIBLE);

        } else {
            progressBar.setVisibility(View.VISIBLE);
            mUserName.setVisibility(View.INVISIBLE);
            mUserStatus.setVisibility(View.INVISIBLE);
            appBarLayout.setExpanded(false, true);
            mSendMessage.setVisibility(View.INVISIBLE);
            mListView.setVisibility(View.INVISIBLE);
        }
    }
    private void alertDialogListView(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        switch (position) {
            case 0:
                builder.setItems(R.array.dialog_nickname_contact, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            if(mNickName.isEmpty()){
                                Toast.makeText(getContext(),"Nick name is empty",Toast.LENGTH_LONG).show();
                            }else{
                                copyText(mNickName);
                                Toast.makeText(getContext(),"Text is copied to the clipboard",Toast.LENGTH_LONG).show();
                            }
                            break;
                        case 1:
                            if(!userCurrentProfile.isEmpty()) {
                                shareLinkToProfile(userCurrentProfile);
                            }
                            break;
                        default:
                            break;
                    }
                });
                builder.show();
                break;
            case 1:
                builder.setItems(R.array.dialog_phone_number_contact, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            if(mPhoneNumber.isEmpty()){
                                Toast.makeText(getContext(),"Phone number is empty",Toast.LENGTH_LONG).show();
                            }else{
                                copyText(mPhoneNumber);
                                Toast.makeText(getContext(),"Text is copied to the clipboard",Toast.LENGTH_LONG).show();
                            }
                            break;
                        default:
                            break;
                    }
                });
                builder.show();
                break;
            case 2://BIO Fragment
                builder.setItems(R.array.dialog_bio_contact, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            if(!mBio.isEmpty()){
                            builder.setMessage(mBio).create();
                            builder.show();
                        }else{
                            Toast.makeText(getContext(),"Bio is empty",Toast.LENGTH_LONG).show();
                        }
                            break;
                        default:
                            break;
                    }
                });
                builder.show();
                break;
            default:
                break;
        }
    }
    private void shareLinkToProfile(String uid) {
        Intent myIntent = new Intent(Intent.ACTION_SEND);
        myIntent.setType("text/plain");
        String shareBody = "Application Messenger\nLink to profile user:" + "\nmessenger.me/" + uid;
        myIntent.putExtra(Intent.EXTRA_SUBJECT, shareBody);
        myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(myIntent, "Share"));
    }
    private void copyText(String text){
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copy text", text);
        clipboard.setPrimaryClip(clip);
    }
    private void dataListView(Map<String, Object> dataMap) {
        String[] userInfo = {"", "", ""};
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            if (entry.getKey().equals("nickName")) {
                userInfo[0] = String.valueOf(entry.getValue());
                this.mNickName = String.valueOf(entry.getValue());
                if (userInfo[0].isEmpty()) {
                    userInfo[0] = "User not added information";
                }
            }
            if (entry.getKey().equals("phoneNumber")) {
                userInfo[1] = String.valueOf(entry.getValue());
                this.mPhoneNumber = String.valueOf(entry.getValue());
                if (userInfo[1].isEmpty()) {
                    userInfo[1] = "User not added information";
                }
            }
            if (entry.getKey().equals("bio")) {
                userInfo[2] = String.valueOf(entry.getValue());
                this.mBio = String.valueOf(entry.getValue());
                if (userInfo[2].isEmpty()) {
                    userInfo[2] = "User not added information";
                }
            }
            if (entry.getKey().equals("status")) {
                this.mUserStatus.setText(String.valueOf(entry.getValue()));
            }
            if (entry.getKey().equals("firstName")) {
                this.mUsername = String.valueOf(entry.getValue());
                this.mUserName.setText(mUsername);
            }
            if (entry.getKey().equals("lastName")) {
                this.mUsername += " " + String.valueOf(entry.getValue());
                this.mUserName.setText(mUsername);
            }
            if (entry.getKey().equals("imageUser")) {
                Log.d(TAG,"IMAGE USER BLYAT: " + String.valueOf(entry.getValue()));
            }

            if (entry.getKey().equals("imageUser")) {
                String Uri = String.valueOf(entry.getValue());

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                if(!Uri.isEmpty()){
                    storageRef.child("uploadsUserIcon/").child(String.valueOf(entry.getValue())).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for 'users/me/profile.png'
                            Glide.with(mActivity).load(uri).centerInside().into(mUserImage);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                        }
                    });
                }
            }
        }
        String[] subtitleLV = getResources().getStringArray(R.array.lv_my_profile);
        Integer[] imagesOne = {R.drawable.ic_round_alternate_email_24,
                R.drawable.ic_round_call_24,
                R.drawable.ic_round_info_24};

        for (int i = 0; i < imagesOne.length; i++) {
            mListAdapter.add(new RowItem(imagesOne[i], userInfo[i], subtitleLV[i]));
        }
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
        menu.findItem(R.id.menu_settings).setVisible(false);
        menu.findItem(R.id.menu_logout).setVisible(false);
        menu.findItem(R.id.menu_create_group).setVisible(false);

        mViewModel.getCheckFriend(userCurrentProfile).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    menu.findItem(R.id.menu_add_to_contacts).setVisible(false);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_add_to_contacts){
            Log.d(TAG,"Clicked add to contacts");
            mViewModel.getAddUser(userCurrentProfile).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if(aBoolean){
                        Toast.makeText(getContext(),"You send request for current user",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }
}
