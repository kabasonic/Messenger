package com.kabasonic.messenger.ui.bottomnavigation.groups;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.ui.adapters.AdapterTabsGroups;

public class GroupsFragment extends Fragment {

    public static final String TAG = "GroupsFragment";
    private ViewPager viewPager;
    private AdapterTabsGroups adapterTabsGroups;
    private TabLayout tabLayout;
    private TabLayout.Tab mTabRequset;
    private BadgeDrawable mRequestBadget;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.groups_fragment,container,false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        initTabsViewPager(view);
    }

    private void initTabsViewPager(View view) {
        tabLayout = view.findViewById(R.id.groups_tab);
        adapterTabsGroups = new AdapterTabsGroups(getChildFragmentManager(),tabLayout.getTabCount());
        viewPager = view.findViewById(R.id.vp_groups);
        viewPager.setAdapter(adapterTabsGroups);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        mTabRequset = tabLayout.getTabAt(2);
        mRequestBadget = mTabRequset.getOrCreateBadge();
        mRequestBadget.setNumber(100);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    // Create app top bar menu
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu,menu);
        menu.findItem(R.id.menu_qr_code_scan).setVisible(false);
        menu.findItem(R.id.menu_logout).setVisible(false);
        menu.findItem(R.id.menu_create_group).setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int idMenuItem = item.getItemId();
        switch (idMenuItem){
            case R.id.menu_search:
                Log.i(TAG,"Click search button");
                break;
            case R.id.menu_create_group:
                Log.i(TAG,"Click more button");
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}