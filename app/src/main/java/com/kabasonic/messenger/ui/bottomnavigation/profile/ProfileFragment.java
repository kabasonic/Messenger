package com.kabasonic.messenger.ui.bottomnavigation.profile;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.models.User;
import com.kabasonic.messenger.ui.adapters.AdapterProfileDoubleItem;
import com.kabasonic.messenger.ui.adapters.AdapterProfileSingleItem;
import com.kabasonic.messenger.ui.adapters.items.RowItem;
import com.kabasonic.messenger.ui.bottomnavigation.LoadingDialog;
import com.squareup.picasso.Picasso;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";
    //ProfileViewModel profileViewModel;
    // Toolbar mToolbar;
    //AppBarLayout appBarLayout;
    private ListView mFirstLV, mSecondLV;
    private TextView mUserStatus, mUsername;
    private FloatingActionButton mEditUserName;
    private ArrayList<RowItem> mRowListFirst;
    private ArrayList<RowItem> mRowListSecond;
    private ImageView imageUser;
    private DatabaseReference mDatabase;

    private Uri mImageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.profile_fragment, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mStorageRef = FirebaseStorage.getInstance().getReference("uploadsUserIcon/");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users");
        initViewElements();
        createLists();
        buildListView();
        listenerLists();
        listenerButtons();
        imageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"Image click");
                chooseImage();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getUserFirebase();
    }

    private void chooseImage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.dialog_profile_user_image, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "Opened dialog window ");
                switch (which) {
                    case 0:
                        fileChoose();
                        Log.i(TAG, "Selected item " + which);
                        break;
                    case 1:
                        deleteImage();
                        Log.i(TAG, "Selected item " + which);
                        break;
                    default:
                        Log.i(TAG, "Not selected item ");
                        break;
                }
            }
        });
        builder.show();
    }

    private void deleteImage() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Object> newValues = new HashMap<>();
                newValues.put("imageUser","");
                mDatabase.child("users").child(user.getUid()).updateChildren(newValues);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
            //Picasso.with(this).load(mImageUri).into(imageUser);
            Picasso.get().load(mImageUri).into(imageUser);

            if (mUploadTask != null && mUploadTask.isInProgress()) {
                Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadFile();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserFirebase();
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
            Log.i(TAG, "Click logout button");
            logoutUser();
        }
        return super.onOptionsItemSelected(item);
    }

    private void listenerButtons() {
        mEditUserName.setOnClickListener(v -> {
            Navigation.findNavController(getView()).navigate(R.id.editUsernameFragment);
        });
    }

    private void initViewElements() {
        imageUser = getView().findViewById(R.id.image_profile_app_bar);
        mUsername = getView().findViewById(R.id.userNameProfile);
        mUserStatus = getView().findViewById(R.id.userStatus);
        mFirstLV = getView().findViewById(R.id.firstLV);
        mSecondLV = getView().findViewById(R.id.secondLV);
        mEditUserName = getView().findViewById(R.id.edit_profile_fab);
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        NavDirections action = ProfileFragmentDirections.actionProfileFragmentToOTPNumberFragment();
        Navigation.findNavController(getView()).navigate(action);
    }

    private void buildListView() {
//        firstLV = getView().findViewById(R.id.firstLV);
//        secondLV = getView().findViewById(R.id.secondLV);

//        AdapterProfileDoubleItem adapterThreeRow = new AdapterProfileDoubleItem(getContext(), R.layout.double_row_profile, mRowListFirst);
//        mFirstLV.setAdapter(adapterThreeRow);

        AdapterProfileSingleItem adapterTwoRow = new AdapterProfileSingleItem(getContext(), R.layout.single_row_profile, mRowListSecond);
        mSecondLV.setAdapter(adapterTwoRow);

    }

    private void listenerLists() {
        mFirstLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                actionsFirstList(position);
            }
        });

        mSecondLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private void createLists() {
        String[] titleFirstLV = getResources().getStringArray(R.array.listOneTitleProfile);
        String[] subtitleFirstLV = getResources().getStringArray(R.array.listOneSubtitleProfile);
        String[] titleSecondLV = getResources().getStringArray(R.array.listTwoTitleProfile);

        Integer[] imagesOne = {R.drawable.ic_round_alternate_email_24,
                R.drawable.ic_round_call_24,
                R.drawable.ic_round_info_24};

        Integer[] imagesTwo = {R.drawable.ic_round_notifications_24,
                R.drawable.ic_round_lock_24,
                R.drawable.ic_round_color_lens_24,
                R.drawable.ic_round_language_24,
                R.drawable.ic_round_live_help_24,
                R.drawable.ic_round_help_24};

        //mRowListFirst = new ArrayList<>();
        mRowListSecond = new ArrayList<>();

//        for (int i = 0; i < imagesOne.length; i++) {
//            mRowListFirst.add(new RowItem(imagesOne[i], titleFirstLV[i], subtitleFirstLV[i]));
//        }

        for (int i = 0; i < imagesTwo.length; i++) {
            mRowListSecond.add(new RowItem(imagesTwo[i], titleSecondLV[i]));
        }
    }

    private void actionsFirstList(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        switch (position) {
            case 0:
                builder.setItems(R.array.dialog_nickname, (dialog, which) -> {
                    Log.i(TAG, "Opened dialog window ");
                    switch (which) {
                        case 0:
                            Log.i(TAG, "Selected item " + which);
                            NavDirections action = ProfileFragmentDirections.actionProfileFragmentToEditNicknameFragment();
                            Navigation.findNavController(getView()).navigate(action);
                            break;
                        case 1:
                            Log.i(TAG, "Selected item " + which);
                            break;
                        case 2:
                            Log.i(TAG, "Selected item " + which);
                            break;
                        default:
                            Log.i(TAG, "Not selected item ");
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
                            Log.i(TAG, "Selected item " + which);
                            break;
                        case 1:
                            Log.i(TAG, "Selected item " + which);
                            break;
                        default:
                            Log.i(TAG, "Not selected item ");
                            break;
                    }
                });
                builder.show();
                break;
            case 2://BIO Fragment
                builder.setItems(R.array.dialog_bio, (dialog, which) -> {
                    Log.i(TAG, "Opened dialog window ");
                    switch (which) {
                        case 0:
                            NavDirections action = ProfileFragmentDirections.actionProfileFragmentToEdiBioFragment();
                            Navigation.findNavController(getView()).navigate(action);
                            Log.i(TAG, "Selected item " + which);
                            break;
                        case 1:
                            final String[] messageBio = new String[1];
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String messageBio = null;
                                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                                        if(dataSnapshot.getKey().equals("bio")){
                                            if(String.valueOf(dataSnapshot.getValue()).isEmpty()){
                                                messageBio = "You don't have a bio.";
                                            } else{
                                                messageBio = String.valueOf(dataSnapshot.getValue());
                                                break;
                                            }
                                        }
                                    }
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                                    builder1.setMessage(messageBio).create();
                                    builder1.show();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            Log.i(TAG, "Selected item " + which);
                            break;
                        default:
                            Log.i(TAG, "Not selected item ");
                            break;
                    }
                });
                builder.show();
                break;
            default:
                break;
        }
    }



    private void getUserFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
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
                    mUsername.setText(userName);
                    break;
                case "lastName":
                    Log.d(TAG, "lastName: " + String.valueOf(item.getValue()));
                    userName += " " + String.valueOf(item.getValue()).trim();
                    mUsername.setText(userName);
                    break;
                case "imageUser":
                    Log.d(TAG, "imageUser: " + String.valueOf(item.getValue()));
                    String Uri = String.valueOf(item.getValue());
                    //Glide.with(ProfileFragment.this).load(Uri).fitCenter().into(imageUser);
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    if(!Uri.isEmpty()){
                        storageRef.child("uploadsUserIcon/").child(String.valueOf(item.getValue())).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Got the download URL for 'users/me/profile.png'
                                Glide.with(ProfileFragment.this).load(uri).centerInside().into(imageUser);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                            }
                        });
                    }
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

        String[] subtitleLV = getContext().getResources().getStringArray(R.array.listOneSubtitleProfile);

        Integer[] imagesOne = {R.drawable.ic_round_alternate_email_24,
                R.drawable.ic_round_call_24,
                R.drawable.ic_round_info_24};

        mRowListFirst = new ArrayList<>();

        for (int i = 0; i < imagesOne.length; i++) {
            mRowListFirst.add(new RowItem(imagesOne[i], userinfo[i], subtitleLV[i]));
        }
        AdapterProfileDoubleItem adapterThreeRow = new AdapterProfileDoubleItem(getContext(), R.layout.double_row_profile, mRowListFirst);
        mFirstLV.setAdapter(adapterThreeRow);

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
                                    //mProgressBar.setProgress(0);
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

}