package com.kabasonic.messenger.ui.bottomnavigation.contacts;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
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
import com.kabasonic.messenger.ui.adapters.AdapterTabsContacts;
import com.kabasonic.messenger.ui.bottomnavigation.contacts.tabs.AllContactsFragment;

public class ContactsFragment extends Fragment {
    public static final String TAG = "ContactsFragment";

    private AdapterTabsContacts adapterTabsContacts;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private BadgeDrawable mRequsetBadget;
    private TabLayout.Tab mTabRequest;
    MainActivity mActivity;

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
        View root;
        root = inflater.inflate(R.layout.contacts_fragment, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        initTabsViewPager(view);
        //setBudgestToRequest();
        countRequest();
    }

    private void setBudgestToRequest(int countRequest) {
        mTabRequest = tabLayout.getTabAt(2);
        assert mTabRequest != null;
        mRequsetBadget = mTabRequest.getOrCreateBadge();
        if(countRequest != 0){
            mRequsetBadget.setVisible(true);
            mRequsetBadget.setNumber(countRequest);
        }else{
            mRequsetBadget.setVisible(false);
        }
    }

    private void initTabsViewPager(View view) {
        tabLayout = view.findViewById(R.id.contacts_tab);
        adapterTabsContacts = new AdapterTabsContacts(getChildFragmentManager(), tabLayout.getTabCount());
        viewPager = view.findViewById(R.id.vp_contacts);
        viewPager.setAdapter(adapterTabsContacts);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                Log.i(TAG, "TAB POSITION: " + tab.getPosition());
                switch (tab.getPosition()) {
                    case 0://Tab All
                        break;
                    case 1://Tab Online
                        break;
                    case 2://Tab Request
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void countRequest(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("request").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int countRequest = 0;

                countRequest = (int) snapshot.getChildrenCount();
                Log.d(TAG,"Count request: " + countRequest);
                setBudgestToRequest(countRequest);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}