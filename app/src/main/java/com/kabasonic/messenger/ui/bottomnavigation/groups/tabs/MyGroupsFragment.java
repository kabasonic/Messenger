package com.kabasonic.messenger.ui.bottomnavigation.groups.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.kabasonic.messenger.R;

public class MyGroupsFragment extends Fragment {

    private MyGroupsViewModel myGroupsViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        myGroupsViewModel =
                ViewModelProviders.of(this).get(MyGroupsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_my_groups, container, false);
        final TextView textView = root.findViewById(R.id.text);
        myGroupsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
