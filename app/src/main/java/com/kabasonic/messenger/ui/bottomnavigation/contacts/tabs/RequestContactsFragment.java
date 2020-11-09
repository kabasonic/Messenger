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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kabasonic.messenger.MainActivity;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.ui.adapters.AdapterRequestItem;
import com.kabasonic.messenger.ui.adapters.items.RowItem;

import java.util.ArrayList;

public class RequestContactsFragment extends Fragment {

    public static final String TAG = "RequestContactsFragment";

    public ArrayList<RowItem> mRequestItems = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private AdapterRequestItem mAdapterRequestItem;
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
        View root = inflater.inflate(R.layout.fragment_request_contacts, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createExampleList();
        buildRecyclerView();
    }

    public void createExampleList() {
        mRequestItems = new ArrayList<>();
        mRequestItems.add(new RowItem(R.drawable.image_profile_test,"Diadia Pasha","Jestem pracownikim firmy BRUKIWKA.ua"));
        mRequestItems.add(new RowItem(R.drawable.image_profile_test,"Filip Duda",getResources().getString(R.string.lorem_ipsum_desc)));
        mRequestItems.add(new RowItem(R.drawable.image_profile_test,"Yura Shnyt",getResources().getString(R.string.lorem_ipsum_desc)));

    }
    public void buildRecyclerView() {
        mRecyclerView = getView().findViewById(R.id.rvRequestContacts);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mActivity);
        mAdapterRequestItem = new AdapterRequestItem(mRequestItems);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapterRequestItem);
        mAdapterRequestItem.setOnItemClickListener(new AdapterRequestItem.RequestItemRow() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getContext(),"Clicked row",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemAccept(int position) {
                Log.i(TAG,"Cliked button ACCEPT " + position);
                mRequestItems.remove(position);
                mAdapterRequestItem.notifyItemRemoved(position);
            }

            @Override
            public void onItemDecline(int position) {
                Log.i(TAG,"Cliked button DECLINE " + position);
                mRequestItems.remove(position);
                mAdapterRequestItem.notifyItemRemoved(position);
            }
        });
    }
}
