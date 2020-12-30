package com.kabasonic.messenger.ui.bottomnavigation.profile;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.kabasonic.messenger.MainActivity;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.ui.adapters.AdapterMyProfile;
import com.kabasonic.messenger.ui.adapters.items.RowItem;
import com.kabasonic.messenger.ui.bottomnavigation.profile.viewmodels.ProfileViewModel;
import com.kabasonic.messenger.utils.LoadingDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static android.app.Activity.RESULT_OK;
public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";

    private TextView mUserName;
    private TextView mUserStatus;
    private ListView mListView;
    private ImageView mImageProfile;
    private FloatingActionButton mFabEdit;
    private ArrayList<RowItem> mListAdapter = new ArrayList<>();
    private MainActivity mActivity;
    private ProfileViewModel mViewModel;
    private ProgressBar progressBar;
    private AppBarLayout appBarLayout;
    private AdapterMyProfile mAdapter;
    private String mMyProfileUid;
    private String mNickName;
    private String mPhoneNumber;
    private String mBio;
    private String mUsername;

    private Uri mImageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;


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
        View root = inflater.inflate(R.layout.profile_fragment, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploadsUserIcon/");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users");

        initViewElements(view);

        mViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        mViewModel.init();
        mViewModel.getMyProfile().observe(getViewLifecycleOwner(), new Observer<Map<String, Object>>() {
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

        mFabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Clicked FAB");
                //Navigation.findNavController(getView()).navigate(R.id.editUsernameFragment);
                NavDirections action = ProfileFragmentDirections.actionProfileFragmentToEditUsernameFragment();
                Navigation.findNavController(getView()).navigate(action);
            }
        });

        mImageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogImage();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                alertDialogListView(i);
            }
        });

        mAdapter = new AdapterMyProfile(getContext(), R.layout.double_row_profile, mListAdapter);
        mListView.setAdapter(mAdapter);
    }

    private void alertDialogImage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(R.array.dialog_profile_user_image, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    //Add new image
                    builder.setItems(R.array.dialog_profile_user_choose_image, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(i==0){
                                //Add image with gallery
                                fileChoose();
                            }
//                            if(i==1){
//                                //Add image with camera
//                            }
                        }
                    });
                    builder.show();
                }
                if(i==1){
                    //Delete image
                    mViewModel.getDeleteImage().observe(getViewLifecycleOwner(), new Observer<String>() {
                        @Override
                        public void onChanged(String s) {
                            if(!s.isEmpty()){
                                Toast.makeText(getContext(),s,Toast.LENGTH_LONG).show();
//                                mImageProfile.setImageResource(R.drawable.default_user_image);
                                Glide.with(mActivity).load(R.drawable.default_user_image).centerInside().into(mImageProfile);
                            }
                        }
                    });
                }
            }
        });
        builder.show();
    }
    private void alertDialogListView(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        switch (position) {
            case 0:
                builder.setItems(R.array.dialog_nickname, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            NavDirections action = ProfileFragmentDirections.actionProfileFragmentToEditNicknameFragment();
                            Navigation.findNavController(getView()).navigate(action);
                            break;
                        case 1:
                            if(mNickName.isEmpty()){
                                Toast.makeText(getContext(),"Nick name is empty",Toast.LENGTH_LONG).show();
                            }else{
                                copyText(mNickName);
                                Toast.makeText(getContext(),"Text is copied to the clipboard",Toast.LENGTH_LONG).show();
                            }
                            break;
                        case 2:
                            if(!mMyProfileUid.isEmpty()) {
                                shareLinkToProfile(mMyProfileUid);
                            }
                            break;
                        default:
                            break;
                    }
                });
                builder.show();
                break;
            case 1:
                builder.setItems(R.array.dialog_phone_number, (dialog, which) -> {
                    Log.i(TAG, "Opened dialog window ");
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
                builder.setItems(R.array.dialog_bio, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            NavDirections action = ProfileFragmentDirections.actionProfileFragmentToEdiBioFragment();
                            Navigation.findNavController(getView()).navigate(action);
                            break;
                        case 1:
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

    private void initViewElements(View view) {
        appBarLayout = (AppBarLayout) view.findViewById(R.id.app_bar_profile);
        mUserName = view.findViewById(R.id.userNameProfile);
        mUserStatus = view.findViewById(R.id.userStatus);
        mListView = view.findViewById(R.id.lv_profile);
        mImageProfile = view.findViewById(R.id.image_profile_app_bar);
        mFabEdit = view.findViewById(R.id.edit_profile_fab);
        progressBar = view.findViewById(R.id.progressBar_myProfile);
    }
    private void progressView(boolean action) {
        if (action) {
            progressBar.setVisibility(View.INVISIBLE);
            mUserName.setVisibility(View.VISIBLE);
            mUserStatus.setVisibility(View.VISIBLE);
            appBarLayout.setExpanded(true, true);
            mFabEdit.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.VISIBLE);

        } else {
            progressBar.setVisibility(View.VISIBLE);
            mUserName.setVisibility(View.INVISIBLE);
            mUserStatus.setVisibility(View.INVISIBLE);
            appBarLayout.setExpanded(false, true);
            mFabEdit.setVisibility(View.INVISIBLE);
            mListView.setVisibility(View.INVISIBLE);
        }
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
            if (entry.getKey().equals("uid")) {
                this.mMyProfileUid = String.valueOf(entry.getValue());
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
                String Uri = String.valueOf(entry.getValue());

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                if(!Uri.isEmpty()){
                    storageRef.child("uploadsUserIcon/").child(String.valueOf(entry.getValue())).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for 'users/me/profile.png'
                            Glide.with(mActivity).load(uri).centerInside().into(mImageProfile);
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

    private void fileChoose() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(mImageProfile);

            if (mUploadTask != null && mUploadTask.isInProgress()) {
                Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadFile();
            }
        }
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = ((AppCompatActivity)getActivity()).getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        final LoadingDialog loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child(user.getUid()
                    + "." + getFileExtension(mImageUri));
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                }

                            }, 500);
                            Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_LONG).show();
                            loadingDialog.dismisDialog();
                            HashMap<String, Object> newValues = new HashMap<>();
                            newValues.put("imageUser",String.valueOf(user.getUid()+"."+getFileExtension(mImageUri)));
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            mDatabaseRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Map<String, Object> userValues = new HashMap<String, Object>();
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        userValues.put(dataSnapshot.getKey(), dataSnapshot.getValue());
                                    }

                                    for (Map.Entry<String, Object> entry : newValues.entrySet()) {
                                        String key = entry.getKey();
                                        Object value = entry.getValue();
                                        userValues.put(key, value);
                                    }

                                    mDatabaseRef.child(user.getUid()).updateChildren(userValues);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
//                            mProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    //Create app top bar menu
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.menu_add_to_contacts).setVisible(false);
        menu.findItem(R.id.menu_search).setVisible(false);
        menu.findItem(R.id.menu_create_group).setVisible(false);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int idMenuItem = item.getItemId();
        if (idMenuItem == R.id.menu_logout) {
            logoutUser();
        }

        if (idMenuItem == R.id.menu_settings) {
            NavDirections action = ProfileFragmentDirections.actionProfileFragmentToSettingsFragment();
            Navigation.findNavController(getView()).navigate(action);
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        NavDirections action = ProfileFragmentDirections.actionProfileFragmentToOTPNumberFragment();
        Navigation.findNavController(getView()).navigate(action);
    }


}
