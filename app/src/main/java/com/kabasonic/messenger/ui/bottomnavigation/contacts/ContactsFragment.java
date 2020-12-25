package com.kabasonic.messenger.ui.bottomnavigation.contacts;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.kabasonic.messenger.MainActivity;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.ui.adapters.AdapterTabsContacts;
import com.kabasonic.messenger.ui.bottomnavigation.contacts.viewmodels.RequestContactsViewModel;

public class ContactsFragment extends Fragment {
    public static final String TAG = "ContactsFragment";

    private AdapterTabsContacts adapterTabsContacts;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private BadgeDrawable mRequsetBadget;
    private TabLayout.Tab mTabRequest;
    private MainActivity mActivity;
    private RequestContactsViewModel mViewModel;

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
        mViewModel = ViewModelProviders.of(this).get(RequestContactsViewModel.class);
        mViewModel.init();
        mViewModel.getCountRequests().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                setBudgestToRequest(integer);
            }
        });

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
    }

}