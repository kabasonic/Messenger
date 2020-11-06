package com.kabasonic.messenger.ui.bottomnavigation.groups.tabs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kabasonic.messenger.MainActivity;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.ui.adapters.AdapterDoubleItem;
import com.kabasonic.messenger.ui.adapters.items.RowItem;

import java.util.ArrayList;

public class MyGroupsFragment extends Fragment {

    public static final String TAG = "MyGroupsFragment";

    MainActivity mActivity;
    public ArrayList<RowItem> mRowsItems;
    private RecyclerView mRecyclerView;
    private AdapterDoubleItem mAdapterDoubleItem;
    private RecyclerView.LayoutManager mLayoutManager;

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
        View root = inflater.inflate(R.layout.fragment_my_groups,container,false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createExampleList();
        buildRecyclerView();
    }

    private void buildRecyclerView() {
        mRecyclerView = getView().findViewById(R.id.rvMyGroups);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mActivity);
        mAdapterDoubleItem = new AdapterDoubleItem(mRowsItems);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapterDoubleItem);
        mAdapterDoubleItem.setOnItemClickListener(new AdapterDoubleItem.SingleItemRow() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getContext(),"Cliked row",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createExampleList() {
        mRowsItems = new ArrayList<>();
        mRowsItems.add(new RowItem(R.drawable.image_profile_test,"Elite Politechnika Lubelska","Private group"));
        mRowsItems.add(new RowItem(R.drawable.image_profile_test,"Elite Politechnika Lubelska","Private group"));
        mRowsItems.add(new RowItem(R.drawable.image_profile_test,"Elite Politechnika Lubelska","Private group"));
        mRowsItems.add(new RowItem(R.drawable.image_profile_test,"Elite Politechnika Lubelska","Private group"));
        mRowsItems.add(new RowItem(R.drawable.image_profile_test,"Elite Politechnika Lubelska","Private group"));
        mRowsItems.add(new RowItem(R.drawable.image_profile_test,"Elite Politechnika Lubelska","Private group"));
    }


}
