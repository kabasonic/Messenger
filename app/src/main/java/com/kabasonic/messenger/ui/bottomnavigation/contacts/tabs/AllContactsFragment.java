package com.kabasonic.messenger.ui.bottomnavigation.contacts.tabs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
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
import com.kabasonic.messenger.models.User;
import com.kabasonic.messenger.ui.adapters.AdapterSingleItem;

import java.util.ArrayList;

public class AllContactsFragment extends Fragment {

    public static final String TAG = "AllContactsFragment";

    private ArrayList<User> mUsersList;
    private RecyclerView mRecyclerView;
    private AdapterSingleItem mAdapterSingleItem;
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
        View root = inflater.inflate(R.layout.fragment_all_contacts, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = getView().findViewById(R.id.rvAllContacts);

    }

    @Override
    public void onStart() {
        super.onStart();
        getAllUsers();
    }

    public void buildRecyclerView(ArrayList<User> mRowItems) {
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mActivity);
        mAdapterSingleItem = new AdapterSingleItem(mRowItems);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapterSingleItem.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapterSingleItem);

        mAdapterSingleItem.setOnItemClickListener(new AdapterSingleItem.OnItemClickListener() {
           @Override
           public void onItemClick(String uid) {
               Toast.makeText(getContext(), "Clicked button: " + uid, Toast.LENGTH_SHORT).show();
               navigation(uid);
           }
           @Override
           public void onMoreButtonClick(String uid) {
               /*
               TODO: Navigation chat fragment with User, Write message, Copy link to user, delete with contact
                */
               alertWindow();
           }
        });
    }

    private void navigation(String currentUid){
//        NavDirections action = AllContactsFragmentDirections.actionAllContactsFragmentToUserProfileFragment();
//        Navigation.findNavController(getView()).navigate(action);
//        AllContactsFragmentDirections.ActionAllContactsFragmentToUserProfileFragment action = AllContactsFragmentDirections.actionAllContactsFragmentToUserProfileFragment();
//        action.setUserUid(currentUid);
//        NavController navController = Navigation.findNavController(mActivity, R.id.fragment);
//        navController.navigate(action);
        //Worked version
//        Navigation.findNavController(getView()).navigate(R.id.userProfileFragment);
        Bundle bundle = new Bundle();
        bundle.putString("uid",currentUid);
        Navigation.findNavController(getView()).navigate(R.id.userProfileFragment, bundle);
    }

    private void alertWindow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.dialog_contact, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "Opened dialog window ");
                switch (which) {
                    case 0:
                        Log.i(TAG, "Selected item " + which);
                        break;
                    case 1:
                        Log.i(TAG, "Selected item " + which);
                        break;
                    case 2:
                        Log.i(TAG, "Selected item " + which);
                        break;
                    case 3:
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

    // Create app top bar menu
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.menu_qr_code_scan).setVisible(false);
        menu.findItem(R.id.menu_logout).setVisible(false);
        menu.findItem(R.id.menu_create_group).setVisible(false);

        MenuItem item = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus) {
                getAllUsers();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!TextUtils.isEmpty(query.trim())){
                    searchUsers(query);
                }else{
                    getAllUsers();
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void getAllUsers() {
        mUsersList = new ArrayList<>();
        FirebaseUser userId = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsersList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User modelUser = dataSnapshot.getValue(User.class);
                    if(!modelUser.getUid().equals(userId.getUid())){
                        mUsersList.add(modelUser);
                    }
                    buildRecyclerView(mUsersList);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void searchUsers(String query) {
        mUsersList = new ArrayList<>();
        FirebaseUser userId = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsersList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    if(!user.getUid().equals(userId.getUid())){
                        if(user.getFirstName().toLowerCase().contains(query.toLowerCase())
                                ||user.getLastName().toLowerCase().contains(query.toLowerCase())||
                                user.getNickName().toLowerCase().contains(query.toLowerCase())){
                            mUsersList.add(user);
                        }
                    }
                    buildRecyclerView(mUsersList);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

}
