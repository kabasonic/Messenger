package com.kabasonic.messenger.ui.bottomnavigation.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.kabasonic.messenger.R;
import com.kabasonic.messenger.ui.bottomnavigation.profile.adapter.AdapterItemThreeRowProfile;
import com.kabasonic.messenger.ui.bottomnavigation.profile.adapter.AdapterItemTwoRowProfile;
import com.kabasonic.messenger.ui.bottomnavigation.profile.adapter.ModelItemProfile;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";

    //ProfileViewModel profileViewModel;
    Toolbar toolbar;
    AppBarLayout appBarLayout;
    ListView firstLV,
            secondLV;

    NestedScrollView nestedScrollView;

    private ArrayList<ModelItemProfile> rowListProfileFirst = new ArrayList<ModelItemProfile>() ;
    private ArrayList<ModelItemProfile> rowListProfileSecond = new ArrayList<ModelItemProfile>() ;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
//        profileViewModel =
//                ViewModelProviders.of(this).get(ProfileViewModel.class);
//        View root = inflater.inflate(R.layout.profile_fragment, container, false);


//        final TextView textView = root.findViewById(R.id.text);
//        profileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        View root = inflater.inflate(R.layout.profile_fragment, container, false);
        toolbar = (androidx.appcompat.widget.Toolbar) root.findViewById(R.id.toolbar_profile);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);


        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firstLV = view.findViewById(R.id.firstLV);
        secondLV = view.findViewById(R.id.secondLV);

        String[] titleFirstLV = getResources().getStringArray(R.array.listOneTitleProfile);
        String[] subtitleFirstLV = getResources().getStringArray(R.array.listOneSubtitleProfile);
        String[] titleSecondLV = getResources().getStringArray(R.array.listTwoTitleProfile);

        Integer[] imagesOne = { R.drawable.ic_round_alternate_email_24,
                R.drawable.ic_round_call_24,
                R.drawable.ic_round_info_24};

        Integer[] imagesTwo = { R.drawable.ic_round_notifications_24,
                R.drawable.ic_round_lock_24,
                R.drawable.ic_round_info_24,
                R.drawable.ic_round_language_24,
                R.drawable.ic_round_live_help_24,
                R.drawable.ic_round_help_24};

        for (int i = 0; i < imagesOne.length; i++) {
            ModelItemProfile itemRowOne = new ModelItemProfile(titleFirstLV[i],subtitleFirstLV[i],imagesOne[i]);
            rowListProfileFirst.add(itemRowOne);
        }

        for (int i = 0; i < imagesTwo.length; i++) {
            ModelItemProfile itemRowTwo = new ModelItemProfile(titleSecondLV[i], imagesTwo[i]);
            rowListProfileSecond.add(itemRowTwo);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        AdapterItemThreeRowProfile adapterThreeRow = new AdapterItemThreeRowProfile(getContext(), R.layout.three_row, rowListProfileFirst);
        firstLV.setAdapter(adapterThreeRow);

        AdapterItemTwoRowProfile adapterTwoRow = new AdapterItemTwoRowProfile(getContext(), R.layout.two_row, rowListProfileSecond);
        secondLV.setAdapter(adapterTwoRow);
    }

    //Create app top bar menu
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.menu_search).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int idMenuItem = item.getItemId();
        switch (idMenuItem) {
            case R.id.menu_logout:
                Log.i(TAG, "Click logout button");
                FirebaseAuth.getInstance().signOut();
                NavDirections action = ProfileFragmentDirections.actionProfileFragmentToOTPNumberFragment();
                Navigation.findNavController(getView()).navigate(action);
                break;
            case R.id.menu_qr_code_scan:
                Log.i(TAG, "Click QR code scan button");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

//    public ArrayList getSecondList(String[] titleSecondLV,Integer[] imagesTwo){
//        for (int i = 0; i < imagesTwo.length; i++) {
//
//            ModelItemProfile itemRow = new ModelItemProfile(titleSecondLV[i],imagesTwo[i]);
//            rowListProfile.add(itemRow);
//        }
//        return rowListProfile;
//    }
//
//    public ArrayList getFirstList(String[] titleFirstLV, String[] subtitleFirstLV,Integer[] imagesOne){
//        for (int i = 0; i < imagesOne.length; i++) {
//            ModelItemProfile itemRow = new ModelItemProfile(titleFirstLV[i],subtitleFirstLV[i],imagesOne[i]);
//            rowListProfile.add(itemRow);
//        }
//        return rowListProfile;
//    }




}