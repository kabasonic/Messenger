package com.kabasonic.messenger.ui.bottomnavigation.contacts.tabs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.kabasonic.messenger.ui.adapters.AdapterRequestItem;
import com.kabasonic.messenger.ui.bottomnavigation.contacts.viewmodels.RequestContactsViewModel;

import java.util.List;

public class RequestContactsFragment extends Fragment {

    public static final String TAG = "RequestContactsFragment";

    public List<User> mRequestList;
    private RecyclerView mRecyclerView;
    private AdapterRequestItem mAdapterRequestItem;
    private RecyclerView.LayoutManager mLayoutManager;
    private MainActivity mActivity;
    private RequestContactsViewModel mRequestContactsViewModel;
    private ImageView imageRequest;
    private TextView textRequest;

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
        Log.d(TAG,"onViewCreated");
        mRecyclerView = view.findViewById(R.id.rvRequestContacts);
        imageRequest = view.findViewById(R.id.image_request);
        textRequest = view.findViewById(R.id.text_request);

        mRequestContactsViewModel = ViewModelProviders.of(this).get(RequestContactsViewModel.class);
        mRequestContactsViewModel.init();

        buildRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");

        mRequestContactsViewModel.getRequests().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                if(users.isEmpty()){
                    textRequest.setVisibility(View.VISIBLE);
                    imageRequest.setVisibility(View.VISIBLE);
                }else{
                    textRequest.setVisibility(View.INVISIBLE);
                    imageRequest.setVisibility(View.INVISIBLE);
                }
                mAdapterRequestItem.setRequest(users);
                mAdapterRequestItem.notifyDataSetChanged();
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
        mAdapterRequestItem = new AdapterRequestItem(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapterRequestItem.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapterRequestItem);
        mAdapterRequestItem.setOnItemClickListener(new AdapterRequestItem.RequestItemRow() {
            @Override
            public void onItemClick(String uid) {
                Toast.makeText(getContext(),"Clicked row",Toast.LENGTH_SHORT).show();
                navigation(uid);
            }

            @Override
            public void onItemAccept(String uid, int position) {
                Log.i(TAG,"Cliked button ACCEPT " + uid);
                mAdapterRequestItem.removeItem(position);
                mAdapterRequestItem.notifyItemRemoved(position);
                mRequestContactsViewModel.actionRequest(uid,true);
            }

            @Override
            public void onItemDecline(String uid, int position) {
                Log.i(TAG,"Cliked button DECLINE " + position);
                mAdapterRequestItem.removeItem(position);
                mAdapterRequestItem.notifyItemRemoved(position);
                mRequestContactsViewModel.actionRequest(uid,false);
            }
        });
    }

    private void navigation(String currentUid){
        Bundle bundle = new Bundle();
        bundle.putString("uid",currentUid);
        Navigation.findNavController(getView()).navigate(R.id.userProfileFragment, bundle);
    }

}
