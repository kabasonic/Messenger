package com.kabasonic.messenger.ui.adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.kabasonic.messenger.ui.bottomnavigation.contacts.tabs.AllContactsFragment;
import com.kabasonic.messenger.ui.bottomnavigation.contacts.tabs.OnlineContactsFragment;
import com.kabasonic.messenger.ui.bottomnavigation.contacts.tabs.RequestContactsFragment;

public class AdapterTabsContacts extends FragmentPagerAdapter {
    public static final String TAG = "AdapterTabsContacts";
    private int numberOfTab;

    public AdapterTabsContacts(FragmentManager fragmentManager, int numberOfTab){
        super(fragmentManager);
        this.numberOfTab = numberOfTab;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position){
            case 0:
                fragment = new AllContactsFragment();
                break;
            case 1:
                fragment = new OnlineContactsFragment();
                break;
            case 2:
                fragment = new RequestContactsFragment();
                break;
            default:
                Log.i(TAG,"Null reference");
                fragment = null;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return numberOfTab;
    }
}
