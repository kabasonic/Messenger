package com.kabasonic.messenger.ui.bottomnavigation.groups.tabs;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class AdapterTabsGroups extends FragmentPagerAdapter {
    public static final String TAG = "AdapterTabsGroups";
    private int numberOfTab;

    public AdapterTabsGroups (FragmentManager fragmentManager, int numberOfTab){
        super(fragmentManager);
        this.numberOfTab = numberOfTab;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position){
            case 0:
                fragment = new AllGroupsFragment();
                break;
            case 1:
                fragment = new MyGroupsFragment();
                break;
            case 2:
                fragment = new RequestGroupsFragment();
                break;
            default:
                Log.i(TAG,"Null references");
                fragment = null;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return numberOfTab;
    }
}
