package com.kabasonic.messenger.ui.bottomnavigation.groups.tabs;

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
import com.kabasonic.messenger.models.User;
import com.kabasonic.messenger.ui.adapters.AdapterRequestItem;
import com.kabasonic.messenger.ui.adapters.items.RowItem;

import java.util.ArrayList;

public class RequestGroupsFragment extends Fragment {
    public static final String TAG = "RequestGroupsFragment";

    MainActivity mActivity;
    public ArrayList<User> mRowsItems;
    private RecyclerView mRecyclerView;
    private AdapterRequestItem mAdapterRequestItem;
    private LinearLayoutManager mLayoutManager;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(mActivity instanceof MainActivity){
            mActivity = (MainActivity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_request_groups,container,false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //createExampleList();
        //buildRecyclerView();
    }

    private void buildRecyclerView() {
        mRecyclerView = getView().findViewById(R.id.rvRequestGroups);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager( mActivity);
        mAdapterRequestItem = new AdapterRequestItem(mRowsItems);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapterRequestItem);
        mAdapterRequestItem.setOnItemClickListener(new AdapterRequestItem.RequestItemRow() {
            @Override
            public void onItemClick(String uid) {
                Toast.makeText(getContext(),"Clicked row", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onItemAccept(String uid, int position) {
                Log.i(TAG,"Cliked button ACCEPT " + position);
                mRowsItems.remove(position);
                mAdapterRequestItem.notifyItemRemoved(position);
            }

            @Override
            public void onItemDecline(String uid, int position) {
                Log.i(TAG,"Cliked button DECLINE " + position);
                mRowsItems.remove(position);
                mAdapterRequestItem.notifyItemRemoved(position);
            }
        });
    }






//
//    private void createExampleList() {
//        mRowsItems = new ArrayList<>();
//        mRowsItems.add(new RowItem(R.drawable.image_profile_test,"Elita Politecnika Lubelska","Студенты Политеха. Все здесь, Юра здесь, Дядя Паша кладе бруківку, беху купив, жінка є, ЧОБІТКИ"));
//        mRowsItems.add(new RowItem(R.drawable.image_profile_test,"Elita Politecnika Lubelska","Студенты Политеха. Все здесь, Юра здесь, Дядя Паша кладе бруківку, беху купив, жінка є, ЧОБІТКИ"));
//        mRowsItems.add(new RowItem(R.drawable.image_profile_test,"Elita Politecnika Lubelska","Студенты Политеха. Все здесь, Юра здесь, Дядя Паша кладе бруківку, беху купив, жінка є, ЧОБІТКИ"));
//        mRowsItems.add(new RowItem(R.drawable.image_profile_test,"Elita Politecnika Lubelska","Студенты Политеха. Все здесь, Юра здесь, Дядя Паша кладе бруківку, беху купив, жінка є, ЧОБІТКИ"));
//        mRowsItems.add(new RowItem(R.drawable.image_profile_test,"Elita Politecnika Lubelska","Студенты Политеха. Все здесь, Юра здесь, Дядя Паша кладе бруківку, беху купив, жінка є, ЧОБІТКИ"));
//    }


}
