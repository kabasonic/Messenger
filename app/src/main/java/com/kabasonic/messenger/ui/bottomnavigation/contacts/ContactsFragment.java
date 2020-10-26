package com.kabasonic.messenger.ui.bottomnavigation.contacts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.ui.bottomnavigation.contacts.tabs.AdapterTabsContacts;

public class ContactsFragment extends Fragment{
    public static final String TAG = "ContactsFragment";
    private AdapterTabsContacts adapterTabsContacts;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root;
        root = inflater.inflate(R.layout.contacts_fragment,container,false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initTabsViewPager(view);
    }

    private void initTabsViewPager(View view) {
        tabLayout = view.findViewById(R.id.contacts_tab);
        adapterTabsContacts = new AdapterTabsContacts(getChildFragmentManager(),tabLayout.getTabCount());
        viewPager = view.findViewById(R.id.vp_contacts);
        viewPager.setAdapter(adapterTabsContacts);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
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
}