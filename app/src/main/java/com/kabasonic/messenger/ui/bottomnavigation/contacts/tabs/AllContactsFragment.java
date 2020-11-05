package com.kabasonic.messenger.ui.bottomnavigation.contacts.tabs;

import android.content.Context;
import android.os.Bundle;
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
import com.kabasonic.messenger.ui.adapters.AdapterSingleItem;
import com.kabasonic.messenger.ui.adapters.items.RowItem;

import java.util.ArrayList;

public class AllContactsFragment extends Fragment {

    public static final String TAG = "AllContactsFragment";

    public ArrayList<RowItem> mRowItems = new ArrayList<>();
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
        createExampleList();
        buildRecyclerView();
    }

    public void createExampleList() {
        mRowItems = new ArrayList<>();
        mRowItems.add(new RowItem(R.drawable.image_profile_test,R.drawable.status_online, "Jacek Bura"));
        mRowItems.add(new RowItem(R.drawable.image_profile_test,R.drawable.status_online, "Filip Duda"));
        mRowItems.add(new RowItem(R.drawable.image_profile_test,R.drawable.status_online, "Yura Shnyt"));

    }
    public void buildRecyclerView() {
        mRecyclerView = getView().findViewById(R.id.rvAllContacts);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mActivity);
        mAdapterSingleItem = new AdapterSingleItem(mRowItems);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapterSingleItem);
        mAdapterSingleItem.setOnItemClickListener(new AdapterSingleItem.SingleItemRow() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getContext(),"Clicked row",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMoreButtonClick(int position) {
                Toast.makeText(getContext(),"Clicked button",Toast.LENGTH_SHORT).show();
            }
        });
    }


}
