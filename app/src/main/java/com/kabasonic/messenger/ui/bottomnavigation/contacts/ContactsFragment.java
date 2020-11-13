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
import com.google.firebase.database.FirebaseDatabase;
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
        setBudgestToRequest();
    }

    private void setBudgestToRequest() {
        mTabRequest = tabLayout.getTabAt(2);
        assert mTabRequest != null;
        mRequsetBadget = mTabRequest.getOrCreateBadge();
        mRequsetBadget.setNumber(12);
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

//    // Create app top bar menu
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        setHasOptionsMenu(true);
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        inflater.inflate(R.menu.main_menu, menu);
//        menu.findItem(R.id.menu_qr_code_scan).setVisible(false);
//        menu.findItem(R.id.menu_logout).setVisible(false);
//        menu.findItem(R.id.menu_create_group).setVisible(false);
//
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int idMenuItem = item.getItemId();
//        switch (idMenuItem) {
//            case R.id.menu_search:
//                Log.i(TAG, "Click search button");
//
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

}