package com.kabasonic.messenger.ui.bottomnavigation.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.AppBarLayout;
import com.kabasonic.messenger.R;

public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";
    private ProfileViewModel profileViewModel;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
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
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        return root;
    }



    //Create app top bar menu
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu,menu);
        menu.findItem(R.id.menu_search).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int idMenuItem = item.getItemId();
        switch (idMenuItem){
            case R.id.menu_logout:
                Log.i(TAG,"Click logout button");
                break;
            case R.id.menu_qr_code_scan:
                Log.i(TAG,"Click QR code scan button");
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}