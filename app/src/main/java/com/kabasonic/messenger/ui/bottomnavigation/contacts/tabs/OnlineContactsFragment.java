package com.kabasonic.messenger.ui.bottomnavigation.contacts.tabs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kabasonic.messenger.MainActivity;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.models.User;
import com.kabasonic.messenger.ui.adapters.AdapterContactItem;
import com.kabasonic.messenger.ui.bottomnavigation.contacts.viewmodels.OnlineContactsViewModel;
import com.kabasonic.messenger.ui.userchat.UserChat;

import java.util.List;

public class OnlineContactsFragment extends Fragment {

    public static final String TAG = "OnlineContactsFragment";

    private RecyclerView mRecyclerView;
    private AdapterContactItem mAdapterContactItem;
    private RecyclerView.LayoutManager mLayoutManager;
    private MainActivity mActivity;
    private OnlineContactsViewModel mOnlineContactsViewModel;
    private ImageView imageContact;
    private TextView textContact;

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
        View root = inflater.inflate(R.layout.fragment_online_contacts, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG,"onViewCreated");
        mRecyclerView = getView().findViewById(R.id.rvOnlineContacts);

        imageContact = view.findViewById(R.id.image_online_contacts);
        textContact = view.findViewById(R.id.text_online_contacts);

        mOnlineContactsViewModel = ViewModelProviders.of(this).get(OnlineContactsViewModel.class);
        mOnlineContactsViewModel.init();

        buildRecyclerView();

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        mOnlineContactsViewModel.getOnlineContacts().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                Log.d(TAG,"Users size Online: " + users.size());
                if(users.isEmpty()){
                    textContact.setVisibility(View.VISIBLE);
                    imageContact.setVisibility(View.VISIBLE);
                }else{
                    textContact.setVisibility(View.INVISIBLE);
                    imageContact.setVisibility(View.INVISIBLE);
                }
                mAdapterContactItem.setUsers(users);
                mAdapterContactItem.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
    }

    public void buildRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mActivity);
        mAdapterContactItem = new AdapterContactItem(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapterContactItem.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapterContactItem);

        mAdapterContactItem.setOnItemClickListener(new AdapterContactItem.OnItemClickListener() {
            @Override
            public void onItemClick(String uid, int position) {
                Toast.makeText(getContext(), "Clicked button: " + uid, Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putString("uid",uid);
                Navigation.findNavController(getView()).navigate(R.id.userProfileFragment, bundle);
            }
            @Override
            public void onMoreButtonClick(String uid, int position) {
                alertWindow(uid,position);
            }
        });
    }

    private void alertWindow(String uid, int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.dialog_contact, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG,"Opened dialog window ");
                switch (which){
                    case 0:
                        Log.i(TAG,"Selected item " + which);
                        Intent intent = new Intent(getContext(), UserChat.class);
                        intent.putExtra("uid",uid);
                        getContext().startActivity(intent);
                        break;
                    case 1:
                        Log.i(TAG,"Selected item " + which);
                        shareLinkToProfile(uid);
                        break;
                    case 2:
                        Log.i(TAG,"Selected item " + which);
                        mOnlineContactsViewModel.deleteContact(uid);
                        mAdapterContactItem.deleteItem(position);
                        mAdapterContactItem.notifyItemRemoved(position);
                        //mAdapterContactItem.notifyDataSetChanged();
                        break;
                    default:
                        Log.i(TAG,"Not selected item ");
                        break;
                }
            }
        });
        builder.show();
    }

    private void shareLinkToProfile(String uid){
        Intent myIntent = new Intent(Intent.ACTION_SEND);
        myIntent.setType("text/plain");
        String shareBody = "Application Messenger\nLink to profile user:" +"\nmessenger.me/" + uid;
        myIntent.putExtra(Intent.EXTRA_SUBJECT, shareBody);
        myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(myIntent, "Share"));
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
        menu.findItem(R.id.menu_add_to_contacts).setVisible(false);
        menu.findItem(R.id.menu_logout).setVisible(false);
        menu.findItem(R.id.menu_create_group).setVisible(false);
        menu.findItem(R.id.menu_search).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

}
