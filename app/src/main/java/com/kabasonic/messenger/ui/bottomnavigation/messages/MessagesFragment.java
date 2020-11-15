package com.kabasonic.messenger.ui.bottomnavigation.messages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.inputmethodservice.Keyboard;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kabasonic.messenger.MainActivity;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.ui.adapters.AdapterMessageItem;
import com.kabasonic.messenger.ui.adapters.AdapterSingleItem;
import com.kabasonic.messenger.ui.adapters.items.RowItem;

import java.util.ArrayList;

public class MessagesFragment extends Fragment {
    public static final String TAG = "MessagesFragment";
    private RecyclerView mRecyclerView;
    public ArrayList<RowItem> mRowItem;
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

        createExampleList();
        buildRecyclerView();


    }

    private void createExampleList() {
        mRowItem = new ArrayList<>();
        mRowItem.add(new RowItem(R.drawable.image_profile_test,false,false,"Pavlo","asdas","send","20:20",2));
        mRowItem.add(new RowItem(R.drawable.image_profile_test,true,false,"Pavlo","asdas","accepted","20:20",2));
        mRowItem.add(new RowItem(R.drawable.image_profile_test,false,true,"Pavlo","asdas","sending","20:20",0));
    }

    private void buildRecyclerView() {
        mRecyclerView = getView().findViewById(R.id.rvMessages);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mActivity);
        mAdapterMessageItem = new AdapterMessageItem(mRowItem);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapterMessageItem);
        mAdapterMessageItem.setOnItemClickListener(new AdapterMessageItem.ItemRow() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getContext(), "Clicked row", Toast.LENGTH_SHORT).show();
            }
        });
    }

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
        menu.findItem(R.id.menu_qr_code_scan).setVisible(false);
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
